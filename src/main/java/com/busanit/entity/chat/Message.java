package com.busanit.entity.chat;

import com.busanit.domain.chat.MessageDTO;
import com.busanit.entity.BaseTimeEntity;
import com.busanit.entity.Member;
import com.busanit.repository.MemberRepository;
import jakarta.persistence.*;
import lombok.*;

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

    private String messageTitle;

    private boolean isRead;


}