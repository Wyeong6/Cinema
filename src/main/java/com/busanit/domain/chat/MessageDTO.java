package com.busanit.domain.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageDTO {

    private Long id;

    private String content;


}
