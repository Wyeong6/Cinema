package com.busanit.domain;


import com.busanit.entity.Notice;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDTO {

    private Long noticeId;
    @NotNull
    private String noticeTitle;
    @NotNull
    private String noticeContent;
    private String memberEmail;
    private int viewCount;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    public static NoticeDTO toDTO(Notice notice) {
        return NoticeDTO.builder()
                .noticeId(notice.getNoticeId())
                .noticeTitle(notice.getNoticeTitle())
                .noticeContent(notice.getNoticeContent())
                .memberEmail(notice.getMemberEmail())
                .viewCount(notice.getViewCount())
                .regDate(notice.getRegDate())
                .updateDate(notice.getUpdateDate())
                .build();
    }
}
