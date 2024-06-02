package com.busanit.domain.chat;

import com.busanit.entity.chat.ChatRoom;
import com.busanit.entity.chat.Message;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private LocalDateTime lastReadTimestamp;
    // 읽지 않은 메세지 수를 저장할 필드
    private int unreadMessageCount;

    //채팅룸의 해당 메세지도 반환
    public static ChatRoomDTO toChatRoomDTO(ChatRoom chatRoom, String userEmail, String userName, List<MessageDTO> messageDTOs) {
        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .userEmail(userEmail)
                .userName(userName)
                .messageTitle(chatRoom.getTitle())
                .messages(messageDTOs)
                .build();
    }

    //채팅룸 찾을 때 사용
    public static ChatRoomDTO toChatRoomDTO(ChatRoom chatRoom, String userEmail, String userName) {
        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .userEmail(userEmail)
                .userName(userName)
                .messageTitle(chatRoom.getTitle())
                .messages(new ArrayList<>()) // 메시지 정보를 비워두거나 기본값으로 처리
                .build();
    }
}
