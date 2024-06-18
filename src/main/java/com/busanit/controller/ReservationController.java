package com.busanit.controller;

import com.busanit.domain.ScheduleDTO;
import com.busanit.domain.SeatDTO;
import com.busanit.domain.TheaterDTO;
import com.busanit.domain.TheaterNumberDTO;
import com.busanit.domain.movie.MovieDTO;
import com.busanit.entity.Schedule;
import com.busanit.entity.Theater;
import com.busanit.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final MovieService movieService;
    private final TheaterService theaterService;
    private final TheaterNumberService theaterNumberService;
    private final ScheduleService scheduleService;

    @GetMapping("/screeningSchedule")
    public String screeningSchedule(Model model) {
        try {
            List<MovieDTO> allMovies = movieService.getAll();
            model.addAttribute("movies", allMovies);
            System.out.println("Movies: " + allMovies);
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

            Map<String, List<SeatDTO>> seatsByColumn = theaterNumberDTO.getSeats().stream()
                    .sorted(Comparator.comparingLong(SeatDTO::getSeatRow)) // SeatDTO의 seatRow 필드를 기준으로 정렬
                    .collect(Collectors.groupingBy(SeatDTO::getSeatColumn));

            model.addAttribute("scheduleDTO", scheduleDTO);
            model.addAttribute("movieDTOs", movieDTOs);
            model.addAttribute("seatsByColumn", seatsByColumn);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve movie list: " + e.getMessage());
        }

        return "reservation/seat_selection";
    }

}
