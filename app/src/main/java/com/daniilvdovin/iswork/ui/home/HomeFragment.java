package com.daniilvdovin.iswork.ui.home;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniilvdovin.iswork.Core;
import com.daniilvdovin.iswork.R;
import com.daniilvdovin.iswork.databinding.FragmentHomeBinding;
import com.daniilvdovin.iswork.models.Category;
import com.daniilvdovin.iswork.models.Task;
import com.daniilvdovin.iswork.ui.home.adapters.TaskAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    TaskAdapter adapter = new TaskAdapter(new ArrayList<Task>());
    ProgressBar wait_bar;
    TextView error_alert;
    CardView allert_bar;
    FloatingActionButton bt_fillters;
    SearchView searchView;
    ArrayList<Task> tasks = new ArrayList<>();

    Category _filter_item;
    boolean _remote = false;
    int _price_min = 0;
    int w_api = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        bt_fillters = root.findViewById(R.id.bt_filters);

        searchView = requireActivity().findViewById(R.id.toolbar).findViewById(R.id.searchview);
        searchView.setVisibility(View.VISIBLE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.search(tasks,query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.search(tasks,newText);
                return false;
            }
        });

        recyclerView = root.findViewById(R.id.rec_task);
        error_alert = root.findViewById(R.id.tv_e_dis);
        allert_bar = root.findViewById(R.id.alert_bar);
        allert_bar.setVisibility(View.GONE);
        wait_bar = root.findViewById(R.id.wait_bar);
        wait_bar.setVisibility(View.VISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_divider_background));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);



        TabLayout tl_select_bar = root.findViewById(R.id.tl_select_bar);
        resetRecView("/task/all");
        tl_select_bar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                w_api = tab.getPosition();
                loadData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        bt_fillters.setOnClickListener((v -> {
            showBottomSheetDialog();
        }));
        return root;
    }

    void resetRecView(String api) {
        tasks.clear();
        wait_bar.setVisibility(View.VISIBLE);
        allert_bar.setVisibility(View.GONE);
        JSONObject object = new JSONObject();
        try {
            object.put("token", Core._user.token.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.update(tasks);
        Core._post(getActivity().getApplicationContext(), api, object,
                (result) -> {
                    synchronized (result) {
                        if (result.get("error") != null) {
                            wait_bar.setVisibility(View.GONE);
                            allert_bar.setVisibility(View.VISIBLE);
                            error_alert.setText(result.get("error").toString());
                            adapter.update(new ArrayList<Task>());
                            return null;
                        }
                        for (Map<String, Object> element : ((ArrayList<Map<String, Object>>) result.get("tasks"))) {
                            if(_filter_item!=null){
                                if(element.get("category")!=null && ((Double)element.get("category")).intValue()==_filter_item.id)
                                    if(((Double)element.get("price")).intValue()>_price_min) {
                                        if (_remote) {
                                            if (element.get("location").equals("0"))
                                                tasks.add(new Task(element));
                                        } else {
                                            tasks.add(new Task(element));
                                        }
                                    }
                            }else{
                                if(((Double)element.get("price")).intValue()>_price_min)
                                    tasks.add(new Task(element));
                            }
                        }
                        wait_bar.setVisibility(View.GONE);
                        adapter.update(tasks);
                    }
                    return null;
                });
    }
    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.fallters_button_bar);
        bottomSheetDialog.setDismissWithAnimation(true);

        Spinner categories = bottomSheetDialog.findViewById(R.id.spinner);
        List<String> cat_text = new ArrayList<>();
        if(Core._categories==null){
            Core._post(getContext(),"/public/categories",new JSONObject(),(result)->{
                Core._categories.clear();
                for (Map<String, Object> element : ((List<Map<String, Object>>) result.get("categories"))) {
                    Category category = new Category(element);
                    if(category!=null) {
                        Core._categories.add(category);
                    }
                }
                synchronized (Core._categories){
                    cat_text.clear();
                    for (Category s:Core._categories) {
                        if(s.parent==0 && s.name!=null){
                            cat_text.add(s.name);
                        }
                    }
                }
                return null;
            });
        }else {
            cat_text.clear();
            for (Category s:Core._categories) {
                if(s.parent==0 && s.name!=null){
                    cat_text.add(s.name);
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, cat_text.toArray());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(adapter);
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _filter_item = Core._categories.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                _filter_item = null;
            }
        };
        categories.setOnItemSelectedListener(itemSelectedListener);

        TextView price_text_min = bottomSheetDialog.findViewById(R.id.textView10);
        SeekBar price = bottomSheetDialog.findViewById(R.id.seekBar2);
        price.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                price_text_min.setText("Стоймость от "+progress+" руб");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                _price_min = seekBar.getProgress();
            }
        });

        Switch is_remote = bottomSheetDialog.findViewById(R.id.switch2);
        is_remote.setOnClickListener(v -> {
            _remote = is_remote.isChecked();
        });

        ((Button)bottomSheetDialog.findViewById(R.id.button4)).setOnClickListener(v->{
            _remote = false;
            _price_min = 0;
            _filter_item = null;
            loadData();
            bottomSheetDialog.cancel();
        });
        ((Button)bottomSheetDialog.findViewById(R.id.button5)).setOnClickListener(v->{
            loadData();
            bottomSheetDialog.cancel();
        });
        bottomSheetDialog.show();
    }
    public void loadData(){
        switch (w_api) {
            case 0:
                resetRecView("/task/all");
                break;
            case 1:
                resetRecView("/task/my");
                break;
            case 2:
                resetRecView("/task/myexecute");
                break;
            default:
                break;
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}