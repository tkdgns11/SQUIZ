package com.ssafy.domain.board.repository;

import com.ssafy.domain.board.entity.BoardPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

}
