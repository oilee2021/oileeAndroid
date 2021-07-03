package com.oileemobile.activity.technician;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.TransitionManager;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.oileemobile.BuildConfig;
import com.oileemobile.R;
import com.oileemobile.activity.AboutUsActivity;
import com.oileemobile.activity.BaseActivity;
import com.oileemobile.activity.FaqActivity;
import com.oileemobile.activity.SupportActivity;
import com.oileemobile.adapters.DrawerListAdapter;
import com.oileemobile.databinding.ActivityTechnicianMainBinding;
import com.oileemobile.dialogs.ConfirmationDialog;
import com.oileemobile.fragment.technician.TechnicianMainFragment;
import com.oileemobile.fragment.technician.TechnicianNotificationFragment;
import com.oileemobile.fragment.technician.TechnicianProfileFragment;
import com.oileemobile.models.DrawerListItemModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.ApiResponse;
import com.oileemobile.network.response.technician.OnlineStatusResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class TechnicianMainActivity extends BaseActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, PopupMenu.OnMenuItemClickListener {
    public ActivityTechnicianMainBinding binding;
    private int selectedDrawerItem;
    private List<DrawerListItemModel> itemList;
    private DrawerListAdapter drawerListAdapter;
    private boolean didUserClick;
    private PopupMenu popupMenu;
    private long lastBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_technician_main);

        selectedDrawerItem = -1;
        itemList = new ArrayList<>();
        didUserClick = true;

        setupToolbar();
        setupPopupMenu();
        setupDrawerItemList();
        setupBottomNavigation();
        binding.myToolbar.toolbarBack.setOnClickListener(this);
        binding.bottomNavigation.setSelectedItemId(R.id.navigation_home);
    }

    private void setupPopupMenu() {
        popupMenu = new PopupMenu(this, binding.myToolbar.toolbarMenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_technician_notifications, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
    }

    public void updateHeaderView(int height, boolean showAnimation) {
        if(showAnimation) TransitionManager.beginDelayedTransition(binding.drawerLayout);
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dpToPx(this, height));
        binding.backgroundView.setLayoutParams(layoutParams);
    }

    public void updateHeaderView(int height) {
        updateHeaderView(height, true);
    }

    public void updateUserImage() {
        Utils.loadPicassoImage(PrefManager.getString(PrefManager.USER_IMAGE), binding.navView.drawerUserPic, null);
    }

    private void showBottomBar() {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) binding.bottomNavigationSection.getLayoutParams();
        HideBottomViewOnScrollBehavior slideBehavior = (HideBottomViewOnScrollBehavior) layoutParams.getBehavior();
        slideBehavior.slideUp(binding.bottomNavigationSection);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (fragment instanceof TechnicianNotificationFragment) {
            ((TechnicianNotificationFragment) fragment).callClearNotificationsApi();
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        callGetOnlineStatusApi();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back:
                openDrawer();
                break;

            case R.id.toolbar_menu:
                popupMenu.show();
                break;
        }
    }

    private void setupDrawerItemList() {
        itemList.add(new DrawerListItemModel(R.drawable.ic_home_oilee_grey, R.string.home));
        itemList.add(new DrawerListItemModel(R.drawable.ic_profile_oilee, R.string.my_profile));
        itemList.add(new DrawerListItemModel(R.drawable.ic_upcomingjob_oilee, R.string.upcoming_jobs));
        itemList.add(new DrawerListItemModel(R.drawable.ic_payment_oilee, R.string.booking_history));
        //itemList.add(new DrawerListItemModel(R.drawable.ic_vehicle, R.string.check_in_out));
        itemList.add(new DrawerListItemModel(R.drawable.ic_support_oilee, R.string.support));
        itemList.add(new DrawerListItemModel(R.drawable.ic_faqs_oilee, R.string.faq));
        itemList.add(new DrawerListItemModel(R.drawable.ic_about_oilee, R.string.about_us));
        itemList.add(new DrawerListItemModel(R.drawable.ic_logout_oilee, R.string.logout));

        binding.navView.drawerList.setLayoutManager(new LinearLayoutManager(this));
        drawerListAdapter = new DrawerListAdapter(itemList, (position) -> {
            closeDrawer();
            boolean isActivated = true;
            if(selectedDrawerItem != position) {
                switch (position) {
                    case 0:
                        binding.bottomNavigation.setSelectedItemId(R.id.navigation_home);
                        break;

                    case 1:
                        binding.bottomNavigation.setSelectedItemId(R.id.navigation_profile);
                        break;

                    case 2:
                        if(Utils.isTechnicianActivated())
                            ActivityController.startActivity(TechnicianMainActivity.this, UpcomingJobsActivity.class);
                        else
                            isActivated = false;
                        break;

                    case 3:
                        ActivityController.startActivity(TechnicianMainActivity.this, TechnicianBookingHistory.class);
                        break;

                    /*case 4:
                        if(Utils.isTechnicianActivated())
                            ActivityController.startActivity(TechnicianMainActivity.this, CheckInOutActivity.class);
                        else
                            isActivated = false;
                        break;*/

                    /*case 5:
                        ActivityController.startActivity(TechnicianMainActivity.this, MyVehiclesActivity.class);
                        break;*/

                    case 4:
                        ActivityController.startActivity(TechnicianMainActivity.this, SupportActivity.class);
                        break;

                    case 5:
                        ActivityController.startActivity(TechnicianMainActivity.this, FaqActivity.class);
                        break;

                    case 6:
                        ActivityController.startActivity(TechnicianMainActivity.this, AboutUsActivity.class);
                        break;

                    case 7:
                        new ConfirmationDialog(R.string.logout, R.string.logout_confirmation, R.string.logout, R.string.cancel, which -> {
                            if(which == DialogInterface.BUTTON_POSITIVE) {
                                Utils.logoutUser(this, false);
                            }
                        }).show(getSupportFragmentManager(), "");
                        break;
                }

                if(!isActivated) Toast.makeText(this, R.string.under_review, Toast.LENGTH_SHORT).show();
            }
        });
        binding.navView.drawerList.setAdapter(drawerListAdapter);

        binding.navView.drawerUserName.setText(PrefManager.getString(PrefManager.USER_NAME));
        binding.navView.drawerUserEmail.setText(PrefManager.getString(PrefManager.USER_EMAIL));
        Utils.loadPicassoImage(PrefManager.getString(PrefManager.USER_IMAGE), binding.navView.drawerUserPic, null);
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(selectedDrawerItem != 0) {
                        switchToMainFragment();
                        drawerListAdapter.notifyDataSetChanged();
                    }
                    return true;

                case R.id.navigation_notifications:
                    if(Utils.isTechnicianActivated()) {
                        if (binding.bottomNavigation.getSelectedItemId() != R.id.navigation_notifications) {
                            switchToOtherFragment(R.string.title_notifications, new TechnicianNotificationFragment(), -1);
                            drawerListAdapter.notifyDataSetChanged();
                        }
                        return true;
                    } else {
                        Toast.makeText(this, R.string.under_review, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                case R.id.navigation_profile:
                    if(selectedDrawerItem != 1) {
                        switchToOtherFragment(R.string.my_profile, new TechnicianProfileFragment(), 1);
                        drawerListAdapter.notifyDataSetChanged();
                    }
                    return true;
            }
            return false;
        });
    }

    private void switchToMainFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new TechnicianMainFragment()).commit();
        binding.myToolbar.toolbarEndSwitch.setVisibility(View.VISIBLE);
        binding.myToolbar.toolbarTitle.setText(R.string.home);
        binding.myToolbar.toolbarMenu.setVisibility(View.GONE);
        showBottomBar();

        if(selectedDrawerItem != -1)
            itemList.get(selectedDrawerItem).setSelected(false);

        selectedDrawerItem = 0;
        itemList.get(0).setSelected(true);
    }

    private void switchToOtherFragment(@StringRes int title, Fragment fragment, int position) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
        binding.myToolbar.toolbarEndSwitch.setVisibility(View.GONE);
        binding.myToolbar.toolbarTitle.setText(title);
        showBottomBar();

        if(selectedDrawerItem != -1)
            itemList.get(selectedDrawerItem).setSelected(false);

        selectedDrawerItem = position;
        if(position != -1) {
            itemList.get(position).setSelected(true);
            binding.myToolbar.toolbarMenu.setVisibility(View.GONE);
        } else {
            binding.myToolbar.toolbarMenu.setVisibility(View.VISIBLE);
        }
    }

    private void setupToolbar() {
        binding.myToolbar.toolbarBack.setImageResource(R.drawable.ic_menu);
        binding.myToolbar.toolbarEndSwitch.setOnCheckedChangeListener(this);
        binding.myToolbar.toolbarMenu.setOnClickListener(this);

        String version = "Ver. " + BuildConfig.VERSION_NAME;
        binding.navView.drawerVersion.setText(version);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.toolbar_end_switch) {
            if(didUserClick) {
                if (isChecked) {
                    callMakeTechnicianOnlineApi();
                } else {
                    callMakeTechnicianOfflineApi();
                }
            }
        }
    }

    private void callMakeTechnicianOnlineApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_TECHNICIAN_ONLINE, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    PrefManager.putBoolean(PrefManager.USER_ONLINE_STATUS, true);
                    binding.myToolbar.toolbarEndSwitch.setText(R.string.technician_online_text);
                } else {
                    Utils.showSnackbar(TechnicianMainActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(TechnicianMainActivity.this, failureMessage);
            }
        }), true);
    }

    private void callMakeTechnicianOfflineApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_TECHNICIAN_OFFLINE, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    PrefManager.putBoolean(PrefManager.USER_ONLINE_STATUS, false);
                    binding.myToolbar.toolbarEndSwitch.setText(R.string.technician_offline_text);
                } else {
                    Utils.showSnackbar(TechnicianMainActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(TechnicianMainActivity.this, failureMessage);
            }
        }), true);
    }

    private void callGetOnlineStatusApi() {
        didUserClick = false;
        ServiceManager.callServerApi(this, ServiceManager.API_CHECK_ONLINE_STATUS, new ApiCallBack<>(new ApiResponseListener<OnlineStatusResponse>() {
            @Override
            public void onApiSuccess(OnlineStatusResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    boolean status = response.getData().isOnline();
                    PrefManager.putBoolean(PrefManager.USER_ONLINE_STATUS, status);
                    binding.myToolbar.toolbarEndSwitch.setChecked(true);
                    binding.myToolbar.toolbarEndSwitch.setText(status ? R.string.technician_online_text : R.string.technician_offline_text);
                    binding.myToolbar.toolbarEndSwitch.setChecked(status);
                }
                didUserClick = true;
            }

            @Override
            public void onApiFailure(String failureMessage) {
                didUserClick = true;
            }
        }), false);
    }

    private void openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START);
    }

    private void closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    private boolean isDrawerOpen() {
        return binding.drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_technician_notifications, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(isDrawerOpen()) {
            closeDrawer();
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
            if(fragment instanceof TechnicianMainFragment) {
                if(System.currentTimeMillis() - lastBackPressed < 2000) {
                    super.onBackPressed();
                } else {
                    lastBackPressed = System.currentTimeMillis();
                    Toast.makeText(this, R.string.exit_confirmation, Toast.LENGTH_SHORT).show();
                }
            } else binding.navView.drawerList.getChildAt(0).callOnClick();
        }
    }
}
