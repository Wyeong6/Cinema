package com.busanit.repository;

import com.busanit.entity.Member;
import com.busanit.entity.chat.ChatRoom;
import com.busanit.entity.chat.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m WHERE m.email = :memberEmail")
    Page<ChatRoom> findByMemberEmail(@Param("memberEmail") String memberEmail, Pageable pageable);

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m1 JOIN cr.members m2 WHERE m1.email = :recipient AND m2.email = :readEmail AND cr.type = 'active'")
    List<ChatRoom> findByRecipientAndSender(@Param("recipient") String recipient, @Param("readEmail") String readEmail);

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m WHERE m.email = :loginUser AND cr.type = 'active'")
    List<ChatRoom> findActiveChatRoomsByMemberEmail(@Param("loginUser") String loginUser);

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m WHERE m.id = :memberId")
    List<ChatRoom> findByMembersId(@Param("memberId") Long memberId);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.id = :chatRoomId")
    ChatRoom findByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    //타입에 따라 채팅방 조회
    Page<ChatRoom> findByMembersEmailAndType(String memberEmail, String type, Pageable pageable);



}
