package com.busanit.domain;

import com.busanit.entity.Snack;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SnackDTO {

    private String snack_nm; // 스낵명

    private String snack_image; // 이미지 이름

    private String snack_alt; // 이미지 설명(alt)

    private Long snack_price; // 가격

    private Long snack_stock; // 수량

    private String snack_set; // 구성품(세트)

    private String snack_detail; // 스낵 상세 설명

}
