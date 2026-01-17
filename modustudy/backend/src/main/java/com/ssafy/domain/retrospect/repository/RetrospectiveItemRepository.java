package com.ssafy.domain.retrospect.repository;

import com.ssafy.domain.retrospect.entity.RetrospectiveItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetrospectiveItemRepository extends JpaRepository<RetrospectiveItem, Long> {

}
