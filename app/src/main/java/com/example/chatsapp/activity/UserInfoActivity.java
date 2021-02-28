package com.example.chatsapp.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatsapp.databinding.ActivityUserInfoBinding;
import com.example.chatsapp.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfoActivity extends AppCompatActivity {

    private ActivityUserInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String uID = getIntent().getStringExtra("userID");

        getUserDetail(uID);

    }

    private void getUserDetail(String uID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(uID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    binding.setUserModel(userModel);

                    String name = userModel.getName();
                    if (name.contains(" ")) {
                        String[] split = name.split(" ");
                        binding.tvProfileFName.setText(split[0]);
                        StringBuilder lastName = new StringBuilder();
                        for (int i = 1; i < split.length; i++) {
                            lastName.append(split[i]).append(" ");
                        }
                        binding.tvProfileLName.setText(lastName.toString().trim());
                    } else {
                        binding.tvProfileFName.setText("");
                        binding.tvProfileLName.setText(name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}