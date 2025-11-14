package com.nextstepsenegal.app.repository;

import com.nextstepsenegal.app.domain.BourseConcours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BourseConcoursRepository extends JpaRepository<BourseConcours, Long> {
}
