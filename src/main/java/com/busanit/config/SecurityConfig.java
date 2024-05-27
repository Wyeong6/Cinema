package com.busanit.config;

import com.busanit.handler.CustomFormLoginSuccessHandler;
import com.busanit.handler.CustomSocialLoginSuccessHandler;
import com.busanit.service.MemberService;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberService memberService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable()) // csrf 끄기
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.disable()) // cors 끄기
//                .sessionManagement(httpSecuritySessionManagementConfigurer ->
//                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // 세션 생성 정책 설정
//                .sessionManagement(httpSecuritySessionManagementConfigurer ->
//                        httpSecuritySessionManagementConfigurer.sessionFixation().none())
                .authorizeHttpRequests(authorizeHttpRequestConfigurer -> authorizeHttpRequestConfigurer
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .requestMatchers("/assets/**", "/images/**", "/jquery/**").permitAll()
                        .requestMatchers("/", "/member/**", "/**").permitAll() // 모든 사용자에게 허용 (수정예정) /** 부분 수정하기(개발하기 편하려고 다 오픈한거임)
                        .requestMatchers("/admin/**").hasRole("ADMIN") // ADMIN 에게만 허용 (수정예정) admin 전부를 거부하는건 너무 광범위함 보안에 취약해지는듯 관리자 페이지를 전부 구현하면 해당 페이지만 거부해야겠음
                        .anyRequest().authenticated() // 나머지 요청은 인증된 사용자만 접근 가능
                ) // 인증별 권한 설정

                .formLogin(formLoginConfigurer -> formLoginConfigurer
                        .loginPage("/member/login") // login 기본페이지 (주석 처리하면 기본 시큐리티 페이지로 사용 가능)
                        //.loginProcessingUrl("/loginProcess")
                        .usernameParameter("email")
                        .failureUrl("/member/login/error") // 로그인 실패했을때 이동할 url
//                        .defaultSuccessUrl("/main") // 성공했을때 이동할 url
                        .successHandler(authenticationFormLoginSuccessHandler())
                ) // loginForm and login Process setting

                .oauth2Login(OAuth2LoginConfigurer -> OAuth2LoginConfigurer
                        .loginPage("/member/login")
                        .failureUrl("/member/login/error")
//                        .defaultSuccessUrl("/board/list")
                        .successHandler(authenticationSocialLoginSuccessHandler())
                )   // social login setting

                .logout(logoutConfigurer -> logoutConfigurer
                        .logoutUrl("/member/logout")
                        //.logoutUrl("/logout") // 주석 처리하면 기본 시큐리티 페이지로 사용 가능
                        .logoutSuccessUrl("/member/login") // 로그아웃 성공시 이동할 url
                        .invalidateHttpSession(true)    // 로그아웃 이후 세션 전체 삭제 여부
                        .clearAuthentication(true)      // 로그아웃 시 인증정보 삭제 여부
                ) // logout setting

                .build();
    }

    // 패스워드 암호화
    @Bean
    public PasswordEncoder passwordEncoder(){
        // BCryptPasswordEncoder의 해시 함수를 이용해서 비밀번호 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationFormLoginSuccessHandler(){
        return new CustomFormLoginSuccessHandler();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSocialLoginSuccessHandler(){
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }
}
