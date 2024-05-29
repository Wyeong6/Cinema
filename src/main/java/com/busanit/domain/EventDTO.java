package com.busanit.domain;

import com.busanit.entity.Event;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

    private Long id;
    @NotNull
    private String event_name;
    private String memberEmail;
    @NotNull
    private String event_image;
    @NotNull
    private String event_alt;
    @NotNull
    private String event_detail;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    public static EventDTO toDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .event_name(event.getEvent_name())
                .memberEmail(event.getMemberEmail())
                .event_image(event.getEvent_image())
                .event_alt(event.getEvent_alt())
                .event_detail(event.getEvent_detail()) // 소문자로 수정
                .regDate(event.getRegDate())
                .updateDate(event.getUpdateDate())
                .build();
    }

}