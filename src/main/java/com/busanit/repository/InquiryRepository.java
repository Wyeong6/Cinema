package com.busanit.repository;

import com.busanit.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>{

    // 타입별로 문의를 필터링하여 페이지 형식으로 반환하는 메소드
    Page<Inquiry> findByType(String type, Pageable pageable);
}
