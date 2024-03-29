package com.daniilvdovin.iswork;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Entity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.collection.ArraySet;

import com.daniilvdovin.iswork.models.Category;
import com.daniilvdovin.iswork.models.Task;
import com.daniilvdovin.iswork.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.FileEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import okhttp3.MultipartBody;


public class Core {

    public static String Host = "http://itswork.detaltex.ru/api";
    public static User _user;
    public static List<Category> _categories = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void _post(Context context, String api, JSONObject params, Function<Map<String, Object>, Object> function) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("charset","utf-8");
        client.addHeader("Accept-Charset", "UTF-8");
        Log.e("API", api);
        Log.e("PARAMS", params.toString());
        StringEntity entity = new StringEntity(params.toString(), StandardCharsets.UTF_8);
        client.post(context, Core.Host + api, entity, "application/json;charset=utf-8", new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if(response!=null)
                function.apply(JsonToMap(response.toString()));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if(response!=null)
                function.apply(JsonToMap(response.toString()));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                if(responseString!=null)
                function.apply(JsonToMap(responseString));
            }
        });
    }
    public static void _upload(Context context,File file){
        RequestParams params = new RequestParams();
        try {
            params.put("file", file);
        } catch(FileNotFoundException e) {}
        HttpEntity entity = new FileEntity(file);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(context,Core.Host+"/upload",entity,"multipart/form-data", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("IMG",responseString);
            }
        });
    }
    public static void getImage(Context context,Integer id,Function<Bitmap, Object> function){
        JSONObject param = new JSONObject();
        try {
            param.put("token",Core._user.token);
            param.put("id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept-Charset", "UTF-8");
        StringEntity entity = new StringEntity(param.toString(), StandardCharsets.UTF_8);
        client.get(context, Core.Host + "/getAvatar?token="+Core._user.token+"&id="+id, entity, "image/png", new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
                function.apply(bitmap);
            }
        });
    }
    public static Category getCategoryById(int id){
        for (Category c :Core._categories){
           if(c.id == id)
               return c;
        }
        return null;
    }
    public static Map<String, Object> JsonToMap(String response) {
        Log.e("RES STRING", response);
        return new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {
        }.getType());
    }


}
