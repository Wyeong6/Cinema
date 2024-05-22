package com.busanit.customerService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cs")
public class LostFoundController {
    @GetMapping("/lostfound")
    public String inquiry() {
        return "cs/lostFound";
    }
}