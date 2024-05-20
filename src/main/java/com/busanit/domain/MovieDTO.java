package com.busanit.domain;

import com.busanit.entity.movie.Genre;
import com.busanit.entity.movie.Movie;
import com.busanit.entity.movie.MovieStillCut;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {

    private Long id;
    private String title;
    private String poster;
    private String overview;
    private String runtime;
    @JsonProperty("release_date")
    private String releaseDate;
    private String certifications;
    private String video;
    @JsonProperty("poster_path")
    private String posterPath;
    private String backdropPath;
    private String popularity;
    @JsonProperty("vote_average")
    private double voteAverage;
    @JsonProperty("genre_ids")
    private List<Integer> genreIds;
    private List<String> Genres;
    private List<String> stillCutPaths;

    public static MovieDTO convertToDTO(Movie movie){
        MovieDTO movieDTO = new MovieDTO();

        movieDTO.setId(movie.getMovieId());
        movieDTO.setTitle(movie.getTitle());
        movieDTO.setOverview(movie.getOverview());

        movie.getImages().stream().findFirst().ifPresent(image -> {
            movieDTO.setBackdropPath(image.getBackdropPath());
            movieDTO.setPosterPath(image.getPosterPath());
        });

        Optional.ofNullable(movie.getMovieDetail()).ifPresentOrElse(detail -> {
            movieDTO.setPopularity(detail.getPopularity());
            movieDTO.setVoteAverage(detail.getVoteAverage());
            movieDTO.setVideo(detail.getVideo());
            movieDTO.setReleaseDate(detail.getReleaseDate());
            movieDTO.setRuntime(detail.getRuntime());
            movieDTO.setCertifications(detail.getCertification());
        }, () -> {
            movieDTO.setVoteAverage(0);
            movieDTO.setVideo(null);
        });

        Optional.ofNullable(movie.getStillCuts()).ifPresent(stillCuts -> {
            List<String> stillCutPaths = stillCuts.stream()
                    .map(MovieStillCut::getStillCuts)
                    .collect(Collectors.toList());
            movieDTO.setStillCutPaths(stillCutPaths);
        });

        Optional.ofNullable(movie.getGenres()).ifPresent(genres -> {
            List<String> genreNames = genres.stream()
                    .map(Genre::getGenreName)
                    .collect(Collectors.toList());
            movieDTO.setGenres(genreNames);
        });

        return movieDTO;
    }
}
