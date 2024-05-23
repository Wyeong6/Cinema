package com.busanit.controller;

import com.busanit.customerService.Notice.*;
import com.busanit.customerService.util.PaginationUtil;
import com.busanit.domain.SnackDTO;
import com.busanit.entity.Snack;
import com.busanit.service.SnackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
public class AdminPageController {

    private final SnackService snackService;
    private final NoticeService noticeService;

    @GetMapping("/adminMain")
    public String adminMain() {
        return "admin/admin_layout";
    }

    /*기존 adminPage 삭제예정*/
    @GetMapping("/adminMain2")
    public String adminMain2() {
        return "testAdminMain";
    }

    @PostMapping("/movie")
    public String movie() {
        return "admin/adminMoviePage";
    }

    @PostMapping("/member")
    public String memberManagement() {
        return "admin/adminMemberManagementPage";
    }

    @GetMapping("/snackList")
    public String snackList() {
        return "admin/admin_snack_list";
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

    @GetMapping("/notice/{id}/{currentPage}")
    public String showNoticeDetails(Model model,
                                    @PathVariable Long id,
                                    @PathVariable int currentPage,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        Notice notice = noticeService.getNoticeById(id);
        if (notice == null) {
            return "redirect:/admin/notice";
        }
        noticeService.incrementViewCount(notice);

        String content = notice.getContent();
        content = content.replace("\n", "<br/>");

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("notice", notice);
        model.addAttribute("content", content);
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
    public String addNotice(@ModelAttribute NoticeDTO noticeDTO, BindingResult result, Model model) {
        model.addAttribute("urlLoad", "/admin/notice/add");
        noticeService.NoticeSave(noticeDTO);
        return "admin/admin_layout";
    }
}