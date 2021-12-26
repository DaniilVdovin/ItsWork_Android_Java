package com.daniilvdovin.iswork.ui.task;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.daniilvdovin.iswork.Core;
import com.daniilvdovin.iswork.MainActivity;
import com.daniilvdovin.iswork.R;
import com.daniilvdovin.iswork.models.Task;
import com.daniilvdovin.iswork.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskDetails extends Fragment {

    Task task;
    TextView status, views, title, price, description, address, category;

    User user;
    View UserBar;
    ImageView imageView;
    TextView fullname;
    RatingBar starBar;

    Button responsed, close, answer, complite;

    public TaskDetails() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.searchView.setVisibility(View.GONE);
        if (getArguments() != null) {
            task = (Task) getArguments().getParcelable("task");
            //reload task
            //loadData();
        }
    }

    @Override
    public void onResume() {
        loadData();
        super.onResume();
    }
    AsyncHttpClient client;
    private void loadData() {
        if(client != null)
            client.cancelAllRequests(true);
        if(responsed != null)
            responsed.setEnabled(false);
        JSONObject param = new JSONObject();
        try {
            param.put("token",Core._user.token);
            param.put("id",task.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        client = Core._post(getContext(),"/task/get",param,result->{
            synchronized (result){
                task = new Task(result);
                if(responsed != null)
                    responsed.setEnabled(true);
            }
            return null;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_details, container, false);
        status = view.findViewById(R.id.tv_task_d_status);
        views = view.findViewById(R.id.tv_task_d_views);
        title = view.findViewById(R.id.tv_task_d_title);
        price = view.findViewById(R.id.tv_task_d_price);
        description = view.findViewById(R.id.tv_task_d_des);
        address = view.findViewById(R.id.tv_task_d_address);
        category = view.findViewById(R.id.tv_task_d_address2);

        UserBar = view.findViewById(R.id.user_bar);
        fullname = UserBar.findViewById(R.id.tv_user_fullname);
        starBar = UserBar.findViewById(R.id.rb_user_start);
        imageView = UserBar.findViewById(R.id.iv_user_image);

        close = view.findViewById(R.id.button2);
        answer = view.findViewById(R.id.button6);
        responsed = view.findViewById(R.id.button);
        complite = view.findViewById(R.id.button7);


        View card_contact = view.findViewById(R.id.cardView4);
        TextView text_contact = card_contact.findViewById(R.id.tv_task_d_des2);


        if (task.author == Core._user.id) {
            if (task.executer == 0 && task.status == 1 && task.author == Core._user.id) {
                close.setVisibility(View.VISIBLE);
                responsed.setVisibility(View.VISIBLE);
            }
            if (task.executer != 0 && task.status == 2)
                complite.setVisibility(View.VISIBLE);
        }
        if (task.executer == Core._user.id) {
           if(task.status == 2){
               complite.setVisibility(View.VISIBLE);
           }
           JSONObject param = new JSONObject();
            try {
                param.put("token",Core._user.token);
                param.put("id",task.id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
           Core._post(getContext(),"/task/getContact",param,result->{
               synchronized (result){
                   Log.e("Contact","msg: "+result);
                   card_contact.setVisibility(View.VISIBLE);
                   text_contact.setText(result.get("contact").toString());
               }
               return null;
           });
        }
        if(task.author != Core._user.id && task.executer != Core._user.id){
            if(task.status == 1){
                answer.setVisibility(View.VISIBLE);
            }
        }
        close.setOnClickListener(v -> {
            int status = (task.executer == 0 ? 0 : 3);
            JSONObject param = new JSONObject();
            try {
                param.put("token", Core._user.token);
                param.put("title", task.title);
                param.put("description", task.description);
                param.put("price", task.price);
                param.put("location", task.location);
                param.put("taskid", task.id);
                param.put("status", status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Core._post(getContext(), "/task/update", param, result -> {
                Navigation.findNavController(view).popBackStack();
                return null;
            });
        });
        answer.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("task", task);
            Navigation.findNavController(view).navigate(R.id.addRespondFragment, bundle);
        });
        complite.setOnClickListener(v -> {
            JSONObject param = new JSONObject();
            try {
                param.put("token",Core._user.token);
                param.put("taskid",task.id);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            Core._post(getContext(),"/task/execute/complite",param,result-> {return null;});
            Navigation.findNavController(this.views).popBackStack();
        });
        responsed.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("task", task);
            Navigation.findNavController(view).navigate(R.id.respondsViewFragment, bundle);
        });
        status.setText(
                task.status == 0
                        ? "закрыто"
                        : task.status == 2
                            ? "В работе"
                            : task.status == 3
                                ? "Выполнено"
                                : "открыто");
        views.setText("" + task.views);
        title.setText(task.title);
        price.setText("до " + task.price + " P");
        description.setText(task.description);

        if(task.location.equals("0")){
            address.setText("Можно выполнить удаленно");
        }else {
            address.setText(task.location);
        }
        category.setText(Core.getCategoryById(task.category).name);

        loaduser();
        return view;
    }

    void loaduser() {
        JSONObject param = new JSONObject();
        try {
            param.put("token", Core._user.token);
            param.put("id", task.author);
            param.put("all",0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Core._post(getContext(), "/getuser", param, (result) -> {
            synchronized (result) {
                user = new User(result);
                fullname.setText(user.fullName);
                starBar.setRating(user.stars);
                Picasso.get().load(Core.Host + "/getAvatar?token=" + Core._user.token + "&id=" + task.author+"&avatar="+Core._user.avatar).into(imageView);
                UserBar.setOnClickListener((v) -> {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", user);
                    Navigation.findNavController(this.views).navigate(R.id.userFragment, bundle);
                });
            }
            return null;
        });

    }
}