package com.busanit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class TheaterNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String theaterIdx; // 지점내 상영관 고유 번호

    private Long theaterNumber; // 지점내 상영관 고유 번호

    private Long seatsPerTheater; // 상영관 별 좌석 수

    @OneToMany(mappedBy = "theaterNumber", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    private Theater theater;

    public static TheaterNumber toEntity(Theater theater) {
        TheaterNumber theaterNumber = new TheaterNumber();
        theaterNumber.setTheater(theater);

        return theaterNumber;
    }


}

