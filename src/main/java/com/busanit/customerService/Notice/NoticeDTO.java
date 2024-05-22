package com.busanit.customerService.Notice;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder
public class NoticeDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDate regDate;
    private LocalDate updateDate;
    private int viewCount;
    private boolean pinned;

}
