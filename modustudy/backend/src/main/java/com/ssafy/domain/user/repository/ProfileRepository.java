package com.ssafy.domain.user.repository;

import com.ssafy.domain.user.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

}
