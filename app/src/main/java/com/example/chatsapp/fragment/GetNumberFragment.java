package com.example.chatsapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.chatsapp.R;
import com.example.chatsapp.activity.AllConstants;
import com.example.chatsapp.databinding.FragmentGetNumberBinding;
import com.example.chatsapp.permissons.Permissons;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class GetNumberFragment extends Fragment {

    private FragmentGetNumberBinding binding;
    private String number;
    private Permissons permissons;

    public GetNumberFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_get_number, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {

        permissons = new Permissons();
        permissons.requestContact(getActivity());
        permissons.requestStorage(getActivity());

        binding.btGenerate.setOnClickListener(v -> {
            checkNumber();
            if (checkNumber()) {
                String phoneNumber = binding.codePicker.getSelectedCountryCodeWithPlus() + number;
                sendOTP(phoneNumber);
            }
        });
    }

    private boolean checkNumber() {

        number = binding.edtNumber.getText().toString();

        if (number.isEmpty()) {
            binding.edtNumber.setError("Enter number");
            return false;
        } else if (number.length() < 10) {
            binding.edtNumber.setError("Invalid Number");
            return false;
        } else {
            binding.edtNumber.setError(null);
            return true;
        }
    }

    private void sendOTP(String phoneNumber) {
        binding.progressLayout.setVisibility(View.VISIBLE);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder()
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(getActivity())
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {

                    if (e instanceof FirebaseAuthInvalidCredentialsException)
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    else if (e instanceof FirebaseTooManyRequestsException)
                        Toast.makeText(getContext(), "The SMS quota for the project has been exceeded ", Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    binding.progressLayout.setVisibility(View.GONE);

                }

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                    Toast.makeText(getContext(), "Verification code sent..", Toast.LENGTH_LONG).show();

                    Fragment fragment = new VerifyNumberFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(AllConstants.VERIFICATION_ID, s);
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment)
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_in)
                            .commit();
                    binding.progressLayout.setVisibility(View.VISIBLE);
                    binding.btGenerate.setEnabled(false);
                }
            };

}