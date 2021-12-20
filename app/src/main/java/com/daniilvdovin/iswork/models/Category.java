package com.daniilvdovin.iswork.models;

import java.util.Map;

public class Category {
    public int id,parent;
    public String name,icon;

    public Category(Map<String,Object> json){
        this.id = ((Double)json.get("id")).intValue();
        this.parent = ((Double)json.get("parent")).intValue();
        this.name = json.get("name").toString();
        if(json.get("icon")!=null)
            this.icon = json.get("icon").toString();
        else
            this.icon = "";
    }

}
