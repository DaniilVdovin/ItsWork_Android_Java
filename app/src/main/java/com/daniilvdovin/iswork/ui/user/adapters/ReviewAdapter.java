package com.daniilvdovin.iswork.ui.user.adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.daniilvdovin.iswork.Core;
import com.daniilvdovin.iswork.R;
import com.daniilvdovin.iswork.models.Review;
import com.daniilvdovin.iswork.models.Task;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    ArrayList<Review> reviews;

    public ReviewAdapter(ArrayList<Review> tasks) {
        this.reviews = tasks;
    }

    public void update(ArrayList<Review> tasks){
        this.reviews.clear();
        this.reviews.addAll(tasks);
        this.notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        final Review review = reviews.get(position);
        holder.user_name.setText(review.username);
        String filtered = Pattern.compile("(^ *$\\n*)", Pattern.MULTILINE).matcher(review.description).replaceAll("");
        holder.description.setText(filtered);
        holder.stars.setRating(review.star);

        Picasso.get()
                .load(Core.Host+"/getAvatar?token="+Core._user.token+"&id="+review.author)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewHolder extends RecyclerView.ViewHolder{
        TextView user_name,description;
        ImageView image;
        RatingBar stars;
        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.textView12);
            description = itemView.findViewById(R.id.textView8);
            stars = itemView.findViewById(R.id.ratingBar);
            image = itemView.findViewById(R.id.iv_user_image2);
        }
    }
}
