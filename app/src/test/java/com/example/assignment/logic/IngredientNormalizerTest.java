package com.example.assignment.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

// Checks that ingredient names get cleaned up the same way every time.
public class IngredientNormalizerTest {

    @Test
    public void lowercasesAndTrims() {
        assertEquals("butter", IngredientNormalizer.normalize("  Butter "));
    }

    @Test
    public void collapsesExtraSpaces() {
        assertEquals("olive oil", IngredientNormalizer.normalize("Olive    Oil"));
    }

    @Test
    public void makesCommonPluralsSingular() {
        assertEquals("egg", IngredientNormalizer.normalize("Eggs"));
        assertEquals("tomato", IngredientNormalizer.normalize("Tomatoes"));
        assertEquals("berry", IngredientNormalizer.normalize("Berries"));
        assertEquals("peach", IngredientNormalizer.normalize("Peaches"));
    }

    @Test
    public void onlyChangesTheLastWord() {
        assertEquals("cherry tomato", IngredientNormalizer.normalize("Cherry Tomatoes"));
    }

    @Test
    public void handlesIrregularWords() {
        assertEquals("bay leaf", IngredientNormalizer.normalize("Bay Leaves"));
        assertEquals("asparagus", IngredientNormalizer.normalize("Asparagus"));
    }

    @Test
    public void singularAndPluralGiveTheSameResult() {
        assertEquals(IngredientNormalizer.normalize("onion"), IngredientNormalizer.normalize("Onions"));
    }

    @Test
    public void nullBecomesEmptyString() {
        assertEquals("", IngredientNormalizer.normalize(null));
    }
}