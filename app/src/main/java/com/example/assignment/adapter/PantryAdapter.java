package com.example.assignment.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.R;
import com.example.assignment.logic.ExpiryChecker;
import com.example.assignment.model.PantryItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Connects the list of PantryItems to the RecyclerView.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    // the Activity decides what happens when a row or its delete button is tapped
    public interface OnItemActionListener {
        void onItemClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemActionListener listener;

    private boolean expiryAlertsEnabled = true;
    private int warningDays = 3;
    private Date today = new Date();

    public PantryAdapter(OnItemActionListener listener) {
        this.listener = listener;
    }

    // replaces the list with fresh data from the database
    public void setItems(List<PantryItem> newItems) {
        today = new Date();   // so "expires in X days" is worked out from right now
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    // settings from the Settings screen
    public void setExpiryWarning(boolean enabled, int days) {
        expiryAlertsEnabled = enabled;
        warningDays = days;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        Context context = holder.itemView.getContext();

        holder.nameText.setText(item.getName());
        holder.quantityText.setText(context.getString(R.string.quantity_format,
                formatQuantity(item.getQuantity()), item.getUnit()));
        bindExpiry(holder, item, context);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    // Shows the expiry line. Rows get recycled, so the colour has to be reset every time,
    // otherwise a red warning from one item could show up on another.
    private void bindExpiry(PantryViewHolder holder, PantryItem item, Context context) {
        String expiryDate = item.getExpiryDate();
        if (expiryDate == null) {
            holder.expiryText.setVisibility(View.GONE);
            return;
        }
        holder.expiryText.setVisibility(View.VISIBLE);
        holder.expiryText.setTextColor(holder.defaultExpiryColor);

        Integer days = expiryAlertsEnabled ? ExpiryChecker.daysUntil(expiryDate, today) : null;

        if (days == null || days > warningDays) {
            // alerts off, or not expiring soon: just show the date
            holder.expiryText.setText(context.getString(R.string.expiry_format, expiryDate));
        } else if (days < 0) {
            int daysAgo = -days;
            holder.expiryText.setText(context.getResources()
                    .getQuantityString(R.plurals.expired_days_ago, daysAgo, daysAgo));
            holder.expiryText.setTextColor(ContextCompat.getColor(context, R.color.status_missing));
        } else if (days == 0) {
            holder.expiryText.setText(R.string.expires_today);
            holder.expiryText.setTextColor(ContextCompat.getColor(context, R.color.status_missing));
        } else {
            holder.expiryText.setText(context.getResources()
                    .getQuantityString(R.plurals.expires_in_days, days, days));
            holder.expiryText.setTextColor(ContextCompat.getColor(context, R.color.status_warning));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // shows 3.0 as "3" and 1.50 as "1.5" so quantities look natural
    public static String formatQuantity(double quantity) {
        if (quantity == Math.rint(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.format(Locale.getDefault(), "%.2f", quantity).replaceAll("0+$", "");
    }

    // holds the views in one row so findViewById isn't called on every scroll
    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;
        final TextView quantityText;
        final TextView expiryText;
        final ImageButton deleteButton;
        final ColorStateList defaultExpiryColor;   // the normal colour, to reset recycled rows

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_item_name);
            quantityText = itemView.findViewById(R.id.text_item_quantity);
            expiryText = itemView.findViewById(R.id.text_item_expiry);
            deleteButton = itemView.findViewById(R.id.button_delete);
            defaultExpiryColor = expiryText.getTextColors();
        }
    }
}