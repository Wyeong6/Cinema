package com.busanit.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class TheaterDTO {
    private Long id; // 지점별 고유번호
    private String theaterName; // 상영관 지점명
    private String region; // 지역
    private Long theaterCount; // 상영관 갯수
    private List<Long> seatsPerTheater; // 상영관 별 좌석 수
    private List<Long> theaterNumber; // 지점내 상영관 고유 번호 배열
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
}
