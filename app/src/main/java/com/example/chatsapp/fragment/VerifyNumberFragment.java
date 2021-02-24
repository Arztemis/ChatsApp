package com.example.chatsapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.chatsapp.R;
import com.example.chatsapp.activity.AllConstants;
import com.example.chatsapp.databinding.FragmentVerifyNumberBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;


public class VerifyNumberFragment extends Fragment {

    private FragmentVerifyNumberBinding binding;
    private String verificationId, pin;
    private FirebaseAuth firebaseAuth;

    public VerifyNumberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_verify_number, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {

        //Khởi tạo để dùng các thực thể duy nhất trong class FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //Lấy dữ liệu được đóng gói qua Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            verificationId = bundle.getString(AllConstants.VERIFICATION_ID);
        }

        binding.btVerify.setOnClickListener(v -> {

            checkPin();
            if (checkPin()) {
                binding.progressLayout.setVisibility(View.VISIBLE);
                binding.progressBar.start();

                verifyPin(pin);
            }
        });
    }

    private boolean checkPin() {

        pin = Objects.requireNonNull(binding.pinView.getText()).toString();
        if (pin.isEmpty()) {
            binding.pinView.setError("Enter the PIN");
            return false;
        } else if (pin.length() < 6) {
            binding.pinView.setError("Invalid OTP");
            return false;
        } else {
            binding.pinView.setError(null);
            return true;
        }
    }

    private void verifyPin(String pin) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, pin);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                getFragmentManager().beginTransaction().replace(R.id.container, new UserDataFragment()).commit();
            } else {
                Toast.makeText(getContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                binding.btVerify.setEnabled(false);
            }
        });
    }
}