package com.busanit.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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
}
