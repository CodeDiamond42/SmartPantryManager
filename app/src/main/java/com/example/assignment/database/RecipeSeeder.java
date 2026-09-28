package com.example.assignment.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.assignment.logic.IngredientNormalizer;
import com.example.assignment.model.Recipe;
import com.example.assignment.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Inserts the built-in recipe collection. Called once from DatabaseHelper.onCreate().
 *
 * Units must use the same values as R.array.units (g, kg, ml, L, pcs, tsp, tbsp, cup)
 * so the matching logic can compare them with pantry items.
 */
final class RecipeSeeder {

    private RecipeSeeder() {
        // Utility class - no instances needed
    }

    static void seed(SQLiteDatabase db) {
        for (Recipe recipe : buildRecipes()) {
            insertRecipe(db, recipe);
        }
    }

    private static List<Recipe> buildRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        recipes.add(recipe("Scrambled Eggs",
                "1. Whisk the eggs with the milk and salt.\n"
                        + "2. Melt the butter in a pan over low heat.\n"
                        + "3. Add the eggs and stir gently until just set.",
                ingredient("Eggs", 3, "pcs"),
                ingredient("Milk", 30, "ml"),
                ingredient("Butter", 10, "g"),
                ingredient("Salt", 0.25, "tsp")));

        recipes.add(recipe("Simple Tomato Pasta",
                "1. Boil the pasta in salted water until cooked, then drain.\n"
                        + "2. Fry the chopped garlic in olive oil for 1 minute.\n"
                        + "3. Add the chopped tomatoes and simmer for 10 minutes.\n"
                        + "4. Stir the pasta into the sauce and serve.",
                ingredient("Pasta", 200, "g"),
                ingredient("Tomatoes", 3, "pcs"),
                ingredient("Garlic clove", 2, "pcs"),
                ingredient("Olive oil", 15, "ml"),
                ingredient("Salt", 0.5, "tsp")));

        recipes.add(recipe("Cheese Toast",
                "1. Butter one side of each bread slice.\n"
                        + "2. Place grated cheese on the unbuttered side.\n"
                        + "3. Toast under the grill until the cheese melts.",
                ingredient("Bread slices", 2, "pcs"),
                ingredient("Cheddar cheese", 50, "g"),
                ingredient("Butter", 10, "g")));

        // TODO: need to add more recipes here until there are 15-20 in total.
        // After changing this list, uninstall the app from the emulator (or increase
        // DATABASE_VERSION) so onCreate() runs again and the new recipes are inserted.

        return recipes;
    }

    // Small helper that builds a Recipe with its ingredients in one line.
    private static Recipe recipe(String name, String steps, RecipeIngredient... ingredients) {
        Recipe recipe = new Recipe(name, steps);
        for (RecipeIngredient ingredient : ingredients) {
            recipe.addIngredient(ingredient);
        }
        return recipe;
    }

    private static RecipeIngredient ingredient(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }

    // Inserts the recipe row, then one row per ingredient linked by recipe_id.
    private static void insertRecipe(SQLiteDatabase db, Recipe recipe) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(DatabaseHelper.COL_NAME, recipe.getName());
        recipeValues.put(DatabaseHelper.COL_STEPS, recipe.getSteps());
        long recipeId = db.insert(DatabaseHelper.TABLE_RECIPES, null, recipeValues);

        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_RECIPE_ID, recipeId);
            values.put(DatabaseHelper.COL_NAME, ingredient.getName());
            values.put(DatabaseHelper.COL_NORMALIZED_NAME,
                    IngredientNormalizer.normalize(ingredient.getName()));
            values.put(DatabaseHelper.COL_QUANTITY, ingredient.getQuantity());
            values.put(DatabaseHelper.COL_UNIT, ingredient.getUnit());
            db.insert(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, values);
        }
    }
}
