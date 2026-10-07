package com.example.assignment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.R;
import com.example.assignment.model.Recipe;
import com.example.assignment.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shows recipes as cards: name, number of ingredients, prep time and a preview.
 * The same adapter is used for the "Almost there" list, where each card also
 * shows the one ingredient that's missing.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    // the Activity decides what happens when a recipe is tapped
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final Map<Long, String> missingNames = new HashMap<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    // for the normal suggestions list (nothing missing)
    public void setRecipes(List<Recipe> newRecipes) {
        setRecipes(newRecipes, Collections.emptyMap());
    }

    // for the "Almost there" list, with the missing ingredient for each recipe
    public void setRecipes(List<Recipe> newRecipes, Map<Long, String> missing) {
        recipes.clear();
        recipes.addAll(newRecipes);
        missingNames.clear();
        missingNames.putAll(missing);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        Context context = holder.itemView.getContext();
        int count = recipe.getIngredients().size();

        holder.nameText.setText(recipe.getName());
        // e.g. "3 ingredients · 10 min"
        String ingredientCount = context.getResources()
                .getQuantityString(R.plurals.ingredient_count, count, count);
        holder.metaText.setText(context.getString(R.string.recipe_card_meta,
                ingredientCount, recipe.getPrepMinutes()));
        holder.previewText.setText(ingredientPreview(recipe));

        // only "almost there" cards have a missing ingredient
        String missing = missingNames.get(recipe.getId());
        if (missing != null) {
            holder.missingText.setText(context.getString(R.string.status_missing, missing));
            holder.missingText.setVisibility(View.VISIBLE);
        } else {
            holder.missingText.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    // e.g. "Oats, Milk, Honey"
    private static String ingredientPreview(Recipe recipe) {
        StringBuilder preview = new StringBuilder();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (preview.length() > 0) {
                preview.append(", ");
            }
            preview.append(ingredient.getName());
        }
        return preview.toString();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;
        final TextView metaText;
        final TextView previewText;
        final TextView missingText;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_recipe_name);
            metaText = itemView.findViewById(R.id.text_recipe_meta);
            previewText = itemView.findViewById(R.id.text_ingredient_preview);
            missingText = itemView.findViewById(R.id.text_missing);
        }
    }
}