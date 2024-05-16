package com.busanit.customerService.Faq;

import com.busanit.customerService.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cs")
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping("/faq")
    public String showFAQ(Model model,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size) {

        Page<FaqDTO> faqPage = faqService.getFAQPaginated(page, size);
        model.addAttribute("faqList", faqPage.getContent()); // Get content from Page
        int totalPages = faqPage.getTotalPages(); // Get total pages from Page
        PaginationUtil.addPaginationAttributes(model, page, size, totalPages);
        // Calculate page numbers to display
        List<Integer> pageNumbers = PaginationUtil.calculatePageNumbers(page, totalPages);
        model.addAttribute("pageNumbers", pageNumbers);

        return "cs/faq"; // Return the main HTML file that includes the fragment
    }

    @GetMapping("/faq/add")
    public String showFAQAddForm(Model model) {
        model.addAttribute("faqDTO", FaqDTO.builder().build());
        return "cs/faqAdd";
    }

    @PostMapping("/faq/add")
    public String addFAQ(@ModelAttribute FaqDTO faqDTO) {
        faqService.FaqSave(faqDTO);
        return "redirect:/cs/faq";
    }
}