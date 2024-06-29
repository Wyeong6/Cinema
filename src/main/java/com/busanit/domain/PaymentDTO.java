package com.busanit.domain;

import com.busanit.entity.Payment;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PaymentDTO {
    private Long id;
    private Long member_id;
    private String buyerEmail;
    private String productName;
    private String productIdx;
    private String productType;
    private String content1;
    private String content2;
    private String content3;
    private String content4;
    private String productCount;
    private Integer totalPrice;
    private String paymentType;
    private String merchantUid;
    private String impUid;
    private String applyNum;
    private String paymentStatus;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    public static PaymentDTO toDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .member_id(payment.getMember().getId())
                .buyerEmail(payment.getBuyerEmail())
                .productName(payment.getProductName())
                .productIdx(payment.getProductIdx())
                .paymentType(payment.getPaymentType())
                .content1(payment.getContent1())
                .content2(payment.getContent2())
                .content3(payment.getContent3())
                .content4(payment.getContent4())
                .productCount(payment.getProductCount())
                .totalPrice(payment.getTotalPrice())
                .productType(payment.getProductType())
                .merchantUid(payment.getMerchantUid())
                .impUid(payment.getImpUid())
                .applyNum(payment.getApplyNum())
                .paymentStatus(payment.getPaymentStatus())
                .regDate(payment.getRegDate())
                .updateDate(payment.getUpdateDate())
                .build();
    }
}
