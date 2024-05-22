package com.busanit.entity;

import com.busanit.constant.Role;
import com.busanit.domain.MemberRegFormDTO;
import com.busanit.entity.movie.Comment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity {

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

    private Boolean checkedTermsE;

    private Boolean checkedTermsS;
    //멤버와 댓글 연관관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Comment> comment = new ArrayList<>();

    public void addComment(Comment comment){
        this.comment.add(comment);
        comment.setMember(this);
    }
    //멤버와 이벤트게시글 연관관계
    @ManyToMany
    @JoinTable(
            name = "member_event",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events = new ArrayList<>();

    public void addEvent(Event event) {
        if (events == null) {
            events = new ArrayList<>();
        }
        events.add(event);
    }

    // 일반 폼 회원 생성
    public static Member createMember(MemberRegFormDTO regFormDTO, PasswordEncoder passwordEncoder) {
        String password = passwordEncoder.encode(regFormDTO.getPassword());
        return Member.builder()
                .name(regFormDTO.getName())
                .email(regFormDTO.getEmail())
                .password(password)
                .age(regFormDTO.getAge())
                .role(Role.USER)
                .social(false)
                .grade_code(4)
                .checkedTermsE(regFormDTO.getCheckedTermsE() != null ? regFormDTO.getCheckedTermsE() : true) // 회원가입시 약관에 동의한 것으로 간주함
                .checkedTermsS(regFormDTO.getCheckedTermsS() != null ? regFormDTO.getCheckedTermsS() : false)
                .build();
    }

    // 관리자 생성
    public static Member createMember2(MemberRegFormDTO regFormDTO, PasswordEncoder passwordEncoder) {
        String password = passwordEncoder.encode(regFormDTO.getPassword());
        return Member.builder()
                .name(regFormDTO.getName())
                .email(regFormDTO.getEmail())
                .password(password)
                .age(regFormDTO.getAge())
                .role(Role.ADMIN)
                .social(false)
                .grade_code(1)
                .checkedTermsE(true)
                .checkedTermsS(false)
                .build();
    }

}
