package com.mss.demo.schedular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/checkMissingIntervals")
    public ResponseEntity<String> checkMissingIntervals(@RequestParam String scheduleNameCode) {
        scheduleService.checkMissingIntervals(scheduleNameCode);
        return ResponseEntity.ok("Check complete");
    }
}

