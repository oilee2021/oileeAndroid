package com.oileemobile.dialogs;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.oileemobile.R;
import com.oileemobile.databinding.FragmentUploadCarImagesBinding;
import com.oileemobile.interfaces.OnUploadCarImages;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

import static android.app.Activity.RESULT_OK;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-09 00:59
 **/
public class UploadCarImagesDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private FragmentUploadCarImagesBinding binding;
    private OnUploadCarImages mListener;
    private File[] files;
    private ImageView lastClickedView;
    private Uri cameraPhotoUri;
    private Context mContext;
    private int size = 0;

    public UploadCarImagesDialogFragment() {}

    public static UploadCarImagesDialogFragment newInstance(int size, String message) {
        Bundle args = new Bundle();
        args.putInt("size", size);
        args.putString("message", message);
        UploadCarImagesDialogFragment fragment = new UploadCarImagesDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_car_images, container, false);

        if (getArguments() != null) {
            size = getArguments().getInt("size", 0);
            binding.textUploadImagesTitle.setText(getString(R.string.upload_four_vehicle_images, size));
            binding.textUploadImagesSubtitle.setText(getArguments().getString("message"));
        }

        files = new File[size];
        for (int i = 0; i < size; i++) {
            ImageView view = (ImageView) inflater.inflate(R.layout.layout_image_add, binding.linearAddPhotos, false);
            binding.linearAddPhotos.addView(view);
            int finalI = i;
            view.setOnClickListener(v -> {
                lastClickedView = view;
                lastClickedView.setTag(finalI);
                showImageSelectDialog();
            });
        }

        binding.buttonSubmit.setOnClickListener(this);

        return binding.getRoot();
    }

    private void checkValidationAndProceed() {
        for (int i = 0; i < size; i++) {
            if (files[i] == null) {
                Toast.makeText(getContext(), "Please select " + size + " images.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if(mListener != null) mListener.onImagesSelected(files);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_submit:
                checkValidationAndProceed();
                break;
        }
    }

    private void showImageSelectDialog() {
        if(files[(int) lastClickedView.getTag()] == null) {
            if(Utils.checkCameraPermission(mContext)) {
                getImageFromCamera();
            } else {
                Utils.requestCameraPermission(this);
            }
        } else {
            showRemovePictureDialog();
        }
    }

    private void showRemovePictureDialog() {
        ConfirmationDialog.OnClickListener clickListener = (which) -> {
            if(which == DialogInterface.BUTTON_POSITIVE) {
                int position = (int) lastClickedView.getTag();
                files[position] = null;
                lastClickedView.setTag(null);
                lastClickedView.setImageResource(R.drawable.ic_add);
            }
        };

        new ConfirmationDialog(R.string.remove_picture, R.string.remove_picture_text,
                R.string.remove, R.string.cancel, clickListener)
                .show(getChildFragmentManager(), "");
    }

    private void getImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File file = null;
            try {
                file = createImageFile();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(file != null) {
                cameraPhotoUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri);
                startActivityForResult(Intent.createChooser(takePictureIntent, "Choose a camera app"), Constants.REQUEST_CODE_CAMERA_PICTURE);
            }
        }
    }

    private File createImageFile() {
        File myFile = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            /*for (File file : storageDir.listFiles())
                file.delete();*/

            myFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return myFile;
    }

    private void onImageSelected(File file) {
        files[(int) lastClickedView.getTag()] = file;
        Utils.loadPicassoImage(file, lastClickedView, null);
    }

    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                onImageSelected(new File(resultUri.getPath()));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }
        } else if (requestCode == Constants.REQUEST_CODE_CAMERA_PICTURE
                && resultCode == RESULT_OK) {
            Utils.startImagePicker(this, cameraPhotoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utils.CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImageFromCamera();
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    new InformationDialog(R.string.mission_permission, R.string.missing_camera_permission, R.string.open_settings,
                            this::openAppSettings).show(getChildFragmentManager(), "");
                }
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if(context instanceof OnUploadCarImages) {
            mListener = (OnUploadCarImages) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mContext = null;
    }
}
