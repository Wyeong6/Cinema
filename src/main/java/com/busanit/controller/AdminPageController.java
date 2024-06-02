package com.busanit.controller;

import com.busanit.customerService.Notice.*;
import com.busanit.domain.EventDTO;
import com.busanit.customerService.Notice.NoticeDTO;
import com.busanit.customerService.Notice.NoticeService;
import com.busanit.domain.TheaterNumberDTO;
import com.busanit.domain.SnackDTO;
import com.busanit.domain.chat.ChatRoomDTO;
import com.busanit.domain.TheaterDTO;
import com.busanit.entity.Snack;
import com.busanit.entity.chat.Message;
import com.busanit.repository.MessageRepository;
import com.busanit.service.ChatService;
import com.busanit.entity.Theater;
import com.busanit.service.EventService;
import com.busanit.service.SnackService;
import com.busanit.service.TheaterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminPageController {

    // 병합시점에서 log 사용자가 없고 @Slf4j 와 log 가 중복된거라 log 부분을 주석처리 했습니다.
//    private static final Logger log = LoggerFactory.getLogger(AdminPageController.class);
    private final TheaterService theaterService;
    private final SnackService snackService;
    private final EventService eventService;
    private final NoticeService noticeService;
    private final ChatService chatService;
    private final MessageRepository messageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/adminMain")
    public String adminMain() {
        return "admin/admin_layout";
    }

    /*기존 adminPage 삭제예정*/
    @GetMapping("/adminMain2")
    public String adminMain2(){
        return "admin/testAdminMain";
    }

    @PostMapping("/movie")
    public String movie() {
        return "admin/adminMoviePage";
    }

    @PostMapping("/member")
    public String memberManagement(){
        return "admin/adminMemberManagementPage";
    }

    @GetMapping("/theaterList")
    public String theaterList(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                              @PageableDefault(size = 15, sort = "updateDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TheaterDTO> theaterDTOList = null;

        theaterDTOList = theaterService.getTheaterAll(pageable);
        model.addAttribute("theaterDTOList", theaterDTOList);

        int startPage= Math.max(1, theaterDTOList.getPageable().getPageNumber() - 5);
        int endPage = Math.min(theaterDTOList.getTotalPages(), theaterDTOList.getPageable().getPageNumber() + 5);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/admin_theater_list";
    }

    @GetMapping("/theaterRegister")
    public String showTheaterRegisterForm() {
        return "admin/admin_theater_register";
    }

    @PostMapping("/theaterRegister")
    public String theaterRegister(@Valid TheaterDTO theaterDTO, BindingResult bindingResult, Model model) {
        model.addAttribute("urlLoad", "/admin/theaterRegister");
        if (bindingResult.hasErrors()) {
            return "admin/admin_theater_register";
        }

        try {
            theaterService.save(Theater.toEntity(theaterDTO));
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", e.getMessage());
        }

        return "admin/admin_layout";
    }

    @GetMapping("/theaterGet")
    public String theaterGet(@RequestParam(name = "theaterId") long theaterId, Model model) {
        TheaterDTO theaterDTO = theaterService.getTheaterById(theaterId);
        List<TheaterNumberDTO> theaterNumberDTOs = theaterService.getTheaterNumbersByTheaterId(theaterId);

        model.addAttribute("theaterDTO", theaterDTO);
        model.addAttribute("theaterNumberDTOs", theaterNumberDTOs);

        return "admin/admin_theater_edit";
    }

    @PostMapping("/theaterDelete")
    public String theaterDelete(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "theaterId") long theaterId,
                                Model model, @PageableDefault(size = 15) Pageable pageable, TheaterDTO theaterDTO) {
        theaterService.deleteTheaterById(theaterId);

        Page<TheaterDTO> theaterDTOList = theaterService.getTheaterAll(pageable);
        model.addAttribute("theaterDTOList", theaterDTOList);

        int startPage = Math.max(1, theaterDTOList.getPageable().getPageNumber() - 5);
        int endPage = Math.min(theaterDTOList.getTotalPages(), theaterDTOList.getPageable().getPageNumber() + 5);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/admin_theater_list";
    }

    @GetMapping("/scheduleList")
    public String scheduleList() { return "admin/admin_schedule_list"; }

    @GetMapping("/scheduleRegister")
    public String scheduleRegister() { return "admin/admin_schedule_register"; }

    @GetMapping("/snackList")
    public String snackList(Model model,
                            @PageableDefault(size = 15, sort = "updateDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SnackDTO> snackDTOList = null;

        snackDTOList = snackService.getSnackList(pageable);
        model.addAttribute("snackList", snackDTOList);

        int startPage = Math.max(1, snackDTOList.getPageable().getPageNumber() - 5);
        int endPage = Math.min(snackDTOList.getTotalPages(), snackDTOList.getPageable().getPageNumber() + 5);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/admin_snack_list";
    }

    @PostMapping("/snackList")
    public String snackList(@RequestParam(name = "page", defaultValue = "0") int page,
                            Model model,
                            @PageableDefault(size = 15, sort = "updateDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SnackDTO> snackDTOList = null;

        snackDTOList = snackService.getSnackList(pageable);
        model.addAttribute("snackList", snackDTOList);

        int startPage = Math.max(1, snackDTOList.getPageable().getPageNumber() - 5);
        int endPage = Math.min(snackDTOList.getTotalPages(), snackDTOList.getPageable().getPageNumber() + 5);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "/admin/admin_snack_list";
    }


    @GetMapping("/snackRegister")
    public String snackRegister() {
        return "admin/admin_snack_register";
    }

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

    @GetMapping("/snackEdit")
    public String snackEdit(@RequestParam(name = "snackItemId") long snackItemId,
                            Model model) {

        SnackDTO snackDTO2 = snackService.get(snackItemId);
        model.addAttribute("snack", snackDTO2);

        return "/admin/admin_snack_edit";
    }

    @PostMapping("/snackEdit")
    public String snackEdit(SnackDTO snackDTO, Model model) {

        snackService.editSnack(snackDTO);

        model.addAttribute("urlLoad", "/admin/snackList"); // javascript load function 에 필요함

        return "/admin/admin_layout";
    }

    @PostMapping("/snackDelete")
    public String snackDelete(@RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "snackItemId") long snackItemId,
                              Model model,
                              @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                              SnackDTO snackDTO) {

        snackService.deleteSnack(snackItemId);

        Page<SnackDTO> snackDTOList = null;

        snackDTOList = snackService.getSnackList(pageable);
        model.addAttribute("snackList", snackDTOList);

        int startPage = Math.max(1, snackDTOList.getPageable().getPageNumber() - 5);
        int endPage = Math.min(snackDTOList.getTotalPages(), snackDTOList.getPageable().getPageNumber() + 5);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "/admin/admin_snack_list";
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
    int endPage = Math.min(totalPages, page + 4);

    model.addAttribute("eventList", eventDTO); //이벤트 게시글
    model.addAttribute("currentPage", page); // 현재 페이지 번호 추가
    model.addAttribute("totalPages", totalPages); // 총 페이지 수 추가
    model.addAttribute("startPage", startPage);
    model.addAttribute("endPage", endPage);

    return "admin/admin_event_list";
}

//이벤트 수정 페이지
@GetMapping("/update")
public String editEvent(@RequestParam(name = "eventId") long eventId, Model model) {

    EventDTO event = eventService.getEvent(eventId);
    model.addAttribute("event", event);

    return "admin/admin_event_update"; // 수정 페이지로 이동
}
//이벤트 수정 기능
@PostMapping("/update")
public String updateEvent(@ModelAttribute EventDTO eventDTO, @RequestParam int pageNumber) {

    eventService.updateEvent(eventDTO);

    return "redirect:/admin/eventList?page=" + pageNumber;
}

//이벤트 삭제기능
@GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable("id") Long eventId, @RequestParam int pageNumber) {

        eventService.delete(eventId);

    return "redirect:/admin/eventList?page=" + pageNumber;
    }

    @PostMapping("/help")
    public String help() {
        return "admin/adminHelpPage";
    }

    @GetMapping("/notice")
    public String showNoticeList(Model model,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        noticeService.prepareNoticeList(model, page, size);
        return "/cs/noticeAdmin";
    }

    @GetMapping("/notice/{id}")
    public String showNoticeDetails(Model model,
                                    @PathVariable Long id,
                                    @RequestParam(value = "currentPage", required = false) Integer currentPage) {
        Notice notice = noticeService.getNoticeById(id);
        if (notice == null) {
            return "redirect:/admin/notice";
        }
        noticeService.incrementViewCount(notice);

        model.addAttribute("currentPage", currentPage);

        model.addAttribute("notice", notice);
        return "cs/noticeDetailAdmin";
    }

    @DeleteMapping("/notice/{id}")
    public ResponseEntity<String> deleteNotice(@PathVariable Long id) {
        return noticeService.deleteNoticeById(id)
                ? ResponseEntity.ok("삭제 완료")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제 실패");
    }

    @GetMapping("/notice/add")
    public String addNotice() {
        return "/cs/noticeAddAdmin";
    }

    @PostMapping("/notice/add")
    public String addNotice(Model model,
                            @RequestParam(value = "currentPage", required = false) Integer currentPage,
                            NoticeDTO noticeDTO, BindingResult result) {
        Long id = noticeDTO.getId();
        if (id == null) {
            noticeService.NoticeSave(noticeDTO);
            model.addAttribute("urlLoad", "/admin/notice");
        } else {
            noticeService.NoticeMod(id, noticeDTO);
            model.addAttribute("urlLoad", "/admin/notice/" + id + "?page=" + currentPage);
            model.addAttribute("currentPage", currentPage);
            System.out.println("수정 완료해서 보낼 때: " + currentPage);
        }

        return "admin/admin_layout";
    }

    @GetMapping("/notice/mod/{id}")
    public String modNotice(@PathVariable Long id, Model model,
                            @RequestParam(value = "currentPage", required = false) Integer currentPage ) {
        NoticeDTO noticeDTO = noticeService.findById(id);

        model.addAttribute("id", id);
        model.addAttribute("title", noticeDTO.getTitle());
        model.addAttribute("content", noticeDTO.getContent());
        model.addAttribute("pinned", noticeDTO.isPinned());
        model.addAttribute("currentPage", currentPage);
        System.out.println("수정 페이지로 들어갔을 때 " + currentPage);

        return "cs/noticeAddAdmin";
    }

    //채팅리스트
    @GetMapping("/chatList")
    public String chatList(Model model, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "8") int size){
        Page<ChatRoomDTO> chatRoom = chatService.getChatList(page-1, size);

        int totalPages = chatRoom.getTotalPages();
        int startPage = Math.max(1, page - 5);
        int endPage = Math.min(totalPages, page + 4);


        model.addAttribute("eventList", chatRoom); //이벤트 게시글
        model.addAttribute("currentPage", page); // 현재 페이지 번호 추가
        model.addAttribute("totalPages", totalPages); // 총 페이지 수 추가
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/admin_chatList";
    }
    //채팅 모달창
    @GetMapping("/chatModal")
    public String chatModal(){
        return "admin/admin_chatModal";
    }

    //메세지 확인 여부
    @PostMapping("/{messageId}/read")
    public void markMessageAsRead(@PathVariable Long messageId) {
        chatService.markAsRead(messageId);
    }

    @GetMapping("/unread/{receiverId}")
    public List<Message> getUnreadMessages(@PathVariable Long receiverId) {
        return messageRepository.findByReceiverIdAndIsReadFalse(receiverId);
    }
}