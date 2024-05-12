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
    private boolean social;
    private Role role;
    private Map<String, Object> attr;   // 소셜 로그인 정보

    public OAuth2MemberDTO(String username, String password, String email,
                           boolean social,
                           Collection<? extends GrantedAuthority> authorities){
        super(username, password, authorities);
        this.id = username;
        this.password = password;
        this.email = email;
        this.social = social;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attr;
    }

    @Override
    public String getName() {
        return this.id;
    }

}
