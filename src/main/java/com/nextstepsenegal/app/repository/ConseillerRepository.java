package com.nextstepsenegal.app.repository;

import com.nextstepsenegal.app.domain.Conseiller;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Conseiller entity.
 */
@Repository
public interface ConseillerRepository extends JpaRepository<Conseiller, Long> {
    default Optional<Conseiller> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Conseiller> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Conseiller> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select conseiller from Conseiller conseiller left join fetch conseiller.user",
        countQuery = "select count(conseiller) from Conseiller conseiller"
    )
    Page<Conseiller> findAllWithToOneRelationships(Pageable pageable);

    @Query("select conseiller from Conseiller conseiller left join fetch conseiller.user")
    List<Conseiller> findAllWithToOneRelationships();

    @Query("select conseiller from Conseiller conseiller left join fetch conseiller.user where conseiller.id =:id")
    Optional<Conseiller> findOneWithToOneRelationships(@Param("id") Long id);

    Optional<Conseiller> findByEmail(String email);
}
