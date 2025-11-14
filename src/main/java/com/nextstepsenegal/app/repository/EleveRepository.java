package com.nextstepsenegal.app.repository;

import com.nextstepsenegal.app.domain.Eleve;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Eleve entity.
 */
@Repository
public interface EleveRepository extends JpaRepository<Eleve, Long> {
    default Optional<Eleve> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Eleve> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Eleve> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select eleve from Eleve eleve left join fetch eleve.user", countQuery = "select count(eleve) from Eleve eleve")
    Page<Eleve> findAllWithToOneRelationships(Pageable pageable);

    @Query("select eleve from Eleve eleve left join fetch eleve.user")
    List<Eleve> findAllWithToOneRelationships();

    @Query("select eleve from Eleve eleve left join fetch eleve.user where eleve.id =:id")
    Optional<Eleve> findOneWithToOneRelationships(@Param("id") Long id);

    Optional<Eleve> findByEmail(String email);
}
