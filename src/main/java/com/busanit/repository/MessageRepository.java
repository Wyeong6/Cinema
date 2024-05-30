package com.busanit.repository;

import com.busanit.entity.Member;
import com.busanit.entity.chat.ChatRoom;
import com.busanit.entity.chat.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository  extends JpaRepository<Message, Long> {
    //메세지 확인여부
    List<Message> findByReceiverIdAndIsReadFalse(Long receiverId);

    List<Message> findByChatRoomId(Long chatRoomId);



//    Page<Message> findAll(Pageable pageable);
}
