package com.busanit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    @GetMapping("/")
    public String mypage() {

        return "/layout/layout_mypage";
    }

    @GetMapping("/main")
    public String mypageMain() {

        return "/mypage/mypage_main";
    }
    @GetMapping("/reservation")
    public String mypageReservation() {

        return "/mypage/mypage_reservation";
    }
    @GetMapping("/order")
    public String mypageOrder() {

        return "/mypage/mypage_order";
    }

    @GetMapping("/membership")
    public String mypageMembership() {

        return "/mypage/mypage_membership";
    }
    @GetMapping("/point")
    public String mypagePoint() {

        return "/mypage/mypage_point";
    }
    @GetMapping("/review")
    public String mypageReview() {

        return "/mypage/mypage_review";
    }

    @GetMapping("/infoEdit")
    public String mypageEdit() {

        return "/mypage/mypage_private_info";
    }

}
