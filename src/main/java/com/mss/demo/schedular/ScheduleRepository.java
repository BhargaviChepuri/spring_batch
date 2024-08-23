package com.mss.demo.schedular;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByScheduleNameCodeOrderByCreateDateAsc(String scheduleNameCode);
}

