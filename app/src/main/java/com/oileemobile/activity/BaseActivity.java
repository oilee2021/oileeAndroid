package com.oileemobile.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.oileemobile.utils.ProgressDialog;
import com.oileemobile.utils.Utils;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-08-16 08:37
 **/
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Window w = getWindow();
        //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        ProgressDialog.hideProgressDialog();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Utils.hideKeyboard(this);
            onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }
}
