package com.oileemobile.helpers;

import android.view.View;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-05-15 00:59
 **/
public class PicassoCallback implements Callback {

    private ProgressBar progressBar;

    public PicassoCallback(ProgressBar progressBar) {
        this.progressBar = progressBar;
        if(progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSuccess() {
        if(progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onError(Exception e) {
        if(progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
