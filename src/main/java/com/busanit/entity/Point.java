package com.busanit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Point extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String pointType;

    private Integer points;

    private Integer currentPoints;

    private Integer totalPoints;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 신규 가입시 적립
    public static Point createPoint(Long memberId) {
        Member member = Member.builder().id(memberId).build();
        return Point.builder()
                .content("신규 가입 적립")
                .pointType("+")
                .points(3000)
                .currentPoints(3000)
                .totalPoints(3000)
                .member(member)
                .build();
    }
}
