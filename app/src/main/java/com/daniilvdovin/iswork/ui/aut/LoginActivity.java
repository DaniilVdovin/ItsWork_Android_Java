package com.daniilvdovin.iswork.ui.aut;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.daniilvdovin.iswork.BuildConfig;
import com.daniilvdovin.iswork.Core;
import com.daniilvdovin.iswork.MainActivity;
import com.daniilvdovin.iswork.R;
import com.daniilvdovin.iswork.models.Category;
import com.daniilvdovin.iswork.models.User;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText e_Login, e_Password;
    Button b_LogIn, b_SingIn;
    ProgressBar wait_bar, main;
    View card_login;

    boolean needUpdate = false;
    SharedPreferences preferences;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = getSharedPreferences("ItsWork_Main",MODE_PRIVATE);

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        e_Login = findViewById(R.id.et_login);
        e_Password = findViewById(R.id.et_password);

        b_LogIn = findViewById(R.id.b_login);
        b_SingIn = findViewById(R.id.b_registration);
        b_SingIn.setVisibility(View.GONE);

        wait_bar = findViewById(R.id.wait_login_bar);
        wait_bar.setVisibility(View.GONE);

        card_login = findViewById(R.id.login_card);
        card_login.setVisibility(View.GONE);

        main = findViewById(R.id.main_wait_bar);
        main.setVisibility(View.VISIBLE);

        Core._post(getApplicationContext(),"/public/version",new JSONObject(),(res)->{
            Log.e("V",res.get("version").toString()+"!=" +versionCode);
            needUpdate = (((Double)res.get("version")).intValue() != versionCode);
            if(!needUpdate) {
                card_login.setVisibility(View.VISIBLE);
                b_SingIn.setVisibility(View.VISIBLE);
                main.setVisibility(View.GONE);
                Core._categories.clear();
                Core._post(getApplicationContext(), "/public/categories", new JSONObject(), (result) -> {
                    for (Map<String, Object> element : ((List<Map<String, Object>>) result.get("categories"))) {
                        Category category = new Category(element);
                        if (category != null) {
                            Core._categories.add(category);
                        }
                    }
                    return null;
                });
                b_LogIn.setOnClickListener((view) -> {
                    b_LogIn.setClickable(false);
                    //Login
                    if (e_Login.getText().toString() == "" || e_Login.getText().toString() == null)
                        return;
                    if (e_Password.getText().toString() == "" || e_Password.getText().toString() == null)
                        return;
                    wait_bar.setVisibility(View.VISIBLE);
                    try {
                        JSONObject param = new JSONObject();
                        param.put("login", e_Login.getText().toString());
                        param.put("password", e_Password.getText().toString());
                        Core._post(this, "/login", param,
                                (result) -> {
                                    synchronized (result) {
                                        b_LogIn.setClickable(true);
                                        if (result.get("error") != null) {

                                        } else {
                                            Core._user = new User(result);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("token",Core._user.token.toString());
                                            editor.apply();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finishAffinity();
                                        }
                                        wait_bar.setVisibility(View.GONE);
                                    }
                                    return null;
                                });
                    } catch (Exception e) {
                        wait_bar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                });
                b_SingIn.setOnClickListener((view) -> {
                    startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                });
                loginViewToken();
            }else{
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Ой похоже вышла новыя версия")
                        .setMessage("Перейдите в магазин и обновите приложение")
                        .setPositiveButton("Google Play", (dialog1, which) -> {
                            final String appPackageName = getPackageName();
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                            dialog1.cancel();
                        })
                        .setOnCancelListener(dialog12 -> System.exit(0))
                        .show();

            }
            return null;
        });
    }
    void loginViewToken(){
        if(preferences.contains("token")){
            main.setVisibility(View.VISIBLE);
            card_login.setVisibility(View.INVISIBLE);
            b_LogIn.setVisibility(View.INVISIBLE);
            b_SingIn.setVisibility(View.INVISIBLE);
            e_Login.setVisibility(View.INVISIBLE);
            e_Password.setVisibility(View.INVISIBLE);
            wait_bar.setVisibility(View.VISIBLE);
            try {
                JSONObject param = new JSONObject();
                param.put("token", preferences.getString("token",""));
                Core._post(this, "/login", param,
                        (result) -> {
                            synchronized (result) {
                                if (result.get("error") != null) {
                                } else {
                                    Core._user = new User(result);
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finishAffinity();
                                }
                                wait_bar.setVisibility(View.GONE);
                            }
                            return null;
                        });
            } catch (Exception e) {
                wait_bar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }
}