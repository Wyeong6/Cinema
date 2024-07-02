//package com.busanit.repository;
//
//import com.busanit.entity.SnackPayment;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface SnackPaymentRepository extends JpaRepository<SnackPayment, Long> {
//
//    Slice<SnackPayment> findByMember_Id(Long member_id, Pageable pageable);
//}
