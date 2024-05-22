package com.busanit.entity;

import com.busanit.domain.EventDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String event_name;

    private String event_image;

    private String event_alt;

    private String event_Detail;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime regDate;
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @ManyToMany(mappedBy = "events")
    private List<Member> members = new ArrayList<>();

    public static Event toEntity(EventDTO eventDTO) {
        return Event.builder()
               .event_name(eventDTO.getEvent_name())
               .event_image(eventDTO.getEvent_image())
               .event_alt(eventDTO.getEvent_alt())
               .event_Detail(eventDTO.getEvent_detail())
               .build();
    }
}
