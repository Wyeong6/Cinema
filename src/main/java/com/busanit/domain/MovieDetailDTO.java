package com.busanit.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetailDTO {

    private Long id;
    private String runtime;
    private String release_date;
    private String certificationName;
    private Long movieId;

}
