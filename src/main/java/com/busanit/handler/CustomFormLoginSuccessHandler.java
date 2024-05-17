package com.busanit.handler;

import com.busanit.domain.FormMemberDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Set;

@Log4j2
public class CustomFormLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        FormMemberDTO formMemberDTO = (FormMemberDTO) authentication.getPrincipal();
//
//        // 소셜 로그인이 아닐 경우
//        if(!formMemberDTO.isSocial()){
//            log.info("일반 로그인");
//            response.sendRedirect("/");
//        }else {
//            log.info("소설 로그인 (일반 로그인 불가)");
//            response.sendRedirect("/");
//        }

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/adminMain");
        } else {
            response.sendRedirect("/");
        }
    }
}
