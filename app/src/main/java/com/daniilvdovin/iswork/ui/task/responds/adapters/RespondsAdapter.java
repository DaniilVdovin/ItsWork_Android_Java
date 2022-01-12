package com.daniilvdovin.iswork.ui.task.responds.adapters;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.daniilvdovin.iswork.R;
import com.daniilvdovin.iswork.models.Response;
import com.daniilvdovin.iswork.models.Task;

import java.util.ArrayList;

public class RespondsAdapter extends RecyclerView.Adapter<RespondsAdapter.ResponseHolder> {

    ArrayList<Response> responses;
    int executer = -1;

    public RespondsAdapter(ArrayList<Response> responses,int executer) {
        this.responses = responses;
        this.executer = executer;
    }
    public RespondsAdapter(ArrayList<Response> responses) {
        this.responses = responses;
    }
    public void update(ArrayList<Response> responses){
        this.responses.clear();
        this.responses.addAll(responses);
        this.notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ResponseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ResponseHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_respond, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull ResponseHolder holder, int position) {
        final Response response = responses.get(position);

        if(executer!=0){
            if(response.author == executer)
                holder.mark.setVisibility(View.VISIBLE);
            else
                holder.mark.setVisibility(View.GONE);
        }

        holder.description.setText(response.description.substring(0,response.description.length()>20?20:response.description.length())+(response.description.length()>=20?"...":""));
        holder.price.setText("to "+response.price+" P");

        holder.itemView.setOnClickListener((v)->{
            Bundle bundle = new Bundle();
            bundle.putParcelable("respond", response);
            Navigation.findNavController(holder.itemView).navigate(R.id.addRespondFragment,bundle);
        });
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }

    class ResponseHolder extends RecyclerView.ViewHolder{
        TextView description,price;
        ImageView mark;
        public ResponseHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.textView8);
            price = itemView.findViewById(R.id.textView13);
            mark = itemView.findViewById(R.id.imageView3);

            mark.setVisibility(View.GONE);
        }
    }
}
