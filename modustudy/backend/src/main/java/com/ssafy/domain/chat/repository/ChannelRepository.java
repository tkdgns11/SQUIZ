package com.ssafy.domain.chat.repository;

import com.ssafy.domain.chat.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

}
