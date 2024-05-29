package com.busanit.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SeatsDTO {
    private List<Long> seatsPerTheater; // 상영관 별 좌석 수
    private List<Long> theaterNumber; // 지점내 상영관 고유 번호 배열

    // Lombok의 @Builder와 함께 사용할 기본 생성자 추가
    public SeatsDTO(List<Long> seatsPerTheater, List<Long> theaterNumber) {
        this.seatsPerTheater = seatsPerTheater;
        this.theaterNumber = theaterNumber;
    }
}