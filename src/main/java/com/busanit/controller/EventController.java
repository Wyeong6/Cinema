package com.busanit.controller;

import com.busanit.domain.EventDTO;
import com.busanit.domain.SnackDTO;
import com.busanit.entity.Event;
import com.busanit.entity.Notice;
import com.busanit.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    @GetMapping("/event/eventList")
    public String eventList(Model model, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "8") int size) {
        Page<EventDTO> eventDTO = eventService.getEventList(page -1, size);

        int startPage = Math.max(1, page - 5);
        int endPage = Math.min(eventDTO.getTotalPages(), page + 5);

        model.addAttribute("eventList", eventDTO);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "event/event_list";
    }

    @GetMapping("/event_get/{id}")
    public String eventGet(@PathVariable("id") Long id, Model model) {
        // 서비스 레이어를 통해 특정 ID의 이벤트 상세 정보를 조회합니다.
        EventDTO event = eventService.getEvent(id);
        Event previousEvent = eventService.getPreviousEvent(id);
        Event nextEvent = eventService.getNextEvent(id);
        // 모델에 이벤트 정보를 추가하여 뷰에 전달합니다.
        model.addAttribute("event", event);
        model.addAttribute("previousEvent", previousEvent);
        model.addAttribute("nextEvent", nextEvent);
        return "event/event_get"; // Thymeleaf를 사용한 이벤트 상세 정보 페이지의 이름
    }
}
