package com.busanit.domain;

import com.busanit.entity.Seats;
import com.busanit.entity.Theater;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class TheaterDTO {
    private Long id; // 지점별 고유번호
    private String theaterName; // 상영관 지점명
    private String theaterNameEng; // 상영관 지점명
    private String region; // 지역
    private Long theaterCount; // 상영관 갯수
    private List<String> theaterIdx;
    private List<Long> seatsPerTheater; // 상영관 별 좌석 수
    private List<Long> theaterNumber; // 지점내 상영관 고유 번호 배열
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    // 엔티티를 DTO로 변환하는 메서드
    public static TheaterDTO toDTO(Theater theater) {
        // 상영관의 각 좌석 번호 및 상영관 별 좌석 수를 추출
        List<Long> theaterNumbers = theater.getSeats().stream()
                .map(Seats::getTheaterNumber)
                .collect(Collectors.toList());
        List<Long> seatsPerTheater = theater.getSeats().stream()
                .map(Seats::getSeatsPerTheater)
                .collect(Collectors.toList());
        List<String> theaterIdx = theater.getSeats().stream()
                .map(Seats::getTheaterIdx)
                .toList();

        // DTO 객체 생성 및 반환
        return TheaterDTO.builder()
                .id(theater.getId())
                .theaterName(theater.getTheaterName())
                .theaterNameEng(theater.getTheaterNameEng())
                .region(theater.getRegion())
                .theaterCount(theater.getTheaterCount())
                .regDate(theater.getRegDate())
                .updateDate(theater.getUpdateDate())
                .theaterIdx(theaterIdx)
                .theaterNumber(theaterNumbers)
                .seatsPerTheater(seatsPerTheater)
                .build();
    }
}