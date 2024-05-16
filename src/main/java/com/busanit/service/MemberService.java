package com.busanit.service;

import com.busanit.domain.FormMemberDTO;
import com.busanit.entity.Member;
import com.busanit.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService { /* UserDetailsService 로그인을 위한 처리 */
    private final MemberRepository memberRepository;

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

    // 아이디(이메일) 찾기
    public String findUserEmail(String name, String age) {
        return memberRepository.findUserEmail(name, age);
    }

    // 비밀번호 찾기
    public boolean findUserPassword(String name, String age, String email) {
        Long count = memberRepository.findUserPassword(name, age, email);
        return count > 0; // count가 0보다 크면 true, 그렇지 않으면 false 반환
    }

    // 비밀번호 수정
    public void updatePassword(String password, String email) {
        memberRepository.updatePassword(password, email);
    }

    // 개인정보(이메일) masking
    public String maskingEmail(String email) {
        if (email == null) return null;
        if (email.length() <= 3) return email; // 길이가 3 이하인 경우 그대로 반환
        String maskedPart = email.substring(3).replaceAll(".", "*"); // 앞 3자리를 제외한 나머지를 '*'로 마스킹
        return email.substring(0, 3) + maskedPart;
    }

}
