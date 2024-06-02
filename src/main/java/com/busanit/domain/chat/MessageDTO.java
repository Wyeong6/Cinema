package com.busanit.domain.chat;

import com.busanit.entity.chat.Message;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Long id;
    private String content;
    private String sender;
    private String recipient;
    private boolean isRead;
    private LocalDateTime timestamp;
    private String messageTitle;

    public static MessageDTO toMessageDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .sender(message.getSender().getEmail())
                .recipient(message.getReceiver().getEmail())
                .messageTitle(message.getChatRoom().getTitle())
                .content(message.getContent())
                .messageTitle(message.getMessageTitle())
                .isRead(message.isRead())
                .build();
    }

}
