package com.busanit.entity.chat;

import com.busanit.domain.chat.MessageDTO;
import com.busanit.entity.BaseTimeEntity;
import com.busanit.entity.Member;
import com.busanit.repository.MemberRepository;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    private String content;

//    private String messageTitle;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageReadStatus> readStatuses = new ArrayList<>();
//
//    public void addReadStatus(MessageReadStatus messageReadStatus) {
//        // 현재 메시지에 읽음 상태를 추가
//        this.readStatuses.add(messageReadStatus);
//        // messageReadStatus의 message 참조가 현재 메시지가 아니라면 업데이트
//        if (messageReadStatus.getMessage() != this) {
//            messageReadStatus.setMessage(this);
//        }
//    }


}