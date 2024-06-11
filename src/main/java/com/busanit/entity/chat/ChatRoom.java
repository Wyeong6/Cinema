package com.busanit.entity.chat;

import com.busanit.entity.Member;
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
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String type;


    @ManyToMany
    @JoinTable(name = "member_chatroom",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "chatroom_id"))
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatRoomReadStatus> readStatuses = new ArrayList<>();

    public void addMessage(Message message) {
        this.messages.add(message);
          message.setChatRoom(this);
    }

    public void addMembers(List<Member> newMembers) {
        if (newMembers != null) {
            this.members.addAll(newMembers);
            newMembers.forEach(member -> member.addChatRoom(this));
        }
    }
        public void addReadStatus(ChatRoomReadStatus chatRoomReadStatus) {
        // 현재 메시지에 읽음 상태를 추가
        this.readStatuses.add(chatRoomReadStatus);
        // messageReadStatus의 message 참조가 현재 메시지가 아니라면 업데이트
        if (chatRoomReadStatus.getChatRoom() != this) {
            chatRoomReadStatus.setChatRoom(this);
        }
    }

}