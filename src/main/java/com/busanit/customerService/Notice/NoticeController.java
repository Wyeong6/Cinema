//package com.busanit.customerService.Notice;
//
//import com.busanit.customerService.util.PaginationUtil;
//import com.busanit.service.NoticeService;
//import lombok.extern.java.Log;
//import org.springframework.data.domain.Page;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/cs")
//@Log
//public class NoticeController {
//
//    private final NoticeService noticeService;
//
//    public NoticeController(NoticeService noticeService) {
//        this.noticeService = noticeService;
//    }
//
//    @GetMapping("/notice")
//    public String showNoticeList(Model model,
//                                 @RequestParam(defaultValue = "1") int page,
//                                 @RequestParam(defaultValue = "10") int size) {
//        noticeService.prepareNoticeList(model, page, size);
//        return "cs/notice";
//    }
//
//    @GetMapping("/notice/{id}")
//    public String showNoticeDetails(@PathVariable Long id, Model model,
//                                    @RequestParam(defaultValue = "1") int page,
//                                    @RequestParam(defaultValue = "10") int size) {
//        Notice notice = noticeService.getNoticeById(id);
//        noticeService.prepareNoticeList(model, page, size);
//        if (notice == null) {
//            return "redirect:/cs/notice";
//        }
//
//        noticeService.incrementViewCount(notice);
//
//        Notice previousNotice = noticeService.getPreviousNotice(id);
//        Notice nextNotice = noticeService.getNextNotice(id);
//
//        model.addAttribute("notice", notice);
//        model.addAttribute("previousNotice", previousNotice);
//        model.addAttribute("nextNotice", nextNotice);
//
//        return "cs/noticeDetail";
//    }
//}