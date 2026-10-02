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
import java.util.List;

// Shows a list of recipes as cards (name, how many ingredients, and a preview of them).
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    // the Activity decides what happens when a recipe is tapped
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    // swaps in a new list, e.g. after the pantry changes
    public void setRecipes(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
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
        holder.countText.setText(context.getResources()
                .getQuantityString(R.plurals.ingredient_count, count, count));
        holder.previewText.setText(ingredientPreview(recipe));

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
        final TextView countText;
        final TextView previewText;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_recipe_name);
            countText = itemView.findViewById(R.id.text_ingredient_count);
            previewText = itemView.findViewById(R.id.text_ingredient_preview);
        }
    }
}