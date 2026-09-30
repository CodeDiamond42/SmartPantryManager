package com.example.assignment.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.assignment.logic.RecipeMatcher;
import com.example.assignment.model.PantryItem;
import com.example.assignment.model.Recipe;
import com.example.assignment.model.RecipeIngredient;import com.google.firebase.firestore.util.Assert;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Tests for the strict matching rule (section 2.3 of the brief).
public class RecipeMatcherTest {

    // a few helpers so the tests stay short

    private static RecipeIngredient need(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }

    private static PantryItem have(String name, double quantity, String unit) {
        return new PantryItem(name, quantity, unit, null);
    }

    private static Recipe recipe(String name, RecipeIngredient... ingredients) {
        Recipe recipe = new Recipe(name, "Test steps");
        for (RecipeIngredient ingredient : ingredients) {
            recipe.addIngredient(ingredient);
        }
        return recipe;
    }

    private static boolean isSuggested(Recipe recipe, PantryItem... pantry) {
        List<Recipe> result = RecipeMatcher.findMakeableRecipes(
                Collections.singletonList(recipe), Arrays.asList(pantry));
        return result.contains(recipe);
    }

    // --- the basic strict rule ---

    @Test
    public void suggested_whenAllIngredientsPresentInExactQuantity() {
        Recipe toast = recipe("Toast", need("Bread slices", 2, "pcs"), need("Butter", 10, "g"));
        assertTrue(isSuggested(toast, have("Bread slices", 2, "pcs"), have("Butter", 10, "g")));
    }

    @Test
    public void notSuggested_whenOneOfFiveIngredientsIsMissing() {
        // the example from the brief: having 4 out of 5 isn't enough
        Recipe pasta = recipe("Pasta",
                need("Pasta", 200, "g"), need("Tomatoes", 3, "pcs"), need("Garlic clove", 2, "pcs"),
                need("Olive oil", 15, "ml"), need("Salt", 0.5, "tsp"));
        assertTrue(!isSuggested(pasta,
                have("Pasta", 500, "g"), have("Tomatoes", 5, "pcs"),
                have("Garlic clove", 6, "pcs"), have("Olive oil", 500, "ml")));   // no salt
    }

    @Test
    public void notSuggested_whenQuantityIsTooLow() {
        Recipe omelette = recipe("Omelette", need("Eggs", 3, "pcs"));
        assertTrue(!isSuggested(omelette, have("Eggs", 2, "pcs")));
    }

    @Test
    public void emptyPantry_suggestsNothing() {
        Recipe toast = recipe("Toast", need("Bread slices", 2, "pcs"));
        assertTrue(!isSuggested(toast));
    }

    @Test
    public void recipeWithNoIngredients_isNeverSuggested() {
        assertTrue(!isSuggested(recipe("Empty"), have("Eggs", 12, "pcs")));
    }

    // --- messy real-world input ---

    @Test
    public void matchingIgnoresCaseSpacingAndPlurals() {
        Recipe salad = recipe("Salad", need("tomato", 2, "pcs"), need("Cherry Tomatoes", 5, "pcs"));
        assertTrue(isSuggested(salad, have("  TOMATOES ", 3, "pcs"), have("cherry  tomato", 5, "pcs")));
    }

    @Test
    public void convertsKilogramsToGrams() {
        Recipe bread = recipe("Bread", need("Flour", 500, "g"));
        assertTrue(isSuggested(bread, have("Flour", 1, "kg")));
    }

    @Test
    public void convertsLitresAndCupsToMillilitres() {
        Recipe pancakes = recipe("Pancakes", need("Milk", 1, "cup"));    // 250 ml
        assertTrue(isSuggested(pancakes, have("Milk", 0.5, "L")));      // 500 ml, enough
        assertTrue(!isSuggested(pancakes, have("Milk", 0.2, "L")));     // 200 ml, not enough
    }

    @Test
    public void convertsTeaspoonsAndTablespoons() {
        Recipe dressing = recipe("Dressing", need("Vinegar", 1, "tbsp"));  // 15 ml
        assertTrue(isSuggested(dressing, have("Vinegar", 3, "tsp")));      // also 15 ml
    }

    @Test
    public void unitNamesAreCaseInsensitive() {
        Recipe soup = recipe("Soup", need("Stock", 500, "ml"));
        assertTrue(isSuggested(soup, have("Stock", 1, "l")));   // "l" should work like "L"
    }

    @Test
    public void incompatibleUnits_doNotMatch() {
        // can't turn grams into ml without knowing the density
        Recipe drink = recipe("Milkshake", need("Milk", 250, "ml"));
        assertTrue(!isSuggested(drink, have("Milk", 500, "g")));
    }

    @Test
    public void duplicatePantryEntries_areAddedTogether() {
        Recipe toastie = recipe("Toastie", need("Cheese", 150, "g"));
        assertTrue(isSuggested(toastie, have("Cheese", 100, "g"), have("Cheese", 0.1, "kg")));
    }

    @Test
    public void smallDecimalRoundingErrors_doNotBlockAMatch() {
        // 0.1 + 0.2 comes out as 0.30000000000000004 with doubles
        Recipe sauce = recipe("Sauce", need("Cream", 0.3, "L"));
        assertTrue(isSuggested(sauce, have("Cream", 0.1, "L"), have("Cream", 0.2, "L")));
    }

    // --- lists and missing ingredients ---

    @Test
    public void onlyMakeableRecipesAreReturned_fromAMixedList() {
        Recipe toast = recipe("Toast", need("Bread slices", 2, "pcs"));
        Recipe omelette = recipe("Omelette", need("Eggs", 3, "pcs"));
        List<PantryItem> pantry = Collections.singletonList(have("Bread slices", 4, "pcs"));

        List<Recipe> result = RecipeMatcher.findMakeableRecipes(Arrays.asList(toast, omelette), pantry);

        assertEquals(1, result.size());
        assertEquals("Toast", result.get(0).getName());
    }

    @Test
    public void getMissingIngredients_returnsOnlyWhatIsMissing() {
        Recipe toast = recipe("Cheese Toast",
                need("Bread slices", 2, "pcs"), need("Cheese", 50, "g"), need("Butter", 10, "g"));
        List<PantryItem> pantry = Arrays.asList(have("Bread slices", 2, "pcs"), have("Butter", 250, "g"));

        List<RecipeIngredient> missing = RecipeMatcher.getMissingIngredients(toast, pantry);

        assertEquals(1, missing.size());
        assertEquals("Cheese", missing.get(0).getName());
    }
}