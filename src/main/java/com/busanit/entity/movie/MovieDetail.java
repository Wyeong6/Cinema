package com.busanit.entity.movie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class MovieDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieDetailId;
    //상영시간
    private String runtime;
    //개봉일
    private String releaseDate;
    //심의등급
    private String certification;
    //영화 엔티티와 일대일 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;
}
