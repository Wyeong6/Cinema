package com.busanit.domain.chat;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class MessageReadStatusDTO {

    private Long id;
    private Long memberId;
    private Long messageId;
    private LocalDateTime readTimestamp;
}
