package com.example.secuscan2;

import android.graphics.drawable.Drawable;

public class AppList {

    // Make fields private and final for immutability
    private final String name;
    private final Drawable icon;

    // Constructor to initialize fields
    public AppList(String name, Drawable icon) {
        this.name = name;
        this.icon = icon;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for icon
    public Drawable getIcon() {
        return icon;
    }
}
