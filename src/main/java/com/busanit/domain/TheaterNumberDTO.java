package com.busanit.domain;

import com.busanit.entity.TheaterNumber;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheaterNumberDTO {
    private String theaterIdx;
    private Long theaterNumber; // 지점내 상영관 고유 번호
    private Long seatsPerTheater; // 상영관 별 좌석 수
    private List<SeatDTO> seats; // 상영관에 속한 좌석 정보

    public static TheaterNumberDTO toDTO(TheaterNumber theaterNumber) {
        return TheaterNumberDTO.builder()
                .theaterIdx(theaterNumber.getTheaterIdx())
                .theaterNumber(theaterNumber.getTheaterNumber())
                .seatsPerTheater(theaterNumber.getSeatsPerTheater())
                .seats(theaterNumber.getSeats().stream()
                        .map(SeatDTO::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
