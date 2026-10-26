package com.example.assignment.logic;

import com.example.assignment.model.PantryItem;
import com.example.assignment.model.Recipe;
import com.example.assignment.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Works out which recipes can be made with what's in the pantry.
 *
 * The rule is strict: every ingredient has to be there, in at least the amount
 * the recipe needs. If even one is missing or there isn't enough of it,
 * the recipe doesn't get suggested.
 */
public final class RecipeMatcher {

    // small margin for rounding (0.1 + 0.2 isn't exactly 0.3 with doubles)
    private static final double TOLERANCE = 1e-6;

    private RecipeMatcher() {
    }

    // Goes through all the recipes and keeps the ones we can make right now.
    public static List<Recipe> findMakeableRecipes(List<Recipe> recipes, List<PantryItem> pantry) {
        // add up the pantry once here instead of redoing it for every recipe
        Map<String, Double> pantryTotals = buildPantryTotals(pantry);

        List<Recipe> makeable = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (canMake(recipe, pantryTotals)) {
                makeable.add(recipe);
            }
        }
        return makeable;
    }

    // Recipes that are missing exactly ONE ingredient (or have one that's short).
    // This is the optional "Almost there" list. It's kept completely separate from
    // findMakeableRecipes(), so these never count as suggestions.
    public static List<Recipe> findAlmostThereRecipes(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, Double> pantryTotals = buildPantryTotals(pantry);

        List<Recipe> almostThere = new ArrayList<>();
        for (Recipe recipe : recipes) {
            // a one-ingredient recipe missing its only ingredient isn't really "almost there"
            if (recipe.getIngredients().size() > 1 && countMissing(recipe, pantryTotals, 2) == 1) {
                almostThere.add(recipe);
            }
        }
        return almostThere;
    }

    // Returns the ingredients we don't have (or don't have enough of) for a recipe.
    public static List<RecipeIngredient> getMissingIngredients(Recipe recipe, List<PantryItem> pantry) {
        Map<String, Double> pantryTotals = buildPantryTotals(pantry);
        List<RecipeIngredient> missing = new ArrayList<>();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (!hasEnough(ingredient, pantryTotals)) {
                missing.add(ingredient);
            }
        }
        return missing;
    }

    static boolean canMake(Recipe recipe, Map<String, Double> pantryTotals) {
        if (recipe.getIngredients().isEmpty()) {
            return false;   // no ingredients means bad data, so don't suggest it
        }
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (!hasEnough(ingredient, pantryTotals)) {
                return false;   // one missing ingredient rules the whole recipe out
            }
        }
        return true;
    }

    // Counts missing ingredients, but stops once it reaches 'limit'
    // (for "almost there" we only care whether it's exactly 1).
    private static int countMissing(Recipe recipe, Map<String, Double> pantryTotals, int limit) {
        int missing = 0;
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (!hasEnough(ingredient, pantryTotals)) {
                missing++;
                if (missing >= limit) {
                    break;
                }
            }
        }
        return missing;
    }

    private static boolean hasEnough(RecipeIngredient ingredient, Map<String, Double> pantryTotals) {
        UnitConverter.Category category = UnitConverter.getCategory(ingredient.getUnit());
        if (category == null) {
            return false;   // unknown unit, we can't check it so count it as missing
        }

        String key = matchKey(nameFor(ingredient.getNormalizedName(), ingredient.getName()), category);
        Double available = pantryTotals.get(key);
        if (available == null) {
            return false;   // not in the pantry (or only in a unit that can't convert)
        }

        double required = UnitConverter.toBaseAmount(ingredient.getQuantity(), ingredient.getUnit());
        return available + TOLERANCE >= required;
    }

    // Adds up the pantry per ingredient, so 100 g cheese + 0.1 kg cheese = 200 g.
    // The keys look like "cheese|MASS".
    static Map<String, Double> buildPantryTotals(List<PantryItem> pantry) {
        Map<String, Double> totals = new HashMap<>();
        for (PantryItem item : pantry) {
            UnitConverter.Category category = UnitConverter.getCategory(item.getUnit());
            if (category == null || item.getQuantity() <= 0) {
                continue;
            }
            String key = matchKey(nameFor(item.getNormalizedName(), item.getName()), category);
            double amount = UnitConverter.toBaseAmount(item.getQuantity(), item.getUnit());

            Double current = totals.get(key);
            totals.put(key, current == null ? amount : current + amount);
        }
        return totals;
    }

    // Items loaded from the database already have a normalized name.
    // Items created in the tests don't, so we work it out here.
    private static String nameFor(String normalizedName, String rawName) {
        return normalizedName != null ? normalizedName : IngredientNormalizer.normalize(rawName);
    }

    // Both the name and the unit type have to match,
    // so milk in ml won't be matched with milk in grams.
    private static String matchKey(String normalizedName, UnitConverter.Category category) {
        return normalizedName + "|" + category.name();
    }
}