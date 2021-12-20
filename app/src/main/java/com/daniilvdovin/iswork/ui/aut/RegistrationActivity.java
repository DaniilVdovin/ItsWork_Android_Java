package com.daniilvdovin.iswork.ui.aut;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.daniilvdovin.iswork.Core;
import com.daniilvdovin.iswork.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class RegistrationActivity extends AppCompatActivity {

    ImageView avatar;
    EditText fullname, email, address, pass1, pass2;
    Button commit;
    byte[] avatar_bytes;
    Drawable bck;

    boolean isEmailOk,isPassOk;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        avatar = findViewById(R.id.iv_reg_avatar);
        fullname = findViewById(R.id.et_reg_fullname);
        email = findViewById(R.id.et_reg_email);
        address = findViewById(R.id.et_reg_address);

        pass1 = findViewById(R.id.et_reg_password);
        pass2 = findViewById(R.id.et_reg_password_again);

        bck = pass2.getBackground();

        commit = findViewById(R.id.bt_commit);

        GradientDrawable errr_shape = new GradientDrawable();
        errr_shape.setShape(GradientDrawable.RECTANGLE);
        errr_shape.setCornerRadii(new float[]{15, 15, 15, 15, 15, 15, 15, 15});

        verifyStoragePermissions(RegistrationActivity.this);

        avatar.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 2);
        });

        //EmailCorrector
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("@")) {
                    if (s.toString().split("@").length >= 2) {
                        if (s.toString().split("@")[1].contains(".")) {
                            errr_shape.setColor(Color.rgb(0, 240, 0));
                            errr_shape.setStroke(3, Color.rgb(0, 190, 0));
                            isEmailOk = true;
                            email.setBackground(errr_shape);
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    RegistrationActivity.this.runOnUiThread(() -> {
                                        email.setBackground(bck);
                                    });
                                }
                            }, 3000);
                            return;
                        }
                    }
                }
                isEmailOk = false;
                errr_shape.setColor(Color.rgb(240, 0, 0));
                errr_shape.setStroke(3, Color.rgb(190, 0, 0));
                email.setBackground(errr_shape);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        RegistrationActivity.this.runOnUiThread(() -> {
                            email.setBackground(bck);
                        });
                    }
                }, 3000);
                return;
            }
        });

        //PassCorrector
        pass1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    pass1.setBackground(bck);
                    return;
                }
                if (s.toString().length() < 6) {
                    errr_shape.setColor(Color.argb(190, 240, 0, 0));
                    errr_shape.setStroke(3, Color.rgb(190, 0, 0));
                } else {
                    errr_shape.setColor(Color.rgb(0, 240, 0));
                    errr_shape.setStroke(3, Color.rgb(0, 190, 0));
                }
                pass1.setBackground(errr_shape);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        RegistrationActivity.this.runOnUiThread(() -> {
                            pass1.setBackground(bck);
                        });
                    }
                }, 3000);
            }
        });
        pass2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("S", s.toString() + "=" + pass1.getText().toString());
                if (!s.toString().contains(pass1.getText().toString())) {
                    errr_shape.setColor(Color.rgb(240, 0, 0));
                    errr_shape.setStroke(3, Color.rgb(190, 0, 0));
                    isPassOk = false;
                } else {
                    errr_shape.setColor(Color.rgb(0, 240, 0));
                    errr_shape.setStroke(3, Color.rgb(0, 190, 0));
                    isPassOk = true;
                }
                pass2.setBackground(errr_shape);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        RegistrationActivity.this.runOnUiThread(() -> {
                            pass2.setBackground(bck);
                        });
                    }
                }, 3000);
            }
        });

        commit.setOnClickListener(v -> {
            if(avatar_bytes==null)return;
            if(!isEmailOk && !isPassOk)return;
            if(address.getText().toString().length()<1)return;
            if(fullname.getText().toString().length()<1)return;

            JSONObject param = new JSONObject();
            try {
                param.put("login",email.getText().toString());
                param.put("password",pass1.getText().toString());
                param.put("fullname",fullname.getText().toString());
                param.put("location",address.getText().toString());
                param.put("email",email.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Core._post(getApplicationContext(),"/register",param,(result)->{
                synchronized (result){
                    File outputFile = new File("photo.jpg");
                    try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                        outputStream.write(avatar_bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Core._upload(getApplicationContext(),outputFile);
                    onBackPressed();
                }
                return null;
            });
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = imageReturnedIntent.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                Bitmap bitmap = BitmapFactory.decodeFile(imgDecodableString);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,10,bos);
                Bitmap comp = BitmapFactory.decodeStream(new ByteArrayInputStream(bos.toByteArray()));
                avatar_bytes = bos.toByteArray();
                avatar.setImageBitmap(comp);
            }
        }
    }
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}