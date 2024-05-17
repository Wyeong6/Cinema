package com.busanit.entity.movie;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
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
    private List<Genre> genres = new ArrayList<>();

    //영화 상세보기 관계
    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_detail_id")
    private MovieDetail movieDetail;


    //이미지 관계
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<MovieImage> images;

    //영화 스틸컷 관계
    @ManyToMany
    @JoinTable(name = "stillCuts",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_still_cut_id"))
    private List<MovieStillCut> stillCuts = new ArrayList<>();


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
        if (movieDetail != null) {
            movieDetail.setMovie(this);
        }
    }
    //이미지 추가
    public void addImage(MovieImage image) {
        this.images.add(image);
        image.setMovie(this);
    }

    //--------------- 생성자
    public Movie() {
        this.images = new ArrayList<>();
    }
    // genreIds 설정 메소드
    public void setGenreIds(List<Genre> genreIds) {
        this.genres = genreIds;
    }

    public boolean hasImage(String posterPath, String backdropPath) {
        for (MovieImage image : this.images) { // this.images는 Movie 객체에 속한 MovieImage 객체들의 리스트를 가정합니다.
            if (image.getPosterPath().equals(posterPath) && image.getBackdropPath().equals(backdropPath)) {
                return true;
            }
        }
        return false;
    }
}
