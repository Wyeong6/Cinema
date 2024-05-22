package com.busanit.domain;

import com.busanit.entity.Snack;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SnackDTO {

    private Long id;

    private String snack_nm; // 스낵명

    private String snack_image; // 이미지 이름

    private String snack_alt; // 이미지 설명(alt)

    private Long snack_price; // 가격

    private Long snack_stock; // 수량

    private String snack_set; // 구성품(세트)

    private String snack_detail; // 스낵 상세 설명

    private LocalDateTime regDate;

    private LocalDateTime updateDate;

    public static SnackDTO toDTO(Snack snack) {
        return SnackDTO.builder()
                .id(snack.getId())
                .snack_nm(snack.getSnack_nm())
                .snack_image(snack.getSnack_image())
                .snack_alt(snack.getSnack_alt())
                .snack_price(snack.getSnack_price())
                .snack_stock(snack.getSnack_stock())
                .snack_set(snack.getSnack_set())
                .snack_detail(snack.getSnack_detail())
                .build();
    }

}
