package com.busanit.controller;

import com.busanit.domain.EventDTO;
import com.busanit.domain.NoticeDTO;
import com.busanit.entity.Notice;
import com.busanit.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    //공지사항 리스트
    @GetMapping("/notice/noticeList")
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

        return "notice/notice_list";
    }
    @GetMapping("/notice_get/{noticeId}")
    public String eventGet(@PathVariable("noticeId") Long noticeId, Model model) {
        // 서비스 레이어를 통해 특정 ID의 이벤트 상세 정보를 조회합니다.
        NoticeDTO notice = noticeService.getEvent(noticeId);
        // 모델에 이벤트 정보를 추가하여 뷰에 전달합니다.
        model.addAttribute("notice", notice);
        return "notice/notice_get"; // Thymeleaf를 사용한 이벤트 상세 정보 페이지의 이름
    }
}
