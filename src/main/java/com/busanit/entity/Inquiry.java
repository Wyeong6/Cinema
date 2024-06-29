package com.busanit.entity;

import com.busanit.domain.InquiryDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = {AuditingEntityListener.class})
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String subject;
    @Column(length = 1000)
    private String message;
    private String type;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    @CreatedDate
    private LocalDateTime createdAt;
    @OneToOne(mappedBy = "inquiry", cascade = CascadeType.ALL)
    private InquiryReply reply;

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

    // Inquiry 타입 변경 메서드
    public void markAsAnswered() {
        this.type = "답변완료";
    }

    public static Inquiry toEntity(InquiryDTO dto) {
        return Inquiry.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .subject(dto.getSubject())
                .message(dto.getMessage())
                .type("미답변")
                .build();
    }
}
