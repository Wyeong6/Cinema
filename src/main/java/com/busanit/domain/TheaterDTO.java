package com.busanit.domain;

import com.busanit.entity.Theater;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheaterDTO {
    private Long id; // 지점별 고유번호
    private String theaterName; // 상영관 지점명
    private String theaterNameEng; // 상영관 지점명
    private String region; // 지역
    private Long theaterCount; // 상영관 갯수
    private List<TheaterNumberDTO> theaterNumbers; // 상영관 번호와 좌석 정보
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    public static TheaterDTO toDTO(Theater theater) {
        List<TheaterNumberDTO> theaterNumbers = theater.getTheaterNumbers().stream()
                .map(TheaterNumberDTO::toDTO)
                .collect(Collectors.toList());

        return TheaterDTO.builder()
                .id(theater.getId())
                .theaterName(theater.getTheaterName())
                .theaterNameEng(theater.getTheaterNameEng())
                .region(theater.getRegion())
                .theaterCount(theater.getTheaterCount())
                .regDate(theater.getRegDate())
                .updateDate(theater.getUpdateDate())
                .theaterNumbers(theaterNumbers)
                .build();
    }
}
