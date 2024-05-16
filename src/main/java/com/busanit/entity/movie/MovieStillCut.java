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

    @ManyToMany(mappedBy = "stillCuts")
    private List<Movie> movies = new ArrayList<>();;
}
