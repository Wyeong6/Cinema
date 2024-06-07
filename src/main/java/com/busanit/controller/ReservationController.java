package com.busanit.controller;

import com.busanit.domain.TheaterDTO;
import com.busanit.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final TheaterService theaterService;

    @GetMapping("/screeningSchedule")
    public String screeningSchedule(Model model) {
        model.addAttribute("theaters", null);
        return "reservation/screening_schedule";
    }

    @GetMapping("/getTheatersByRegion")
    @ResponseBody
    public List<TheaterDTO> getTheatersByRegion(@RequestParam String region) {
        System.out.println("Region: " + region);
        return theaterService.findTheatersByRegion(region);
    }
}
