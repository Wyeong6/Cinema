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

    //로그인한 멤버이베일로 채팅방 반환
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m WHERE m.email = :memberEmail")
    Page<ChatRoom> findByMemberEmail(@Param("memberEmail") String memberEmail, Pageable pageable);

    // 두명의 유저가 들어있는 활성중인 채팅방 반환
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m1 JOIN cr.members m2 WHERE m1.email = :recipient AND m2.email = :readEmail AND cr.type = 'active'")
    List<ChatRoom> findByRecipientAndSender(@Param("recipient") String recipient, @Param("readEmail") String readEmail);

    //로그인한 유저의 활성중인 채팅방 반환
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m WHERE m.email = :loginUser AND cr.type = 'active'")
    List<ChatRoom> findActiveChatRoomsByMemberEmail(@Param("loginUser") String loginUser);

    //chatRoomId로 채팅방 반환
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.id = :chatRoomId")
    ChatRoom findByChatRoomId(@Param("chatRoomId") Long chatRoomId);


//    Page<ChatRoom> findByMembersEmailAndType(String memberEmail, String type, Pageable pageable);
    //타입별로 메세지생성시간에 따라 채팅방반환
    @Query("SELECT DISTINCT c " +
            "FROM ChatRoom c " +
            "JOIN FETCH c.messages m " +
            "JOIN c.readStatuses rs " +
            "WHERE rs.member.email = :memberEmail " +
            "AND c.type = :type " +
            "ORDER BY m.regDate DESC")
    Page<ChatRoom> findByMembersEmailAndType(@Param("memberEmail") String memberEmail, @Param("type") String type, Pageable pageable);
}
