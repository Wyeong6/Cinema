package com.busanit.repository;

import com.busanit.entity.Theater;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
    @NotNull
    Optional<Theater> findById(@NotNull Long id);
}