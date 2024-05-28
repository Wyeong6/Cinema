package com.busanit.domain.chat;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {

    private Long id;
    private String name;
    private String userEmail;
    private String messageTitle;
}
