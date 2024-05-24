package com.busanit.controller;


import com.busanit.domain.EventDTO;
import com.busanit.customerService.Notice.NoticeDTO;
import com.busanit.customerService.Notice.NoticeService;
import com.busanit.customerService.util.PaginationUtil;
import com.busanit.domain.SnackDTO;
import com.busanit.entity.Event;
import com.busanit.entity.Snack;
import com.busanit.service.EventService;
import com.busanit.service.SnackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPageController {

    private final SnackService snackService;

    private final EventService eventService;

    private final NoticeService noticeService;


    @GetMapping("/adminMain")
    public String adminMain(){
        return "admin/admin_layout";
    }

    /*기존 adminPage 삭제예정*/
    @GetMapping("/adminMain2")
    public String adminMain2(){
        return "testAdminMain";
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
    public String snackRegister(@Valid SnackDTO snackDTO, BindingResult bindingResult, Model model) {

        model.addAttribute("urlLoad", "/admin/snackRegister"); // javascript load function 에 필요함
        if(bindingResult.hasErrors()) {
            return "admin/admin_snack_register";
        }
        try {
        snackService.saveSnack(Snack.toEntity(snackDTO));
        } catch(IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "admin/admin_layout";
    }
    //이벤트 등록페이지 이동
    @GetMapping("/eventRegister")
    public String eventRegister() { return "admin/admin_event_register"; }
    //이벤트 등록 기능
    @PostMapping("/eventRegister")
    public String eventRegister(@Valid EventDTO eventDTO, BindingResult bindingResult, Model model) {


        model.addAttribute("urlLoad", "/admin/eventRegister"); // javascript load function 에 필요함
        if(bindingResult.hasErrors()) {
            return "admin/admin_event_register";
        }
        try {
            System.out.println("eventDTO: " + eventDTO.toString());
            eventService.saveEvent(eventDTO);
        } catch(IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "admin/admin_layout";
    }

@GetMapping("/eventList")
public String eventList(Model model, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "8") int size) {
    Page<EventDTO> eventDTO = eventService.getEventList(page -1 , size);

    int totalPages = eventDTO.getTotalPages();
    int startPage = Math.max(1, page - 5);
    int endPage = Math.min(totalPages, page + 4); // 페이지를 5개 보여주되, 첫 페이지에서 시작할 경우 보정

    // 모델에 데이터 추가
    model.addAttribute("eventList", eventDTO); //이벤트 게시글
    model.addAttribute("currentPage", page); // 현재 페이지 번호 추가
    model.addAttribute("totalPages", totalPages); // 총 페이지 수 추가
    model.addAttribute("startPage", startPage);
    model.addAttribute("endPage", endPage);

    return "admin/admin_event_list";

}

//이벤트 수정 페이지
@GetMapping("/update/{id}")
public String editEvent(@PathVariable("id") Long eventId, Model model) {
    System.out.println("업데이트 기능 ");
    EventDTO event = eventService.getEvent(eventId);
    model.addAttribute("event", event);
    System.out.println("업데이트 후");
    return "admin/admin_event_update"; // 수정 페이지로 이동
}
//이벤트 수정 기능
@PostMapping("/update")
public String updateEvent(@ModelAttribute EventDTO eventDTO) {
    System.out.println(" 수정시작 기능 ");
    eventService.updateEvent(eventDTO);
    System.out.println("업데이트 기능 ");

    return "redirect:/admin/eventList";
}

//이벤트 삭제기능
@GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable("id") Long eventId) {

        eventService.delete(eventId);

    return "redirect:/admin/eventList";
    }


    @PostMapping("/help")
    public String help(){ return "admin/adminHelpPage"; }

    @GetMapping("/notice")
    public String showNoticeList(Model model,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        noticeService.prepareNoticeList(model, page, size);
        return "/cs/noticeAdmin";
    }
}
