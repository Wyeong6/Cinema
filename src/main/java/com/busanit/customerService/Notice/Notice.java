package com.busanit.customerService.Notice;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Size(max = 5000)
    private String content;
    private LocalDate regDate;
    private LocalDate updatedDate;
    @Column(nullable = false, columnDefinition = "Int DEFAULT 0")
    private int viewCount = 0;
    @Column(nullable = true)
    private boolean pinned = false;
}