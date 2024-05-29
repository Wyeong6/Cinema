package com.busanit.domain;

import com.busanit.entity.Member;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegFormDTO {

    private Long id;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
//    @Length(min=8, max=16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요.")
    private String password;

//    @Min(6)
//    @Max(6)
    @NotEmpty(message = "필수 입력 값입니다.")
    private String age;

    private boolean social;

    private Integer grade_code;

    private Boolean checkedTermsE;

    private Boolean checkedTermsS;

    public static MemberRegFormDTO toDTO(Member member) {
        return MemberRegFormDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .password(member.getPassword())
                .age(member.getAge())
                .social(member.isSocial())
                .grade_code(member.getGrade_code())
                .checkedTermsE(member.getCheckedTermsE())
                .checkedTermsS(member.getCheckedTermsS())
                .build();
    }
}