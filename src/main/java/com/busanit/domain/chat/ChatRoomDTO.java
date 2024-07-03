package com.busanit.domain.chat;

import com.busanit.entity.chat.ChatRoom;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.busanit.entity.chat.ChatRoomReadStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {

    private Long id;
    private String userName;
    private String userEmail;
    private String adminEmail;
    private String chatRoomTitle;
    List<MessageDTO> messages;
    private LocalDateTime lastReadTimestamp;
    private int unreadMessageCount;
    private String type;

    //채팅룸의 해당 메세지도 반환
    public static ChatRoomDTO toChatRoomDTO(ChatRoom chatRoom, String userEmail, String userName, List<MessageDTO> messageDTOs) {

        // 사용자의 마지막 읽은 시각을 찾음
        LocalDateTime lastReadTime = chatRoom.getReadStatuses().stream()
                .filter(readStatus -> userEmail.equals(readStatus.getMember().getEmail())) // 해당 사용자의 이메일과 일치하는 항목 찾기
                .map(ChatRoomReadStatus::getLastReadTimestamp) // 마지막 읽은 시각 가져오기
                .findFirst() // 첫 번째 항목 선택 (해당 사용자에 대한 정보가 여러 개일 수 있지만, 일반적으로는 가장 최근 것만 필요)
                .orElse(null); // 만약 해당 사용자의 읽은 정보가 없다면 null 반환

        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .userEmail(userEmail)
                .userName(userName)
                .chatRoomTitle(chatRoom.getTitle())
                .messages(messageDTOs)
                .lastReadTimestamp(lastReadTime)
                .type(chatRoom.getType())
                .build();
    }
}
