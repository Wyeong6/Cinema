package com.busanit.entity.movie;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class MovieStillCut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieStillCutId;

    @Lob
    @Column(length = 1024)
    private String stillCuts;

    @ManyToMany
    @JoinTable(name = "stillCuts",
            joinColumns = @JoinColumn(name = "movie_still_cut_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id"))
    private List<Movie> movies = new ArrayList<>();
}
