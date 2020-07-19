package com.ProLabs.arstudyboard.Utility;

import com.google.gson.annotations.SerializedName;

public class ItemList {
    @SerializedName("name")
    private String name;

    public ItemList(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
