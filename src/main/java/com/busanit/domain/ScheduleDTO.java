package com.busanit.domain;

import com.busanit.domain.movie.MovieDTO;
import com.busanit.entity.Schedule;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {
    private Long id;
    private Long movieId;
    private Long theaterNumberId;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private Boolean status;

    public static ScheduleDTO toDTO(Schedule schedule) {
        return ScheduleDTO.builder()
                .id(schedule.getId())
                .movieId(schedule.getMovie().getMovieId())
                .theaterNumberId(schedule.getTheaterNumber().getId())
                .date(schedule.getDate())
                .startTime(schedule.getStartTime().toString())
                .endTime(schedule.getEndTime().toString())
                .status(schedule.getStatus())
                .build();
    }
}

