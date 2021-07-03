package com.oileemobile.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.oileemobile.BuildConfig;
import com.oileemobile.R;
import com.oileemobile.adapters.DrawerListAdapter;
import com.oileemobile.databinding.ActivityCustomerMainBinding;
import com.oileemobile.dialogs.ConfirmationDialog;
import com.oileemobile.fragment.MainFragment;
import com.oileemobile.fragment.MyProfileFragment;
import com.oileemobile.models.DrawerListItemModel;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CustomerMainActivity extends BaseActivity implements MainFragment.OnMainFragmentInteractionListener,
        View.OnClickListener/*, OnRatingSubmittedListener*/ {

    private ActivityCustomerMainBinding binding;
    private int selectedDrawerItem;
    private List<DrawerListItemModel> itemList;
    private DrawerListAdapter drawerListAdapter;
    private long lastBackPressed;
    /*private Handler mHandler;
    private Runnable mRunnable;
    private InvoiceDialogFragment invoiceDialogFragment;
    private LatestInvoiceModel latestInvoiceModel;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer_main);

        selectedDrawerItem = -1;
        itemList = new ArrayList<>();

        setupToolbar();
        setupDrawerItemList();
        setupBottomNavigation();
        binding.myToolbar.toolbarBack.setOnClickListener(this);
        binding.myToolbar.toolbarEndIcon.setOnClickListener(this);
        /*mHandler = new Handler();
        mRunnable = () -> {
            callGetLatestInvoiceApi();
            mHandler.postDelayed(mRunnable, Constants.MAX_INTERVAL);
        };*/
        binding.bottomNavigation.setSelectedItemId(R.id.navigation_home);
        //callGetLatestInvoiceApi();
    }

    public void updateUserImage() {
        Utils.loadPicassoImage(PrefManager.getString(PrefManager.USER_IMAGE), binding.navView.drawerUserPic, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mHandler.postDelayed(mRunnable, Constants.MAX_INTERVAL);
    }

    @Override
    protected void onStop() {
        //mHandler.removeCallbacks(mRunnable);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back:
                openDrawer();
                break;

            case R.id.toolbar_end_icon:
                ActivityController.startActivity(CustomerMainActivity.this, NotificationActivity.class);
                break;
        }
    }

    private void setupToolbar() {
        binding.myToolbar.toolbarBack.setImageResource(R.drawable.ic_menu);
        binding.myToolbar.toolbarLogo.setVisibility(View.VISIBLE);
        binding.myToolbar.toolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        String version = "Ver. " + BuildConfig.VERSION_NAME;
        binding.navView.drawerVersion.setText(version);
    }

    private void switchToMainFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MainFragment()).commit();
        binding.myToolbar.toolbarLogo.setVisibility(View.VISIBLE);
        binding.myToolbar.toolbarEndIcon.setVisibility(View.VISIBLE);
        binding.myToolbar.toolbarTitle.setVisibility(View.GONE);

        if(selectedDrawerItem != -1)
            itemList.get(selectedDrawerItem).setSelected(false);

        selectedDrawerItem = 0;
        itemList.get(0).setSelected(true);
    }

    private void switchToOtherFragment(@StringRes int title, Fragment fragment, int position) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
        binding.myToolbar.toolbarLogo.setVisibility(View.GONE);
        binding.myToolbar.toolbarEndIcon.setVisibility(View.GONE);
        binding.myToolbar.toolbarTitle.setVisibility(View.VISIBLE);
        binding.myToolbar.toolbarTitle.setText(title);

        if(selectedDrawerItem != -1)
            itemList.get(selectedDrawerItem).setSelected(false);

        selectedDrawerItem = position;
        itemList.get(position).setSelected(true);
    }

    @Override
    public void onOilChangeButtonPressed() {
        ActivityController.startActivity(this, SelectAddressActivity.class);
        //ActivityController.startActivity(this, SelectPaymentMethodActivity.class);
    }

    private void setupDrawerItemList() {
        itemList.add(new DrawerListItemModel(R.drawable.ic_home_oilee_grey, R.string.home));
        itemList.add(new DrawerListItemModel(R.drawable.ic_profile_oilee, R.string.my_profile));
        itemList.add(new DrawerListItemModel(R.drawable.ic_location_oilee, R.string.saved_addresses));
        itemList.add(new DrawerListItemModel(R.drawable.ic_car_oilee, R.string.saved_vehicles));
        itemList.add(new DrawerListItemModel(R.drawable.ic_reminder_oilee, R.string.schedule_a_reminder));
        itemList.add(new DrawerListItemModel(R.drawable.ic_payment_oilee, R.string.saved_payments));
        itemList.add(new DrawerListItemModel(R.drawable.ic_history_oilee, R.string.oilee_history));
        itemList.add(new DrawerListItemModel(R.drawable.ic_report_oilee, R.string.report_an_issue));
        itemList.add(new DrawerListItemModel(R.drawable.ic_invite_oilee, R.string.invite_people));
        itemList.add(new DrawerListItemModel(R.drawable.ic_faqs_oilee, R.string.faq));
        itemList.add(new DrawerListItemModel(R.drawable.ic_about_oilee, R.string.about_us));
        itemList.add(new DrawerListItemModel(R.drawable.ic_logout_oilee, R.string.logout));

        binding.navView.drawerList.setLayoutManager(new LinearLayoutManager(this));
        drawerListAdapter = new DrawerListAdapter(itemList, (position) -> {
            closeDrawer();
            if(selectedDrawerItem != position) {
                switch (position) {
                    case 0:
                        binding.bottomNavigation.setSelectedItemId(R.id.navigation_home);
                        break;

                    case 1:
                        binding.bottomNavigation.setSelectedItemId(R.id.navigation_profile);

                        break;
                    case 6:
                        binding.bottomNavigation.setSelectedItemId(R.id.navigation_history);
                        break;

                    case 5:
                        ActivityController.startActivity(CustomerMainActivity.this, MySavedPaymentActivity.class);
                        break;

                    case 3:
                        ActivityController.startActivity(CustomerMainActivity.this, MyVehiclesActivity.class);
                        break;

                    case 2:
                        ActivityController.startActivity(CustomerMainActivity.this, MyAddressActivity.class);
                        break;

                    case 8:
                        inviteMembers();
                        break;

                    case 4:
                        ActivityController.startActivity(CustomerMainActivity.this, ScheduleReminderActivity.class);
                        break;

                    case 9:
                        ActivityController.startActivity(CustomerMainActivity.this, FaqActivity.class);
                        break;

                    case 10:
                        ActivityController.startActivity(CustomerMainActivity.this, AboutUsActivity.class);
                        break;

                    case 7:
                        ActivityController.startActivity(CustomerMainActivity.this, SupportActivity.class);
                        break;

                    case 11:
                        new ConfirmationDialog(R.string.logout, R.string.logout_confirmation, R.string.logout, R.string.cancel, which -> {
                            if(which == DialogInterface.BUTTON_POSITIVE) {
                                Utils.logoutUser(this, false);
                            }
                        }).show(getSupportFragmentManager(), "");
                        break;
                }
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

                case R.id.navigation_history:
                    if(selectedDrawerItem != 2) {
                        ActivityController.startActivity(CustomerMainActivity.this, MyOrderHistory.class);
                    }
                    return false;

                case R.id.navigation_profile:
                    if(selectedDrawerItem != 1) {
                        switchToOtherFragment(R.string.my_profile, new MyProfileFragment(), 1);
                        drawerListAdapter.notifyDataSetChanged();
                    }
                    return true;
            }
            return false;
        });
    }

    /*private void callGetLatestInvoiceApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_LATEST_INVOICE, new ApiCallBack<>(new ApiResponseListener<LatestInvoiceResponse>() {
            @Override
            public void onApiSuccess(LatestInvoiceResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    latestInvoiceModel = response.getData();
                    boolean showNewDialog = true;
                    if(invoiceDialogFragment != null && invoiceDialogFragment.isVisible()) {
                        if(invoiceDialogFragment.getInvoiceId() == latestInvoiceModel.getId()) {
                            showNewDialog = false;
                        }
                    }

                    if(showNewDialog) {
                        invoiceDialogFragment = InvoiceDialogFragment.newInstance(latestInvoiceModel);
                        invoiceDialogFragment.show(getSupportFragmentManager(), "");
                    }
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {

            }
        }), false);
    }*/

    /*private void callRatingTechnicianApi(String rating, String reviewText) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setRatings(rating);
        apiRequest.setReview_text(reviewText);
        apiRequest.setJob_id(String.valueOf(latestInvoiceModel.getJob_id()));

        ServiceManager.callServerApi(this, ServiceManager.API_RATE_TECHNICIAN, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    new InformationDialog(R.string.success, R.string.rating_submitted, null)
                            .show(getSupportFragmentManager(), "");
                } else {
                    Utils.showSnackbar(CustomerMainActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(CustomerMainActivity.this, failureMessage);
            }
        }), apiRequest);
    }*/

    /*@Override
    public void onRatingSubmitted(String rating, String reviewText) {
        if(invoiceDialogFragment != null) invoiceDialogFragment.dismiss();
        callRatingTechnicianApi(rating, reviewText);
    }*/

    private void inviteMembers() {
        String shareBody = "I found a qualified and experienced technician for my car oil service through Oilee. It's simple and quick and I highly recommend it." +
                "\n\nTo download the app for free, click here..\nhttps://play.google.com/";
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Invite!");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Invite!"));
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
    public void onBackPressed() {
        if(isDrawerOpen()) {
            closeDrawer();
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
            if(fragment instanceof MainFragment)
                if(System.currentTimeMillis() - lastBackPressed < 2000) {
                    super.onBackPressed();
                } else {
                    lastBackPressed = System.currentTimeMillis();
                    Toast.makeText(this, R.string.exit_confirmation, Toast.LENGTH_SHORT).show();
                }
            else binding.navView.drawerList.getChildAt(0).callOnClick();
        }
    }
}
