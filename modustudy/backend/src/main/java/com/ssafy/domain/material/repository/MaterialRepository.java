package com.ssafy.domain.material.repository;

import com.ssafy.domain.material.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {

}
