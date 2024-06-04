//package com.busanit.customerService.Notice;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface NoticeRepository extends JpaRepository<Notice, Long> {
//
//    @Query("SELECT n FROM Notice n ORDER BY n.pinned DESC, n.id DESC")
//    Page<Notice> findAllOrderedByPinnedAndId(Pageable pageable);
//
//    @Query("SELECT n FROM Notice n WHERE n.id < :id ORDER BY n.id DESC")
//    Page<Notice> findPreviousNotice(@Param("id") Long id, Pageable pageable);
//
//    @Query("SELECT n FROM Notice n WHERE n.id > :id ORDER BY n.id ASC")
//    Page<Notice> findNextNotice(@Param("id") Long id, Pageable pageable);
//
//}
