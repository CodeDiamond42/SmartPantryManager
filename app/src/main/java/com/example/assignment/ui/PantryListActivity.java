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
import com.example.assignment.model.PantryItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Home screen: shows every pantry item in a RecyclerView.
 * Tap a row to edit it, tap the bin icon to delete it, tap + to add a new one.
 */
public class PantryListActivity extends AppCompatActivity
        implements PantryAdapter.OnItemActionListener {

    private DatabaseHelper dbHelper;
    private PantryAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_pantry);

        dbHelper = DatabaseHelper.getInstance(this);

        recyclerView = findViewById(R.id.recycler_pantry);
        emptyText = findViewById(R.id.text_empty);

        adapter = new PantryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = findViewById(R.id.fab_add);
        addButton.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
    }

    /**
     * onResume runs every time this screen comes back into view, e.g. after
     * returning from AddEditIngredientActivity, so the list is always up to date.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    // READ: loads items from the database and shows either the list or the empty message.
    private void loadPantryItems() {
        List<PantryItem> items = dbHelper.getAllPantryItems();
        adapter.setItems(items);

        boolean isEmpty = items.isEmpty();
        emptyText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    /** Opens the edit screen, passing the item's id through the Intent. */
    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    // DELETE: asks for confirmation before removing the item.
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
