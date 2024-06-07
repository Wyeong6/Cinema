package com.busanit.repository;

import com.busanit.entity.Theater;
import com.busanit.entity.TheaterNumber;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TheaterNumberRepository extends JpaRepository<TheaterNumber, Long> {
    @NotNull
    Optional<TheaterNumber> findById(@NotNull Long id);

    @NotNull
    List<TheaterNumber> findByTheaterId(@NotNull Number theaterId);
}