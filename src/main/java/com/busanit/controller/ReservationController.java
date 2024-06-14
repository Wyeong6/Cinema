package com.busanit.controller;

import com.busanit.domain.TheaterDTO;
import com.busanit.domain.TheaterNumberDTO;
import com.busanit.domain.movie.MovieDTO;
import com.busanit.entity.Theater;
import com.busanit.service.MovieService;
import com.busanit.service.MovieService2;
import com.busanit.service.TheaterNumberService;
import com.busanit.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final TheaterService theaterService;
    private final TheaterNumberService theaterNumberService;
    private final MovieService movieService;

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

}
