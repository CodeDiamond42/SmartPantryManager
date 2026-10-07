package com.example.assignment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.R;
import com.example.assignment.adapter.PantryAdapter;
import com.example.assignment.database.DatabaseHelper;
import com.example.assignment.logic.ExpiryChecker;
import com.example.assignment.model.PantryItem;
import com.example.assignment.settings.PreferencesManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;

/**
 * Home screen: shows every pantry item in a RecyclerView.
 * Tap a row to edit it, tap the bin icon to delete it, tap + to add a new one.
 * If expiry alerts are on, a banner at the top counts items expiring soon.
 */
public class PantryListActivity extends AppCompatActivity
        implements PantryAdapter.OnItemActionListener {

    private DatabaseHelper dbHelper;
    private PreferencesManager prefs;
    private PantryAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyText;
    private TextView expiryBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_pantry);

        dbHelper = DatabaseHelper.getInstance(this);
        prefs = new PreferencesManager(this);

        recyclerView = findViewById(R.id.recycler_pantry);
        emptyText = findViewById(R.id.text_empty);
        expiryBanner = findViewById(R.id.text_expiry_banner);

        adapter = new PantryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = findViewById(R.id.fab_add);
        addButton.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));

        // set up the bottom navigation bar with the Pantry tab selected
        BottomNavHelper.setup(this, findViewById(R.id.bottom_nav), R.id.nav_pantry);
    }

    // onResume runs every time this screen comes back into view (after adding/editing,
    // or after changing settings), so the list and settings are always up to date
    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {
        boolean alertsOn = prefs.isExpiryAlertsEnabled();
        int warningDays = prefs.getExpiryWarningDays();

        List<PantryItem> items = dbHelper.getAllPantryItems(prefs.isSortByExpiry());
        adapter.setExpiryWarning(alertsOn, warningDays);
        adapter.setItems(items);

        boolean isEmpty = items.isEmpty();
        emptyText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        updateExpiryBanner(items, alertsOn, warningDays);
    }

    // "2 items are expired or expiring soon" at the top, only when alerts are on
    private void updateExpiryBanner(List<PantryItem> items, boolean alertsOn, int warningDays) {
        int count = 0;
        if (alertsOn) {
            Date now = new Date();
            for (PantryItem item : items) {
                if (ExpiryChecker.isExpiringSoon(item.getExpiryDate(), now, warningDays)) {
                    count++;
                }
            }
        }
        if (count > 0) {
            expiryBanner.setText(getResources().getQuantityString(R.plurals.expiring_banner, count, count));
            expiryBanner.setVisibility(View.VISIBLE);
        } else {
            expiryBanner.setVisibility(View.GONE);
        }
    }

    // opens the edit screen, passing the item's id through the Intent
    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    // asks before deleting, because it can't be undone
    @Override
    public void onDeleteClick(PantryItem item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, item.getName()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    if (dbHelper.deletePantryItem(item.getId())) {
                        Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
                        loadPantryItems();
                    }
                })
                .show();
    }
}