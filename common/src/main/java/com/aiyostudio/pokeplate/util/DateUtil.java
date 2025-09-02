package com.aiyostudio.pokeplate.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtil {

    public static long toEpochMilli(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime toLocalDateTime(long millisecond) {
        return Instant.ofEpochMilli(millisecond).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
