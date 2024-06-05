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

    private Boolean contentType; // 멤버등급에 필요 - 영화면 true, 나머지 false

    private String pointType; // + or -

    private Integer points; // 해당 포인트

    private Integer currentPoints; // 합계

    private Integer totalPoints; // 누적

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 신규 가입시 적립
    public static Point createPoint(Long memberId) {
        Member member = Member.builder().id(memberId).build();
        return Point.builder()
                .content("신규 가입 적립")
                .contentType(false)
                .pointType("+")
                .points(3000)
                .currentPoints(3000)
                .totalPoints(3000)
                .member(member)
                .build();
    }
}
