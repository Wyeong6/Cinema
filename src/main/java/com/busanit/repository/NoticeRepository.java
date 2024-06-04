package com.busanit.repository;

import com.busanit.entity.Event;
import com.busanit.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findAll(Pageable pageable);

    //중복체크
    boolean existsByNoticeTitleAndNoticeContent(String noticeTitle, String noticeContent);
}
