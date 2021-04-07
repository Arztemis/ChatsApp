package com.example.chatsapp.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.chatsapp.R;
import com.example.chatsapp.fragment.GetNumberFragment;
import com.example.chatsapp.utils.Util;

public class MainActivity extends AppCompatActivity {

    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        util = new Util();
        //Khởi tạo Fragment GetNumber ở MainActivity, add nó vào ngay khi MainActivity được khởi động

//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        Fragment fragment = new GetNumberFragment();
//        ft.add(R.id.container, fragment).commit();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new GetNumberFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, fragment)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_in)
                .commit();

    }

}