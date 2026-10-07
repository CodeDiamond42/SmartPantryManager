package com.example.assignment.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment.R;
import com.example.assignment.adapter.PantryAdapter;
import com.example.assignment.database.DatabaseHelper;
import com.example.assignment.logic.IngredientNormalizer;
import com.example.assignment.logic.UnitConverter;
import com.example.assignment.model.PantryItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

/**
 * One screen used for both adding and editing a pantry item.
 * If an item id is passed in the Intent, the screen works in edit mode.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    // key for the item id passed from PantryListActivity
    public static final String EXTRA_ITEM_ID = "com.example.assignment.EXTRA_ITEM_ID";

    // key for keeping the chosen expiry date when the screen is rotated
    private static final String STATE_EXPIRY_DATE = "state_expiry_date";

    private static final long NO_ID = -1L;
    private static final int MAX_NAME_LENGTH = 50;
    private static final double MAX_QUANTITY = 100000;
    // letters, spaces, hyphens and apostrophes, must start with a letter
    private static final String NAME_PATTERN = "\\p{L}[\\p{L} '\\-]*";

    private DatabaseHelper dbHelper;
    private TextInputLayout nameLayout;
    private TextInputLayout quantityLayout;
    private TextInputEditText nameInput;
    private TextInputEditText quantityInput;
    private Spinner unitSpinner;
    private TextView expiryText;

    private long itemId = NO_ID;
    private String selectedExpiryDate;   // "yyyy-MM-dd" or null
    private String lastSuggestedUnit;    //only change the unit when the suggestion changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // back arrow
        }

        dbHelper = DatabaseHelper.getInstance(this);

        nameLayout = findViewById(R.id.layout_name);
        quantityLayout = findViewById(R.id.layout_quantity);
        nameInput = findViewById(R.id.input_name);
        quantityInput = findViewById(R.id.input_quantity);
        unitSpinner = findViewById(R.id.spinner_unit);
        expiryText = findViewById(R.id.text_expiry_value);

        // fill the unit dropdown from res/values/arrays.xml
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                this, R.array.units, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);

        findViewById(R.id.button_pick_date).setOnClickListener(v -> showDatePicker());
        findViewById(R.id.button_clear_date).setOnClickListener(v -> setExpiryDate(null));
        findViewById(R.id.button_save).setOnClickListener(v -> saveItem());

        setExpiryDate(null);

        // decide between add mode and edit mode
        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, NO_ID);
        if (itemId != NO_ID) {
            setTitle(R.string.title_edit_ingredient);
            loadExistingItem();
        } else {
            setTitle(R.string.title_add_ingredient);
        }

        // After a rotation, put back the date the user picked. The name, quantity and
        // unit restore themselves, but the date only lives in a variable, so it would be lost.
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_EXPIRY_DATE)) {
            setExpiryDate(savedInstanceState.getString(STATE_EXPIRY_DATE));
        }

        // suggest the right unit as the user types the name
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // only pick the unit for the user when adding, never when editing
                updateUnitSuggestion(itemId == NO_ID);
            }
        });
        updateUnitSuggestion(false);   // show the hint for an existing item without changing its unit
    }

    // saves the chosen expiry date before the screen is destroyed (e.g. on rotation)
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_EXPIRY_DATE, selectedExpiryDate);
    }

    // Edit mode: fills the form with the item's current values.
    private void loadExistingItem() {
        PantryItem item = dbHelper.getPantryItemById(itemId);
        if (item == null) {
            Toast.makeText(this, R.string.item_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        nameInput.setText(item.getName());
        quantityInput.setText(PantryAdapter.formatQuantity(item.getQuantity()));
        selectUnit(item.getUnit());
        setExpiryDate(item.getExpiryDate());
    }

    /**
     * Looks up which unit the recipes use for this ingredient and shows it under the name,
     * e.g. "Recipes measure honey in tbsp...". This stops people adding honey in grams
     * when every recipe measures it by volume (which would never match).
     */
    private void updateUnitSuggestion(boolean autoSelect) {
        String name = getText(nameInput);
        String recipeUnit = name.isEmpty()
                ? null
                : dbHelper.getRecipeUnitFor(IngredientNormalizer.normalize(name));

        if (recipeUnit == null) {
            nameLayout.setHelperText(null);
            lastSuggestedUnit = null;
            return;
        }

        nameLayout.setHelperText(unitHint(name, recipeUnit));
        if (autoSelect && !recipeUnit.equals(lastSuggestedUnit)) {
            selectUnit(recipeUnit);
        }
        lastSuggestedUnit = recipeUnit;
    }

    private String unitHint(String name, String unit) {
        String ingredient = name.toLowerCase(Locale.getDefault());
        UnitConverter.Category category = UnitConverter.getCategory(unit);
        if (category == UnitConverter.Category.VOLUME) {
            return getString(R.string.unit_hint_volume, ingredient, unit);
        } else if (category == UnitConverter.Category.MASS) {
            return getString(R.string.unit_hint_mass, ingredient, unit);
        }
        return getString(R.string.unit_hint_count, ingredient);
    }

    // selects a unit in the dropdown by its text, e.g. "ml"
    private void selectUnit(String unit) {
        String[] units = getResources().getStringArray(R.array.units);
        for (int i = 0; i < units.length; i++) {
            if (units[i].equals(unit)) {
                unitSpinner.setSelection(i);
                return;
            }
        }
    }

    // opens a calendar, starting on the current expiry date if one is set
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedExpiryDate != null) {
            String[] parts = selectedExpiryDate.split("-");
            calendar.set(Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]) - 1,      // Calendar months start at 0
                    Integer.parseInt(parts[2]));
        }
        new DatePickerDialog(this,
                (view, year, month, day) -> setExpiryDate(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void setExpiryDate(String date) {
        selectedExpiryDate = date;
        expiryText.setText(date == null ? getString(R.string.no_expiry_date) : date);
    }

    // checks the form, then creates or updates the item
    private void saveItem() {
        nameLayout.setError(null);
        quantityLayout.setError(null);

        String name = getText(nameInput);
        // some keyboards use a comma as the decimal separator
        String quantityText = getText(quantityInput).replace(',', '.');
        boolean isValid = true;

        // name checks
        if (name.isEmpty()) {
            nameLayout.setError(getString(R.string.error_name_required));
            isValid = false;
        } else if (name.length() > MAX_NAME_LENGTH) {
            nameLayout.setError(getString(R.string.error_name_too_long));
            isValid = false;
        } else if (!name.matches(NAME_PATTERN)) {
            nameLayout.setError(getString(R.string.error_name_invalid));
            isValid = false;
        }

        // quantity checks
        double quantity = 0;
        if (quantityText.isEmpty()) {
            quantityLayout.setError(getString(R.string.error_quantity_required));
            isValid = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityText);
                if (quantity <= 0) {
                    quantityLayout.setError(getString(R.string.error_quantity_positive));
                    isValid = false;
                } else if (quantity > MAX_QUANTITY) {
                    quantityLayout.setError(getString(R.string.error_quantity_too_large));
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                quantityLayout.setError(getString(R.string.error_quantity_invalid));
                isValid = false;
            }
        }

        if (!isValid) {
            return;   // errors are shown under the fields
        }

        String unit = unitSpinner.getSelectedItem().toString();

        if (itemId == NO_ID) {
            PantryItem newItem = new PantryItem(name, quantity, unit, selectedExpiryDate);
            if (dbHelper.insertPantryItem(newItem) == -1) {
                Toast.makeText(this, R.string.error_save_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, R.string.item_added, Toast.LENGTH_SHORT).show();
        } else {
            PantryItem updatedItem = new PantryItem(itemId, name, null, quantity, unit, selectedExpiryDate);
            if (!dbHelper.updatePantryItem(updatedItem)) {
                Toast.makeText(this, R.string.error_save_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, R.string.item_updated, Toast.LENGTH_SHORT).show();
        }
        finish();   // back to the pantry list, whose onResume() reloads the data
    }

    // reads trimmed text from an input field without risking a null
    private String getText(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    // toolbar back arrow closes this screen
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}