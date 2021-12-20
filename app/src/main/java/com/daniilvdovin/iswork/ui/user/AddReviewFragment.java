package com.daniilvdovin.iswork.ui.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.daniilvdovin.iswork.Core;
import com.daniilvdovin.iswork.R;
import com.daniilvdovin.iswork.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddReviewFragment extends Fragment {

    User user;
    public AddReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_review, container, false);
        Button send = view.findViewById(R.id.button3);
        EditText text = view.findViewById(R.id.editTextTextMultiLine);
        RatingBar stars = view.findViewById(R.id.ratingBar2);

        stars.setStepSize(1);

        send.setOnClickListener(v -> {
            if (stars.getRating()==0)return;
            if (text.getText().toString().isEmpty())return;
            send.setClickable(false);

            String filtered = Pattern.compile("(^ *$\\n*)", Pattern.MULTILINE).matcher(text.getText().toString()).replaceAll("");

            JSONObject param = new JSONObject();
            try {
                param.put("token", Core._user.token);
                param.put("recipient",user.id);
                param.put("description",filtered);
                param.put("star",stars.getRating());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Core._post(getContext(),"/getme/reviews/add",param,result->{
                synchronized (result){
                    Navigation.findNavController(view).popBackStack();
                    send.setClickable(true);
                }
                return null;
            });
        });

        return view;
    }
}