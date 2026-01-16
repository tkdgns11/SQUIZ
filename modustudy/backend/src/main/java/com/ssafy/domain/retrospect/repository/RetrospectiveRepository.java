package com.ssafy.domain.retrospect.repository;

import com.ssafy.domain.retrospect.entity.Retrospective;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetrospectiveRepository extends JpaRepository<Retrospective, Long> {

}
