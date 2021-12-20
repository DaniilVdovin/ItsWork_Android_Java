package com.daniilvdovin.iswork.models;

import java.util.Map;

public class Review {
    public int id,author,recipient,star,data;
    public String description,username;

    public Review(Map<String,Object> json) {
        this.id = ((Double)json.get("id")).intValue();
        this.author = ((Double)json.get("author")).intValue();
        this.recipient = ((Double)json.get("recipient")).intValue();
        this.star = ((Double)json.get("star")).intValue();
        this.data = ((Double)json.get("data")).intValue();
        this.description = json.get("description").toString();
        this.username = json.get("username").toString();
    }
}
