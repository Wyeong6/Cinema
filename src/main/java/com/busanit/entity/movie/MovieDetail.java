package com.busanit.entity.movie;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
    //평점
//    private double voteAverage;
    //비디오
    private String video;
    //인기
    private String popularity;

    //영화 엔티티와 일대일 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;


}


