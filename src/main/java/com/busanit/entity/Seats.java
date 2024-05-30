package com.busanit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Seats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "theater_idx")
    private String theaterIdx; // 지점내 상영관 고유 번호

    @Column(name = "theater_number")
    private Long theaterNumber; // 지점내 상영관 고유 번호

    @Column(name = "seats_per_theater")
    private Long seatsPerTheater; // 상영관 별 좌석 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id") // 변경된 점: 외래 키 이름을 `theater_id`로 변경
    private Theater theater;
}
