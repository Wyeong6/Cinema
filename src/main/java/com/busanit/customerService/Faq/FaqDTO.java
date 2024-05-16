package com.busanit.customerService.Faq;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
public class FaqDTO {
    private Long id;
    private String title;
    private String content;
}