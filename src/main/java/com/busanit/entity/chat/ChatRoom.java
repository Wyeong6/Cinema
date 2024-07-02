package com.busanit.entity.chat;

import com.busanit.entity.Member;
import jakarta.persistence.*;
import lombok.*;
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
            joinColumns = @JoinColumn(name = "chatroom_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
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
            newMembers.forEach(member -> {
                if (!this.members.contains(member)) {
                    this.members.add(member); // 기존 채팅방에 새로운 멤버 추가
                    member.getChatRooms().add(this); // 새로운 멤버의 채팅방 목록에 현재 채팅방 추가
                }
            });
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