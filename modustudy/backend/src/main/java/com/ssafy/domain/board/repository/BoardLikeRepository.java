package com.ssafy.domain.board.repository;

import com.ssafy.domain.board.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

}
