package com.gameservergroup.gsgcore.utils;

import java.time.Instant;

/*
 * Lucko's TimeUtil
 */

public class TimeUtil {

    public static long now() {
        return Instant.now().toEpochMilli();
    }

    public static long nowUnix() {
        return Instant.now().getEpochSecond();
    }

    public static String toShortForm(long seconds) {
        if (seconds == 0) {
            return "0s";
        }

        long minute = seconds / 60;
        seconds = seconds % 60;
        long hour = minute / 60;
        minute = minute % 60;
        long day = hour / 24;
        hour = hour % 24;

        StringBuilder time = new StringBuilder();
        if (day != 0) {
            time.append(day).append("d ");
        }
        if (hour != 0) {
            time.append(hour).append("h ");
        }
        if (minute != 0) {
            time.append(minute).append("m ");
        }
        if (seconds != 0) {
            time.append(seconds).append("s");
        }

        return time.toString().trim();
    }

    public static String toLongForm(long seconds) {
        if (seconds == 0) {
            return "0 seconds";
        }

        long minute = seconds / 60;
        seconds = seconds % 60;
        long hour = minute / 60;
        minute = minute % 60;
        long day = hour / 24;
        hour = hour % 24;

        StringBuilder time = new StringBuilder();
        if (day != 0) {
            time.append(day);
        }
        if (day == 1) {
            time.append(" day ");
        } else if (day > 1) {
            time.append(" days ");
        }
        if (hour != 0) {
            time.append(hour);
        }
        if (hour == 1) {
            time.append(" hour ");
        } else if (hour > 1) {
            time.append(" hours ");
        }
        if (minute != 0) {
            time.append(minute);
        }
        if (minute == 1) {
            time.append(" minute ");
        } else if (minute > 1) {
            time.append(" minutes ");
        }
        if (seconds != 0) {
            time.append(seconds);
        }
        if (seconds == 1) {
            time.append(" second");
        } else if (seconds > 1) {
            time.append(" seconds");
        }

        return time.toString().trim();
    }
}
