package com.busanit.repository;

import com.busanit.entity.Schedule;
import com.busanit.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}