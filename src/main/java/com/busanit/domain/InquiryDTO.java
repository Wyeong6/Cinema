package com.busanit.domain;

import com.busanit.entity.Inquiry;
import com.busanit.entity.Member;
import com.busanit.entity.Notice;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDTO {

    private Long id;
    private String name;
    private String email;
    private String recipientEmail;
    private String subject;
    private String message;
    private String type;

    public static InquiryDTO toDTO(Inquiry inquiry) {

        String recipientEmail = Optional.ofNullable(inquiry.getMember())
                .map(Member::getEmail)
                .orElse("비회원");

        return InquiryDTO.builder()
                .id(inquiry.getId())
                .name(inquiry.getName())
                .recipientEmail(inquiry.getEmail())
                .email(recipientEmail)
                .subject(inquiry.getSubject())
                .message(inquiry.getMessage())
                .type(inquiry.getType())
                .build();
    }
}

