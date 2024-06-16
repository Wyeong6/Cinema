package com.busanit.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String subject;
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    // 연관관계 메서드
    public void setMember(Member member) {
        this.member = member;
        member.getInquiries().add(this);
    }

    public void removeMember(Member member) {
        if (this.member != null && this.member.equals(member)) {
            this.member = null;
            member.getInquiries().remove(this);
        }
    }
}
