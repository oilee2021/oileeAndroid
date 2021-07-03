package com.oileemobile.models;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-08-30 01:03
 **/
public class DrawerListItemModel implements Serializable {
    private int icon, title;
    private boolean isSelected;

    public DrawerListItemModel(@DrawableRes int icon, @StringRes int title) {
        this.icon = icon;
        this.title = title;
        this.isSelected = false;
    }

    public int getIcon() {
        return icon;
    }

    public int getTitle() {
        return title;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
