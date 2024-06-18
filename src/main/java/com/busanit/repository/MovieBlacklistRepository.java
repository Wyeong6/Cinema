package com.busanit.repository;

import com.busanit.entity.movie.MovieBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieBlacklistRepository extends JpaRepository<MovieBlacklist, Long> {
    
}
