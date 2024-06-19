package com.busanit.controller;

import com.busanit.domain.InquiryDTO;
import com.busanit.entity.Inquiry;
import com.busanit.entity.InquiryReply;
import com.busanit.repository.InquiryReplyRepository;
import com.busanit.service.InquiryService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public String sendInquiry(@Valid @ModelAttribute("Inquiry") InquiryDTO inquiryDTO) throws MessagingException {

            inquiryService.sendInquiry(inquiryDTO.getName(),inquiryDTO.getEmail(), inquiryDTO.getSubject(), inquiryDTO.getMessage());
            inquiryService.InquiryRegister(inquiryDTO); // 폼 데이터를 서비스를 통해 저장하거나 처리하는 로직 수행

        return "redirect:/inquiry"; // 폼 제출 후 보여줄 페이지로 리다이렉트
    }

    @PostMapping("/admin/sendReply")
    public ResponseEntity<String> sendReply(
                            @RequestParam("inquiryId") Long inquiryId,
                             @RequestParam("recipientEmail") String recipientEmail,
                             @RequestParam("userName") String userName,
                             @RequestParam("subject") String subject,
                             @RequestParam("message") String message,
                             @RequestParam("replyMessage") String replyMessage) throws MessagingException {

        try {
            // 사용자의 이메일로 문의 답변과 함께 이메일 전송
            inquiryService.sendInquiryReply(userName, recipientEmail, subject, message, replyMessage);

            // 데이터베이스에 문의 답변 저장
            inquiryService.InquiryReplyRegister(replyMessage, inquiryId);

            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failure");
        }
    }
}
