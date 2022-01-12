package com.daniilvdovin.iswork.models;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.daniilvdovin.iswork.Core;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Map;
import java.util.function.DoubleToIntFunction;

public class User implements Parcelable {
    public int id,stars;
    public String
            fullName,
            login,
            token,
            location ,
            email,
            password,
            avatar;

    public ArrayList<Task> tasks;
    public ArrayList<Review> reviews;
    public ArrayList<Response> responces;

    public User(){}//Init
    public User(Map<String, Object> json){
        id = ((Double) json.get("id")).intValue();
        stars = ((Double) json.get("stars")).intValue();
        fullName = (String) json.get("fullname");
        login = (String) json.get("login");
        token = (String) json.get("token");
        location = (String) json.get("location");
        email = (String) json.get("email");
        password = (String) json.get("password");
        avatar = (String) json.get("avatar");

        tasks = new ArrayList<>();
        if(json.containsKey("tasks"))
        for (Map<String,Object> element:((ArrayList<Map<String,Object>>) json.get("tasks"))) {
            tasks.add(new Task(element));
        }
        reviews = new ArrayList<>();
        if(json.containsKey("reviews"))
        for (Map<String,Object> element:((ArrayList<Map<String,Object>>) json.get("reviews"))) {
            reviews.add(new Review(element));
        }
        responces = new ArrayList<>();
        if(json.containsKey("responces"))
            for (Map<String,Object> element:((ArrayList<Map<String,Object>>) json.get("responces"))) {
                responces.add(new Response(element));
            }

    }

    protected User(Parcel in) {
        id = in.readInt();
        stars = in.readInt();
        fullName = in.readString();
        login = in.readString();
        token = in.readString();
        location = in.readString();
        email = in.readString();
        password = in.readString();
        avatar = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(stars);
        dest.writeString(fullName);
        dest.writeString(login);
        dest.writeString(token);
        dest.writeString(location);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(avatar);
    }
}
