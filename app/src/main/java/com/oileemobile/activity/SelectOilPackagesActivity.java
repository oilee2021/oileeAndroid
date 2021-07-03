package com.oileemobile.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.transition.TransitionManager;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.adapters.OilPackagesAdapter;
import com.oileemobile.databinding.ActivitySelectOilPackagesBinding;
import com.oileemobile.interfaces.OnOilPackageSelectListener;
import com.oileemobile.models.OilPackageModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.PackageResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Utils;

public class SelectOilPackagesActivity extends BaseActivity implements View.OnClickListener, OnOilPackageSelectListener {

    private ActivitySelectOilPackagesBinding binding;
    private int selectedCategoryPosition;
    private int selectedPackagePosition;
    private boolean isSpecialRequestSelected;
    private List<OilPackageModel> packages;
    private OilPackagesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_oil_packages);

        setupToolbar();

        selectedCategoryPosition = -1;
        selectedPackagePosition = -1;
        callGetPackagesApi();

        binding.oilPackSubmit.setOnClickListener(this);
        binding.rlSpecialRequest.setOnClickListener(this);
        binding.rlTechnicianDecide.setOnClickListener(this);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.select_oil_packages);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_technician_decide:
                if (selectedCategoryPosition != -1 || selectedPackagePosition != 0) {
                    onOilPackageSelected(-1, 0);
                }
                break;

            case R.id.rl_special_request:
                isSpecialRequestSelected = !isSpecialRequestSelected;
                TransitionManager.beginDelayedTransition(binding.mainView);
                binding.specialRequestTick.setImageResource(isSpecialRequestSelected ? R.drawable.ic_tick : R.drawable.ic_untick);
                break;

            case R.id.oil_pack_submit:
                if (selectedPackagePosition > -1) {
                    if (selectedCategoryPosition > -1) {
                        OilPackageModel.PackagesBean packagesBean = packages.get(selectedCategoryPosition).getPackages().get(selectedPackagePosition);
                        int selectedPackageId = packagesBean.getId();

                        Bundle bundle = getIntent().getExtras();
                        bundle.putInt("package_id", packagesBean.getId());
                        bundle.putDouble("package_price", Double.parseDouble(packagesBean.getPrice().substring(1)));
                        if (isSpecialRequestSelected)
                            ActivityController.startActivity(this, SpecialRequestActivity.class, bundle);
                        else
                            ActivityController.startActivity(this, SpecialInstructionActivity.class, bundle, false, false, false);
                    }
                }
                break;
        }
    }

    @Override
    public void onOilPackageSelected(int categoryPosition, int packagePosition) {
        binding.technicianDecideTick.setImageResource(categoryPosition == -1 ? R.drawable.ic_tick : R.drawable.ic_untick);

        if (selectedCategoryPosition > -1) {
            OilPackageModel.PackagesBean oldPackageBean = packages.get(selectedCategoryPosition).getPackages().get(selectedPackagePosition);
            oldPackageBean.setSelected(false);
            mAdapter.updateItem(selectedCategoryPosition);
        }

        if (categoryPosition > -1) {
            OilPackageModel.PackagesBean newPackageBean = packages.get(categoryPosition).getPackages().get(packagePosition);
            newPackageBean.setSelected(true);
            mAdapter.updateItem(categoryPosition);
        }

        selectedCategoryPosition = categoryPosition;
        selectedPackagePosition = packagePosition;
        binding.oilPackSubmit.setEnabled(true);
    }

    private void callGetPackagesApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_PACKAGES, new ApiCallBack<>(new ApiResponseListener<PackageResponse>() {
            @Override
            public void onApiSuccess(PackageResponse response) {
                if(response.getData() != null) {
                    packages = response.getData();
                    TransitionManager.beginDelayedTransition(binding.mainView);
                    mAdapter = new OilPackagesAdapter(packages, SelectOilPackagesActivity.this);
                    binding.recyclerPackages.setItemAnimator(null);
                    binding.recyclerPackages.addItemDecoration(new DividerItemDecoration(SelectOilPackagesActivity.this, DividerItemDecoration.VERTICAL));
                    binding.recyclerPackages.setAdapter(mAdapter);
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(SelectOilPackagesActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
