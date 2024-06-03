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

    private LocalDateTime lastReadTimestamp;

    @ManyToMany(mappedBy = "chatRooms")
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

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

}