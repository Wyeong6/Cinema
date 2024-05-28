package com.busanit.domain.chat;

import com.busanit.entity.chat.Message;
import lombok.*;

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
    private String messageTitle;



}
