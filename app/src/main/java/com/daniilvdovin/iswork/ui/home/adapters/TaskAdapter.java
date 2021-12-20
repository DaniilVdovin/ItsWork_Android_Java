package com.daniilvdovin.iswork.ui.home.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.daniilvdovin.iswork.R;
import com.daniilvdovin.iswork.models.Task;

import java.io.Serializable;
import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    ArrayList<Task> tasks;

    public TaskAdapter(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
    public void update(ArrayList<Task> tasks){
        this.tasks.clear();
        this.tasks.addAll(tasks);
        this.notifyDataSetChanged();
    }
    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task_activity, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        final Task task = tasks.get(position);
        holder.title.setText(task.title);
        holder.description.setText(task.description.substring(0,task.description.length()>20?20:task.description.length())+(task.description.length()>=20?"...":""));
        holder.price.setText("to "+task.price+" P");

        int res = android.R.drawable.ic_dialog_dialer;
        switch (task.status){
            case 0: res = android.R.drawable.ic_lock_idle_lock;
                break;
            case 1: res = android.R.drawable.ic_dialog_dialer;
                break;
            case 2: res = android.R.drawable.ic_lock_idle_alarm;
                break;
            case 3: res = android.R.drawable.checkbox_on_background;
                break;
            case 4: res = android.R.drawable.ic_menu_save;
                break;
        }
        holder.image.setImageResource(res);
        holder.image.setColorFilter(Color.GRAY);

        holder.itemView.setOnClickListener((v)->{
            Bundle bundle = new Bundle();
            bundle.putParcelable("task", task);
            Navigation.findNavController(holder.itemView).navigate(R.id.taskDetails,bundle);
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskHolder extends RecyclerView.ViewHolder{
        TextView title,description,price;
        ImageView image;
        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            description = itemView.findViewById(R.id.tv_dis);
            price = itemView.findViewById(R.id.tv_price);

            image = itemView.findViewById(R.id.i_task_icon);
        }
    }
}
