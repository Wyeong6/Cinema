package com.busanit.entity;

import com.busanit.constant.Role;
import com.busanit.domain.MemberRegFormDTO;
import com.busanit.domain.PaymentDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String merchantUid;
    private String impUid;
    private String applyNum; // 카드 승인 번호
    private String buyerEmail; // 결제사에서 받아오는 메일
    private String paymentType;
    private String paymentStatus; // 결제사에서 값을 못 받아오는 것 같음

    private String productName;
    private String productIdx;
    private String productType; // 영화 or 스낵
    private String content1; // 상영일자
    private String content2; // 시간
    private String content3; // 상영관
    private String content4; // 좌석
    private String productCount; // 개수 (영화 예시: 성인:1 청소년:1 경로:0)
    private Integer totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // DTO -> Entity
    public static Payment toEntity(PaymentDTO paymentDTO, Long memberId){
        Member member = Member.builder().id(memberId).build();
        return Payment.builder()
                .id(paymentDTO.getId())
                .buyerEmail(paymentDTO.getBuyerEmail())
                .productName(paymentDTO.getProductName())
                .productIdx(paymentDTO.getProductIdx())
                .paymentType(paymentDTO.getPaymentType())
                .content1(paymentDTO.getContent1())
                .content2(paymentDTO.getContent2())
                .content3(paymentDTO.getContent3())
                .content4(paymentDTO.getContent4())
                .productCount(paymentDTO.getProductCount())
                .totalPrice(paymentDTO.getTotalPrice())
                .productType(paymentDTO.getProductType())
                .merchantUid(paymentDTO.getMerchantUid())
                .impUid(paymentDTO.getImpUid())
                .applyNum(paymentDTO.getApplyNum())
                .paymentStatus(paymentDTO.getPaymentStatus())
                .member(member)
                .build();
    }
}
