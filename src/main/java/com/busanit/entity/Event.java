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

    private String eventName;

    private String memberEmail;

    private String eventImage;

    private String eventAlt;

    private int viewCount;

    private String eventDetail;
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
                .eventName(eventDTO.getEventName())
                .memberEmail(eventDTO.getMemberEmail())
                .eventImage(eventDTO.getEventImage())
                .eventAlt(eventDTO.getEventAlt())
                .eventDetail(eventDTO.getEventDetail())
                .build();
    }

    public void update(EventDTO eventDTO) {
        this.eventName = eventDTO.getEventName();
        this.memberEmail = eventDTO.getMemberEmail();
        this.eventImage = eventDTO.getEventImage();
        this.eventAlt = eventDTO.getEventAlt();
        this.eventDetail = eventDTO.getEventDetail();
        this.viewCount = eventDTO.getViewCount();
    }
}
