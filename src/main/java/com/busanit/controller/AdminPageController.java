package com.busanit.controller;

import com.busanit.domain.*;
import com.busanit.domain.chat.ChatRoomDTO;
import com.busanit.domain.movie.CommentDTO;
import com.busanit.domain.movie.MovieDTO;
import com.busanit.entity.Member;
import com.busanit.entity.Snack;
import com.busanit.entity.*;
import com.busanit.entity.movie.Comment;
import com.busanit.repository.MovieRepository;
import com.busanit.repository.TheaterNumberRepository;
import com.busanit.service.*;
import com.busanit.service.ChatService;
import com.busanit.service.EventService;
import com.busanit.service.SnackService;
import com.busanit.service.TheaterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminPageController {

    // 병합시점에서 log 사용자가 없고 @Slf4j 와 log 가 중복된거라 log 부분을 주석처리 했습니다.
//    private static final Logger log = LoggerFactory.getLogger(AdminPageController.class);
    private final TheaterService theaterService;
    private final TheaterNumberService theaterNumberService;
    private final SeatService seatService;
    private final ScheduleService scheduleService;
    private final MovieRepository movieRepository;
    private final TheaterNumberRepository theaterNumberRepository;
    private final SnackService snackService;
    private final EventService eventService;
    private final NoticeService noticeService;
    private final ChatService chatService;
    private final MemberService memberService;
    private final MovieService movieService;
    private final InquiryService inquiryService;
    private final CommentService commentService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/adminMain")
    public String adminMain() {
        return "admin/admin_layout";
    }

    /*기존 adminPage 삭제예정*/
    @GetMapping("/adminMain2")
    public String adminMain2() {
        return "admin/testAdminMain";
    }

    @PostMapping("/memberList")
    public String memberManagement(Model model) {
        List<Member> memberList = memberService.getAllMembers();
        // 할일 - DTO로 바꿔서 담기
        model.addAttribute("memberList", memberList);
        return "admin/admin_member_list";
    }

    @PostMapping("/movieList")
    public String movieList(Model model, @RequestParam(defaultValue = "0") int page) {

        int pageSize = 10; // 한 페이지에 표시할 데이터 수
        List<MovieDTO> movieList = movieService.getMoviesWithPaging(page, pageSize);
        int totalPages = (int) Math.ceil(movieService.getTotalMovies() / (double) pageSize);

        model.addAttribute("movieList", movieList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "admin/admin_movie_list";

    }

    @PostMapping("/member")
    public String memberManagement() {
        return "admin/adminMemberManagementPage";
    }

    @PostMapping("/movieRegister")
    public String movieRegister() {
        return "admin/admin_movie_register";
    }

    @PostMapping("/movieUpdate")
    public String movieUpdate(@RequestParam(name = "movieId") Long movieId, Model model) {

        model.addAttribute("movieId", movieId);

        return "admin/admin_movie_register";
    }

    @PostMapping("/commentList")
    public String commentList(Model model) {
        List<CommentDTO> commentList = commentService.getAllComment();

        model.addAttribute("commentList", commentList);
        return "admin/admin_comment_list";
    }

    @GetMapping("/theaterList")
    public String theaterList(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                              @PageableDefault(size = 15, sort = "updateDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TheaterDTO> theaterDTOList = null;

        theaterDTOList = theaterService.getTheaterAll(pageable);
        model.addAttribute("theaterDTOList", theaterDTOList);

        int startPage = Math.max(1, theaterDTOList.getPageable().getPageNumber() - 5);
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
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "admin/admin_theater_register";
        }

        try {
            theaterService.save(Theater.toEntity(theaterDTO));
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/admin/adminMain";
    }

    @PostMapping("/checkTheaterName")
    public ResponseEntity<Map<String, Boolean>> checkDuplicateTheaterName(@RequestParam String theaterName) {
        boolean isDuplicate = theaterService.isTheaterNameDuplicate(theaterName);
        Map<String, Boolean> response = new HashMap<>();
        response.put("duplicate", isDuplicate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkTheaterNameEng")
    public ResponseEntity<Map<String, Boolean>> checkDuplicateTheaterNameEng(@RequestParam String theaterNameEng) {
        boolean isDuplicate = theaterService.isTheaterNameEngDuplicate(theaterNameEng);
        Map<String, Boolean> response = new HashMap<>();
        response.put("duplicate", isDuplicate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/seatRegister")
    public String showSeatRegisterFormSelect(@RequestParam(required = false) String region,
                                             @RequestParam(required = false) String theaterName,
                                             Model model) {
        TheaterDTO theaterDTO = new TheaterDTO();

        theaterDTO.setRegion(region != null ? region : "");
        theaterDTO.setTheaterName(theaterName != null ? theaterName : "");

        if (theaterDTO.getRegion() != null && theaterDTO.getTheaterName() != null
                && !theaterDTO.getRegion().isEmpty() && !theaterDTO.getTheaterName().isEmpty()) {
            // theaterService.getTheaterDTOWithSeats()로 seatForm 객체 업데이트
            theaterDTO = theaterService.getTheaterDTOWithSeats(region, theaterName);
        }
        model.addAttribute("theaterDTO", theaterDTO);

        return "admin/admin_seat_register";
    }

    @PostMapping("/seatRegister")
    public String Seatsregister(@RequestParam("seatData") String seatData) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<SeatDTO> seatDTOList;
        try {
            seatDTOList = objectMapper.readValue(seatData, new TypeReference<List<SeatDTO>>() {
            });
            seatService.save(seatDTOList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "error";
        }

        return "redirect:/admin/adminMain";
    }

    @GetMapping("/getTheatersByRegion")
    @ResponseBody
    public List<TheaterDTO> getTheatersByRegion(@RequestParam String region) {
        System.out.println("Region: " + region);
        return theaterService.findTheatersByRegion(region);
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

    @PostMapping("/theaterNumberDelete")
    public ResponseEntity<TheaterDTO> theaterNumberDelete(@RequestParam(name = "theaterNumberId") long theaterNumberId) {
        try {
            long theaterId = theaterNumberService.getTheaterIdByTheaterNumberId(theaterNumberId); // theaterId 가져오기
            theaterNumberService.deleteTheaterNumberById(theaterNumberId);
            theaterService.decreaseTheaterCountById(theaterId);

            // 삭제 후 업데이트된 TheaterDTO를 반환
            TheaterDTO updatedTheaterDTO = theaterService.getTheaterById(theaterId);
            return ResponseEntity.ok(updatedTheaterDTO);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 정보를 로깅하거나 콘솔에 출력합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/scheduleList")
    public String scheduleList(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                               @PageableDefault(size = 15, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ScheduleDTO> scheduleDTOList = null;

        scheduleDTOList = scheduleService.getScheduleAll(pageable);
        model.addAttribute("scheduleDTOList", scheduleDTOList);

        int startPage = Math.max(1, scheduleDTOList.getPageable().getPageNumber() - 5);
        int endPage = Math.min(scheduleDTOList.getTotalPages(), scheduleDTOList.getPageable().getPageNumber() + 5);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/admin_schedule_list";
    }

    @GetMapping("/scheduleRegister")
    public String scheduleRegister(Model model) {
        try {
            List<MovieDTO> allMovies = movieService.getAll();
            model.addAttribute("movies", allMovies);
            System.out.println("Movies: " + allMovies);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve movie list: " + e.getMessage());
        }

        return "admin/admin_schedule_register";
    }

    @PostMapping("/scheduleRegister")
    public ResponseEntity<String> scheduleRegister(@RequestBody @Valid ScheduleDTO scheduleDTO, BindingResult bindingResult) {
        // 로깅 추가
        System.out.println("Received ScheduleDTO: " + scheduleDTO);

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> errors.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(errors.toString());
        }

        try {
            scheduleService.save(scheduleDTO);
            return ResponseEntity.ok("Schedule saved successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // 또는 로깅 프레임워크를 사용하여 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/scheduleEdit")
    public String scheduleEdit(@RequestParam(name = "scheduleId") long scheduleId, Model model) {

        try {
            List<MovieDTO> allMovies = movieService.getAll();
            ScheduleDTO scheduleDTO = scheduleService.getScheduleById(scheduleId);

            model.addAttribute("movies", allMovies);
            model.addAttribute("schedule", scheduleDTO);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve movie list: " + e.getMessage());
        }

        return "admin/admin_schedule_edit";
    }

    @PostMapping("/scheduleEdit")
    public ResponseEntity<String> scheduleEdit(@RequestBody @Valid ScheduleDTO scheduleDTO, BindingResult bindingResult) {
        // 로깅 추가
        System.out.println("Received ScheduleDTO: " + scheduleDTO);

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> errors.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(errors.toString());
        }

        if (scheduleDTO.getId() == null) {
            return ResponseEntity.badRequest().body("Schedule ID is required.");
        }

        try {
            scheduleService.editSchedule(scheduleDTO);
            return ResponseEntity.ok("Schedule saved successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // 또는 로깅 프레임워크를 사용하여 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/scheduleDelete")
    public String scheduleDelete(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "scheduleId") long scheduleId,
                                 Model model, @PageableDefault(size = 15) Pageable pageable, ScheduleDTO scheduleDTO) {
        scheduleService.deleteScheduleById(scheduleId);

        Page<ScheduleDTO> scheduleDTOList = scheduleService.getScheduleAll(pageable);
        model.addAttribute("scheduleDTOList", scheduleDTOList);

        int startPage = Math.max(1, scheduleDTOList.getPageable().getPageNumber() - 5);
        int endPage = Math.min(scheduleDTOList.getTotalPages(), scheduleDTOList.getPageable().getPageNumber() + 5);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/admin_schedule_list";
    }

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
        if (bindingResult.hasErrors()) {
            return "admin/admin_snack_register";
        }
        try {
            snackService.saveSnack(Snack.toEntity(snackDTO));
        } catch (IllegalStateException e) {
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
    public String eventRegPage() {
        return "admin/admin_event_register";
    }

    //이벤트 등록 기능
    @PostMapping("/eventRegister")
    public String eventRegister(@Valid EventDTO eventDTO, BindingResult bindingResult, Model model) {

        model.addAttribute("urlLoad", "/admin/eventRegister");
        if (bindingResult.hasErrors()) {
            return "admin/admin_event_register";
        }

        // 중복 체크 로직 추가
        if (eventService.isDuplicate(eventDTO.getEventDetail(), eventDTO.getEventName())) {
            return "admin/admin_layout";
        }

        eventService.saveEvent(eventDTO);

        return "admin/admin_layout";
    }

    @GetMapping("/eventList")
    public String eventList(Model model, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "8") int size) {
        Page<EventDTO> eventDTO = eventService.getEventList(page - 1, size);

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
    @GetMapping("/eventUpdate")
    public String editEvent(@RequestParam(name = "eventId") long eventId, Model model) {

        EventDTO event = eventService.getEvent(eventId);
        model.addAttribute("event", event);

        return "admin/admin_event_update"; // 수정 페이지로 이동
    }

    //이벤트 수정 기능
    @PostMapping("/eventUpdate")
    public String updateEvent(@ModelAttribute EventDTO eventDTO, @RequestParam int pageNumber) {

        eventService.updateEvent(eventDTO);

        return "redirect:/admin/eventList?page=" + pageNumber;
    }

    //이벤트 삭제기능
    @GetMapping("/eventDelete/{id}")
    public String deleteEvent(@PathVariable("id") Long eventId, @RequestParam int pageNumber) {

        eventService.delete(eventId);

        return "redirect:/admin/eventList?page=" + pageNumber;
    }

    @PostMapping("/help")
    public String help() {
        return "admin/adminHelpPage";
    }

    //공지사항 등록페이지 이동
    @GetMapping("/noticeRegister")
    public String noticeRegPage() {

        return "admin/admin_notice_register";
    }

    //공지사항 등록기능
    @PostMapping("/noticeRegister")
    public String noticeRegister(@Valid NoticeDTO noticeDTO, BindingResult bindingResult, Model model) {

        model.addAttribute("urlLoad", "/admin/noticeRegister");
        if (bindingResult.hasErrors()) {
            return "admin/admin_notice_register";
        }

        // 중복 체크 로직 추가
        if (noticeService.isDuplicate(noticeDTO.getNoticeTitle(), noticeDTO.getNoticeContent())) {
            return "admin/admin_layout";
        }

        noticeService.saveNotice(noticeDTO);
        return "admin/admin_layout";
    }

    //공지사항 리스트
    @GetMapping("/noticeList")
    public String noticeList(Model model, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "8") int size) {
        Page<NoticeDTO> noticeDTO = noticeService.getNoticeList(page - 1, size);

        int totalPages = noticeDTO.getTotalPages();
        int startPage = Math.max(1, page - 5);
        int endPage = Math.min(totalPages, page + 4);

        model.addAttribute("noticeList", noticeDTO); //이벤트 게시글
        model.addAttribute("currentPage", page); // 현재 페이지 번호 추가
        model.addAttribute("totalPages", totalPages); // 총 페이지 수 추가
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/admin_notice_list";
    }

    //공지사항 수정 페이지
    @GetMapping("/noticeUpdatePage")
    public String noticeEdit(@RequestParam(name = "noticeId") long noticeId, Model model) {

        NoticeDTO notice = noticeService.getNotice(noticeId);
        model.addAttribute("notice", notice);

        return "admin/admin_notice_update"; // 수정 페이지로 이동
    }

    //공지사항 수정 기능
    @PostMapping("/noticeUpdate")
    public String updateNotice(@ModelAttribute NoticeDTO noticeDTO, @RequestParam int pageNumber) {

        noticeService.updateNotice(noticeDTO);

        return "redirect:/admin/noticeList?page=" + pageNumber;
    }

    //공지사항 삭제기능
    @GetMapping("/noticeDelete/{noticeId}")
    public String deleteNotice(@PathVariable("noticeId") Long noticeId, @RequestParam int pageNumber) {

        noticeService.delete(noticeId);

        return "redirect:/admin/noticeList?page=" + pageNumber;
    }

    //채팅리스트 페이지 이동
    @GetMapping("/chatList")
    public String chatList() {
        return "admin/admin_chatList";
    }

    //채팅리스트
    @PostMapping("/getChatList")
    @ResponseBody
    public Map<String, Object> ChatList(@RequestParam(defaultValue = "1") int activePage,
                                        @RequestParam(defaultValue = "1") int inactivePage,
                                        @RequestParam(defaultValue = "8") int size) {
        System.out.println("Received activePage: " + activePage + ", inactivePage: " + inactivePage);
        String memberEmail = movieService.getUserEmail();

        // 활성 채팅방 목록 가져오기
        Page<ChatRoomDTO> activeChatRooms = chatService.getActiveChatList(activePage - 1, size, memberEmail);
        Map<String, Object> activeChatResponse = addPagingChatList("active", activeChatRooms, activePage, memberEmail);
        // 비활성 채팅방 목록 가져오기
        Page<ChatRoomDTO> inactiveChatRooms = chatService.getInactiveChatList(inactivePage - 1, size, memberEmail);
        Map<String, Object> inactiveChatResponse = addPagingChatList("inactive", inactiveChatRooms, inactivePage, memberEmail);

        // 두 개의 목록을 합쳐서 반환
        Map<String, Object> combinedResponse = new HashMap<>();
        combinedResponse.putAll(activeChatResponse);
        combinedResponse.putAll(inactiveChatResponse);

        return combinedResponse;
    }

    //채팅방 페이징
    private Map<String, Object> addPagingChatList(String type, Page<ChatRoomDTO> chatRooms, int page, String memberEmail) {
        int totalPages = chatRooms.getTotalPages();
        int startPage = Math.max(1, page - 4);
        int endPage = Math.min(totalPages, page + 4);

        Map<String, Object> response = new HashMap<>();
        response.put(type + "ChatRoom", chatRooms.getContent());
        response.put(type + "CurrentPage", page);
        response.put(type + "TotalPages", totalPages);
        response.put(type + "StartPage", startPage);
        response.put(type + "EndPage", endPage);
        response.put(type + "MemberEmail", memberEmail);

        return response;
    }

    //문의리스트 페이지 이동
    @GetMapping("/inquiry")
    public String inquiryList() {
        return "admin/admin_inquiry_list";
    }

    //문의리스트 반환
    @GetMapping("/inquiryList")
    public String inquiryList(Model model, @RequestParam(defaultValue = "1") int unansweredPage,
                              @RequestParam(defaultValue = "1") int answeredPage,
                              @RequestParam(defaultValue = "8") int size) {

        // 미답변 문의 리스트와 페이지 정보
        Page<InquiryDTO> unansweredInquiries = inquiryService.getUnansweredInquiryList(unansweredPage - 1, size);
        addPagingInquiryList(model, "unanswered", unansweredInquiries, unansweredPage);

        // 답변 완료된 문의 리스트와 페이지 정보
        Page<InquiryDTO> answeredInquiries = inquiryService.getAnsweredInquiryList(answeredPage - 1, size);
        addPagingInquiryList(model, "answered", answeredInquiries, answeredPage);

        System.out.println("컨트롤answeredPage" + answeredPage);
        System.out.println("컨트롤unansweredPage" + unansweredPage);


        model.addAttribute("answeredPage", answeredPage);
        model.addAttribute("unansweredPage", unansweredPage);

        return "admin/admin_inquiry_list";
    }

    // 페이징으로 변환
    private void addPagingInquiryList(Model model, String type, Page<InquiryDTO> inquiries, int page) {
        System.out.println("pageSize " + page);
        int totalPages = inquiries.getTotalPages();
        System.out.println("totalPages " + totalPages);
        int startPage = Math.max(1, page - 4);
        System.out.println("startPage " + startPage);
        int endPage = Math.min(totalPages, page + 4);
        System.out.println("endPage " + endPage);
        model.addAttribute(type + "InquiryList", inquiries);
        model.addAttribute("current" + type + "Page", page);
        model.addAttribute("total" + type + "Pages", totalPages);
        model.addAttribute("start" + type + "Page", startPage);
        model.addAttribute("end" + type + "Page", endPage);
    }

    //해당 답변내용반환
    @GetMapping("/inquiryReplies/{inquiryId}")
    public ResponseEntity<InquiryReplyDTO> getInquiryDetails(@PathVariable Long inquiryId, Model model) {
        InquiryReplyDTO inquiryReply = inquiryService.findInquiryReplyByInquiryId(inquiryId);
        return ResponseEntity.ok(inquiryReply);
    }

    // 미답변 문의의 갯수를 조회하여 ResponseEntity로 반환
    @GetMapping("/unansweredInquiryCount")
    @ResponseBody
    public ResponseEntity<Integer> getUnansweredInquiryCount() {
        try {
            int updatedCount = inquiryService.getUnansweredInquiryCount();
            System.out.println("Updated unanswered count: " + updatedCount);
            messagingTemplate.convertAndSend("/Topic/unansweredCount", updatedCount);
            System.out.println("WebSocket 메시지 전송: " + updatedCount);
            return ResponseEntity.ok(updatedCount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }
}