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

    private String memberEmail;

    private String event_image;

    private String event_alt;

    private String event_detail;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime regDate;
    @UpdateTimestamp
    private LocalDateTime updateDate;

//    @ManyToMany(mappedBy = "events", fetch = FetchType.LAZY)
//    private List<Member> members = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "member_event",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Member> members = new ArrayList<>();


    public void addMember(Member member) {
        this.members.add(member);
        member.getEvents().add(this);
    }
    public List<Member> getMembers() {
        if (this.members == null) {
            this.members = new ArrayList<>();
        }
        return this.members;
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
}
