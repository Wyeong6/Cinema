package com.busanit.controller;

import com.busanit.domain.*;
import com.busanit.domain.chat.ChatRoomDTO;
import com.busanit.domain.movie.MovieDTO;
import com.busanit.entity.*;
import com.busanit.entity.movie.Movie;
import com.busanit.repository.MessageRepository;
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
    public String memberManagement(Model model){
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
    public String memberManagement(){
        return "admin/adminMemberManagementPage";
    }

    @PostMapping("/movieRegister")
    public String movieRegister(Model model) {
        return "admin/admin_movie_register";
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
            seatDTOList = objectMapper.readValue(seatData, new TypeReference<List<SeatDTO>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "error";
        }

        try {
            seatService.save(seatDTOList);
        } catch (Exception e) {
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

        int startPage= Math.max(1, scheduleDTOList.getPageable().getPageNumber() - 5);
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

        return "admin/admin_schedule_register"; }

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
    public String scheduleEdit(@RequestParam(name="scheduleId") long scheduleId, Model model) {

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

//    //채팅리스트
//    @RequestMapping(value = "/getChatList", method = {RequestMethod.GET, RequestMethod.POST})
//    public Map<String, Object> chatListApi(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "1") int size) {
//
//        System.out.println(" 채팅리스트 발아아앙동");
//        String memberEmail = movieService.getUserEmail();
//        Page<ChatRoomDTO> chatRoom = chatService.getChatList(page - 1, size, memberEmail);
//
//        int totalPages = chatRoom.getTotalPages();
//        int startPage = Math.max(1, page - 5);
//        int endPage = Math.min(totalPages, page + 4);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("chatRoom", chatRoom.getContent());
//        response.put("currentPage", page);
//        response.put("totalPages", totalPages);
//        response.put("startPage", startPage);
//        response.put("endPage", endPage);
//        response.put("memberEmail", memberEmail);
//
//        return response;
//    }

//    //채팅리스트
//    @GetMapping("/chatList")
//    @ResponseBody
//    public Map<String, Object> chatList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
//        String memberEmail = "admin@example.com"; // 예시용 이메일
//        Page<ChatRoomDTO> chatRooms = chatService.getChatList(page - 1, size, memberEmail);
//
//        int totalPages = chatRooms.getTotalPages();
//        int startPage = Math.max(1, page - 5);
//        int endPage = Math.min(totalPages, page + 4);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("chatRooms", chatRooms.getContent());
//        response.put("currentPage", page);
//        response.put("totalPages", totalPages);
//        response.put("startPage", startPage);
//        response.put("endPage", endPage);
//        response.put("memberEmail", memberEmail);
//
//        return response;
//    }

    @PostMapping("/getChatList")
    @ResponseBody
    public Map<String, Object> pagingChatList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "8") int size) {
        String memberEmail =  movieService.getUserEmail();

        Page<ChatRoomDTO> chatRooms = chatService.getChatList(page - 1, size, memberEmail);

        int totalPages = chatRooms.getTotalPages();
        int startPage = Math.max(1, page - 5);
        int endPage = Math.min(totalPages, page + 4);

        Map<String, Object> response = new HashMap<>();
        response.put("chatRoom", chatRooms.getContent());
        response.put("currentPage", page);
        response.put("totalPages", totalPages);
        response.put("startPage", startPage);
        response.put("endPage", endPage);
        response.put("memberEmail", memberEmail);

        return response;
    }

//    @MessageMapping("/updateChatList") // 클라이언트에서 메시지 보낼 때 사용할 주제
//    @SendTo("/queue/chatList") // 클라이언트가 구독할 주제
//    public Map<String, Object> updateChatList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "8") int size) {
//        System.out.println("리스트업데이트시작");
//        String memberEmail = movieService.getUserEmail();
//        Page<ChatRoomDTO> chatRoom = chatService.getChatList(page - 1, size, memberEmail);
//
//        int totalPages = chatRoom.getTotalPages();
//        int startPage = Math.max(1, page - 5);
//        int endPage = Math.min(totalPages, page + 4);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("chatRoom", chatRoom.getContent());
//        response.put("currentPage", page);
//        response.put("totalPages", totalPages);
//        response.put("startPage", startPage);
//        response.put("endPage", endPage);
//        response.put("memberEmail", memberEmail);
//
//        // WebSocket 클라이언트에게 업데이트된 채팅 리스트 전송
////        messagingTemplate.convertAndSend("/queue/chatList", response);
//
//        System.out.println("리스트업데이트돼라얍");
//        return response;
//    }

//    @PostMapping("/admin/chatList") // POST 방식으로 요청 처리
//    @ResponseBody
//    public Map<String, Object> chatListApi(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "1") int size) {
//        String memberEmail = movieService.getUserEmail();
//        Page<ChatRoomDTO> chatRoom = chatService.getChatList(page - 1, size, memberEmail);
//
//        int totalPages = chatRoom.getTotalPages();
//        int startPage = Math.max(1, page - 5);
//        int endPage = Math.min(totalPages, page + 4);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("chatRoom", chatRoom.getContent());
//        response.put("currentPage", page);
//        response.put("totalPages", totalPages);
//        response.put("startPage", startPage);
//        response.put("endPage", endPage);
//        response.put("memberEmail", memberEmail);
//
//        return response;
//    }



    //채팅 모달창
    @GetMapping("/chatModal")
    public String chatModal() {
        return "admin/admin_chatModal";
    }

//    @GetMapping("/noticeList")
//    public String showNoticeList(Model model,
//                                 @RequestParam(defaultValue = "1") int page,
//                                 @RequestParam(defaultValue = "10") int size) {
//        noticeService.prepareNoticeList(model, page, size);
//        return "/cs/noticeAdmin";
//    }
//
//    @GetMapping("/notice/{id}")
//    public String showNoticeDetails(Model model,
//                                    @PathVariable Long id,
//                                    @RequestParam(value = "currentPage", required = false) Integer currentPage) {
//        Notice notice = noticeService.getNoticeById(id);
//        if (notice == null) {
//            return "redirect:/admin/notice";
//        }
//        noticeService.incrementViewCount(notice);
//
//        model.addAttribute("currentPage", currentPage);
//        model.addAttribute("notice", notice);
//
//        return "cs/noticeDetailAdmin";
//    }
//
//    @DeleteMapping("/notice/{id}")
//    public ResponseEntity<String> deleteNotice(@PathVariable Long id) {
//        return noticeService.deleteNoticeById(id)
//                ? ResponseEntity.ok("삭제 완료")
//                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제 실패");
//    }
//
//    @GetMapping("/notice/add")
//    public String addNotice() {
//        return "/cs/noticeAddAdmin";
//    }
//
//    @PostMapping("/notice/add")
//    public String addNotice(Model model,
//                            @RequestParam(value = "currentPage", required = false) Integer currentPage,
//                            NoticeDTO noticeDTO, BindingResult result) {
//        Long id = noticeDTO.getId();
//        if (id == null) {
//            noticeService.NoticeSave(noticeDTO);
//            model.addAttribute("urlLoad", "/admin/notice");
//        } else {
//            noticeService.NoticeMod(id, noticeDTO);
//            model.addAttribute("urlLoad", "/admin/notice/" + id + "?page=" + currentPage);
//            model.addAttribute("currentPage", currentPage);
//            System.out.println("수정 완료해서 보낼 때: " + currentPage);
//        }
//
//        return "admin/admin_layout";
//    }
//
//    @GetMapping("/notice/mod/{id}")
//    public String modNotice(@PathVariable Long id, Model model,
//                            @RequestParam(value = "currentPage", required = false) Integer currentPage ) {
//        NoticeDTO noticeDTO = noticeService.findById(id);
//
//        model.addAttribute("id", id);
//        model.addAttribute("title", noticeDTO.getTitle());
//        model.addAttribute("content", noticeDTO.getContent());
//        model.addAttribute("pinned", noticeDTO.isPinned());
//        model.addAttribute("currentPage", currentPage);
//        System.out.println("수정 페이지로 들어갔을 때 " + currentPage);
//
//        return "cs/noticeAddAdmin";
//    }



}