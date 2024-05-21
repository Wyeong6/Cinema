package com.busanit.service;

import com.busanit.domain.SnackDTO;
import com.busanit.entity.Snack;
import com.busanit.repository.SnackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SnackService {
    private final SnackRepository snackRepository;

    // 리스트 불러오기
    public Page<SnackDTO> getSnackList(Pageable pageable) {
        Page<Snack> snackList = snackRepository.findAll(pageable);

        // Page<Entity> -> Page<Dto> 변환
        return snackList.map(entity -> SnackDTO.builder()
                .id(entity.getId())
                .snack_nm(entity.getSnack_nm())
                .snack_image(entity.getSnack_image())
                .snack_alt(entity.getSnack_alt())
                .snack_price(entity.getSnack_price())
                .snack_stock(entity.getSnack_stock())
                .snack_set(entity.getSnack_set())
                .snack_detail(entity.getSnack_detail())
                .regDate(entity.getRegDate())
                .updateDate(entity.getUpdateDate())
                .build());
    }

    public SnackDTO get(Long id) {

        Snack snack = snackRepository.findById(id).orElseThrow(() -> new NullPointerException("snack null"));
        return SnackDTO.toDTO(snack);
    }

    // 스낵 저장(관리자 페이지)
    public void saveSnack(Snack snack) {
        snackRepository.save(snack);
    }
}
