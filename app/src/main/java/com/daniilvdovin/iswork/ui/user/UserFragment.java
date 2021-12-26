package com.daniilvdovin.iswork.ui.user;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.daniilvdovin.iswork.CircleTransform;
import com.daniilvdovin.iswork.Core;
import com.daniilvdovin.iswork.MainActivity;
import com.daniilvdovin.iswork.R;
import com.daniilvdovin.iswork.Tools;
import com.daniilvdovin.iswork.models.Review;
import com.daniilvdovin.iswork.models.User;
import com.daniilvdovin.iswork.ui.user.adapters.ReviewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class UserFragment extends Fragment {

    User user;
    TextView name,aboutme,address;
    ImageView avatar;
    RatingBar stars;
    RecyclerView reviews;
    FloatingActionButton add_review;
    ReviewAdapter reviewAdapter;
    ProgressBar progressBar_upload;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public UserFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MainActivity.searchView.setVisibility(View.GONE);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getParcelable("user");
        }
    }

    @Override
    public void onResume() {
        JSONObject param = new JSONObject();
        try {
            param.put("token", Core._user.token);
            param.put("id",user.id);
            param.put("all",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Core._post(getContext(), "/getuser", param, (result) -> {
            synchronized (result) {
                user = new User(result);
                reviewAdapter.update(user.reviews);
                stars.setRating(user.stars);

                Picasso.get()
                        .load(Core.Host+"/getAvatar?token="+Core._user.token+"&id="+user.id+"&avatar="+user.avatar)
                        .resize(512,512)
                        .centerCrop(1)
                        .transform(new CircleTransform())
                        .into(avatar);
            }
            return null;
        });
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        name = view.findViewById(R.id.tv_user_name);
        aboutme = view.findViewById(R.id.tv_user_aboutme);
        address =view.findViewById(R.id.tv_user_address);
        stars = view.findViewById(R.id.tb_user_stars);
        avatar = view.findViewById(R.id.iv_user_avatar);
        reviews = view.findViewById(R.id.rec_reviews);
        add_review = view.findViewById(R.id.fab_add_review);
        progressBar_upload = view.findViewById(R.id.progressBar);

        name.setText(user.fullName);
        aboutme.setText(user.email);
        address.setText(user.location);

        progressBar_upload.setVisibility(View.GONE);
        stars.setRating(user.stars);
        reviewAdapter = new ReviewAdapter(user.reviews);
        reviews.setNestedScrollingEnabled(false);
        reviews.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        reviews.setAdapter(reviewAdapter);

        Picasso.get()
                .load(Core.Host+"/getAvatar?token="+Core._user.token+"&id="+user.id+"&avatar="+user.avatar)
                .resize(512,512)
                .centerCrop(1)
                .transform(new CircleTransform())
                .into(avatar);

        if(user.id == Core._user.id){
            avatar.setOnClickListener(v -> {
                verifyStoragePermissions(getActivity());
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 2);
            });
            add_review.setVisibility(View.GONE);
        }else{
            add_review.setVisibility(View.VISIBLE);
            add_review.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                Navigation.findNavController(view).navigate(R.id.addReviewFragment,bundle);
            });
        }

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = imageReturnedIntent.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                Bitmap bitmap = BitmapFactory.decodeFile(imgDecodableString);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,10,bos);
                Bitmap comp = BitmapFactory.decodeStream(new ByteArrayInputStream(bos.toByteArray()));

                File outputFile = new File(Environment.getExternalStorageDirectory()
                        + "/photo.jpg");
                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    outputStream.write(bos.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Tools.PicassoClear.clearCache(getActivity().getApplicationContext());

                Core._upload(imgDecodableString,progressBar_upload);

                //avatar.setImageBitmap(comp);
                Picasso.get()
                        .load(selectedImage)
                        .resize(512,512)
                        .centerCrop(1)
                        .transform(new CircleTransform())
                        .into(avatar);
            }
        }
    }
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}