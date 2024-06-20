package com.busanit.repository;

import com.busanit.entity.Schedule;
import com.busanit.entity.Seat;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, String> {
}