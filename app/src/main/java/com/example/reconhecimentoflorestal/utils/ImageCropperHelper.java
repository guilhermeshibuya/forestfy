package com.example.reconhecimentoflorestal.utils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.example.reconhecimentoflorestal.R;
import com.example.reconhecimentoflorestal.SharedViewModel;
import com.example.reconhecimentoflorestal.ui.results.ResultsFragment;
import com.serenegiant.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ImageCropperHelper {
    private final ActivityResultLauncher<CropImageContractOptions> cropImageLauncher;
    private final Context context;
    private final SharedViewModel viewModel;

    public ImageCropperHelper(Fragment fragment, SharedViewModel viewModel) {
        this.context = fragment.requireContext();
        this.viewModel = viewModel;

        this.cropImageLauncher = fragment.registerForActivityResult(new CropImageContract(), result -> {
           if (result.isSuccessful()) {
               Bitmap cropped = BitmapFactory.decodeFile(result.getUriFilePath(context, true));
               saveImage(cropped);
               deleteTempFile();
           }
        });
    }

    public void launchImageCropper(Uri uri) {
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = true;
        cropImageOptions.imageSourceIncludeCamera = true;

        cropImageOptions.autoZoomEnabled = true;

        cropImageOptions.toolbarColor = Color.rgb(90, 194,121);
        cropImageOptions.activityMenuTextColor = Color.rgb(0, 22,9);
        cropImageOptions.toolbarBackButtonColor = Color.rgb(0, 22,9);
        cropImageOptions.activityMenuIconColor = Color.rgb(0, 22,9);

        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(uri, cropImageOptions);

        cropImageLauncher.launch(cropImageContractOptions);
    }

    public void saveImage(Bitmap bitmap) {
        File file = FileUtils.getCaptureFile(
                context,
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
            context.getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            MediaScannerConnection.scanFile(context.getApplicationContext(), new String[]{ file.getAbsolutePath()}, null, null);

            Toast.makeText(
                    context.getApplicationContext(),
                    context.getString(R.string.save_image_success),
                    Toast.LENGTH_SHORT).show();

            Bitmap cropped = BitmapFactory.decodeFile(file.getAbsolutePath());
            viewModel.setImage(cropped);

            ((AppCompatActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, new ResultsFragment())
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            Toast.makeText(
                    context.getApplicationContext(),
                    context.getString(R.string.save_image_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTempFile() {
        File tempFile = new File(context.getCacheDir(), "temp.jpg");
        if (tempFile.exists() && !tempFile.delete()) {
            Log.e("CROP IMAGE", "Erro ao excluir a imagem temporária");
        }
    }
}
