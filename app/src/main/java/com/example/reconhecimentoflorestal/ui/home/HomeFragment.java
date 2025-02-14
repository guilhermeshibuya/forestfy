package com.example.reconhecimentoflorestal.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.example.reconhecimentoflorestal.R;
import com.example.reconhecimentoflorestal.SharedViewModel;
import com.example.reconhecimentoflorestal.ui.MainActivity;
import com.example.reconhecimentoflorestal.ui.camera.CameraFragment;
import com.example.reconhecimentoflorestal.utils.ImageCropperHelper;
import com.google.android.material.button.MaterialButton;
import com.hjq.permissions.XXPermissions;
import com.serenegiant.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener{
    private ImageCropperHelper imageCropperHelper;

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        imageCropperHelper = new ImageCropperHelper(this, viewModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        initListeners(view);
        return view;
    }

    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                imageCropperHelper.launchImageCropper(imageUri);
            }
        }
    });

    private void getImageFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getImage.launch(intent);
    }

    private void initListeners(View view) {
        MaterialButton btnOpenCamera = view.findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setOnClickListener(this);

        MaterialButton btnChooseImage = view.findViewById(R.id.btnChoosePicture);
        btnChooseImage.setOnClickListener(this);
    }

    private void openCamera() {
        List<String> needPermissions = new ArrayList<>();
        needPermissions.add(android.Manifest.permission.CAMERA);
        needPermissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);

        XXPermissions.with(this)
                .permission(needPermissions)
                .request((permissions, all) -> {
                    if (all) {
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frameLayout, new CameraFragment())
                                .addToBackStack(null)
                                .commit();
                    }

                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnOpenCamera) {
           openCamera();
        } else if (v.getId() == R.id.btnChoosePicture) {
            getImageFile();
        }
    }
}