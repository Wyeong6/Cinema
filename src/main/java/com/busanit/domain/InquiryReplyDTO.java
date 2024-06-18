package com.busanit.domain;

import com.busanit.entity.InquiryReply;
import lombok.*;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryReplyDTO {

    private Long id;
    private String replyMessage;

    public static InquiryReplyDTO toDTO(InquiryReply inquiryReply) {
        return InquiryReplyDTO.builder()
                .id(inquiryReply.getId())
                .replyMessage(inquiryReply.getReplyMessage())
                .build();
    }
}
