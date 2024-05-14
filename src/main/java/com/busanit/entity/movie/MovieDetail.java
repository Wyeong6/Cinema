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
    private int runtime;
    //개봉일
    private LocalDate releaseDate;
    //평점
    private double score;
    //비디오
    private String video;
    //스틸컷
    private String stillCut;
    //심의등급
    private String certificationName;
    //영화 엔티티와 일대일 관계
    @OneToOne(mappedBy = "movieDetail",fetch = FetchType.LAZY)
    private Movie movie;
}
