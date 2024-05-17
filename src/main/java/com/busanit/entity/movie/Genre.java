package com.busanit.entity.movie;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long genreId;
    //장르 이름
    private String genreName;
    //해당 장르에 속한 영화들의 목록
    @ManyToMany(mappedBy = "genres")
    private List<Movie> movies = new ArrayList<>();

}
