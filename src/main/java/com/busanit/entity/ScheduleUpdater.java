package com.busanit.entity;

import com.busanit.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleUpdater {

    private final ScheduleRepository scheduleRepository;

    public ScheduleUpdater(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Scheduled(cron = "0 0 0 1 * *")
//    @Scheduled(cron = "0/1 * * * * *")
    @Transactional
    public void updateScheduleStatus() {
        List<Schedule> schedules = scheduleRepository.findAll();
        schedules.forEach(Schedule::updateStatus);
        scheduleRepository.saveAll(schedules); // 업데이트된 상태를 저장
    }
}

