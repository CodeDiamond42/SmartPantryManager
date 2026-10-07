package com.example.assignment.logic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Works out how many days are left before a pantry item expires.
 * Uses SimpleDateFormat
 */
public final class ExpiryChecker {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final long MILLIS_PER_DAY = 24L * 60 * 60 * 1000;

    private ExpiryChecker() {
    }

    // Days from today until the expiry date: 0 = today, negative = already expired.
    // Returns null if there's no date or it can't be read.
    public static Integer daysUntil(String expiryDate, Date now) {
        if (expiryDate == null) {
            return null;
        }
        // today's date as the user sees it, in their own time zone
        String today = new SimpleDateFormat(DATE_PATTERN, Locale.US).format(now);

        // compare both dates in UTC so daylight saving can't make a day 23 or 25 hours long
        SimpleDateFormat utcFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        utcFormat.setLenient(false);   // reject dates like 2026-13-45
        try {
            long expiryMillis = utcFormat.parse(expiryDate).getTime();
            long todayMillis = utcFormat.parse(today).getTime();
            return (int) ((expiryMillis - todayMillis) / MILLIS_PER_DAY);
        } catch (ParseException e) {
            return null;
        }
    }

    // true if the item is already expired or expires within warningDays
    public static boolean isExpiringSoon(String expiryDate, Date now, int warningDays) {
        Integer days = daysUntil(expiryDate, now);
        return days != null && days <= warningDays;
    }
}