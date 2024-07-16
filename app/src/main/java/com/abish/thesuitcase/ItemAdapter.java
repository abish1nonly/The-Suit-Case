package com.abish.thesuitcase;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final Context context;
    private final List<ItemModel> itemList;
    private int expandedItemPosition = -1;
    private OnEditButtonClickListener editButtonClickListener;
    private OnDeleteButtonClickListener deleteButtonClickListener;
    private OnShareButtonClickListener shareButtonClickListener;

    public interface OnEditButtonClickListener {
        void onEditButtonClick(ItemModel item);
    }
    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClick(ItemModel item, int position);
    }
    public interface OnShareButtonClickListener {
        void onShareButtonClick(ItemModel item);
    }
    public ItemAdapter(Context context, List<ItemModel> itemList, OnEditButtonClickListener editButtonClickListener, OnDeleteButtonClickListener deleteButtonClickListener, OnShareButtonClickListener shareButtonClickListener) {
        this.context = context;
        this.itemList = itemList;
        this.editButtonClickListener = editButtonClickListener;
        this.deleteButtonClickListener = deleteButtonClickListener;
        this.shareButtonClickListener = shareButtonClickListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemModel item = itemList.get(position);

        holder.itemNameTextView.setText(item.getName());
        holder.itemDescriptionTextView.setText("Description: " + item.getDescription());
        holder.itemPriceTextView.setText("Price: " + item.getPrice());
        holder.purchaseCheckBoxView.setChecked(item.isPurchaseChecked());

        boolean hasValidCoordinates = item.getLatitude() != 0 || item.getLongitude() != 0;

        // Set visibility of mapView based on coordinates validity
        holder.mapView.setVisibility(hasValidCoordinates ? View.VISIBLE : View.GONE);

        if (hasValidCoordinates) {
            // Update the map only for items with valid coordinates
            holder.mapView.getMapAsync(googleMap -> {
                Double latitude = item.getLatitude();
                Double longitude = item.getLongitude();
                LatLng location = new LatLng(latitude, longitude);

                googleMap.addMarker(new MarkerOptions().position(location).title(item.getName()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
                googleMap.getUiSettings().setAllGesturesEnabled(false);
            });
        }
        holder.expandCollapseImageView.setOnClickListener(v -> {
            if (holder.getAdapterPosition() == expandedItemPosition) {
                // Collapse the currently expanded item
                collapseItem(holder, expandedItemPosition);
                expandedItemPosition = -1;
            } else {
                // Expand the clicked item
                expandItem(holder, holder.getAdapterPosition());
            }
        });
        holder.editButton.setOnClickListener(v -> {
            // Pass the item to the listener when the "Edit" button is clicked
            if (editButtonClickListener != null) {
                editButtonClickListener.onEditButtonClick(item);
            }
        });
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteButtonClickListener != null) {
                deleteButtonClickListener.onDeleteButtonClick(itemList.get(position), position);
            }
        });
        holder.shareButton.setOnClickListener(v -> {
            if (shareButtonClickListener != null) {
                shareButtonClickListener.onShareButtonClick(itemList.get(position));
            }
        });
        boolean isExpanded = holder.getAdapterPosition() == expandedItemPosition;
        holder.optionsLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        int iconResource = isExpanded ? R.drawable.ic_expand_less_24 : R.drawable.ic_expand_more_24;
        holder.expandCollapseImageView.setImageResource(iconResource);
    }
    private void expandItem(ItemViewHolder holder, int position) {
        // Collapse the currently expanded item before expanding a new one
        if (expandedItemPosition != -1) {
            collapseItem(holder, expandedItemPosition);
        }

        // Expand the clicked item with fade animation
        expandedItemPosition = position;
        toggleVisibilityWithAnimation(holder.optionsLayout, true);
    }
    private void collapseItem(ItemViewHolder holder, int position) {
        // Collapse the item at the specified position with fade animation
        toggleVisibilityWithAnimation(holder.optionsLayout, false);
        expandedItemPosition = -1;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        Button editButton, deleteButton, shareButton;
        TextView itemNameTextView;
        TextView itemDescriptionTextView;
        TextView itemPriceTextView;
        MapView mapView;
        ImageView expandCollapseImageView;
        LinearLayout optionsLayout;
        CheckBox purchaseCheckBoxView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemDescriptionTextView = itemView.findViewById(R.id.itemDescriptionTextView);
            itemPriceTextView = itemView.findViewById(R.id.itemPriceTextView);
            mapView = itemView.findViewById(R.id.mapView);
            expandCollapseImageView = itemView.findViewById(R.id.expandCollapseImageView);
            optionsLayout = itemView.findViewById(R.id.optionsLayout);
            purchaseCheckBoxView = itemView.findViewById(R.id.purchaseCheckBoxView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            shareButton = itemView.findViewById(R.id.shareButton);
            mapView.onCreate(null);

            // Set click listener for expandCollapseImageView
            expandCollapseImageView.setOnClickListener(v -> {
                optionsLayout.setVisibility(optionsLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

                // Change the expand/collapse icon accordingly
                int iconResource = optionsLayout.getVisibility() == View.VISIBLE ?
                        R.drawable.ic_expand_less_24 : R.drawable.ic_expand_more_24;
                expandCollapseImageView.setImageResource(iconResource);
            });
        }
    }
    private static void toggleVisibilityWithAnimation(View view, boolean expand) {
        ObjectAnimator fadeAnimator;
        if (expand) {
            // Fade-in animation
            fadeAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        } else {
            // Fade-out animation
            fadeAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        }

        fadeAnimator.setDuration(500); // Set the duration of the animation in milliseconds
        fadeAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeAnimator.start();

        // Toggle visibility after animation
        fadeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(expand ? View.VISIBLE : View.GONE);
            }
        });
    }

}