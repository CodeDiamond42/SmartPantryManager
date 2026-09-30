package com.example.assignment.logic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Converts units into grams, ml or pieces so amounts can be compared.
 * Mass and volume can't be mixed (you'd need to know how dense the food is),
 * so grams vs ml just won't match.
 */
public final class UnitConverter {

    public enum Category { MASS, VOLUME, COUNT }

    private static final Map<String, Category> CATEGORIES = new HashMap<>();
    private static final Map<String, Double> TO_BASE_FACTOR = new HashMap<>();

    static {
        // everything gets converted to g, ml or pcs
        register("g", Category.MASS, 1);
        register("kg", Category.MASS, 1000);
        register("ml", Category.VOLUME, 1);
        register("l", Category.VOLUME, 1000);
        register("tsp", Category.VOLUME, 5);
        register("tbsp", Category.VOLUME, 15);
        register("cup", Category.VOLUME, 250);
        register("pcs", Category.COUNT, 1);
    }

    private UnitConverter() {
    }

    private static void register(String unit, Category category, double factorToBase) {
        CATEGORIES.put(unit, category);
        TO_BASE_FACTOR.put(unit, factorToBase);
    }

    // lowercase it so "L" and "l" both work
    private static String clean(String unit) {
        return unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
    }

    // returns null if it's not a unit we know
    public static Category getCategory(String unit) {
        return CATEGORIES.get(clean(unit));
    }

    public static boolean isSupported(String unit) {
        return getCategory(unit) != null;
    }

    // kg and g are compatible, g and ml are not
    public static boolean areCompatible(String unitA, String unitB) {
        Category categoryA = getCategory(unitA);
        return categoryA != null && categoryA == getCategory(unitB);
    }

    // e.g. 1.5 kg -> 1500 (g)
    public static double toBaseAmount(double quantity, String unit) {
        Double factor = TO_BASE_FACTOR.get(clean(unit));
        if (factor == null) {
            throw new IllegalArgumentException("Unsupported unit: " + unit);
        }
        return quantity * factor;
    }
}