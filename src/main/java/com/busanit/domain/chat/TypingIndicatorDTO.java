package com.busanit.domain.chat;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingIndicatorDTO {

    private String sender;
    private String recipient;
    private String typing;
}
