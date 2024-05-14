package com.busanit.repository;

import com.busanit.entity.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Movie 엔티티 만들어야함. 지금은 임시로 만들어둔것 ( 테이블 제작중!! )
}
