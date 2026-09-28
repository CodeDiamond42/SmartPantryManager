package com.example.assignment.model;

/**
 * One ingredient that a recipe requires, with the quantity needed.
 * Maps to one row in the recipe_ingredients table.
 */
public class RecipeIngredient {

    private long id;
    private long recipeId;
    private String name;
    private String normalizedName;
    private double quantity;
    private String unit;

    /** Constructor used when seeding recipes (ids are assigned by the database). */
    public RecipeIngredient(String name, double quantity, String unit) {
        this(-1, -1, name, null, quantity, unit);
    }

    /** Constructor used when reading an existing row from the database. */
    public RecipeIngredient(long id, long recipeId, String name, String normalizedName,
                            double quantity, String unit) {
        this.id = id;
        this.recipeId = recipeId;
        this.name = name;
        this.normalizedName = normalizedName;
        this.quantity = quantity;
        this.unit = unit;
    }

    public long getId() { return id; }
    public long getRecipeId() { return recipeId; }
    public String getName() { return name; }
    public String getNormalizedName() { return normalizedName; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
}
