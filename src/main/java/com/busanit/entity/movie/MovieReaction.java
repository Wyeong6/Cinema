package com.busanit.entity.movie;

import com.busanit.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class MovieReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reactionId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Enumerated(EnumType.STRING)
    private ReactionType reactionType;

    public MovieReaction(Member member, Movie movie, ReactionType reactionType) {
        this.member = member;
        this.movie = movie;
        this.reactionType = reactionType;
    }
}
