package com.busanit.service;


import com.busanit.domain.InquiryDTO;
import com.busanit.domain.InquiryReplyDTO;
import com.busanit.domain.NoticeDTO;
import com.busanit.entity.Inquiry;
import com.busanit.entity.InquiryReply;
import com.busanit.entity.Member;
import com.busanit.entity.Notice;
import com.busanit.repository.InquiryReplyRepository;
import com.busanit.repository.InquiryRepository;
import com.busanit.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.messaging.MessagingException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class InquiryService {

    private final MemberRepository memberRepository;
    private final InquiryRepository inquiryRepository;
    private final InquiryReplyRepository inquiryReplyRepository;
    private final JavaMailSender mailSender;

    //properties 에 설정한 환경변수 값을 가져옴
    @Value("${spring.mail.username}")
    private String adminEmail;

    //문의 추가
    public void InquiryRegister(InquiryDTO inquiryDTO) throws MessagingException, jakarta.mail.MessagingException  {

        String email = getAuthenticatedUserEmail();
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        Inquiry inquiry;

        if (optionalMember.isPresent()) { // 회원인 경우
            Member member = optionalMember.get();
            inquiry = Inquiry.toEntity(inquiryDTO);
            inquiry.setMember(member);

        } else {   // 비회원인 경우
            inquiry = Inquiry.toEntity(inquiryDTO);
        }
        inquiryRepository.save(inquiry);

    }

    //문의답변 추가
    public void InquiryReplyRegister(String replyMessage,Long inquiryId) throws MessagingException, jakarta.mail.MessagingException {
        Optional<Inquiry> optionalInquiry = inquiryRepository.findById(inquiryId);
        if (optionalInquiry.isPresent()) {
            Inquiry inquiry = optionalInquiry.get();

            // 문의글의 타입을 "answered"로 변경
            inquiry.markAsAnswered();
            inquiryRepository.save(inquiry);

            // InquiryReply 엔티티 생성
            InquiryReply inquiryReply = InquiryReply.toEntity(replyMessage);
            inquiryReply.setInquiry(inquiry);
            inquiryReplyRepository.save(inquiryReply);

        }
    }

    public InquiryDTO findById(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid inquiry Id:" + id));

        return InquiryDTO.toDTO(inquiry);
    }

    public InquiryReplyDTO findInquiryReplyByInquiryId(Long inquiryId) {
        InquiryReply inquiryReply = inquiryReplyRepository.findByInquiryId(inquiryId);
        return InquiryReplyDTO.toDTO(inquiryReply);
    }

//    //문의리스트 반환
//    public Page<InquiryDTO> getInquiryList(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
//        Page<Inquiry> inquiryPage = inquiryRepository.findAll(pageable);
//        List<InquiryDTO> inquiryDTOList = inquiryPage.getContent().stream()
//                .map(InquiryDTO::toDTO)
//                .collect(Collectors.toList());
//        return new PageImpl<>(inquiryDTOList, pageable, inquiryPage.getTotalElements());
//    }

    public Page<InquiryDTO> getUnansweredInquiryList(int page, int size) {
        return getInquiriesByType("미답변", page, size);
    }

    public Page<InquiryDTO> getAnsweredInquiryList(int page, int size) {
        return getInquiriesByType("답변완료", page, size);
    }


    private Page<InquiryDTO> getInquiriesByType(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Inquiry> inquiryPage = inquiryRepository.findByType(type, pageable);
        List<InquiryDTO> inquiryDTOList = inquiryPage.getContent().stream()
                .map(InquiryDTO::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(inquiryDTOList, pageable, inquiryPage.getTotalElements());
    }




    //문의하기 이메일전송
    public void sendInquiry(String userName, String recipientEmail, String subject, String message) throws MessagingException, jakarta.mail.MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom("qudgns8882@naver.com");
        helper.setTo("qudgns8882@naver.com");
        helper.setSubject(subject);
        // 메일 본문 설정
        String mailContent = "<div style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "<h3>사용자 문의</h3>"
                + "<p>안녕하세요,</p>"
                + "<p>다음과 같은 문의가 접수되었습니다:</p>"
                + "<div style='display: inline-block; max-width: 100%; padding: 10px; border: 1px solid #ddd; background-color: #f9f9f9;'>"
                + "<p><strong>사용자 이름:</strong> " + userName + "</p>"
                + "<p><strong>답변받을 이메일:</strong> " + recipientEmail + "</p>"
                + "<p><strong>문의 내용:</strong></p>"
                + "<p>" + message + "</p>"
                + "</div>"
                + "<p>감사합니다.</p>"
                + "<p>관리자 드림</p>"
                + "</div>";

        helper.setText(mailContent, true);

        mailSender.send(mimeMessage);
    }

    //문의답변 이메일전송
    public void sendInquiryReply(String userName, String recipientEmail, String subject, String message ,String replyMessage) throws MessagingException, jakarta.mail.MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom("qudgns8882@naver.com");
        helper.setTo(recipientEmail);
        helper.setSubject(subject);

        // 메일 본문 설정
        String mailContent = "<div style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "<h3>문의 답변 내용</h3>"
                + "<p>안녕하세요, " + userName + "님.</p>"
                + "<p>고객님의 문의에 대해 아래와 같이 답변드립니다:</p>"
                + "<div style='padding: 10px; border: 1px solid #ddd; background-color: #f9f9f9;'>"
                + "<p><strong>문의 내용:</strong></p>"
                + "<p>" + message + "</p>"
                + "</div>"
                + "<div style='padding: 10px; border: 1px solid #ddd; background-color: #f9f9f9;'>"
                + "<p><strong>답변 내용:</strong></p>"
                + "<p>" + replyMessage + "</p>"
                + "</div>"
                + "<p>추가 질문이 있으시면 언제든지 <a href='http://localhost:8080/inquiry#contact'>여기</a>를 클릭하여 문의해 주세요.</p>"
                + "<p>감사합니다.</p>"
                + "<p>고객 지원팀 드림</p>"
                + "</div>";


        helper.setText(mailContent, true);

        mailSender.send(mimeMessage);
    }

    //로그인한 유저의 이메일을 리턴
    public String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            System.out.println("Authenticated user email: " + authentication.getName());
            return authentication.getName();
        }
        return null;
    }
}
