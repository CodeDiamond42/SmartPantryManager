package com.example.assignment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.R;
import com.example.assignment.model.RecipeIngredient;

import java.util.List;
import java.util.Set;

// Lists a recipe's ingredients on the detail screen, with a green tick if it's
// in the pantry or a red cross if it's missing.
public class RecipeIngredientAdapter
        extends RecyclerView.Adapter<RecipeIngredientAdapter.IngredientViewHolder> {

    private final List<RecipeIngredient> ingredients;
    private final Set<Long> missingIds;   // ids of the ingredients the pantry doesn't cover

    public RecipeIngredientAdapter(List<RecipeIngredient> ingredients, Set<Long> missingIds) {
        this.ingredients = ingredients;
        this.missingIds = missingIds;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        RecipeIngredient ingredient = ingredients.get(position);
        Context context = holder.itemView.getContext();
        boolean missing = missingIds.contains(ingredient.getId());

        // e.g. "200 g Pasta"
        holder.ingredientText.setText(context.getString(R.string.ingredient_line,
                PantryAdapter.formatQuantity(ingredient.getQuantity()),
                ingredient.getUnit(),
                ingredient.getName()));

        holder.statusText.setText(missing ? R.string.symbol_missing : R.string.symbol_have);
        holder.statusText.setTextColor(ContextCompat.getColor(context,
                missing ? R.color.status_missing : R.color.status_have));
        // so screen readers say something useful instead of "tick"
        holder.statusText.setContentDescription(context.getString(
                missing ? R.string.cd_missing : R.string.cd_have));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        final TextView statusText;
        final TextView ingredientText;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            statusText = itemView.findViewById(R.id.text_status_symbol);
            ingredientText = itemView.findViewById(R.id.text_ingredient);
        }
    }
}