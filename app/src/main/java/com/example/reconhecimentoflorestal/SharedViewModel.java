package com.example.reconhecimentoflorestal;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.serenegiant.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Bitmap> image = new MutableLiveData<>();

    public void setImage(Bitmap bitmap) {
        image.setValue(bitmap);
    }

    public LiveData<Bitmap> getImage() {
        return image;
    }
}
