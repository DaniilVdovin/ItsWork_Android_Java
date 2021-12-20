package com.daniilvdovin.iswork.ui.task.responds;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daniilvdovin.iswork.R;
import com.daniilvdovin.iswork.models.Task;
import com.daniilvdovin.iswork.ui.task.responds.adapters.RespondsAdapter;
import com.daniilvdovin.iswork.ui.user.adapters.ReviewAdapter;

public class RespondsViewFragment extends Fragment {
    public Task task;

    public RespondsViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            task = getArguments().getParcelable("task");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_responds_view, container, false);
        RecyclerView responds = view.findViewById(R.id.rec_responds);

        RespondsAdapter reviewAdapter = new RespondsAdapter(task.responses,task.executer);
        responds.setNestedScrollingEnabled(false);
        responds.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        responds.setAdapter(reviewAdapter);

        return view;
    }
}