package com.example.assignment.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.R;
import com.example.assignment.adapter.RecipeIngredientAdapter;
import com.example.assignment.database.DatabaseHelper;
import com.example.assignment.logic.RecipeMatcher;
import com.example.assignment.model.Recipe;
import com.example.assignment.model.RecipeIngredient;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Shows one recipe: its name, prep time, whether the pantry covers it, the full
 * ingredient list (ticked or crossed) and the method.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "com.example.assignment.EXTRA_RECIPE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // back arrow
        }
        setTitle(R.string.title_recipe_detail);

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = dbHelper.getRecipeById(recipeId);

        if (recipe == null) {
            Toast.makeText(this, R.string.recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // work out which ingredients (if any) the pantry doesn't cover
        List<RecipeIngredient> missing =
                RecipeMatcher.getMissingIngredients(recipe, dbHelper.getAllPantryItems());
        Set<Long> missingIds = new HashSet<>();
        for (RecipeIngredient ingredient : missing) {
            missingIds.add(ingredient.getId());
        }

        TextView nameText = findViewById(R.id.text_recipe_name);
        TextView prepTimeText = findViewById(R.id.text_prep_time);
        TextView statusText = findViewById(R.id.text_status);
        TextView stepsText = findViewById(R.id.text_steps);
        RecyclerView ingredientList = findViewById(R.id.recycler_ingredients);

        nameText.setText(recipe.getName());
        prepTimeText.setText(getString(R.string.prep_time, recipe.getPrepMinutes()));
        stepsText.setText(recipe.getSteps());
        showStatus(statusText, missing);

        ingredientList.setLayoutManager(new LinearLayoutManager(this));
        // the whole page scrolls, so the list itself doesn't need to
        ingredientList.setNestedScrollingEnabled(false);
        ingredientList.setAdapter(new RecipeIngredientAdapter(recipe.getIngredients(), missingIds));
    }

    // green "you have everything" or red "Missing: ..." line under the recipe name
    private void showStatus(TextView statusText, List<RecipeIngredient> missing) {
        if (missing.isEmpty()) {
            statusText.setText(R.string.status_can_make);
            statusText.setTextColor(ContextCompat.getColor(this, R.color.status_have));
        } else {
            StringBuilder names = new StringBuilder();
            for (RecipeIngredient ingredient : missing) {
                if (names.length() > 0) {
                    names.append(", ");
                }
                names.append(ingredient.getName());
            }
            statusText.setText(getString(R.string.status_missing, names.toString()));
            statusText.setTextColor(ContextCompat.getColor(this, R.color.status_missing));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}