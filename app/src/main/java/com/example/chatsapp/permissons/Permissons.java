package com.example.chatsapp.permissons;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.chatsapp.activity.AllConstants;

public class Permissons {
    public boolean isStorageOk(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestStorage(Fragment fragment) {
        fragment.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, AllConstants.STORAGE_REQUEST_CODE);
    }

    public boolean isContactOk(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestContact(Fragment fragment) {
        fragment.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}
                , AllConstants.CONTACT_REQUEST_CODE);
    }

    public void requestSms(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECEIVE_SMS}
                , AllConstants.SMS_REQUSET_CODE);
    }

}
