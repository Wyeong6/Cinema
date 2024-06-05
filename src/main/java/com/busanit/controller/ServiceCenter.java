package com.busanit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class ServiceCenter {

    @GetMapping("/cs/service_center")
    public String serviceCenter() {
        return "/cs/service_center"; // service_center.html 파일을 반환
    }
}
