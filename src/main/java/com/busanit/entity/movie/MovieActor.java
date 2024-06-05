package com.busanit.entity.movie;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieActor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actorId;

    // 배우 이름
    private String actorName;

    // 배우 성별
    private String actorGender;

    // 배우 사진
    private String actorProfilePic;


    @ManyToMany
    @JoinTable(name = "actors",
            joinColumns = @JoinColumn(name = "movie_actor_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id"))
    private List<Movie> movies = new ArrayList<>();

    public MovieActor(String name, String gender, String profilePath) {
        this.actorName = name;
        this.actorGender = gender;
        this.actorProfilePic = profilePath;
    }
}
