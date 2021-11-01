package klykov.aids;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class Time {
    private final static LocalDateTime ldt = LocalDateTime.now(ZoneId.of("Europe/Moscow"));

    public static String get() {
        return ldt.format(DateTimeFormatter.ofPattern("d.MM.yyyy HH:mm:ss"));
    }
}
