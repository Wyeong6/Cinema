package com.busanit.customerService.Notice;

import com.busanit.customerService.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/cs")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/notice")
    public String showNoticeList(Model model,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) {

        Page<NoticeDTO> noticePage = noticeService.getNoticePaginated(page, size);
        model.addAttribute("noticeList", noticePage.getContent()); // Get content from Page
        int totalPages = noticePage.getTotalPages(); // Get total pages from Page
        PaginationUtil.addPaginationAttributes(model, page, size, totalPages);

        // Calculate page numbers to display
        List<Integer> pageNumbers = PaginationUtil.calculatePageNumbers(page, totalPages);
        model.addAttribute("pageNumbers", pageNumbers);

        return "cs/notice";
    }

    @GetMapping("/notice/{id}")
    public String showNoticeDetails(@PathVariable Long id, Model model) {
        Notice notice = noticeService.getNoticeById(id);
        if (notice == null) {
            return "redirect:/cs/notice";
        }

        noticeService.incrementViewCount(notice);

        Notice previousNotice = noticeService.getPreviousNotice(id);
        Notice nextNotice = noticeService.getNextNotice(id);

        model.addAttribute("notice", notice);
        model.addAttribute("previousNotice", previousNotice);
        model.addAttribute("nextNotice", nextNotice);

        return "cs/noticeDetail";
    }
}