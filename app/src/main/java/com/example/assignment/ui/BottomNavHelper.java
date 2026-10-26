package com.example.assignment.ui;

import android.app.Activity;
import android.content.Intent;

import com.example.assignment.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This file sets up the bottom navigation bar the same way on every main screen,
 * so the code isn't copied into each Activity.
 */
public final class BottomNavHelper {

    private BottomNavHelper() {
    }

    public static void setup(Activity activity, BottomNavigationView nav, int currentItemId) {
        // highlight this screen's tab first, before the listener is attached,
        // otherwise setting it would trigger a navigation
        nav.setSelectedItemId(currentItemId);

        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == currentItemId) {
                return true;   // already on this screen
            }

            Class<?> target = targetFor(itemId);
            if (target == null) {
                return false;
            }

            Intent intent = new Intent(activity, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            if (target == PantryListActivity.class) {
                // go back to the existing Pantry screen and close whatever is on top of it
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            } else {
                activity.startActivity(intent);
                // keep the back stack as just [Pantry, current tab]
                if (!(activity instanceof PantryListActivity)) {
                    activity.finish();
                }
            }
            // return false so this screen's tab stays highlighted;
            // the new screen highlights its own tab
            return false;
        });
    }

    // which screen each tab opens (if/else because R.id values aren't constants in newer Gradle)
    private static Class<?> targetFor(int itemId) {
        if (itemId == R.id.nav_pantry) {
            return PantryListActivity.class;
        } else if (itemId == R.id.nav_suggestions) {
            return SuggestedRecipesActivity.class;
        } else if (itemId == R.id.nav_settings) {
            return SettingsActivity.class;
        }
        return null;
    }
}