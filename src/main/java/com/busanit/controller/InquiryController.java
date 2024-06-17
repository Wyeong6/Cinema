package com.busanit.controller;

import com.busanit.domain.InquiryDTO;
import com.busanit.entity.Inquiry;
import com.busanit.service.InquiryService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    // 문의하기 폼 페이지로 이동
    @GetMapping("/inquiry")
    public String showInquiryForm(Model model) {
        System.out.println("inquiry진입");
        model.addAttribute("Inquiry", new InquiryDTO());
        return "notice/service_center"; // inquiryForm.html과 같은 템플릿 파일을 사용하여 폼을 렌더링
    }

    //문의 등록
    @PostMapping("/register/inquiry")
    public String processInquiryForm(@Valid @ModelAttribute("Inquiry") InquiryDTO inquiryDTO) throws MessagingException {

            inquiryService.sendInquiryEmail(inquiryDTO.getName(),inquiryDTO.getEmail(), inquiryDTO.getSubject(), inquiryDTO.getMessage());
            inquiryService.register(inquiryDTO); // 폼 데이터를 서비스를 통해 저장하거나 처리하는 로직 수행

        return "redirect:/inquiry"; // 폼 제출 후 보여줄 페이지로 리다이렉트
    }

    @PostMapping("/admin/sendReply")
    @ResponseBody
    public String sendReply(@RequestBody InquiryDTO inquiryDTO) {
        //데이터베이스에 문의 답변 저장

        //사용자의 이메일로 문의답변과 함께 이메일 전송

        return "success";
    }

//    @GetMapping("/replyInquiry")
//    public String replyInquiry(@RequestParam(name = "id") long id, Model model) {
//        InquiryDTO inquiryDTO = inquiryService.findById(id);
//        model.addAttribute("inquiry", inquiryDTO);
//        return "admin/admin_reply_inquiry";
//    }
}
