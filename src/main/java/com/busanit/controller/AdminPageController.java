package com.busanit.controller;

import com.busanit.domain.SnackDTO;
import com.busanit.service.SnackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPageController {

    private final SnackService snackService;

    @GetMapping("/adminMain")
    public String adminMain(){
        return "admin/admin_layout";
    }

    /*기존 adminPage 삭제예정*/
    @GetMapping("/adminMain2")
    public String adminMain2(){
        return "admin/adminMain";
    }

    @PostMapping("/movie")
    public String movie(){
        return "admin/adminMoviePage";
    }

    @PostMapping("/member")
    public String memberManagement(){
        return "admin/adminMemberManagementPage";
    }

    @GetMapping("/snackList")
    public String snackList() { return "admin/admin_snack_list"; }

    @GetMapping("/snackRegister")
    public String snackRegister() { return "admin/admin_snack_register"; }

    @PostMapping("/snackRegister")
    public String snackRegister(@Valid SnackDTO snackDTO, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "admin/admin_snack_register";
        }

        return "admin/admin_snack_register";
    }

    @PostMapping("/help")
    public String help(){
        return "admin/adminHelpPage";
    }
}
