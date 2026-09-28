package com.example.assignment.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe with its preparation steps and the list of ingredients it requires.
 * Maps to one row in the recipes table plus its rows in recipe_ingredients.
 */
public class Recipe {

    private long id;
    private String name;
    private String steps;   // numbered steps separated by new lines
    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    // Constructor for a new recipe that has not been saved yet.
    public Recipe(String name, String steps) {
        this(-1, name, steps);
    }

    // Constructor used when reading an existing recipe from the database.
    public Recipe(long id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
    }

    public void addIngredient(RecipeIngredient ingredient) {
        ingredients.add(ingredient);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }

    public List<RecipeIngredient> getIngredients() { return ingredients; }
}
