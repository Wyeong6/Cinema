package com.busanit.domain.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnterNotificationDTO {
    private String sender;
    private String recipient;
    private String chatRoomId;
    private String type;
}
