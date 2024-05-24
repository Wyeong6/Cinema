package com.busanit.repository;

import com.busanit.entity.Snack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SnackRepository extends JpaRepository<Snack, Long> {

    // 스낵 추천 리스트(랜덤) - 중복 제거 (DISTINCT 는 데이터가 많을 때 쓰면 성능에 영향을 줄 수 있음)
    @Query("SELECT DISTINCT s FROM Snack s ORDER BY function('RAND')")
    Page<Snack> findAllRandom(Pageable pageable);

    // 스낵 추천 리스트(랜덤) - 중복 포함
//    @Query("SELECT s FROM Snack s ORDER BY function('RAND')")
//    Page<Snack> findAllRandom(Pageable pageable);
}
