package com.busanit.controller;

import com.busanit.domain.EventDTO;
import com.busanit.domain.SnackDTO;
import com.busanit.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
//    @GetMapping("/eventList")
//    public String eventList(Model model, @PageableDefault(size = 8, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
//        Page<EventDTO> eventDTO = null;
//
//        eventDTO = eventService.getEventList(pageable);
//        model.addAttribute("eventList", eventDTO);
//    }



//    @GetMapping("/snackList")
//    public String snackList(Model model, @PageableDefault(size = 8, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
//        Page<SnackDTO> snackDTOList = null;
//
//        snackDTOList = snackService.getSnackList(pageable);
//        model.addAttribute("snackList", snackDTOList);
//
//        int startPage = Math.max(1, snackDTOList.getPageable().getPageNumber() -5);
//        int endPage = Math.min(snackDTOList.getTotalPages(), snackDTOList.getPageable().getPageNumber() +5);
//        model.addAttribute("startPage", startPage);
//        model.addAttribute("endPage", endPage);
//
//        return "snack/snack_list";
//    }
//
//    @GetMapping("/detail")
//    public String detail(Long id, Model model, @PageableDefault(size = 3) Pageable pageable) {
//
//        SnackDTO snackDTO = snackService.get(id);
//        model.addAttribute("snack", snackDTO);
//
//        // 스낵 추천 리스트(랜덤)
//        Page<SnackDTO> snackDTOList = null;
//        snackDTOList = snackService.getSnackListRandom(pageable);
//        model.addAttribute("snackList", snackDTOList);
//
//        return "snack/snack_get";
//    }
}
