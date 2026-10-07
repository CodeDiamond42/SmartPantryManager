package com.example.assignment.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.assignment.logic.IngredientNormalizer;
import com.example.assignment.model.Recipe;
import com.example.assignment.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Puts the built-in recipes into the database. Only called once, from
 * DatabaseHelper.onCreate(), the first time the app runs.
 * Each recipe has a name, roughly how many minutes it takes, the steps,
 * and its ingredients.
 * Units have to be from the units list (g, kg, ml, L, pcs, tsp, tbsp, cup),
 * and each ingredient always uses the same kind of unit across all recipes
 * (butter is always grams, milk is always ml, eggs are always pcs etc.)
 * so the matching works properly.
 */
final class RecipeSeeder {

    private RecipeSeeder() {
    }

    static void seed(SQLiteDatabase db) {
        for (Recipe recipe : buildRecipes()) {
            insertRecipe(db, recipe);
        }
    }

    private static List<Recipe> buildRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        //Breakfast:

        recipes.add(recipe("Scrambled Eggs", 10,
                "1. Whisk the eggs with the milk and salt.\n"
                        + "2. Melt the butter in a pan over low heat.\n"
                        + "3. Add the eggs and stir gently until just set.",
                ingredient("Eggs", 3, "pcs"),
                ingredient("Milk", 30, "ml"),
                ingredient("Butter", 10, "g"),
                ingredient("Salt", 0.25, "tsp")));

        recipes.add(recipe("Cheese Omelette", 10,
                "1. Whisk the eggs, milk and salt together.\n"
                        + "2. Melt the butter in a pan over medium heat.\n"
                        + "3. Pour in the eggs and let them set for a minute.\n"
                        + "4. Sprinkle the cheese over half, fold over and serve.",
                ingredient("Eggs", 3, "pcs"),
                ingredient("Milk", 30, "ml"),
                ingredient("Cheddar cheese", 40, "g"),
                ingredient("Butter", 10, "g"),
                ingredient("Salt", 0.25, "tsp")));

        recipes.add(recipe("French Toast", 15,
                "1. Whisk the eggs, milk and cinnamon in a shallow bowl.\n"
                        + "2. Dip each bread slice in the mixture on both sides.\n"
                        + "3. Fry in butter for 2 minutes a side until golden.",
                ingredient("Bread slices", 4, "pcs"),
                ingredient("Eggs", 2, "pcs"),
                ingredient("Milk", 100, "ml"),
                ingredient("Butter", 20, "g"),
                ingredient("Cinnamon", 0.5, "tsp")));

        recipes.add(recipe("Pancakes", 25,
                "1. Mix the flour, sugar and baking powder in a bowl.\n"
                        + "2. Whisk in the eggs and milk until smooth.\n"
                        + "3. Melt a little butter in a pan and add a ladle of batter.\n"
                        + "4. Flip when bubbles appear and cook the other side.",
                ingredient("Flour", 150, "g"),
                ingredient("Sugar", 15, "g"),
                ingredient("Baking powder", 1, "tsp"),
                ingredient("Eggs", 2, "pcs"),
                ingredient("Milk", 250, "ml"),
                ingredient("Butter", 20, "g")));

        recipes.add(recipe("Honey Porridge", 10,
                "1. Put the oats and milk in a pot over medium heat.\n"
                        + "2. Stir for 4-5 minutes until thick and creamy.\n"
                        + "3. Drizzle with honey and serve.",
                ingredient("Oats", 50, "g"),
                ingredient("Milk", 250, "ml"),
                ingredient("Honey", 1, "tbsp")));

        recipes.add(recipe("Banana Oat Smoothie", 5,
                "1. Peel and slice the banana.\n"
                        + "2. Blend the banana, milk, oats and honey until smooth.\n"
                        + "3. Serve straight away.",
                ingredient("Banana", 1, "pcs"),
                ingredient("Milk", 250, "ml"),
                ingredient("Oats", 30, "g"),
                ingredient("Honey", 1, "tbsp")));

        // Light meals:

        recipes.add(recipe("Cheese Toast", 10,
                "1. Butter one side of each bread slice.\n"
                        + "2. Place grated cheese on the unbuttered side.\n"
                        + "3. Toast under the grill until the cheese melts.",
                ingredient("Bread slices", 2, "pcs"),
                ingredient("Cheddar cheese", 50, "g"),
                ingredient("Butter", 10, "g")));

