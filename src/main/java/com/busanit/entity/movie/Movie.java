package com.busanit.entity.movie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Movie {

    @Id
    private Long movieId;

    //영화제목
    private String title;

    //영화줄거리
    @Column(length = 1024)
    private String overview;

    //장르 관계
    @ManyToMany
    @JoinTable(name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<Genre> genres;


    //영화 상세보기 관계
    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_detail_id")
    private MovieDetail movieDetail;


    //이미지 관계
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<MovieImage> images;

    //영화 스틸컷 관계
    @ManyToMany
    @JoinTable(name = "stillCuts",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_still_cut_id"))
    private List<MovieStillCut> stillCuts = new ArrayList<>();

    //----------------얀관메서드

    public void addStillCut(MovieStillCut stillCut) {
        this.stillCuts.add(stillCut);
        stillCut.getMovies().add(this);
    }

    //장르추가
    public void addGenre(Genre genre) {
        this.genres.add(genre);
        genre.getMovies().add(this);
    }
    // 영화 상세 설정
    public void setMovieDetail(MovieDetail movieDetail) {
        this.movieDetail = movieDetail;
        movieDetail.setMovie(this);
    }
    //이미지 추가
    public void addImage(MovieImage image) {
        this.images.add(image);
        image.setMovie(this);
    }


//    private String voteAverage; // 영화 점수
    // 찾아야함
    //
















    //  (image 테이블로 분리예정)
    //  private String stillCut; // 영화 스틸컷
    //  private String posterPath; // 영화 포스터
    //  private String backdropPath; // 영화 배경사진

    // (장르 테이블로 분리예정)
    // private String genres; // 영화 장르


}
