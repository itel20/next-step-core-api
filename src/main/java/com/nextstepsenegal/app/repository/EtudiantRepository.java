package com.nextstepsenegal.app.repository;

import com.nextstepsenegal.app.domain.Etudiant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Etudiant entity.
 */
@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    default Optional<Etudiant> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Etudiant> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Etudiant> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select etudiant from Etudiant etudiant left join fetch etudiant.user",
        countQuery = "select count(etudiant) from Etudiant etudiant"
    )
    Page<Etudiant> findAllWithToOneRelationships(Pageable pageable);

    @Query("select etudiant from Etudiant etudiant left join fetch etudiant.user")
    List<Etudiant> findAllWithToOneRelationships();

    @Query("select etudiant from Etudiant etudiant left join fetch etudiant.user where etudiant.id =:id")
    Optional<Etudiant> findOneWithToOneRelationships(@Param("id") Long id);

    Optional<Etudiant> findByEmail(String email);
}
