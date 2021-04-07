package com.example.chatsapp.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chatsapp.R;
import com.example.chatsapp.fragment.ChatFragment;
import com.example.chatsapp.fragment.ContactFragment;
import com.example.chatsapp.fragment.ProfileFragment;
import com.example.chatsapp.utils.Util;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class DashBoardActivity extends AppCompatActivity {

    private Fragment fragment = null;
    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        util = new Util();

        ChipNavigationBar navigationBar = findViewById(R.id.navigationChip);

        if (savedInstanceState == null) {
            navigationBar.setItemSelected(R.id.chat, true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.dashboardContainer, new ChatFragment())
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_in)
                    .commit();
        }


        navigationBar.setOnItemSelectedListener(position -> {
            switch (position) {
                case R.id.chat:
                    fragment = new ChatFragment();
                    break;
                case R.id.contacts:
                    fragment = new ContactFragment();
                    break;
                case R.id.profile:
                    fragment = new ProfileFragment();
                    break;
            }

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.dashboardContainer, fragment)
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_in)
                        .commit();
            }

        });
    }

    @Override
    protected void onResume() {
        util.updateOnlineStatus("online");
        super.onResume();
    }

    @Override
    protected void onStop() {
        util.updateOnlineStatus("offline");
        super.onStop();
    }
}