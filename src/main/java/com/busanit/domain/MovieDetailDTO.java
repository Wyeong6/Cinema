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

    private Long id; // movie_detail_id 와 아무관계없음. api에서 주는 ID인거임

    private String runtime;
    private String release_date;
    private String certification;

}