        recipes.add(recipe("Tuna Mayo Sandwich", 5,
                "1. Drain the tuna and mix it with the mayonnaise.\n"
                        + "2. Spread the mixture on one bread slice.\n"
                        + "3. Add the lettuce, top with the other slice and cut in half.",
                ingredient("Bread slices", 2, "pcs"),
                ingredient("Canned tuna", 80, "g"),
                ingredient("Mayonnaise", 1, "tbsp"),
                ingredient("Lettuce leaves", 2, "pcs")));

        recipes.add(recipe("Egg Mayo Sandwich", 15,
                "1. Boil the eggs for 10 minutes, then cool and peel them.\n"
                        + "2. Mash the eggs with the mayonnaise and salt.\n"
                        + "3. Spread between the bread slices and cut in half.",
                ingredient("Eggs", 2, "pcs"),
                ingredient("Bread slices", 2, "pcs"),
                ingredient("Mayonnaise", 1, "tbsp"),
                ingredient("Salt", 0.25, "tsp")));

        recipes.add(recipe("Garlic Mushrooms on Toast", 15,
                "1. Slice the mushrooms and crush the garlic.\n"
                        + "2. Fry both in butter for 5 minutes until soft.\n"
                        + "3. Toast the bread and spoon the mushrooms on top.",
                ingredient("Mushrooms", 200, "g"),
                ingredient("Garlic clove", 2, "pcs"),
                ingredient("Butter", 20, "g"),
                ingredient("Bread slices", 2, "pcs")));

        recipes.add(recipe("Tomato Soup", 35,
                "1. Chop the onion and garlic and fry them in butter until soft.\n"
                        + "2. Add the chopped tomatoes, stock cube, salt and 500 ml of water.\n"
                        + "3. Simmer for 20 minutes.\n"
                        + "4. Blend until smooth and serve.",
                ingredient("Tomatoes", 6, "pcs"),
                ingredient("Onion", 1, "pcs"),
                ingredient("Garlic clove", 2, "pcs"),
                ingredient("Butter", 20, "g"),
                ingredient("Stock cube", 1, "pcs"),
                ingredient("Salt", 0.5, "tsp")));

        recipes.add(recipe("Vegetable Soup", 35,
                "1. Peel and dice the carrots, potatoes and onion.\n"
                        + "2. Put them in a pot with the stock cube, salt and 1 L of water.\n"
                        + "3. Simmer for 20 minutes, add the peas and cook 5 more minutes.",
                ingredient("Carrot", 2, "pcs"),
                ingredient("Potatoes", 2, "pcs"),
                ingredient("Onion", 1, "pcs"),
                ingredient("Frozen peas", 100, "g"),
                ingredient("Stock cube", 1, "pcs"),
                ingredient("Salt", 0.5, "tsp")));

        //  Dinners:

        recipes.add(recipe("Simple Tomato Pasta", 20,
                "1. Boil the pasta in salted water until cooked, then drain.\n"
                        + "2. Fry the chopped garlic in olive oil for 1 minute.\n"
                        + "3. Add the chopped tomatoes and simmer for 10 minutes.\n"
                        + "4. Stir the pasta into the sauce and serve.",
                ingredient("Pasta", 200, "g"),
                ingredient("Tomatoes", 3, "pcs"),
                ingredient("Garlic clove", 2, "pcs"),
                ingredient("Olive oil", 15, "ml"),
                ingredient("Salt", 0.5, "tsp")));

        recipes.add(recipe("Macaroni Cheese", 30,
                "1. Boil the pasta until just cooked, then drain.\n"
                        + "2. Melt the butter in a pot, stir in the flour and cook for 1 minute.\n"
                        + "3. Slowly whisk in the milk and stir until the sauce thickens.\n"
                        + "4. Stir in most of the cheese, then the pasta.\n"
                        + "5. Top with the rest of the cheese and grill until golden.",
                ingredient("Pasta", 250, "g"),
                ingredient("Cheddar cheese", 150, "g"),
                ingredient("Milk", 500, "ml"),
                ingredient("Butter", 30, "g"),
                ingredient("Flour", 30, "g")));

        recipes.add(recipe("Spaghetti Bolognese", 40,
                "1. Fry the chopped onion and garlic in olive oil until soft.\n"
                        + "2. Add the mince and brown it, breaking it up as it cooks.\n"
                        + "3. Add the chopped tomatoes and salt and simmer for 20 minutes.\n"
                        + "4. Boil the pasta, drain, and serve with the sauce.",
                ingredient("Pasta", 250, "g"),
                ingredient("Beef mince", 400, "g"),
                ingredient("Onion", 1, "pcs"),
                ingredient("Garlic clove", 2, "pcs"),
                ingredient("Tomatoes", 4, "pcs"),
                ingredient("Olive oil", 1, "tbsp"),
                ingredient("Salt", 0.5, "tsp")));

