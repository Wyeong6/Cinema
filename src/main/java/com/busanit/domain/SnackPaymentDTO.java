package com.busanit.domain;

import com.busanit.entity.SnackPayment;
import com.busanit.service.SnackService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SnackPaymentDTO {
    private Long id;
    private Long member_id;
    private Long snack_id;
    private String content;
    private Long count;
    private Long price;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    private SnackDTO snack; // 스낵 정보 포함

    // Slice<Entity> -> Slice<DTO> 변환
    public static Slice<SnackPaymentDTO> toDTOList(Slice<SnackPayment> snackPaymentList, SnackService snackService) {
        return snackPaymentList.map(entity -> {
            SnackDTO snackDTO = snackService.findSnackById(entity.getSnack_id());

            return SnackPaymentDTO.builder()
                    .id(entity.getId())
                    .snack_id(entity.getSnack_id())
                    .content(entity.getContent())
                    .count(entity.getCount())
                    .price(entity.getPrice())
                    .regDate(entity.getRegDate())
                    .updateDate(entity.getUpdateDate())
                    .snack(snackDTO) // SnackDTO 설정
                    .build();
        });
    }
}
