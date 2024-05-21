package com.busanit.entity.movie;

import com.busanit.domain.CommentDTO;
import com.busanit.entity.BaseEntity;
import com.busanit.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cno;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private String grade;

    private String memberEmail;

    private Long movieId;

    private Long gpa;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_movie_id")
    private Movie movie;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_member_id")
    private Member member;

    //빌더를 사용하면 코
    public static Comment dtoToEntity(CommentDTO dto) {
        return Comment.builder()
                .cno(dto.getCno())
                .comment(dto.getComment())
                .grade(dto.getGrade())
                .movieId(dto.getMovieId())
                .gpa(dto.getGpa())
                .memberEmail(dto.getMemberEmail())
                .build();
    }
}
