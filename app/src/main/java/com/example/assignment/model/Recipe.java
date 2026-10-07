package com.example.assignment.model;

import java.util.ArrayList;
import java.util.List;

// A recipe with its steps, how long it takes, and the ingredients it needs.
public class Recipe {

    private long id;
    private String name;
    private String steps;          // numbered steps separated by new lines
    private int prepMinutes;       // roughly how long it takes to make
    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    // used by the unit tests
    public Recipe(String name, String steps) {
        this(-1, name, steps, 0);
    }

    // used when seeding a new recipe (the database gives it an id)
    public Recipe(String name, String steps, int prepMinutes) {
        this(-1, name, steps, prepMinutes);
    }

    // used when reading a recipe back from the database
    public Recipe(long id, String name, String steps, int prepMinutes) {
        this.id = id;
        this.name = name;
        this.steps = steps;
        this.prepMinutes = prepMinutes;
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

    public int getPrepMinutes() { return prepMinutes; }
    public void setPrepMinutes(int prepMinutes) { this.prepMinutes = prepMinutes; }

    public List<RecipeIngredient> getIngredients() { return ingredients; }
}