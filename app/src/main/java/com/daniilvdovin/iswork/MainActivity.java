package com.daniilvdovin.iswork;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.daniilvdovin.iswork.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public static  SearchView searchView;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.getRoot().findViewById(R.id.toolbar);


        searchView = toolbar.findViewById(R.id.searchview);
        EditText searchEditText = (EditText) searchView.findViewById(R.id.search_src_text);

        searchEditText.setPadding(130,0,0,0);
        searchEditText.setTextColor(getResources().getColor(R.color.black_light));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));


        setSupportActionBar(toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            searchView.clearFocus();
            navController.navigate(item.getItemId());
            mAppBarConfiguration.getOpenableLayout().close();
            switch (item.getItemId()){
                case R.id.nav_home:
                    searchView.setVisibility(View.VISIBLE);
                    break;
                default:
                    searchView.setVisibility(View.GONE);
                    break;
            }
            return true;
        });

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.name_text_header)).setText(Core._user.fullName);
        Picasso.get()
                .load(Core.Host+"/getAvatar?token="+Core._user.token+"&id="+Core._user.id+"&avatar="+Core._user.avatar)
                .transform(new CircleTransform())
                .into(((ImageView)navigationView.getHeaderView(0).findViewById(R.id.imageView)));

        (navigationView.getHeaderView(0)).setOnClickListener((v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", Core._user);
            navController.navigate(R.id.userFragment,bundle);
            mAppBarConfiguration.getOpenableLayout().close();
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}