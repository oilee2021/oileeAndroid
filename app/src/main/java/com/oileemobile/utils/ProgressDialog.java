package com.oileemobile.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.oileemobile.R;

public class ProgressDialog {
    //private static ProgressDialog progressDialog;
    private static AlertDialog alertDialog;

    private ProgressDialog() {}

    /*public static ProgressDialog getInstance() {
        if(progressDialog == null)
            progressDialog = new ProgressDialog();

        return progressDialog;
    }*/

    public static void showProgressDialog(Context context) {
        if(alertDialog != null && alertDialog.isShowing()) {
            return;
        }

        View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog_layout, null, false);
        alertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .create();

        if(alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (!(context instanceof Activity && ((Activity) context).isFinishing()))
            alertDialog.show();
    }

    public static void hideProgressDialog() {
        if(alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();
    }
}
