package com.daniilvdovin.iswork.ui.task.responds;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.daniilvdovin.iswork.CircleTransform;
import com.daniilvdovin.iswork.Core;
import com.daniilvdovin.iswork.Filters;
import com.daniilvdovin.iswork.R;
import com.daniilvdovin.iswork.models.Response;
import com.daniilvdovin.iswork.models.Task;
import com.daniilvdovin.iswork.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Function;

public class AddRespondFragment extends Fragment {

    Response respond;
    Task task;
    User user;
    boolean isAdd = false;
    TextView fullname;
    RatingBar starBar;
    ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(getArguments().containsKey("respond")){
                respond = getArguments().getParcelable("respond");
                isAdd = false;
            }
            if(getArguments().containsKey("task")){
                task = getArguments().getParcelable("task");
                isAdd = true;
            }
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_respond, container, false);
        EditText disc = view.findViewById(R.id.editTextTextMultiLine3);
        EditText price = view.findViewById(R.id.editTextTextMultiLine2);
        Button send = view.findViewById(R.id.button8);
        Button execut = view.findViewById(R.id.button9);

        View UserBar = view.findViewById(R.id.user_bar);
        fullname = UserBar.findViewById(R.id.tv_user_fullname);
        starBar = UserBar.findViewById(R.id.rb_user_start);
        imageView = UserBar.findViewById(R.id.iv_user_image);

        View TaskBar = view.findViewById(R.id.task_bar);
        TextView task_title = TaskBar.findViewById(R.id.tv_title);
        TextView task_description = TaskBar.findViewById(R.id.tv_dis);
        TextView task_price = TaskBar.findViewById(R.id.tv_price);
        ImageView task_image = TaskBar.findViewById(R.id.i_task_icon);

        loadtask(result->{
            task_title.setText(result.title);
            task_description.setText(result.description);
            task_price.setText("до "+result.price+" ₽");
            int res = android.R.drawable.ic_dialog_dialer;
            switch (result.status){
                case 0: res = R.drawable.ic_item_close;
                    break;
                case 1: res = R.drawable.ic_item_open;
                    break;
                case 2: res = R.drawable.ic_item_waiting;
                    break;
                case 3: res = R.drawable.ic_item_success;
                    break;
                case 4: res = android.R.drawable.ic_menu_save;
                    break;
            }
            task_image.setImageResource(res);
            TaskBar.setOnClickListener(v-> {
                Bundle bundle = new Bundle();
                bundle.putParcelable("task", result);
                Navigation.findNavController(view).navigate(R.id.taskDetails,bundle);
            });
            if(result.executer != 0){
                send.setVisibility(View.GONE);
                execut.setVisibility(View.GONE);
            }
            return null;
        });

        if(isAdd){
            disc.setFilters(new InputFilter[]{Filters.main_filter});
            price.setFilters(new InputFilter[]{Filters.price_filter});

            execut.setVisibility(View.GONE);
            UserBar.setVisibility(View.GONE);

            if(task.executer != 0){
                send.setVisibility(View.GONE);
                execut.setVisibility(View.GONE);
            }
            send.setOnClickListener(v -> {
                JSONObject param = new JSONObject();
                try {
                    param.put("token",Core._user.token);
                    param.put("taskid",task.id);
                    param.put("description",disc.getText().toString());
                    param.put("price",Integer.parseInt(price.getText().toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Core._post(getContext(),"/task/execute",param,result->{
                    synchronized (result){
                        Navigation.findNavController(view).popBackStack();
                    }
                    return null;
                });
            });
        }else {
            disc.setText(respond.description);
            price.setText(respond.price+" руб.");

            disc.setKeyListener(null);
            price.setKeyListener(null);
            price.setEnabled(false);
            disc.setEnabled(false);
            send.setVisibility(View.GONE);

            if(respond.author != 0){
                send.setVisibility(View.GONE);
                execut.setVisibility(View.GONE);
            }



            Picasso.get()
                    .load(Core.Host + "/getAvatar?token=" + Core._user.token + "&id=" + respond.author)
                    .resize(512,512)
                    .centerCrop(1)
                    .transform(new CircleTransform())
                    .into(imageView);
            loaduser();
            execut.setOnClickListener(v -> {
                JSONObject param = new JSONObject();
                try {
                    param.put("token",Core._user.token);
                    param.put("taskid",respond.task);
                    param.put("executor",respond.author);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Core._post(getContext(),"/task/execute/select",param,result->{
                    synchronized (result){
                        Navigation.findNavController(view).popBackStack();
                    }
                    return null;
                });
            });
        }
        return view;
    }
    void loaduser() {
        JSONObject param = new JSONObject();
        try {
            param.put("token", Core._user.token);
            param.put("id", respond.author);
            param.put("all", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Core._post(getContext(), "/getuser", param, (result) -> {
            synchronized (result) {
                user = new User(result);
                fullname.setText(user.fullName);
                starBar.setRating(user.stars);
            }
            return null;
        });
    }
    void loadtask(Function<Task, Object> function){
        JSONObject param = new JSONObject();
        try {
            param.put("token",Core._user.token);
            if(isAdd)
                param.put("id",task.id);
            else
                param.put("id",respond.task);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Core._post(getContext(),"/task/get",param,result->{
            synchronized (result){
                function.apply(new Task(result));
            }
            return null;
        });
    }
}