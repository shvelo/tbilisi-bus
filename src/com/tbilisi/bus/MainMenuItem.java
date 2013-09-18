package com.tbilisi.bus;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class MainMenuItem {
    public String label;
    public Drawable icon;
    public Intent intent;

    public MainMenuItem(String label, Drawable icon, Intent intent) {
        this.label = label;
        this.icon = icon;
        this.intent = intent;
    }
}
