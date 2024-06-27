package com.busanit.service;

import com.busanit.domain.FormMemberDTO;
import com.busanit.domain.MemberRegFormDTO;
import com.busanit.domain.OAuth2MemberDTO;
import com.busanit.domain.PointDTO;
import com.busanit.entity.Member;
import com.busanit.entity.chat.ChatRoom;
import com.busanit.repository.ChatRoomRepository;
import com.busanit.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService { /* UserDetailsService 로그인을 위한 처리 */
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final PointService pointService;
    private final JavaMailSender mailSender;

    public void saveMember(Member member) {
        // 회원 중복 체크
        validateDuplicateMember(member);
        memberRepository.save(member);
    }

    // 회원 중복 체크
    private void validateDuplicateMember(Member member) {
        Optional<Member> findMember = memberRepository.findByEmail(member.getEmail());

        // isPresent() - Optional 객체가 값을 가지고 있으면 true, 없으면 false 반환
        if (findMember.isPresent()) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> findMember = memberRepository.findByEmail(email);

        if (!findMember.isPresent()) {
            throw new UsernameNotFoundException(email);
        }

        FormMemberDTO dto = new FormMemberDTO(
                findMember.get().getEmail(),
                findMember.get().getPassword(),
                findMember.get().isSocial(),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_" + findMember.get().getRole()))
        );

        return dto;
    }

    public OAuth2User loadOAuth2UserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> findMember = memberRepository.findByEmail(email);

        if (!findMember.isPresent()) {
            throw new UsernameNotFoundException(email);
        }

        OAuth2MemberDTO dto = new OAuth2MemberDTO(
                findMember.get().getName(),
                findMember.get().getPassword(),
                findMember.get().getEmail(),
                findMember.get().isSocial(),
                findMember.get().getAge(),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_" + findMember.get().getRole())),
                findMember.get().getCheckedTermsE(),
                findMember.get().getCheckedTermsS()
        );

        return dto;
    }

    // 사용자 email 로 member_id 찾기
    public Long findUserIdx(String email) { return memberRepository.findUserIdx(email); }

    // 아이디(이메일) 찾기
    public List<String> findUserEmails(String name, String age) {
        return memberRepository.findUserEmails(name, age);
    }
//    public String findUserEmail(String name, String age) {
//        return memberRepository.findUserEmail(name, age);
//    }

    // 비밀번호 찾기
    public boolean findUserPassword(String name, String age, String email) {
        Long count = memberRepository.findUserPassword(name, age, email);
        return count > 0; // count가 0보다 크면 true, 그렇지 않으면 false 반환
    }

    // (비밀번호 변경시) 기존 비밀번호 확인용
    public String passwordCheck(String email) {
        String passwordCheck = memberRepository.findByPassword(email);
        return passwordCheck;
    }

    // 비밀번호 수정
    public void updatePassword(String password, String email) {
        memberRepository.updatePassword(password, email);
    }

    // mypage 개인정보수정 info 가져오기(form + social 회원)
    public MemberRegFormDTO getFormMemberInfo(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NullPointerException("formMember null"));
        return MemberRegFormDTO.toDTO(member);
    }

    // mypage 개인정보수정
    public void editMemberInfo(MemberRegFormDTO memberRegFormDTO){
        memberRepository.save(Member.toEntity(memberRegFormDTO));
    }

    // mypage 회원탈퇴
    public void memberDelete(Long memberId) {

        memberRepository.deleteById(memberId);

    }

    // 개인정보(이메일(단수)) masking
    public String maskingEmail(String email) {
        if (email == null) return null;
        if (email.length() <= 3) return email; // 길이가 3 이하인 경우 그대로 반환
        String maskedPart = email.substring(3).replaceAll(".", "*"); // 앞 3자리를 제외한 나머지를 '*'로 마스킹
        return email.substring(0, 3) + maskedPart;
    }

    // 개인정보(이메일(복수)) masking
    public List<String> maskingEmails(List<String> emails) {
        if (emails == null) return null;

        return emails.stream()
                .map(email -> {
                    if (email == null) return null;
                    if (email.length() <= 3) return email; // 길이가 3 이하인 경우 그대로 반환
                    String maskedPart = email.substring(3).replaceAll(".", "*"); // 앞 3자리를 제외한 나머지를 '*'로 마스킹
                    return email.substring(0, 3) + maskedPart;
                })
                .collect(Collectors.toList());
    }

    // 모든 회원정보 가져오기 ( admin 페이지에서 뿌려주기위함 )
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    // 현재 로그인한 사용자의 이메일
    public String currentLoggedInEmail() {
        String userEmail = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            userEmail = authentication.getName(); // 현재 로그인한 사용자의 이메일
        }
        return userEmail;
    }

    // 현재 로그인한 사용자의 등급 확인(+저장)
    public long userGrade() {
        long userGradeCount = pointService.getPointMovieCount(findUserIdx(currentLoggedInEmail()));
        long userGrade;
        if(userGradeCount >= 10) {
            userGrade = 1;
        } else if(userGradeCount >= 5) {
            userGrade = 2;
        } else if(userGradeCount >= 3) {
            userGrade = 3;
        } else {
            userGrade = 4;
        }
        updateGrade(userGrade, currentLoggedInEmail());
        return userGrade;
    }

    // 멤버십 등급 수정
    public void updateGrade(long userEditGrade, String email) {
        memberRepository.updateGrade(userEditGrade, email);
    }

    // 멤버에게 메일 보내기
    public void sendEmailToMember(String toEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(System.getenv("adminEmail")); // 환경 변수에서 발신자 이메일 가져오기

        mailSender.send(message);
    }
}
