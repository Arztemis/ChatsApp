package com.example.chatsapp.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.chatsapp.R;
import com.example.chatsapp.activity.AllConstants;
import com.example.chatsapp.activity.OTPReciever;
import com.example.chatsapp.databinding.FragmentVerifyNumberBinding;
import com.example.chatsapp.model.UserModel;
import com.example.chatsapp.permissons.Permissons;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;


public class VerifyNumberFragment extends Fragment {

    private FragmentVerifyNumberBinding binding;
    private String verificationId, pin;
    private FirebaseAuth firebaseAuth;
    private Permissons permissons;
    private DatabaseReference databaseReference;

    public VerifyNumberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVerifyNumberBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {

        //Khởi tạo để dùng các thực thể duy nhất trong class FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        //Khởi tạo permisson
        permissons = new Permissons();
        permissons.requestSms(getActivity());
        //khởi tạo pinView cho OTP Reciever
        new OTPReciever().setPinView(binding.pinView);
        //Lấy dữ liệu được đóng gói qua Bundle từ GetNumberFragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            verificationId = bundle.getString(AllConstants.VERIFICATION_ID);
        }

//        databaseReference = FirebaseDatabase.getInstance().getReference("Y");

        binding.btVerify.setOnClickListener(v -> {
            pin = binding.pinView.getText().toString();
//            Log.d("DUCKHANH", "Pin: " + binding.pinView.getText().toString());
            binding.progressLayout.setVisibility(View.VISIBLE);
            binding.progressBar.start();
            verifyPin(pin);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AllConstants.SMS_REQUSET_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permisson SMS accecpt", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getContext(), "Permisson SMS denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void verifyPin(String pin) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, pin);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel userModel = new UserModel("", "", "", firebaseAuth.getCurrentUser().getPhoneNumber()
                        , firebaseAuth.getCurrentUser().getUid(), "online","false");
                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(userModel).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        getFragmentManager().beginTransaction().replace(R.id.container, new UserDataFragment()).commit();
                    } else {
                        Toast.makeText(getContext(), "" + task1.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(getContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}