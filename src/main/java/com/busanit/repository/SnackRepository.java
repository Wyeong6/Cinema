package com.busanit.repository;

import com.busanit.entity.Snack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SnackRepository extends JpaRepository<Snack, Long> {

    // 스낵 추천 리스트(랜덤)
    @Query("SELECT s FROM Snack s ORDER BY function('RAND')")
    Page<Snack> findAllRandom(Pageable pageable);
}
