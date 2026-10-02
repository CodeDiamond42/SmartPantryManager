package com.example.assignment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.R;
import com.example.assignment.adapter.RecipeAdapter;
import com.example.assignment.database.DatabaseHelper;
import com.example.assignment.logic.RecipeMatcher;
import com.example.assignment.model.PantryItem;
import com.example.assignment.model.Recipe;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

/**
 * Shows only the recipes the user can make right now, using the strict
 * matching rule in RecipeMatcher. Tapping a recipe opens its details.
 */
public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper dbHelper;
    private RecipeAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyText;
    private TextView summaryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_suggestions);

        dbHelper = DatabaseHelper.getInstance(this);

        recyclerView = findViewById(R.id.recycler_recipes);
        emptyText = findViewById(R.id.text_empty);
        summaryText = findViewById(R.id.text_summary);

        adapter = new RecipeAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        BottomNavHelper.setup(this, findViewById(R.id.bottom_nav), R.id.nav_suggestions);
    }

    // re-run the matching every time the screen is shown,
    // so the list is always based on the current pantry
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        List<PantryItem> pantry = dbHelper.getAllPantryItems();
        List<Recipe> allRecipes = dbHelper.getAllRecipesWithIngredients();
        List<Recipe> makeable = RecipeMatcher.findMakeableRecipes(allRecipes, pantry);

        adapter.setRecipes(makeable);

        if (makeable.isEmpty()) {
            // different message depending on whether the pantry is empty or just not enough
            emptyText.setText(pantry.isEmpty()
                    ? R.string.empty_suggestions_no_pantry
                    : R.string.empty_suggestions);
            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            summaryText.setVisibility(View.GONE);
        } else {
            summaryText.setText(getResources().getQuantityString(
                    R.plurals.recipes_you_can_make, makeable.size(), makeable.size()));
            summaryText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }

    // pass just the recipe id; the detail screen loads the rest from the database
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}