package com.busanit.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {



    private Long id;
    private String title;
    private String poster;
    private String overview;
    private String runtime;
    private String releaseDate;
    private String genres;
    private String score;
    private String certifications;
    private String stillCut;
    private String video;
}
