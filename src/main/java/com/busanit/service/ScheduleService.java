package com.busanit.service;

import com.busanit.domain.ScheduleDTO;
import com.busanit.domain.SeatDTO;
import com.busanit.domain.TheaterDTO;
import com.busanit.domain.TheaterNumberDTO;
import com.busanit.entity.Schedule;
import com.busanit.entity.Seat;
import com.busanit.entity.Theater;
import com.busanit.entity.TheaterNumber;
import com.busanit.entity.movie.Movie;
import com.busanit.repository.MovieRepository;
import com.busanit.repository.ScheduleRepository;
import com.busanit.repository.SeatRepository;
import com.busanit.repository.TheaterNumberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MovieRepository movieRepository;
    private final TheaterNumberRepository theaterNumberRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public void save(ScheduleDTO scheduleDTO) {
        // Movie 조회
        Long movieId = scheduleDTO.getMovieId();
        if (movieId == null) {
            throw new IllegalArgumentException("Movie ID는 필수입니다.");
        }
        Movie movie = getMovie(movieId);

        // TheaterNumber 조회
        Long theaterNumberId = scheduleDTO.getTheaterNumberId();
        if (theaterNumberId == null) {
            throw new IllegalArgumentException("Theater number ID는 필수입니다.");
        }

        TheaterNumber theaterNumber = getTheaterNumber(theaterNumberId);
        if (theaterNumber == null) {
            throw new IllegalArgumentException("주어진 theaterNumberId에 해당하는 TheaterNumber를 찾을 수 없습니다.");
        }

        Schedule schedule = Schedule.toEntity(scheduleDTO, theaterNumber, movie);

        // 스케줄 엔티티 저장
        scheduleRepository.save(schedule);
    }

    private Movie getMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));
    }

    private TheaterNumber getTheaterNumber(Long theaterNumberId) {
        return theaterNumberRepository.findById(theaterNumberId)
                .orElseThrow(() -> new EntityNotFoundException("TheaterNumber not found"));
    }

    public Page<ScheduleDTO> getScheduleAll(Pageable pageable) {
        Page<Schedule> schedules = scheduleRepository.findAll(pageable);

        // Page<Entity> -> Page<DTO> 변환
        return schedules.map(entity -> {
            return ScheduleDTO.builder()
                    .id(entity.getId())
                    .movieId(entity.getMovie().getMovieId())
                    .movieTitle(entity.getMovie().getTitle())
                    .theaterNumberId(entity.getTheaterNumber().getId())
                    .theaterNumber(entity.getTheaterNumber().getTheaterNumber())
                    .theaterName(entity.getTheaterNumber().getTheater().getTheaterName())
                    .date(entity.getDate())
                    .startTime(entity.getStartTime().toString())
                    .endTime(entity.getEndTime().toString())
                    .sessionType(entity.getSessionType())
                    .totalSeats(entity.getTotalSeats())
                    .unavailableSeats(entity.getUnavailableSeats())
                    .status(entity.getStatus())
                    .build();
        });
    }

    public ScheduleDTO getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new NullPointerException("theater null"));

        return ScheduleDTO.toDTO(schedule);
    }

    public void editSchedule(ScheduleDTO scheduleDTO) {
        // 기존의 스케줄 엔티티를 조회
        Schedule schedule = scheduleRepository.findById(scheduleDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 스케줄을 찾을 수 없습니다."));

        // Movie 조회 및 설정
        Long movieId = scheduleDTO.getMovieId();
        if (movieId == null) {
            throw new IllegalArgumentException("Movie ID는 필수입니다.");
        }
        Movie movie = getMovie(movieId);
        schedule.setMovie(movie);

        // TheaterNumber 조회 및 설정
        Long theaterNumberId = scheduleDTO.getTheaterNumberId();
        if (theaterNumberId == null) {
            throw new IllegalArgumentException("Theater number ID는 필수입니다.");
        }
        TheaterNumber theaterNumber = getTheaterNumber(theaterNumberId);
        schedule.setTheaterNumber(theaterNumber);

        schedule.setStartTimeAndCalculateStatus(scheduleDTO.getDate(), LocalTime.parse(scheduleDTO.getStartTime()), theaterNumber.getSeatsPerTheater());

        scheduleRepository.save(schedule);
    }

    public void deleteScheduleById(Long id) {
        scheduleRepository.deleteById(id);
    }

    // 지점별, 영화별, 일자별 상영일정 찾기
    public List<ScheduleDTO> findSchedulesByConditions(String theaterName, Long movieId, LocalDate date) {
        LocalTime currentTime = LocalTime.now();
        List<Schedule> schedules = scheduleRepository.findSchedulesByConditions(theaterName, movieId, date, currentTime);

        return schedules.stream()
                .map(entity -> {
                    return ScheduleDTO.builder()
                            .id(entity.getId())
                            .movieId(entity.getMovie().getMovieId())
                            .movieTitle(entity.getMovie().getTitle())
                            .theaterNumberId(entity.getTheaterNumber().getId())
                            .theaterNumber(entity.getTheaterNumber().getTheaterNumber())
                            .theaterName(entity.getTheaterNumber().getTheater().getTheaterName())
                            .date(entity.getDate())
                            .startTime(entity.getStartTime().toString())
                            .endTime(entity.getEndTime().toString())
                            .sessionType(entity.getSessionType())
                            .totalSeats(entity.getTotalSeats())
                            .unavailableSeats(entity.getUnavailableSeats())
                            .status(entity.getStatus())
                            .build();
                })
                .collect(Collectors.toList());
    }
}