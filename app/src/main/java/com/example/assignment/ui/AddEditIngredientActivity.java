package com.example.assignment.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment.R;
import com.example.assignment.adapter.PantryAdapter;
import com.example.assignment.database.DatabaseHelper;
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

    // Key for the item id passed from PantryListActivity.
    public static final String EXTRA_ITEM_ID = "com.example.assignment.EXTRA_ITEM_ID";

    private static final long NO_ID = -1L;
    private static final int MAX_NAME_LENGTH = 50;
    private static final double MAX_QUANTITY = 100000;
    // Letters (any language), spaces, hyphens and apostrophes; must start with a letter
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

        // Fill the unit dropdown from res/values/arrays.xml
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                this, R.array.units, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);

        findViewById(R.id.button_pick_date).setOnClickListener(v -> showDatePicker());
        findViewById(R.id.button_clear_date).setOnClickListener(v -> setExpiryDate(null));
        findViewById(R.id.button_save).setOnClickListener(v -> saveItem());

        setExpiryDate(null);

        // Decide between add mode and edit mode
        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, NO_ID);
        if (itemId != NO_ID) {
            setTitle(R.string.title_edit_ingredient);
            loadExistingItem();
        } else {
            setTitle(R.string.title_add_ingredient);
        }
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

        String[] units = getResources().getStringArray(R.array.units);
        for (int i = 0; i < units.length; i++) {
            if (units[i].equals(item.getUnit())) {
                unitSpinner.setSelection(i);
                break;
            }
        }
        setExpiryDate(item.getExpiryDate());
    }

    // Opens a calendar, starting on the current expiry date if one is set.
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

    //Validates the form, then CREATEs or UPDATEs the item.
    private void saveItem() {
        nameLayout.setError(null);
        quantityLayout.setError(null);

        String name = getText(nameInput);
        // Some keyboards use a comma as the decimal separator
        String quantityText = getText(quantityInput).replace(',', '.');
        boolean isValid = true;

        //Name validation
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

        // Quantity validation
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
            // CREATE
            PantryItem newItem = new PantryItem(name, quantity, unit, selectedExpiryDate);
            if (dbHelper.insertPantryItem(newItem) == -1) {
                Toast.makeText(this, R.string.error_save_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, R.string.item_added, Toast.LENGTH_SHORT).show();
        } else {
            // UPDATE
            PantryItem updatedItem = new PantryItem(itemId, name, null, quantity, unit, selectedExpiryDate);
            if (!dbHelper.updatePantryItem(updatedItem)) {
                Toast.makeText(this, R.string.error_save_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, R.string.item_updated, Toast.LENGTH_SHORT).show();
        }
        finish();   // return to the pantry list, whose onResume() reloads the data
    }

    // Safely reads trimmed text from an input field.
    private String getText(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    // Toolbar back arrow closes this screen.
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
