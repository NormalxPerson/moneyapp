package model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public enum DateFormatEnum {
    YYYY_MM_DD("yyyy-MM-dd", "yyyy/MM/dd"),
    MM_DD_YYYY("MM/dd/yyyy", "MM-dd-yyyy"),
    DD_MM_YYYY("dd-MM-yyyy", "dd/MM/yyyy");

    private final List<DateTimeFormatter> formatters = new ArrayList<>();

    DateFormatEnum(String... patterns) {
        for ( String pattern : patterns ) {
            formatters.add(DateTimeFormatter.ofPattern(pattern));
        }
    }

    public LocalDate formatDateInput(String dateString) throws DateTimeException {
        try {
            for ( DateTimeFormatter formatter : formatters ) {
                return LocalDate.parse(dateString, formatter);
            }
        } catch (DateTimeException e) {
        }
    throw new DateTimeParseException("Unable to parse date: " + dateString, dateString, 0);
    }

    public static LocalDate parseWithAllFormats(String dateString) {
        for ( DateFormatEnum format : values()) {
            try {
                return format.formatDateInput(dateString);
            } catch (DateTimeParseException e) {
            }
        }
        throw new IllegalArgumentException("Invalid date format: " + dateString);
    }

}
