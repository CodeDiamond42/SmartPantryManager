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
import com.example.assignment.settings.PreferencesManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shows the recipes the user can make right now (strict matching), and below that,
 * in a clearly separate section, recipes that are only missing one ingredient.
 */
public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper dbHelper;
    private PreferencesManager prefs;
    private RecipeAdapter suggestedAdapter;
    private RecipeAdapter almostThereAdapter;
    private RecyclerView suggestedList;
    private TextView emptyText;
    private TextView summaryText;
    private View almostThereSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_suggestions);

        dbHelper = DatabaseHelper.getInstance(this);
        prefs = new PreferencesManager(this);

        suggestedList = findViewById(R.id.recycler_recipes);
        emptyText = findViewById(R.id.text_empty);
        summaryText = findViewById(R.id.text_summary);
        almostThereSection = findViewById(R.id.section_almost_there);
        RecyclerView almostThereList = findViewById(R.id.recycler_almost_there);

        suggestedAdapter = new RecipeAdapter(this);
        suggestedList.setLayoutManager(new LinearLayoutManager(this));
        suggestedList.setAdapter(suggestedAdapter);

        almostThereAdapter = new RecipeAdapter(this);
        almostThereList.setLayoutManager(new LinearLayoutManager(this));
        almostThereList.setAdapter(almostThereAdapter);

        BottomNavHelper.setup(this, findViewById(R.id.bottom_nav), R.id.nav_suggestions);
    }

    // re-run the matching every time the screen is shown,
    // so both lists are always based on the current pantry
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        List<PantryItem> pantry = dbHelper.getAllPantryItems();
        List<Recipe> allRecipes = dbHelper.getAllRecipesWithIngredients();

        showSuggestions(pantry, allRecipes);
        showAlmostThere(pantry, allRecipes);
    }

    // the main list: strict matches only
    private void showSuggestions(List<PantryItem> pantry, List<Recipe> allRecipes) {
        List<Recipe> makeable = RecipeMatcher.findMakeableRecipes(allRecipes, pantry);
        suggestedAdapter.setRecipes(makeable);

        if (makeable.isEmpty()) {
            // different message depending on whether the pantry is empty or just not enough
            emptyText.setText(pantry.isEmpty()
                    ? R.string.empty_suggestions_no_pantry
                    : R.string.empty_suggestions);
            emptyText.setVisibility(View.VISIBLE);
            suggestedList.setVisibility(View.GONE);
            summaryText.setVisibility(View.GONE);
        } else {
            summaryText.setText(getResources().getQuantityString(
                    R.plurals.recipes_you_can_make, makeable.size(), makeable.size()));
            summaryText.setVisibility(View.VISIBLE);
            suggestedList.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }

    // the optional second list: recipes missing exactly one ingredient
    private void showAlmostThere(List<PantryItem> pantry, List<Recipe> allRecipes) {
        if (!prefs.isShowAlmostThere() || pantry.isEmpty()) {
            almostThereSection.setVisibility(View.GONE);
            return;
        }

        List<Recipe> almostThere = RecipeMatcher.findAlmostThereRecipes(allRecipes, pantry);
        Map<Long, String> missingNames = new HashMap<>();
        for (Recipe recipe : almostThere) {
            String missing = RecipeMatcher.getMissingIngredients(recipe, pantry).get(0).getName();
            missingNames.put(recipe.getId(), missing);
        }

        almostThereAdapter.setRecipes(almostThere, missingNames);
        almostThereSection.setVisibility(almostThere.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // pass just the recipe id; the detail screen loads the rest from the database
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}