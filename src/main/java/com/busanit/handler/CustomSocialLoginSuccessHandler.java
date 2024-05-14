package com.busanit.handler;

import com.busanit.domain.OAuth2MemberDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2MemberDTO oAuth2MemberDTO = (OAuth2MemberDTO) authentication.getPrincipal();

        // 암호화된 패스워드 값
        String encodePassword = oAuth2MemberDTO.getPassword();

        log.info("@@ encodePassword @@" + encodePassword);

        // 소셜 로그인이고 회원의 패스워드가 1111 이거나 나이가 1이면 비밀번호, 나이 변경 처리
        if(oAuth2MemberDTO.isSocial() && (passwordEncoder.matches("1111",encodePassword) || oAuth2MemberDTO.getAge().equals("1"))){
            log.info("비밀번호 변경해야함");

            response.sendRedirect("/member/modifySocialInfo");
            return;
        } else { // 패스워드가 1111이 아닐때
            response.sendRedirect("/");
        }

    }
}
