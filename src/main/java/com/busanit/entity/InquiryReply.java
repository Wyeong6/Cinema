package com.busanit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String replyMessage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", unique = true)
    private Inquiry inquiry;

    public static InquiryReply toEntity(String replyMessage) {
        return InquiryReply.builder()
                .replyMessage(replyMessage)
                .build();
    }
}