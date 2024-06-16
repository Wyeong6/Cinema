package com.busanit.service;


import com.busanit.domain.InquiryDTO;
import com.busanit.entity.Inquiry;
import com.busanit.entity.Member;
import com.busanit.repository.InquiryRepository;
import com.busanit.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class InquiryService {

    private final MemberRepository memberRepository;
    private final InquiryRepository inquiryRepository;
    public void register(InquiryDTO inquiryDTO) {

        String email = getAuthenticatedUserEmail();

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if (optionalMember.isPresent()) {
            // 회원인 경우
            Member member = optionalMember.get();

            Inquiry inquiry = Inquiry.builder()
                    .name(inquiryDTO.getName())
                    .email(inquiryDTO.getEmail())
                    .subject(inquiryDTO.getSubject())
                    .message(inquiryDTO.getMessage())
                    .member(member) // 회원과 연결
                    .build();

            inquiryRepository.save(inquiry);
        } else {
            // 비회원인 경우
            Inquiry inquiry = Inquiry.builder()
                    .name(inquiryDTO.getName())
                    .email(inquiryDTO.getEmail())
                    .subject(inquiryDTO.getSubject())
                    .message(inquiryDTO.getMessage())
                    .build();

            inquiryRepository.save(inquiry);
        }
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
