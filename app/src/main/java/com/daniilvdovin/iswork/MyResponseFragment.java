package com.daniilvdovin.iswork;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daniilvdovin.iswork.models.User;
import com.daniilvdovin.iswork.ui.task.responds.adapters.RespondsAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MyResponseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_response, container, false);
        RecyclerView responds = view.findViewById(R.id.rec_responds);


        responds.setNestedScrollingEnabled(false);
        responds.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        JSONObject param = new JSONObject();
        try {
            param.put("token", Core._user.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Core._post(getContext(), "/getme", param, (result) -> {
            Core._user = new User(result);
            synchronized (result) {
                RespondsAdapter reviewAdapter = new RespondsAdapter(Core._user.responces);
                responds.setAdapter(reviewAdapter);
            }
            return null;
        });


        return view;
    }
}