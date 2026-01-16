package com.ssafy.domain.friend.repository;

import com.ssafy.domain.friend.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

}
