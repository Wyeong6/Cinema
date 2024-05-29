package com.busanit.domain.chat;

import com.busanit.entity.chat.ChatRoom;
import com.busanit.entity.chat.Message;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {

    private Long id;
    private String userName;
    private String userEmail;
    private String messageTitle;
    List<MessageDTO> messages;

    public static ChatRoomDTO toDTO(ChatRoom chatRoom, String userEmail, String userName) {
        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .userEmail(userEmail)
                .userName(userName)
                .messageTitle(chatRoom.getTitle())
                .messages()
                .build();
    }
}
