package com.busanit.repository;

import com.busanit.entity.Theater;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TheaterRepository extends JpaRepository<Theater, Long> {

    @NotNull
    Optional<Theater> findById(@NotNull Long id);

    boolean existsByTheaterName(String theaterName);

    boolean existsByTheaterNameEng(String theaterNameEng);

    @Transactional
    @Modifying
    @Query("UPDATE Theater t SET t.theaterCount = t.theaterCount - 1 WHERE t.id = :theaterId")
    void decreaseTheaterCountById(@Param("theaterId") long theaterId);

    @Transactional
    @Query("SELECT t FROM Theater t WHERE t.region = :theaterRegion")
    List<Theater> findTheatersByRegion(@Param("theaterRegion") String theaterRegion);
}