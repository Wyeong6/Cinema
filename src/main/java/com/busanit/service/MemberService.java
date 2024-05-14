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

    public void saveMember(Member member){
        // 회원 중복 체크
        validateDuplicateMember(member);
        memberRepository.save(member);
    }

    // 회원 중복 체크
    private void validateDuplicateMember(Member member){
        Optional<Member> findMember = memberRepository.findByEmail(member.getEmail());

        // isPresent() - Optional 객체가 값을 가지고 있으면 true, 없으면 false 반환
        if(findMember.isPresent()){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> findMember = memberRepository.findByEmail(email);

        if(!findMember.isPresent()){
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

    public void updatePassword(String password, String email){
        memberRepository.updatePassword(password, email);
    }
}
