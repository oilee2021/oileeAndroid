package com.oileemobile.activity;

import android.os.Bundle;
import android.os.Handler;

import androidx.databinding.DataBindingUtil;

import com.oileemobile.R;
import com.oileemobile.activity.technician.TechnicianMainActivity;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_splash);

        Utils.updateFirebaseToken();
        new Handler().postDelayed(() -> {
            if(PrefManager.getBoolean(PrefManager.IS_LOGGED)) {
                if(PrefManager.getInt(PrefManager.USER_TYPE, Constants.TYPE_CUSTOMER) == Constants.TYPE_CUSTOMER) {
                    ActivityController.startActivity(SplashActivity.this, CustomerMainActivity.class, true);
                } else {
                    ActivityController.startActivity(SplashActivity.this, TechnicianMainActivity.class, true);
                }
            } else {
                ActivityController.startActivity(SplashActivity.this, ChooseTypeActivity.class, true);
            }
        }, 2000);
    }
}
