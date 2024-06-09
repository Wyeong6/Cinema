package com.busanit.domain.chat;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingIndicator {

    private String sender;
    private String recipient;
    private boolean typing;
}
