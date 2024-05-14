package com.busanit.entity;

import com.busanit.constant.Role;
import com.busanit.domain.MemberRegFormDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name="member")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity{

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    //로그인 시 아이디로 이메일 사용
    @Column(unique = true)
    private String email;

    private String password;

    private String age;

    //Role 값을 String 값으로 저장
    @Enumerated(EnumType.STRING)
    private Role role;

    // 소셜 로그인 여부
    private boolean social;

    @Column(nullable = false)
    private Integer grade_code;

    // 일반 폼 회원 생성
    public static Member createMember(MemberRegFormDTO regFormDTO, PasswordEncoder passwordEncoder){
        String password = passwordEncoder.encode(regFormDTO.getPassword());
        return Member.builder()
                .name(regFormDTO.getName())
                .email(regFormDTO.getEmail())
                .password(password)
                .age(regFormDTO.getAge())
                .role(Role.USER)
                .social(false)
                .grade_code(4)
                .build();
    }

    // 관리자 생성
    public static Member createMember2(MemberRegFormDTO regFormDTO, PasswordEncoder passwordEncoder){
        String password = passwordEncoder.encode(regFormDTO.getPassword());
        return Member.builder()
                .name(regFormDTO.getName())
                .email(regFormDTO.getEmail())
                .password(password)
                .age(regFormDTO.getAge())
                .role(Role.ADMIN)
                .social(false)
                .grade_code(1)
                .build();
    }

}
