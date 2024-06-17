package com.busanit.domain;

import com.busanit.entity.Inquiry;
import com.busanit.entity.Notice;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDTO {

    private Long id;
    private String name;
    private String email;
    private String subject;
    private String message;
    private String type;

    public static InquiryDTO toDTO(Inquiry inquiry) {
        return InquiryDTO.builder()
                .id(inquiry.getId())
                .name(inquiry.getName())
                .email(inquiry.getEmail())
                .subject(inquiry.getSubject())
                .message(inquiry.getMessage())
                .type(inquiry.getType())
                .build();
    }
}

