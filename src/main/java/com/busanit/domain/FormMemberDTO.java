package com.busanit.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
public class FormMemberDTO extends User implements UserDetails {

    private String email;
    private String name;
    private boolean social;

    public FormMemberDTO(String username, String password, boolean social,
                         Collection<? extends GrantedAuthority> authorities){
        super(username, password, authorities);
        this.email = username;
        this.social = social;
    }
}
