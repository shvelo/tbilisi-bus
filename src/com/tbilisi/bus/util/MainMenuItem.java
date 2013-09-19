package com.tbilisi.bus.util;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class MainMenuItem {
    public String label;
    public Drawable icon;
    public Intent intent;
    public boolean enabled;

    public MainMenuItem(String label, Drawable icon, boolean enabled, Intent intent) {
        this.label = label;
        this.icon = icon;
        this.intent = intent;
        this.enabled = enabled;
    }
}
