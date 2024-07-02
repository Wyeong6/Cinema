package com.busanit.controller;

import com.busanit.domain.ScheduleDTO;
import com.busanit.domain.SeatDTO;
import com.busanit.domain.TheaterDTO;
import com.busanit.domain.TheaterNumberDTO;
import com.busanit.domain.movie.MovieDTO;
import com.busanit.entity.Schedule;
import com.busanit.entity.Seat;
import com.busanit.entity.Theater;
import com.busanit.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final MovieService movieService;
    private final TheaterService theaterService;
    private final TheaterNumberService theaterNumberService;
    private final ScheduleService scheduleService;
    private final SeatService seatService;
    private final SeatReservationService seatReservationService;
    private final MemberService memberService;

    @GetMapping("/screeningSchedule")
    public String screeningSchedule(Model model) {
        try {
            List<MovieDTO> allMovies = movieService.getAll();
            String userAge = memberService.currentLoggedInAge();

            model.addAttribute("movies", allMovies);
            model.addAttribute("userAge", userAge);
            System.out.println("userage : " + userAge);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve movie list: " + e.getMessage());
        }

        return "reservation/screening_schedule";
    }

    @GetMapping("/screeningSchedule/{movieId}")
    public String screeningScheduleByMovieId(@PathVariable("movieId") Long movieId, Model model) {
        try {
            List<MovieDTO> allMovies = movieService.getAll();
            String userAge = memberService.currentLoggedInAge();

            model.addAttribute("movies", allMovies);
            model.addAttribute("movieId", movieId);
            model.addAttribute("userAge", userAge);
            System.out.println("userage : " + userAge);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve movie list: " + e.getMessage());
        }

        return "reservation/screening_schedule";
    }

    @GetMapping("/getTheatersByRegion")
    @ResponseBody
    public List<TheaterDTO> getTheatersByRegion(@RequestParam String region) {
        System.out.println("Region: " + region);
        return theaterService.findTheatersByRegion(region);
    }

    @GetMapping("/getTheaterByTheaterName")
    @ResponseBody
    public TheaterDTO findByTheaterName(String theaterName) {
        Optional<Theater> theaterOptional = theaterService.findByTheaterName(theaterName);
        Theater theater = theaterOptional.orElseThrow(() -> new IllegalArgumentException("극장을 찾을 수 없습니다: " + theaterName));
        return TheaterDTO.toDTO(theater);
    }

    @GetMapping("/ByConditions")
    @ResponseBody
    public List<ScheduleDTO> getSchedulesByConditions(@RequestParam String theaterName,
                                                      @RequestParam Long movieId,
                                                      @DateTimeFormat(pattern = "yyyy-M-d") @RequestParam LocalDate date) {
        return scheduleService.findSchedulesByConditions(theaterName, movieId, date);
    }

    @GetMapping("/seatSelection/{scheduleId}")
    public String seatSelection(@PathVariable Long scheduleId, Model model) {
        try {
            ScheduleDTO scheduleDTO = scheduleService.getScheduleById(scheduleId);
            List<MovieDTO> movieDTOs = movieService.getMovieDetailInfo(scheduleDTO.getMovieId());
            TheaterNumberDTO theaterNumberDTO = theaterNumberService.getTheaterNumberById(scheduleDTO.getTheaterNumberId());

            // 좌석 정보 가져오기
            List<SeatDTO> seatDTOs = theaterNumberDTO.getSeats();

            // 예약된 좌석 여부 설정
            seatService.markSeatsAsReserved(scheduleId, seatDTOs);

            // 좌석을 열과 행으로 그룹화하여 Map에 저장
            Map<String, List<SeatDTO>> seatsByColumn = seatDTOs.stream()
                    .sorted(Comparator.comparingLong(SeatDTO::getSeatRow))
                    .collect(Collectors.groupingBy(SeatDTO::getSeatColumn));

            model.addAttribute("scheduleDTO", scheduleDTO);
            model.addAttribute("movieDTOs", movieDTOs);
            model.addAttribute("seatsByColumn", seatsByColumn);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve movie list: " + e.getMessage());
        }

        return "reservation/seat_selection";
    }

    // 좌석 예약
    @PostMapping("/reserveSeats")
    @ResponseBody
    public ResponseEntity<String> reserveSeats(@RequestBody Map<String, Object> request) {
        try {
            Long scheduleId = Long.parseLong(request.get("scheduleId").toString());
            String reservedBy = (String) request.get("reservedBy");
            List<Map<String, Object>> selectedSeats = (List<Map<String, Object>>) request.get("selectedSeats");

            if (scheduleId == null || reservedBy == null || selectedSeats == null) {
                return ResponseEntity.badRequest().body("Invalid request data");
            }

            // 전달된 좌석 정보 로그
            System.out.println("Selected Seats: " + selectedSeats);

            List<Seat> seatsToReserve = new ArrayList<>();
            for (Map<String, Object> seatData : selectedSeats) {
                String seatId = (String) seatData.get("seatId");
                Integer seatRow = (Integer) seatData.get("seatRow");
                String seatColumn = (String) seatData.get("seatColumn");

                if (seatId == null || seatRow == null || seatColumn == null) {
                    return ResponseEntity.badRequest().body("Invalid seat data in selectedSeats");
                }

                Seat seat = new Seat();
                seat.setId(seatId);
                seat.setSeatRow(seatRow.longValue()); // 여기서 Integer를 Long으로 변환
                seat.setSeatColumn(seatColumn);

                seatsToReserve.add(seat);
            }

            // 예약 서비스 호출
            for (Seat seat : seatsToReserve) {
                seatReservationService.reserveSeat(scheduleId, seat.getId(), reservedBy);
            }

            // 좌석 예약 후 좌석 업데이트 로직 호출
            seatReservationService.updateAvailableSeats(scheduleId, seatsToReserve);

            return ResponseEntity.ok("Seats reserved successfully");
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid format for scheduleId");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // 예외 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while reserving seats");
        }
    }

    @PostMapping("/reserveSeatsCancel")
    @ResponseBody
    public ResponseEntity<String> cancelSeatReservations(@RequestParam Long scheduleId, @RequestBody List<String> seatIds) {
        try {
            if (scheduleId != null && seatIds != null && !seatIds.isEmpty()) {
                seatReservationService.deleteSeat(scheduleId, seatIds);
                return ResponseEntity.ok("Seat reservations canceled successfully");
            } else {
                return ResponseEntity.badRequest().body("Invalid scheduleId or seatIds");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to cancel seat reservations");
        }
    }

    private Long parseScheduleId(Map<String, Object> request) {
        Object scheduleIdObj = request.get("scheduleId");
        if (scheduleIdObj instanceof Number) {
            return ((Number) scheduleIdObj).longValue();
        } else if (scheduleIdObj instanceof String) {
            return Long.parseLong((String) scheduleIdObj);
        }
        return null;
    }


}
