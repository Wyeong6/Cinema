package com.busanit.domain;

import com.busanit.domain.movie.MovieDTO;
import com.busanit.entity.Payment;
import com.busanit.entity.SnackPayment;
import com.busanit.service.MovieService;
import com.busanit.service.SnackService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Slice;

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

    private MovieDTO movie;
    private SnackDTO snack; // 스낵 정보 포함

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

    public static PaymentDTO toDTOSnack(Payment payment, SnackService snackService) {
        SnackDTO snackDTO = snackService.findSnackById(Long.valueOf(payment.getProductIdx()));

        return PaymentDTO.builder()
                .id(payment.getId())
                .member_id(payment.getMember().getId())
                .productName(payment.getProductName())
                .productIdx(payment.getProductIdx())
                .paymentType(payment.getPaymentType())
                .productCount(payment.getProductCount())
                .totalPrice(payment.getTotalPrice())
                .productType(payment.getProductType())
                .merchantUid(payment.getMerchantUid())
                .impUid(payment.getImpUid())
                .applyNum(payment.getApplyNum())
                .paymentStatus(payment.getPaymentStatus())
                .regDate(payment.getRegDate())
                .updateDate(payment.getUpdateDate())
                .snack(snackDTO) // SnackDTO 설정
                .build();
    }

    public static PaymentDTO toDTOMovie(Payment payment, MovieService movieService) {
        MovieDTO movieDTO = movieService.findMovieById(Long.valueOf(payment.getProductIdx()));

        return PaymentDTO.builder()
                .id(payment.getId())
                .member_id(payment.getMember().getId())
                .productName(payment.getProductName())
                .content1(payment.getContent1())
                .content2(payment.getContent2())
                .content3(payment.getContent3())
                .content4(payment.getContent4())
                .productIdx(payment.getProductIdx())
                .paymentType(payment.getPaymentType())
                .productCount(payment.getProductCount())
                .totalPrice(payment.getTotalPrice())
                .productType(payment.getProductType())
                .merchantUid(payment.getMerchantUid())
                .impUid(payment.getImpUid())
                .applyNum(payment.getApplyNum())
                .paymentStatus(payment.getPaymentStatus())
                .regDate(payment.getRegDate())
                .updateDate(payment.getUpdateDate())
                .movie(movieDTO) // MovieDTO 설정
                .build();
    }

    // Movie Slice<Entity> -> Slice<DTO> 변환
    public static Slice<PaymentDTO> toDTOMovieSlice(Slice<Payment> paymentSlice, MovieService movieService) {
        return paymentSlice.map(entity -> {
            MovieDTO movieDTO = movieService.findMovieById(Long.valueOf(entity.getProductIdx()));

            return PaymentDTO.builder()
                    .id(entity.getId())
                    .productIdx(entity.getProductIdx())
                    .productName(entity.getProductName())
                    .content1(entity.getContent1())
                    .content2(entity.getContent2())
                    .content3(entity.getContent3())
                    .content4(entity.getContent4())
                    .productCount(entity.getProductCount())
                    .totalPrice(entity.getTotalPrice())
                    .merchantUid(entity.getMerchantUid())
                    .impUid(entity.getImpUid())
                    .regDate(entity.getRegDate())
                    .updateDate(entity.getUpdateDate())
                    .movie(movieDTO) // MovieDTO 설정
                    .build();
        });
    }

    // 스낵 Slice<Entity> -> Slice<DTO> 변환
    public static Slice<PaymentDTO> toDTOSnackSlice(Slice<Payment> paymentSlice, SnackService snackService) {
        return paymentSlice.map(entity -> {
            SnackDTO snackDTO = snackService.findSnackById(Long.valueOf(entity.getProductIdx()));

            return PaymentDTO.builder()
                    .id(entity.getId())
                    .productIdx(entity.getProductIdx())
                    .productName(entity.getProductName())
                    .productCount(entity.getProductCount())
                    .totalPrice(entity.getTotalPrice())
                    .merchantUid(entity.getMerchantUid())
                    .impUid(entity.getImpUid())
                    .regDate(entity.getRegDate())
                    .updateDate(entity.getUpdateDate())
                    .snack(snackDTO) // SnackDTO 설정
                    .build();
        });
    }
}
