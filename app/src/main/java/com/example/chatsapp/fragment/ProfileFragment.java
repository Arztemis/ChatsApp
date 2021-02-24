package com.example.chatsapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.chatsapp.databinding.FragmentProfileBinding;
import com.example.chatsapp.model.UserModel;
import com.example.chatsapp.viewmodel.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        profileViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(ProfileViewModel.class);

        Log.d("DUCKHANH", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        Observer<UserModel> observer = new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                binding.setUserModel(userModel);
                String name = userModel.getName();
//                Log.d("DUCKHANH", name);
//                Log.d("DUCKHANH", userModel.getImage());
//                Log.d("DUCKHANH", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                binding.tvProfileNumber.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                if (name.contains(" ")) {
                    String[] split = name.split(" ");
                    binding.tvProfileFName.setText(split[0]);
                    StringBuilder lastName = new StringBuilder();
                    for (int i = 1; i < split.length; i++) {
                        lastName.append(split[i]).append(" ");
                    }
                    binding.tvProfileLName.setText(lastName.toString());
                } else {
                    binding.tvProfileFName.setText("");
                    binding.tvProfileLName.setText(name);
                }
            }
        };
        profileViewModel.getUser().observe(getViewLifecycleOwner(), observer);
        return binding.getRoot();
    }
}