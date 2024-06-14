package com.busanit.repository;


import com.busanit.entity.Member;
import com.busanit.entity.chat.ChatRoom;
import com.busanit.entity.chat.ChatRoomReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomReadStatusRepository extends JpaRepository<ChatRoomReadStatus, Long> {

    Optional<ChatRoomReadStatus> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

    Optional<ChatRoomReadStatus> findByChatRoomIdAndMemberEmail(Long chatRoomId, String memberEmail);


}
