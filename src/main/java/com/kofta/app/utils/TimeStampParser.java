package com.kofta.app.utils;

import java.time.ZonedDateTime;

public class TimeStampParser {

    public static ZonedDateTime parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isBlank()) {
            return ZonedDateTime.now();
        }
        try {
            return ZonedDateTime.parse(timestamp);
        } catch (Exception e) {
            return ZonedDateTime.now();
        }
    }
}
