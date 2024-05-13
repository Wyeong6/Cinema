package com.busanit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPageController {

    @PostMapping("/movie")
    public String movie(){
        return "admin/adminMoviePage";
    }

    @PostMapping("/member")
    public String memberManagement(){
        return "admin/adminMemberManagementPage";
    }

    @PostMapping("/help")
    public String help(){
        return "admin/adminHelpPage";
    }
}
