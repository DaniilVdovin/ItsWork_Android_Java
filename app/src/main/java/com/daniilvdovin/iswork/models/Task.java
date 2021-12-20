package com.daniilvdovin.iswork.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;

public class Task implements Parcelable {

    public int  id,
                author,
                executer,
                price,
                status,
                views,
                category;
    public String   location,
                    title,
                    description;


    public ArrayList<Response> responses;

    public Task(){}//Init
    public Task(Map<String, Object> json) {
        id = ((Double) json.get("id")).intValue();
        author = ((Double) json.get("author")).intValue();
        executer = ((Double) json.get("executer")).intValue();
        price = ((Double) json.get("price")).intValue();
        status = ((Double) json.get("status")).intValue();
        views = ((Double) json.get("views")).intValue();
        category = ((Double) json.get("category")).intValue();

        location = json.get("location").toString();
        title = json.get("title").toString();
        description = json.get("description").toString();

        responses = new ArrayList<>();
        for (Map<String,Object> element:((ArrayList<Map<String,Object>>) json.get("responces"))) {
            responses.add(new Response(element));
        }
    }

    protected Task(Parcel in) {
        id = in.readInt();
        author = in.readInt();
        executer = in.readInt();
        price = in.readInt();
        status = in.readInt();
        views = in.readInt();
        category = in.readInt();
        location = in.readString();
        title = in.readString();
        description = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(author);
        dest.writeInt(executer);
        dest.writeInt(price);
        dest.writeInt(status);
        dest.writeInt(views);
        dest.writeInt(category);
        dest.writeString(location);
        dest.writeString(title);
        dest.writeString(description);
    }
}
