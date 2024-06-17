package com.busanit.entity;

import com.busanit.domain.InquiryDTO;
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
    private String type;
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

    public static Inquiry toEntity(InquiryDTO dto) {
        return Inquiry.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .subject(dto.getSubject())
                .message(dto.getMessage())
                .type("unanswered")
                .build();

    }

}
