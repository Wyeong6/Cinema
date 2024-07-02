package com.busanit.service;

import com.busanit.domain.SnackDTO;
import com.busanit.entity.Snack;
import com.busanit.repository.SnackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    // 스낵 추천 리스트(랜덤)
    public Page<SnackDTO> getSnackListRandom(Pageable pageable) {
        Page<Snack> snackList = snackRepository.findAllRandom(pageable);

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

    // 마이페이지 주문내역조회 리스트 불러오기
    public SnackDTO findSnackById(Long snackId) {
        Snack snack = snackRepository.findById(snackId)
                .orElseThrow(() -> new RuntimeException("Snack not found"));

        return SnackDTO.builder()
                .id(snack.getId())
                .snack_nm(snack.getSnack_nm())
                .snack_image(snack.getSnack_image())
                .snack_alt(snack.getSnack_alt())
                .snack_price(snack.getSnack_price())
                .snack_stock(snack.getSnack_stock())
                .snack_set(snack.getSnack_set())
                .snack_detail(snack.getSnack_detail())
                .regDate(snack.getRegDate())
                .updateDate(snack.getUpdateDate())
                .build();
    }

    // 스낵 저장(관리자 페이지)
    public void saveSnack(Snack snack) {
        snackRepository.save(snack);
    }

    // 스낵 수정(관리자 페이지)
    public void editSnack(SnackDTO snackDTO) {
        snackRepository.save(Snack.toEntity(snackDTO));
    }

    // 스낵 삭제(관리자 페이지)
    public void deleteSnack(long snackItemId) {
        snackRepository.deleteById(snackItemId);
    }
}
