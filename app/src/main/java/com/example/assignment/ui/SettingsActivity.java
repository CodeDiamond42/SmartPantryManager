package com.example.assignment.ui;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment.R;
import com.example.assignment.database.DatabaseHelper;
import com.example.assignment.settings.PreferencesManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;

/**
 * Settings screen. Every change is saved straight away with PreferencesManager,
 * so there's no Save button. The other screens read the settings in onResume().
 */
public class SettingsActivity extends AppCompatActivity {

    private PreferencesManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_settings);

        prefs = new PreferencesManager(this);

        MaterialSwitch expirySwitch = findViewById(R.id.switch_expiry_alerts);
        RadioGroup warningDaysGroup = findViewById(R.id.group_warning_days);
        RadioGroup sortGroup = findViewById(R.id.group_sort);
        MaterialSwitch almostThereSwitch = findViewById(R.id.switch_almost_there);

        // show the saved values first, then attach the listeners,
        // so loading the screen doesn't count as the user changing something
        expirySwitch.setChecked(prefs.isExpiryAlertsEnabled());
        warningDaysGroup.check(radioIdForDays(prefs.getExpiryWarningDays()));
        setGroupEnabled(warningDaysGroup, expirySwitch.isChecked());
        sortGroup.check(prefs.isSortByExpiry() ? R.id.radio_sort_expiry : R.id.radio_sort_name);
        almostThereSwitch.setChecked(prefs.isShowAlmostThere());

        expirySwitch.setOnCheckedChangeListener((button, checked) -> {
            prefs.setExpiryAlertsEnabled(checked);
            setGroupEnabled(warningDaysGroup, checked);   // days only matter when alerts are on
        });
        warningDaysGroup.setOnCheckedChangeListener((group, checkedId) ->
                prefs.setExpiryWarningDays(daysForRadioId(checkedId)));
        sortGroup.setOnCheckedChangeListener((group, checkedId) ->
                prefs.setSortByExpiry(checkedId == R.id.radio_sort_expiry));
        almostThereSwitch.setOnCheckedChangeListener((button, checked) ->
                prefs.setShowAlmostThere(checked));

        findViewById(R.id.button_clear_pantry).setOnClickListener(v -> confirmClearPantry());

        BottomNavHelper.setup(this, findViewById(R.id.bottom_nav), R.id.nav_settings);
    }

    // greys out the day options when expiry alerts are switched off
    private void setGroupEnabled(RadioGroup group, boolean enabled) {
        for (int i = 0; i < group.getChildCount(); i++) {
            group.getChildAt(i).setEnabled(enabled);
        }
    }

    // if/else because R.id values aren't constants in newer Gradle, so switch can't be used
    private int radioIdForDays(int days) {
        if (days == 1) {
            return R.id.radio_days_1;
        } else if (days == 7) {
            return R.id.radio_days_7;
        }
        return R.id.radio_days_3;
    }

    private int daysForRadioId(int radioId) {
        if (radioId == R.id.radio_days_1) {
            return 1;
        } else if (radioId == R.id.radio_days_7) {
            return 7;
        }
        return 3;
    }

    // deleting everything can't be undone, so ask first
    private void confirmClearPantry() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.clear_pantry_title)
                .setMessage(R.string.clear_pantry_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.clear, (dialog, which) -> {
                    int deleted = DatabaseHelper.getInstance(this).deleteAllPantryItems();
                    Toast.makeText(this, getResources().getQuantityString(
                            R.plurals.items_cleared, deleted, deleted), Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}