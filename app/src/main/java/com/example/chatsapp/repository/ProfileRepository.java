package com.example.chatsapp.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatsapp.model.UserModel;
import com.example.chatsapp.utils.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

}
