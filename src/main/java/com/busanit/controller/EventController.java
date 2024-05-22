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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    @GetMapping("/eventList")
    public String eventList(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size) {
        Page<EventDTO> eventDTO = eventService.getEventList(page, size);

        model.addAttribute("eventList", eventDTO);
        model.addAttribute("startPage", Math.max(1, Math.max(1, page - 5))); // null 대신에 1을 사용
        model.addAttribute("endPage", Math.min(eventDTO.getTotalPages(), Math.min(eventDTO.getTotalPages(), page + 5))); // null 대신에 getTotalPages()를 사용

        return "event/event_list";
    }


}
