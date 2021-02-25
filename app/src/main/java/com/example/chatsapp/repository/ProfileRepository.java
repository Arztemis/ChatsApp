package com.example.chatsapp.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatsapp.model.UserModel;
import com.example.chatsapp.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileRepository {

    private DatabaseReference databaseReference;
    private Util util = new Util();
    private MutableLiveData<UserModel> mutableLiveData;

    private static ProfileRepository profileRepository;

    public static ProfileRepository getInstance() {
        return profileRepository = new ProfileRepository();
    }

    public LiveData<UserModel> getUser() {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(util.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        mutableLiveData.setValue(userModel);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return mutableLiveData;
    }

    public void editImage(String uri) {
        final UserModel userModel = mutableLiveData.getValue();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(util.getUID());
        Map<String, Object> map = new HashMap<>();
        map.put("image", uri);
        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    userModel.setImage(uri);
                    mutableLiveData.setValue(userModel);
                    Log.d("DUCKHANH", "Cap nhat anh thanh cong");
                } else {
                    Log.d("DUCKHANH", "Cap nhat that bai");
                }
            }
        });
    }

    public void editStatus(String status) {
        final UserModel userModel = mutableLiveData.getValue();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(util.getUID());
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    userModel.setStatus(status);
                    mutableLiveData.setValue(userModel);
                    Log.d("DUCKHANH", "Cap nhat trang thai thanh cong");
                } else {
                    Log.d("DUCKHANH", "Cap nhat trang thai that bai");
                }
            }
        });
    }

    public void edtUserName(String name) {
        final UserModel userModel = mutableLiveData.getValue();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(util.getUID());
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    userModel.setName(name);
                    mutableLiveData.setValue(userModel);
                    Log.d("DUCKHANH", "Cap nhat ten thanh cong");
                } else {
                    Log.d("DUCKHANH", "Cap nhat ten that bai");
                }
            }
        });
    }
}