        recipes.add(recipe("Savoury Mince and Rice", 35,
                "1. Cook the rice according to the packet.\n"
                        + "2. Fry the chopped onion and grated carrot until soft.\n"
                        + "3. Add the mince and brown it.\n"
                        + "4. Add the chopped tomatoes, stock cube and a splash of water.\n"
                        + "5. Simmer for 15 minutes and serve over the rice.",
                ingredient("Beef mince", 400, "g"),
                ingredient("Onion", 1, "pcs"),
                ingredient("Carrot", 1, "pcs"),
                ingredient("Tomatoes", 2, "pcs"),
                ingredient("Stock cube", 1, "pcs"),
                ingredient("Rice", 200, "g")));

        recipes.add(recipe("Chicken Fried Rice", 25,
                "1. Cook the rice and let it cool (day-old rice works best).\n"
                        + "2. Fry the diced chicken in olive oil until cooked through.\n"
                        + "3. Push it aside, scramble the eggs in the pan, then add the peas.\n"
                        + "4. Add the rice and soy sauce and stir-fry for 3 minutes.",
                ingredient("Rice", 200, "g"),
                ingredient("Chicken breast", 250, "g"),
                ingredient("Eggs", 2, "pcs"),
                ingredient("Frozen peas", 100, "g"),
                ingredient("Soy sauce", 2, "tbsp"),
                ingredient("Olive oil", 2, "tbsp")));

        recipes.add(recipe("Roast Chicken and Potatoes", 50,
                "1. Heat the oven to 200°C.\n"
                        + "2. Cut the potatoes into chunks and crush the garlic.\n"
                        + "3. Toss the chicken and potatoes with olive oil, garlic, salt and pepper.\n"
                        + "4. Roast for 35-40 minutes until golden and cooked through.",
                ingredient("Chicken breast", 400, "g"),
                ingredient("Potatoes", 4, "pcs"),
                ingredient("Garlic clove", 3, "pcs"),
                ingredient("Olive oil", 2, "tbsp"),
                ingredient("Salt", 1, "tsp"),
                ingredient("Black pepper", 0.5, "tsp")));

        recipes.add(recipe("Mashed Potatoes", 30,
                "1. Peel and quarter the potatoes.\n"
                        + "2. Boil for 15-20 minutes until soft, then drain.\n"
                        + "3. Mash with the butter, warm milk and salt until smooth.",
                ingredient("Potatoes", 4, "pcs"),
                ingredient("Butter", 30, "g"),
                ingredient("Milk", 100, "ml"),
                ingredient("Salt", 0.5, "tsp")));

        //  Dessert:

        recipes.add(recipe("Apple Crumble", 45,
                "1. Heat the oven to 180°C.\n"
                        + "2. Peel and slice the apples, toss with the cinnamon and half the sugar.\n"
                        + "3. Rub the butter into the flour, then stir in the rest of the sugar.\n"
                        + "4. Put the apples in a dish, cover with the crumble and bake for 30 minutes.",
                ingredient("Apples", 4, "pcs"),
                ingredient("Flour", 100, "g"),
                ingredient("Butter", 75, "g"),
                ingredient("Sugar", 75, "g"),
                ingredient("Cinnamon", 1, "tsp")));

        return recipes;
    }

    // builds a recipe and adds all its ingredients in one go
    private static Recipe recipe(String name, int prepMinutes, String steps,
                                 RecipeIngredient... ingredients) {
        Recipe recipe = new Recipe(name, steps, prepMinutes);
        for (RecipeIngredient ingredient : ingredients) {
            recipe.addIngredient(ingredient);
        }
        return recipe;
    }

    private static RecipeIngredient ingredient(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }

    // inserts the recipe first to get its id, then each ingredient linked to that id
    private static void insertRecipe(SQLiteDatabase db, Recipe recipe) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(DatabaseHelper.COL_NAME, recipe.getName());
        recipeValues.put(DatabaseHelper.COL_STEPS, recipe.getSteps());
        recipeValues.put(DatabaseHelper.COL_PREP_MINUTES, recipe.getPrepMinutes());
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