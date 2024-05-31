package com.busanit.domain;

import java.time.LocalDateTime;

public class PointDTO {
    private Long id;
    private Long member_id;
    private String content;
    private String pointType;
    private Integer points;
    private Integer currentPoints;
    private Integer totalPoints;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
}
