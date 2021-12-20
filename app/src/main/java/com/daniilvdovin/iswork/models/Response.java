package com.daniilvdovin.iswork.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class Response implements Parcelable {

    public int id, author,task,price,data;
    public String description;

    public Response(int id, int author, int task, int price, int data, String description) {
        this.id = id;
        this.author = author;
        this.task = task;
        this.price = price;
        this.data = data;
        this.description = description;
    }

    public Response(Map<String, Object> json){
        id = ((Double) json.get("id")).intValue();
        author = ((Double) json.get("author")).intValue();
        task = ((Double) json.get("task")).intValue();
        price = ((Double) json.get("price")).intValue();
        data = ((Double) json.get("data")).intValue();
        description = json.get("description").toString();
    }

    protected Response(Parcel in) {
        id = in.readInt();
        author = in.readInt();
        task = in.readInt();
        price = in.readInt();
        data = in.readInt();
        description = in.readString();
    }

    public static final Creator<Response> CREATOR = new Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };

    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", author=" + author +
                ", task=" + task +
                ", price=" + price +
                ", data=" + data +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(author);
        dest.writeInt(task);
        dest.writeInt(price);
        dest.writeInt(data);
        dest.writeString(description);
    }
}
