package com.busanit.domain.chat;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingIndicatorDTO {
    private String chatRoomId;
    private String sender;
    private String recipient;
    private String typing;
}
