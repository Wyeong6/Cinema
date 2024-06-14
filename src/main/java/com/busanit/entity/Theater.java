package com.busanit.entity;

import com.busanit.domain.TheaterDTO;
import com.busanit.domain.TheaterNumberDTO;
import com.busanit.entity.TheaterNumber;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_item_id")
    private Long id;
    private String theaterName; // 상영관 지점명
    private String theaterNameEng; // 상영관 지점명
    private String region; // 지역
    private Long theaterCount; // 상영관 갯수
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TheaterNumber> theaterNumbers = new ArrayList<>();

    // 기본 생성자 추가
    public Theater() {
    }

    public static Theater toEntity(TheaterDTO theaterDTO) {
        Theater theater = new Theater();
        theater.setTheaterName(theaterDTO.getTheaterName());
        theater.setTheaterNameEng(theaterDTO.getTheaterNameEng());
        theater.setRegion(theaterDTO.getRegion());
        theater.setTheaterCount(theaterDTO.getTheaterCount());
        theater.setRegDate(LocalDateTime.now());
        theater.setUpdateDate(theaterDTO.getUpdateDate());

        if (theaterDTO.getTheaterNumbers() != null) {
            for (TheaterNumberDTO theaterNumberDTO : theaterDTO.getTheaterNumbers()) {
                TheaterNumber theaterNumber = new TheaterNumber();
                theaterNumber.setTheater(theater);

                // "지역-지점명-상영관번호" 형태로 설정
                String theaterIdx = theaterDTO.getRegion() + theaterDTO.getTheaterNameEng() + theaterNumberDTO.getTheaterNumber();
                theaterNumber.setTheaterIdx(theaterIdx);

                theaterNumber.setTheaterNumber(theaterNumberDTO.getTheaterNumber());
                theaterNumber.setSeatsPerTheater(theaterNumberDTO.getSeatsPerTheater());
                theater.getTheaterNumbers().add(theaterNumber);
            }
        }

        return theater;
    }
}
