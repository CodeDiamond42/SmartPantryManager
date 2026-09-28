package com.example.assignment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.R;
import com.example.assignment.model.PantryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Connects the list of PantryItems to the RecyclerView.
 * Each item is shown using the item_pantry.xml layout.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    // Lets the Activity decide what happens when a row or its delete button is tapped.
    public interface OnItemActionListener {
        void onItemClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemActionListener listener;

    public PantryAdapter(OnItemActionListener listener) {
        this.listener = listener;
    }

    // Replaces the displayed list with fresh data from the database.
    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    // Called when the RecyclerView needs a new row view.
    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    // Called to fill a (possibly recycled) row view with the data at this position.
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        Context context = holder.itemView.getContext();

        holder.nameText.setText(item.getName());
        holder.quantityText.setText(context.getString(R.string.quantity_format,
                formatQuantity(item.getQuantity()), item.getUnit()));

        if (item.getExpiryDate() != null) {
            holder.expiryText.setVisibility(View.VISIBLE);
            holder.expiryText.setText(context.getString(R.string.expiry_format, item.getExpiryDate()));
        } else {
            holder.expiryText.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Shows 3.0 as "3" and 1.50 as "1.5" so quantities look natural.
    public static String formatQuantity(double quantity) {
        if (quantity == Math.rint(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.format(Locale.getDefault(), "%.2f", quantity).replaceAll("0+$", "");
    }

    // Holds references to the views in one row so findViewById is not repeated.
    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;
        final TextView quantityText;
        final TextView expiryText;
        final ImageButton deleteButton;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_item_name);
            quantityText = itemView.findViewById(R.id.text_item_quantity);
            expiryText = itemView.findViewById(R.id.text_item_expiry);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}
