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
import com.google.android.material.button.MaterialButton;
import com.hjq.permissions.XXPermissions;
import com.serenegiant.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener{
    private SharedViewModel viewModel;

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        initListeners(view);
        return view;
    }

    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(
            new CropImageContract(),
            result -> {
                if (result.isSuccessful()) {
                    Bitmap cropped = BitmapFactory.decodeFile(result.getUriFilePath(requireContext(), true));
                    saveImage(cropped);
                }
            });

    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                launchImageCropper(imageUri);
            }
        }
    });

    private void launchImageCropper(Uri uri) {
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = true;
        cropImageOptions.imageSourceIncludeCamera = true;

        cropImageOptions.autoZoomEnabled = true;

        cropImageOptions.toolbarColor = Color.rgb(90, 194,121);
        cropImageOptions.activityMenuTextColor = Color.rgb(0, 22,9);
        cropImageOptions.toolbarBackButtonColor = Color.rgb(0, 22,9);
        cropImageOptions.activityMenuIconColor = Color.rgb(0, 22,9);

        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(uri, cropImageOptions);

        cropImage.launch(cropImageContractOptions);
    }

    private void getImageFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getImage.launch(intent);
    }

    private void saveImage(Bitmap bitmap) {
        File file = FileUtils.getCaptureFile(
                requireContext().getApplicationContext(),
                Environment.DIRECTORY_DCIM,
                ".jpg");
        try {
            OutputStream outputStream = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            MediaScannerConnection.scanFile(requireContext(), new String[]{ file.getAbsolutePath()}, null, null);

            Toast.makeText(
                    requireContext(),
                    getString(R.string.save_image_success),
                    Toast.LENGTH_SHORT).show();

            Bitmap cropped = BitmapFactory.decodeFile(file.getAbsolutePath());
            viewModel.setImage(cropped);

            ((MainActivity) requireActivity()).switchToResultsFragment();
        } catch (Exception e) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.save_image_error),
                    Toast.LENGTH_SHORT).show();
        }
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