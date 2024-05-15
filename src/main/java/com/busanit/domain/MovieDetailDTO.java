package com.busanit.domain;

import com.busanit.entity.movie.Movie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetailDTO {

    private Long id;
    private String title;
    private String overview;

}
