package com.daniilvdovin.iswork.ui.task.createtask;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.daniilvdovin.iswork.Core;
import com.daniilvdovin.iswork.R;
import com.daniilvdovin.iswork.models.Category;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateTaskFragment extends Fragment {

    EditText name,price,address,description,date,contact;
    CheckBox isRemote;
    Button send;
    Spinner categories;
    int cat;

    public CreateTaskFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_task, container, false);
        name = view.findViewById(R.id.et_task_name);
        price = view.findViewById(R.id.et_task_price);
        address = view.findViewById(R.id.et_task_address);
        description = view.findViewById(R.id.et_task_dis);
        date = view.findViewById(R.id.et_task_date);
        contact = view.findViewById(R.id.et_task_dis2);

        isRemote = view.findViewById(R.id.cb_remote);

        categories = view.findViewById(R.id.spinner2);
        List<String> cat_text = new ArrayList<>();
        if(Core._categories==null){
            Core._post(getContext(),"/public/categories",new JSONObject(),(result)->{
                for (Map<String, Object> element : ((List<Map<String, Object>>) result.get("categories"))) {
                    Category category = new Category(element);
                    if(category!=null) {
                        Core._categories.add(category);
                    }
                }
                synchronized (Core._categories){
                    for (Category s:Core._categories) {
                        if(s.parent==0 && s.name!=null){
                            cat_text.add(s.name);
                        }
                    }
                }
                return null;
            });
        }else {
            for (Category s:Core._categories) {
                if(s.parent==0 && s.name!=null){
                    cat_text.add(s.name);
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, cat_text.toArray());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(adapter);

        send = view.findViewById(R.id.bt_task_send);
        send.setOnClickListener((v)->{
            JSONObject param = new JSONObject();
            try {
                param.put("token",Core._user.token);
                param.put("title",name.getText().toString());
                param.put("description",description.getText().toString());
                param.put("contact",contact.getText().toString());
                param.put("location",address.getText().toString());
                param.put("price",Integer.parseInt(""+price.getText().toString()));
                param.put("category",Core._categories.get(categories.getSelectedItemPosition()).id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Core._post(getContext(),"/task/add",param,(result)->{
                synchronized (result){
                    Navigation.findNavController(view).navigate(R.id.nav_home);
                }
                return null;
            });
        });

        return view;
    }
}