package com.busanit.service;

import com.busanit.domain.ScheduleDTO;
import com.busanit.entity.Schedule;
import com.busanit.entity.TheaterNumber;
import com.busanit.entity.movie.Movie;
import com.busanit.repository.MovieRepository;
import com.busanit.repository.ScheduleRepository;
import com.busanit.repository.TheaterNumberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MovieRepository movieRepository;
    private final TheaterNumberRepository theaterNumberRepository;

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

        // DTO를 엔티티로 변환
        Schedule schedule = Schedule.toEntity(scheduleDTO);
        schedule.setMovie(movie);  // Movie 설정
        schedule.setTheaterNumberAndCalculateSeats(theaterNumber);  // TheaterNumber 설정 및 totalSeats 계산

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
}