package com.busanit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Movie {

    @Id
    private Long movieId; // 영화 id (PK)

    private String title; // 영화 제목

    @Lob
    @Column(length = 1024)
    private String overview; // 영화 줄거리

    private String runtime; // 영화 런타임

    private String releaseDate; // 영화 개봉일

    private String score; // 영화 점수

    private String certifications; // 영화 심의등급

    private String video; // 영화 트레일러 (유튜브 key , 링크아님)

    private String voteAverage; // 영화 점수
    // 찾아야함
    //
















    //  (image 테이블로 분리예정)
    //  private String stillCut; // 영화 스틸컷
    //  private String posterPath; // 영화 포스터
    //  private String backdropPath; // 영화 배경사진

    // (장르 테이블로 분리예정)
    // private String genres; // 영화 장르


}
