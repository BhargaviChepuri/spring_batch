package com.mss.demo.schedular;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public void checkMissingIntervals(String scheduleNameCode) {
        List<Schedule> schedules = scheduleRepository.findByScheduleNameCodeOrderByCreateDateAsc(scheduleNameCode);

        if (schedules.isEmpty()) {
            System.out.println("No schedules found for the specified schedule name code.");
            return;
        }

        // Extract schedule pattern
        String schedulePattern = schedules.get(0).getSchedulePattern();
        ScheduleType scheduleType = determineScheduleType(schedulePattern);
        
        List<LocalDateTime> missingTimestamps = new ArrayList<>();
        
        switch (scheduleType) {
            case DAILY_BY_MINUTE:
                List<Integer> minuteList = extractMinuteList(schedulePattern);
                missingTimestamps = findMissingTimestampsDailyByMinute(schedules, minuteList);
                break;
            case MINUTELY:
                Duration minutelyInterval = extractIntervalDuration(schedulePattern);
                missingTimestamps = findMissingTimestampsMinutely(schedules, minutelyInterval);
                break;
            case HOURLY:
                Duration hourlyInterval = extractIntervalDuration(schedulePattern);
                missingTimestamps = findMissingTimestampsHourly(schedules, hourlyInterval);
                break;
            case MONTHLY:
                int dayOfMonth = extractDayOfMonth(schedulePattern);
                missingTimestamps = findMissingTimestampsMonthly(schedules, dayOfMonth);
                break;
            default:
                throw new IllegalArgumentException("Unsupported schedule pattern: " + schedulePattern);
        }
        
        // Compare missing timestamps with existing records
        compareWithExistingRecords(schedules, missingTimestamps);
    }

    private ScheduleType determineScheduleType(String schedulePattern) {
        if (schedulePattern != null) {
            if (schedulePattern.contains("BYMINUTE=")) {
                return ScheduleType.DAILY_BY_MINUTE;
            } else if (schedulePattern.contains("INTERVAL=")) {
                if (schedulePattern.contains("FREQ=MINUTELY")) {
                    return ScheduleType.MINUTELY;
                } else if (schedulePattern.contains("FREQ=HOURLY")) {
                    return ScheduleType.HOURLY;
                }
            } else if (schedulePattern.contains("FREQ=MONTHLY")) {
                return ScheduleType.MONTHLY;
            }
        }
        throw new IllegalArgumentException("Invalid schedule pattern: " + schedulePattern);
    }

    private List<Integer> extractMinuteList(String schedulePattern) {
        if (schedulePattern != null && schedulePattern.contains("BYMINUTE=")) {
            String[] parts = schedulePattern.split(";");
            for (String part : parts) {
                if (part.startsWith("BYMINUTE=")) {
                    String minuteValues = part.replace("BYMINUTE=", "");
                    return Arrays.stream(minuteValues.split(","))
                                 .map(Integer::parseInt)
                                 .collect(Collectors.toList());
                }
            }
        }
        throw new IllegalArgumentException("Invalid schedule pattern: " + schedulePattern);
    }

    private Duration extractIntervalDuration(String schedulePattern) {
        if (schedulePattern != null && schedulePattern.contains("INTERVAL=")) {
            String[] parts = schedulePattern.split(";");
            for (String part : parts) {
                if (part.startsWith("INTERVAL=")) {
                    int interval = Integer.parseInt(part.replace("INTERVAL=", ""));
                    return Duration.ofMinutes(interval);
                }
            }
        }
        throw new IllegalArgumentException("Invalid schedule pattern: " + schedulePattern);
    }

    private int extractDayOfMonth(String schedulePattern) {
        if (schedulePattern != null && schedulePattern.contains("BYDAY=")) {
            String[] parts = schedulePattern.split(";");
            for (String part : parts) {
                if (part.startsWith("BYDAY=")) {
                    return Integer.parseInt(part.replace("BYDAY=", ""));
                }
            }
        }
        throw new IllegalArgumentException("Invalid schedule pattern: " + schedulePattern);
    }

    private List<LocalDateTime> findMissingTimestampsDailyByMinute(List<Schedule> schedules, List<Integer> minuteList) {
        List<LocalDateTime> missingTimestamps = new ArrayList<>();
        LocalDateTime previousTime = null;

        for (Schedule schedule : schedules) {
            LocalDateTime currentTime = LocalDateTime.parse(schedule.getCreateDate(), DATE_TIME_FORMATTER).withSecond(0).withNano(0);

            if (previousTime != null) {
                LocalDateTime expectedTime = calculateNextExpectedTime(previousTime, minuteList);
                while (currentTime.isAfter(expectedTime)) {
                    missingTimestamps.add(expectedTime);
                    expectedTime = calculateNextExpectedTime(expectedTime, minuteList);
                }
            }

            previousTime = currentTime;
        }
        return missingTimestamps;
    }

    private List<LocalDateTime> findMissingTimestampsMinutely(List<Schedule> schedules, Duration intervalDuration) {
        List<LocalDateTime> missingTimestamps = new ArrayList<>();
        LocalDateTime previousTime = null;

        for (Schedule schedule : schedules) {
            LocalDateTime currentTime = LocalDateTime.parse(schedule.getCreateDate(), DATE_TIME_FORMATTER).withSecond(0).withNano(0);

            if (previousTime != null) {
                LocalDateTime expectedTime = previousTime.plus(intervalDuration);
                while (currentTime.isAfter(expectedTime)) {
                    missingTimestamps.add(expectedTime);
                    expectedTime = expectedTime.plus(intervalDuration);
                }
            }

            previousTime = currentTime;
        }
        return missingTimestamps;
    }

    private List<LocalDateTime> findMissingTimestampsHourly(List<Schedule> schedules, Duration intervalDuration) {
        List<LocalDateTime> missingTimestamps = new ArrayList<>();
        LocalDateTime previousTime = null;

        for (Schedule schedule : schedules) {
            LocalDateTime currentTime = LocalDateTime.parse(schedule.getCreateDate(), DATE_TIME_FORMATTER).withSecond(0).withNano(0);

            if (previousTime != null) {
                LocalDateTime expectedTime = previousTime.plus(intervalDuration);
                while (currentTime.isAfter(expectedTime)) {
                    missingTimestamps.add(expectedTime);
                    expectedTime = expectedTime.plus(intervalDuration);
                }
            }

            previousTime = currentTime;
        }
        return missingTimestamps;
    }

    private List<LocalDateTime> findMissingTimestampsMonthly(List<Schedule> schedules, int dayOfMonth) {
        List<LocalDateTime> missingTimestamps = new ArrayList<>();
        LocalDateTime previousTime = null;

        for (Schedule schedule : schedules) {
            LocalDateTime currentTime = LocalDateTime.parse(schedule.getCreateDate(), DATE_TIME_FORMATTER).withSecond(0).withNano(0);

            if (previousTime != null) {
                LocalDateTime expectedTime = calculateNextMonthlyExpectedTime(previousTime, dayOfMonth);
                while (currentTime.isAfter(expectedTime)) {
                    missingTimestamps.add(expectedTime);
                    expectedTime = calculateNextMonthlyExpectedTime(expectedTime, dayOfMonth);
                }
            }

            previousTime = currentTime;
        }
        return missingTimestamps;
    }

    private LocalDateTime calculateNextExpectedTime(LocalDateTime previousTime, List<Integer> minuteList) {
        int currentMinute = previousTime.getMinute();
        for (int minute : minuteList) {
            if (minute > currentMinute) {
                return previousTime.withMinute(minute).withSecond(0).withNano(0);
            }
        }
        // If no minute is greater, move to the next hour and use the first minute in the list
        return previousTime.plusHours(1).withMinute(minuteList.get(0)).withSecond(0).withNano(0);
    }

    private LocalDateTime calculateNextMonthlyExpectedTime(LocalDateTime previousTime, int dayOfMonth) {
        if (previousTime.getDayOfMonth() < dayOfMonth) {
            return previousTime.withDayOfMonth(dayOfMonth).withHour(0).withMinute(0).withSecond(0).withNano(0);
        } else {
            return previousTime.plusMonths(1).withDayOfMonth(dayOfMonth).withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
    }

    private void compareWithExistingRecords(List<Schedule> schedules, List<LocalDateTime> missingTimestamps) {
        Set<LocalDateTime> existingTimestamps = schedules.stream()
            .map(schedule -> LocalDateTime.parse(schedule.getCreateDate(), DATE_TIME_FORMATTER).withSecond(0).withNano(0))
            .collect(Collectors.toSet());

        for (LocalDateTime missingTimestamp : missingTimestamps) {
            if (!existingTimestamps.contains(missingTimestamp)) {
                System.out.println("Missing Timestamp: " + missingTimestamp.format(DATE_TIME_FORMATTER));
            }
        }
    }

    private enum ScheduleType {
        DAILY_BY_MINUTE,
        MINUTELY,
        HOURLY,
        MONTHLY
    }
}
