//package com.busanit.entity;
//
//import com.busanit.constant.Role;
//import com.busanit.domain.MemberRegFormDTO;
//import com.busanit.domain.TermsAgreeDTO;
//import jakarta.persistence.*;
//import lombok.*;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Entity
//@Table(name="termas_agree")
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class TermsAgree extends BaseTimeEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long termsagree_id;
//
//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
//    private Member member_id;
//
////    @ManyToOne
////    @JoinColumn(name = "terms_code", referencedColumnName = "terms_code")
////    private Terms terms_code;
////
////    private Boolean agree_whether;
//
//    /*test값 수정예정*/
//    private Boolean checkedTermsE;
//    /*test값 수정예정*/
//    private Boolean checkedTermsS;
//
//}
