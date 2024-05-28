package com.busanit.repository;

import com.busanit.entity.chat.ChatRoom;
import com.busanit.entity.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository  extends JpaRepository<Message, Long> {
}
