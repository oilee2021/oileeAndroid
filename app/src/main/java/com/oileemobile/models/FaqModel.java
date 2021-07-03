package com.oileemobile.models;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-10 02:52
 **/
public class FaqModel implements Serializable {
    private int id;
    private String title, description;

    public FaqModel(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
