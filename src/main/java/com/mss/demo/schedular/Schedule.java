package com.mss.demo.schedular;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "schedule1")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String scheduleNameCode;
    private String schedulePattern;
    private String createDate;
    private String scheduleStatus;

    
    // Getters and Setters
}

