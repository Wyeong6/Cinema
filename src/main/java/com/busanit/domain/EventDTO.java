package com.busanit.domain;

import com.busanit.entity.Event;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventDTO {

    private Long id;
    private String event_name;
    private String memberEmail;
    private String event_image;
    private String event_alt;
    private String event_detail;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    public static EventDTO toDTO(Event event){
        return EventDTO.builder()
                .id(event.getId())
                .event_name(event.getEvent_name())
                .event_image(event.getEvent_image())
                .event_alt(event.getEvent_alt())
                .event_detail(event.getEvent_Detail())
                .regDate(event.getRegDate())
                .updateDate(event.getUpdateDate())
                .build();
    }

    public String toString() {
        return "EventDTO{" +
                "id=" + id +
                ", event_name='" + event_name + '\'' +
                ", memberEmail='" + memberEmail + '\'' +
                ", event_image='" + event_image + '\'' +
                ", event_alt='" + event_alt + '\'' +
                ", event_Detail='" + event_detail + '\'' +
                ", regDate=" + regDate +
                ", updateDate=" + updateDate +
                '}';
    }
}
