package com.busanit.entity;

import com.busanit.domain.ScheduleDTO;
import com.busanit.entity.movie.Movie;
import com.busanit.repository.MovieRepository;
import com.busanit.repository.TheaterNumberRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.*;

@Getter
@Setter
@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theaterNumber_id")
    private TheaterNumber theaterNumber;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String sessionType;

    private Long totalSeats = 0L;
    private Long unavailableSeats = 0L;
    private Boolean status = true;

    public static Schedule toEntity(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        schedule.setDate(scheduleDTO.getDate());
        schedule.setStartTime(LocalTime.parse(scheduleDTO.getStartTime()));
        schedule.setEndTime(LocalTime.parse(scheduleDTO.getEndTime()));
        schedule.setUnavailableSeats(0L);
        schedule.updateStatus();
        schedule.setSessionType(determineSessionType(scheduleDTO.getDate(), LocalTime.parse(scheduleDTO.getStartTime())));
        return schedule;
    }

    public void setStartTimeAndCalculateStatus(LocalDate date, LocalTime startTime, Long totalSeats) {
        if (date == null || startTime == null || totalSeats == null) {
            throw new IllegalArgumentException("Date, startTime, and totalSeats must not be null.");
        }

        this.date = date;
        this.startTime = startTime;
        this.totalSeats = totalSeats;

        updateStatus();
    }

    private void updateStatus() {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        boolean isBeforeCurrentDate = date.isBefore(currentDate);
        boolean isEqualCurrentDateAndBeforeCurrentTime = date.equals(currentDate) && startTime.isBefore(currentTime);
        boolean isAvailableSeatsZero = calculateAvailableSeats() == 0;

        if(isBeforeCurrentDate || isEqualCurrentDateAndBeforeCurrentTime || isAvailableSeatsZero) {
            this.status = false;
        } else {
            this.status = true;
        }
    }

    private Long calculateAvailableSeats() {
        return this.totalSeats - this.unavailableSeats;
    }

    private static String determineSessionType(LocalDate date, LocalTime startTime) {
        if (isWeekend(date)) {
            return "주말";
        } else if (isEarlyMorning(startTime)) {
            return "조조";
        } else if (isNightTime(startTime)) {
            return "심야";
        } else {
            return "평일";
        }
    }

    // 조조인지 확인
    private static boolean isEarlyMorning(LocalTime time) {
        return time.getHour() < 12;
    }

    // 심야인지 확인
    private static boolean isNightTime(LocalTime time) {
        return time.getHour() >= 22;
    }

    // 주말인지 확인 (토요일 또는 일요일)
    private static boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}