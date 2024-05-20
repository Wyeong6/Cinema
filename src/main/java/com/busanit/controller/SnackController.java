package com.busanit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/snack")
@RequiredArgsConstructor
public class SnackController {

    @GetMapping("/snackList")
    public String snackList() { return "snack/snack_list"; }

}
