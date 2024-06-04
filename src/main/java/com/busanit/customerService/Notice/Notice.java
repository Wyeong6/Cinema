//package com.busanit.customerService.Notice;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.Size;
//import lombok.*;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.LastModifiedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Entity
//@Setter
//@Getter
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@EntityListeners(value = {AuditingEntityListener.class})
//public class Notice {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String title;
//    @Size(max = 5000)
//    private String content;
//    @CreatedDate
//    @Column(updatable = false)
//    private LocalDate regDate;
//    @LastModifiedDate
//    private LocalDate updatedDate;
//    @Column(nullable = false, columnDefinition = "Int DEFAULT 0")
//    private int viewCount = 0;
//    @Column(nullable = true)
//    private boolean pinned = false;
//}