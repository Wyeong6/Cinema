package com.busanit.repository;

import com.busanit.entity.Event;
import com.busanit.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAll(Pageable pageable);

    boolean existsByEventDetailAndEventName(String eventDetail, String eventName);

    @Query(value = "SELECT * FROM event WHERE id < :id ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Event findPreviousEvent(@Param("id") Long id);

    @Query(value = "SELECT * FROM event WHERE id > :id ORDER BY id ASC LIMIT 1", nativeQuery = true)
    Event findNextEvent(@Param("id") Long id);
}
