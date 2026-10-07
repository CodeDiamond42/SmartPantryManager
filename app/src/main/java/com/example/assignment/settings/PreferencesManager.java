package com.example.assignment.settings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Saves the user's settings with SharedPreferences, so they're kept after
 * the app is closed. Small key/value settings like these don't need the database.
 */
public class PreferencesManager {

    private static final String PREFS_NAME = "smart_pantry_settings";

    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    private static final String KEY_WARNING_DAYS = "expiry_warning_days";
    private static final String KEY_SORT_BY_EXPIRY = "sort_by_expiry";
    private static final String KEY_SHOW_ALMOST_THERE = "show_almost_there";

    // what the app does before the user changes anything
    private static final boolean DEFAULT_EXPIRY_ALERTS = true;
    private static final int DEFAULT_WARNING_DAYS = 3;
    private static final boolean DEFAULT_SORT_BY_EXPIRY = false;
    private static final boolean DEFAULT_SHOW_ALMOST_THERE = true;

    private final SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isExpiryAlertsEnabled() {
        return prefs.getBoolean(KEY_EXPIRY_ALERTS, DEFAULT_EXPIRY_ALERTS);
    }

    // apply() saves in the background, so it doesn't slow the screen down
    public void setExpiryAlertsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    public int getExpiryWarningDays() {
        return prefs.getInt(KEY_WARNING_DAYS, DEFAULT_WARNING_DAYS);
    }

    public void setExpiryWarningDays(int days) {
        prefs.edit().putInt(KEY_WARNING_DAYS, days).apply();
    }

    public boolean isSortByExpiry() {
        return prefs.getBoolean(KEY_SORT_BY_EXPIRY, DEFAULT_SORT_BY_EXPIRY);
    }

    public void setSortByExpiry(boolean sortByExpiry) {
        prefs.edit().putBoolean(KEY_SORT_BY_EXPIRY, sortByExpiry).apply();
    }

    public boolean isShowAlmostThere() {
        return prefs.getBoolean(KEY_SHOW_ALMOST_THERE, DEFAULT_SHOW_ALMOST_THERE);
    }

    public void setShowAlmostThere(boolean show) {
        prefs.edit().putBoolean(KEY_SHOW_ALMOST_THERE, show).apply();
    }
}