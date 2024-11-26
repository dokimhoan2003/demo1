package com.example.demo1.components;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeUtils {
    public String getTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());

        long days = duration.toDays();
        if (days >= 7) {
            return createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        if (days > 0) {
            return days == 1 ? "1 day ago" : days + " days ago";
        }

        long hours = duration.toHours();
        if (hours > 0) {
            return hours + " hours ago";
        }

        long minutes = duration.toMinutes();
        if (minutes > 0) {
            return minutes + " minutes ago";
        }

        return "Just now";
    }
}
