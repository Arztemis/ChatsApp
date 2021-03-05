package com.example.chatsapp.utils;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Util {
    private FirebaseAuth firebaseAuth;

    public String getUID() {
        firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getUid();
    }

    public String currentData() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM HH:mm");
        return simpleDateFormat.format(calendar.getTimeInMillis());
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

//    private SimpleDateFormat sdf() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM HH:mm");
//    }
}
