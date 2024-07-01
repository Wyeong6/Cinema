package com.busanit.domain;

import com.busanit.domain.movie.MovieDTO;
import com.busanit.entity.Schedule;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private String region;
    private Long theaterNumberId;
    private Long theaterNumber;
    private String theaterName;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private String runningTime;
    private String sessionType;
    private Long totalSeats;
    private Long availableSeats;
    private List<SeatDTO> seats;
    private Boolean status;

    public static ScheduleDTO toDTO(Schedule schedule) {
        return ScheduleDTO.builder()
                .id(schedule.getId())
                .movieId(schedule.getMovie().getMovieId())
                .movieTitle(schedule.getMovie().getTitle())
                .region(schedule.getTheaterNumber().getTheater().getRegion())
                .theaterNumberId(schedule.getTheaterNumber().getId())
                .theaterNumber(schedule.getTheaterNumber().getTheaterNumber())
                .theaterName(schedule.getTheaterNumber().getTheater().getTheaterName())
                .date(schedule.getDate())
                .startTime(schedule.getStartTime().toString())
                .endTime(schedule.getEndTime().toString())
                .runningTime(schedule.getMovie().getMovieDetail().getRuntime())
                .sessionType(schedule.getSessionType())
                .totalSeats(schedule.getTotalSeats())
                .totalSeats(schedule.getAvailableSeats())
                .status(schedule.getStatus())
                .build();
    }
}