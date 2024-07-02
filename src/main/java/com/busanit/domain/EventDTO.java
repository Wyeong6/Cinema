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
    private String eventName;
    private String memberEmail;
    @NotNull
    private String eventImage;
    @NotNull
    private String eventAlt;
    @NotNull
    private String eventDetail;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    public static EventDTO toDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .eventName(event.getEventName())
                .memberEmail(event.getMemberEmail())
                .eventImage(event.getEventImage())
                .eventAlt(event.getEventAlt())
                .eventDetail(event.getEventDetail())
                .regDate(event.getRegDate())
                .updateDate(event.getUpdateDate())
                .build();
    }
}
