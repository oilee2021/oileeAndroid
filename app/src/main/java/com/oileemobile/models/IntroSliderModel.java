package com.oileemobile.models;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-23 23:58
 **/
public class IntroSliderModel implements Serializable {
    private int imageId;
    private int title, description;

    public IntroSliderModel(@DrawableRes int imageId, @StringRes int title, @StringRes int description) {
        this.imageId = imageId;
        this.title = title;
        this.description = description;
    }

    public int getImageId() {
        return imageId;
    }

    public int getTitle() {
        return title;
    }

    public int getDescription() {
        return description;
    }
}
