package com.busanit.entity;

import com.busanit.domain.SnackDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "snack")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Snack extends BaseTimeEntity {

    @Id
    @Column(name = "snack_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String snack_nm; // 스낵명

    private String snack_image; // 이미지 이름

    private String snack_alt; // 이미지 설명(alt)

    private Long snack_price; // 가격

    private Long snack_stock; // 수량

    private String snack_set; // 구성품(세트)

    private String snack_detail; // 스낵 상세 설명

    public static Snack toEntity(SnackDTO snackDTO) {
        return Snack.builder()
                .snack_nm(snackDTO.getSnack_nm())
                .snack_image(snackDTO.getSnack_image())
                .snack_alt(snackDTO.getSnack_alt())
                .snack_price(snackDTO.getSnack_price())
                .snack_stock(snackDTO.getSnack_stock())
                .snack_set(snackDTO.getSnack_set())
                .snack_detail(snackDTO.getSnack_detail())
                .build();
    }
}
