package com.busanit.domain;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDTO {

    private String name;
    private String email;
    private String subject;
    private String message;
}
