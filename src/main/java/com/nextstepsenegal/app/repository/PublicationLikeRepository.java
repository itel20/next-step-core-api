package com.nextstepsenegal.app.repository;

import com.nextstepsenegal.app.domain.PublicationLike;
import com.nextstepsenegal.app.domain.enumeration.UserType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the PublicationLike entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PublicationLikeRepository extends JpaRepository<PublicationLike, Long> {
    Optional<PublicationLike> findOneByPublicationIdAndUserIdAndUserType(
        Long publicationId,
        Long userId,
        UserType userType
    );
    long countByPublicationId(Long publicationId);
}
