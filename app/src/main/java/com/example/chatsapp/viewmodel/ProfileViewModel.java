package com.example.chatsapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatsapp.model.UserModel;
import com.example.chatsapp.repository.ProfileRepository;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<UserModel> userModelMutableLiveData = new MutableLiveData<>();
    ProfileRepository profileRepository = ProfileRepository.getInstance();

    public LiveData<UserModel> getUser() {
        return profileRepository.getUser();
    }

}
