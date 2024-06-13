package com.busanit.entity;

import com.busanit.domain.ScheduleDTO;
import com.busanit.entity.movie.Movie;
import com.busanit.repository.MovieRepository;
import com.busanit.repository.TheaterNumberRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theaterNumber_id")
    private TheaterNumber theaterNumber;

    private LocalDate date;

    private LocalTime startTime;
    private LocalTime endTime;

    private Long totalSeats = 0L;
    private Long unavailableSeats = 0L;
    private Boolean status = true;
    private double ticketPrice;

    public static Schedule toEntity(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        schedule.setDate(scheduleDTO.getDate());
        schedule.setStartTime(LocalTime.parse(scheduleDTO.getStartTime()));
        schedule.setEndTime(LocalTime.parse(scheduleDTO.getEndTime()));
        schedule.setUnavailableSeats(0L);
        schedule.updateStatus();
        schedule.determineTicketPrice();

        return schedule;
    }

    public void setTheaterNumberAndCalculateSeats(TheaterNumber theaterNumber) {
        if (theaterNumber == null) {
            throw new IllegalArgumentException("TheaterNumber must not be null.");
        }

        this.theaterNumber = theaterNumber;
        this.totalSeats = theaterNumber.getSeatsPerTheater();

        if (this.totalSeats == null) {
            throw new IllegalArgumentException("Seats per theater must be specified for the given theater.");
        }

        updateStatus();  // totalSeats가 설정되어 있으므로 안전하게 호출할 수 있음
    }

    private void updateStatus() {
        this.status = calculateAvailableSeats() > 0;
    }

    private Long calculateAvailableSeats() {
        return this.totalSeats - this.unavailableSeats;
    }

    public void determineTicketPrice() {
        if (isEarlyMorning(this.startTime)) {
            this.ticketPrice = 9000.0; // 조조 할인 가격 설정
        } else if (isNightTime(this.startTime)) {
            this.ticketPrice = 13000.0; // 심야 할인 가격 설정
        } else if (isWeekend(this.date)) {
            this.ticketPrice = 15000.0; // 주말 가격 설정
        } else {
            this.ticketPrice = 12000.0; // 기본 가격 설정
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