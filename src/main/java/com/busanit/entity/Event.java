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
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String event_name;

    private String memberEmail;

    private String event_image;

    private String event_alt;

    private String event_detail;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime regDate;
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @ManyToMany
    @JoinTable(
            name = "member_event",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Member> members = new ArrayList<>();


    public void addMember(Member member) {
        if (this.members == null) {
            this.members = new ArrayList<>();
        }
        this.members.add(member);
    }

    public static Event toEntity(EventDTO eventDTO) {
        return Event.builder()
                .event_name(eventDTO.getEvent_name())
                .memberEmail(eventDTO.getMemberEmail())
                .event_image(eventDTO.getEvent_image())
                .event_alt(eventDTO.getEvent_alt())
                .event_detail(eventDTO.getEvent_detail())
                .build();
    }

    public void update(EventDTO eventDTO) {
        this.event_name = eventDTO.getEvent_name();
        this.memberEmail = eventDTO.getMemberEmail();
        this.event_image = eventDTO.getEvent_image();
        this.event_alt = eventDTO.getEvent_alt();
        this.event_detail = eventDTO.getEvent_detail();
    }
}
