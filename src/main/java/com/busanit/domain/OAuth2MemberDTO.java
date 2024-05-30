package com.busanit.domain;

import com.busanit.constant.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
public class OAuth2MemberDTO extends User implements OAuth2User {
    private String id;
    private String email;
    private String password;
    private String age;
    private Integer grade_code;
    private boolean social;
    private Role role;
    private Map<String, Object> attr; // 소셜 로그인 정보
    private Boolean checkedTermsE; // 약관 필수
    private Boolean checkedTermsS; // 약관 선택
    private String authorizedClientRegistrationId; // 클라이언트 등록 ID 추가

    public OAuth2MemberDTO(String username, String password, String email,
                           boolean social, String age,
                           Collection<? extends GrantedAuthority> authorities, boolean checkedTermsE, boolean checkedTermsS){
        super(email, password, authorities);
        this.id = username;
        this.password = password;
        this.email = email;
        this.social = social;
        this.age = age;
        this.checkedTermsE = checkedTermsE;
        this.checkedTermsS = checkedTermsS;
        this.authorizedClientRegistrationId = "social"; // 하드코딩된 기본값 설정
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attr;
    }

    @Override
    public String getName() {
        return this.email;
    }
}
