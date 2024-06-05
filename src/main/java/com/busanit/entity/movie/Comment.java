package com.busanit.entity.movie;

import com.busanit.domain.movie.CommentDTO;
import com.busanit.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
    private Double grade;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //빌더를 사용하면 코
    public static Comment dtoToEntity(CommentDTO dto, Movie movie,Member member) {
        return Comment.builder()
                .cno(dto.getCno())
                .comment(dto.getComment())
                .grade(dto.getGrade())
                .member(member)
                .movie(movie)
                .build();
    }
}
