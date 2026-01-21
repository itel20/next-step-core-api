package com.nextstepsenegal.app.repository;

import com.nextstepsenegal.app.domain.PublicationShare;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PublicationShare entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PublicationShareRepository extends JpaRepository<PublicationShare, Long> {
    long countByPublicationId(Long publicationId);
}
