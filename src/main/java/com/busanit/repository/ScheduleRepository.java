package com.busanit.repository;

import com.busanit.entity.Schedule;
import com.busanit.entity.Seat;
import com.busanit.entity.Theater;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Transactional
    @Query("SELECT s FROM Schedule s " +
            "WHERE (:theaterName IS NULL OR s.theaterNumber.theater.theaterName = :theaterName) " +
            "AND (:movieId IS NULL OR s.movie.movieId = :movieId) " +
            "AND (:date IS NULL OR s.date = :date) " +
            "ORDER BY s.startTime")
    List<Schedule> findSchedulesByConditions(@Param("theaterName") String theaterName,
                                             @Param("movieId") Long movieId,
                                             @Param("date") LocalDate date);

    @Transactional
    @Query("SELECT s FROM Schedule s " +
            "WHERE (:theaterName IS NULL OR s.theaterNumber.theater.theaterName = :theaterName) " +
            "AND (:movieId IS NULL OR s.movie.movieId = :movieId) " +
            "AND (:theaterNumber IS NULL OR s.theaterNumber.theaterNumber = :theaterNumber) " +
            "AND (:date IS NULL OR s.date = :date) " +
            "AND (:startTime IS NULL OR s.startTime = :startTime)")
    Schedule findScheduleByConditions(@Param("theaterName") String theaterName,
                                             @Param("movieId") Long movieId,
                                             @Param("theaterNumber") Long theaterNumber,
                                             @Param("date") LocalDate date,
                                             @Param("startTime") LocalTime startTime);
}