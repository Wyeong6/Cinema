package com.busanit.domain.movie;


import com.busanit.entity.movie.Movie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetailDTO {

    private Long id; // movie_detail_id 와 아무관계없음. api에서 주는 ID인거임

    private String runtime;
    private String release_date;



    // ---- JSON타입의 배열속의 배열속 에있는 데이터들을 추출하기위한 추가적인 DTO;
    // -- Detail에서만 사용되기때문에 DetailDTO내부에 작성하였음.
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReleaseDatesDTO {
        private List<ReleaseDatesResult> results;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReleaseDatesResult {
        private String iso_3166_1;
        private List<ReleaseDateInfo> release_dates;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReleaseDateInfo {
        private String certification;
    }

}
