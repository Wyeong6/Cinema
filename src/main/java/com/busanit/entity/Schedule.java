package com.busanit.entity;

import com.busanit.domain.ScheduleDTO;
import com.busanit.entity.movie.Movie;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

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
    private Long availableSeats = 0L;
    private Boolean status = true;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatReservation> seatReservations = new ArrayList<>();

    public static Schedule toEntity(ScheduleDTO scheduleDTO, TheaterNumber theaterNumber, Movie movie) {
        Schedule schedule = new Schedule();
        schedule.setDate(scheduleDTO.getDate());
        schedule.setStartTime(LocalTime.parse(scheduleDTO.getStartTime()));
        schedule.setEndTime(LocalTime.parse(scheduleDTO.getEndTime()));
        schedule.setTotalSeats(theaterNumber.getSeatsPerTheater());
        schedule.setAvailableSeats(schedule.getTotalSeats());
        schedule.setSessionType(determineSessionType(scheduleDTO.getDate(), LocalTime.parse(scheduleDTO.getStartTime())));
        schedule.setTheaterNumber(theaterNumber);
        schedule.setMovie(movie);
        schedule.setTotalSeats(theaterNumber.getSeatsPerTheater());

        // 상태 업데이트
        schedule.updateStatus();

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

    void updateStatus() {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        boolean isBeforeCurrentDate = date.isBefore(currentDate);
        boolean isEqualCurrentDateAndBeforeCurrentTime = date.equals(currentDate) && startTime.isBefore(currentTime);

        // 시간 단위로 실행되는 작업이므로, 시작 시간이 현재 시간보다 빠를 때 상태를 false로 설정
        if (isEqualCurrentDateAndBeforeCurrentTime) {
            this.status = false;
        } else {
            // 그 외의 경우에는 가능한 좌석 수가 0이 아니고, 현재 날짜와 시간 이후에 상영이 시작될 경우에만 상태를 true로 설정
            boolean isAvailableSeatsZero = this.availableSeats == 0;
            if (isBeforeCurrentDate || isAvailableSeatsZero) {
                this.status = false;
            } else {
                this.status = true;
            }
        }
    }

    public void decreaseAvailableSeats(int seatsReserved) {
        this.availableSeats -= seatsReserved;
        updateStatus();
    }

    public void increaseAvailableSeats(int seatsReserved) {
        this.availableSeats += seatsReserved;
        updateStatus();
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

    private static boolean isEarlyMorning(LocalTime time) {
        return time.getHour() < 12;
    }

    private static boolean isNightTime(LocalTime time) {
        return time.getHour() >= 22;
    }

    private static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
}