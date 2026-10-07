package com.example.assignment.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Checks the expiry date maths, using a fixed "today" so the tests always give the same result.
public class ExpiryCheckerTest {

    // pretend today is 10 October 2026 at midday
    private static Date today() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse("2026-10-10 12:00");
    }

    @Test
    public void sameDay_isZero() throws ParseException {
        assertEquals(Integer.valueOf(0), ExpiryChecker.daysUntil("2026-10-10", today()));
    }

    @Test
    public void tomorrow_isOne() throws ParseException {
        assertEquals(Integer.valueOf(1), ExpiryChecker.daysUntil("2026-10-11", today()));
    }

    @Test
    public void yesterday_isMinusOne() throws ParseException {
        assertEquals(Integer.valueOf(-1), ExpiryChecker.daysUntil("2026-10-09", today()));
    }

    @Test
    public void worksAcrossMonths() throws ParseException {
        assertEquals(Integer.valueOf(22), ExpiryChecker.daysUntil("2026-11-01", today()));
    }

    @Test
    public void missingOrInvalidDate_isNull() throws ParseException {
        assertNull(ExpiryChecker.daysUntil(null, today()));
        assertNull(ExpiryChecker.daysUntil("not a date", today()));
        assertNull(ExpiryChecker.daysUntil("2026-13-45", today()));
    }

    @Test
    public void expiringSoon_respectsTheWarningWindow() throws ParseException {
        assertTrue(ExpiryChecker.isExpiringSoon("2026-10-13", today(), 3));    // 3 days away
        assertFalse(ExpiryChecker.isExpiringSoon("2026-10-14", today(), 3));   // 4 days away
        assertTrue(ExpiryChecker.isExpiringSoon("2026-10-01", today(), 3));    // already expired
        assertFalse(ExpiryChecker.isExpiringSoon(null, today(), 3));           // no date
    }
}