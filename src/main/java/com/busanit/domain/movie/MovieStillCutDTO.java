package com.busanit.domain.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieStillCutDTO {

    private Long id;
    private Long movieId;
    private List<ImageDTO> backdrops;
    private List<ImageDTO> posters;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageDTO {
        private String file_path;
    }
}
