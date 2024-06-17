package com.busanit.service;


import com.busanit.domain.InquiryDTO;
import com.busanit.domain.NoticeDTO;
import com.busanit.entity.Inquiry;
import com.busanit.entity.Member;
import com.busanit.entity.Notice;
import com.busanit.repository.InquiryRepository;
import com.busanit.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final JavaMailSender mailSender;
    //추가
    public void register(InquiryDTO inquiryDTO) throws MessagingException, jakarta.mail.MessagingException  {

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

    public InquiryDTO findById(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid inquiry Id:" + id));

        return InquiryDTO.toDTO(inquiry);
    }

    //문의리스트 반환
    public Page<InquiryDTO> getInquiryList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Inquiry> inquiryPage = inquiryRepository.findAll(pageable);
        List<InquiryDTO> inquiryDTOList = inquiryPage.getContent().stream()
                .map(InquiryDTO::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(inquiryDTOList, pageable, inquiryPage.getTotalElements());
    }

    //문의 이메일전송
    public void sendInquiryEmail(String name, String userEmailAddress, String subject, String message) throws MessagingException, jakarta.mail.MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom("qudgns8882@naver.com");
        helper.setTo("dnduddl1381@gmail.com");
        helper.setSubject(subject);
        // 메일 본문 설정
        String mailContent = "문의한 유저: " + name + "\n"
                + "답변받을 이메일: " + userEmailAddress + "\n\n"
                + "문의내용:\n" + message;

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
