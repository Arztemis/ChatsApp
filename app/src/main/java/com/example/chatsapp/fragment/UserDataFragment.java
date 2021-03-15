package com.example.chatsapp.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.chatsapp.activity.AllConstants;
import com.example.chatsapp.activity.DashBoardActivity;
import com.example.chatsapp.databinding.FragmentUserDataBinding;
import com.example.chatsapp.permissons.Permissons;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class UserDataFragment extends Fragment {

    private FragmentUserDataBinding binding;
    private String storagePath, name, status;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    public UserDataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserDataBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Finishing your profile");

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();
        storagePath = firebaseAuth.getUid() + "Media/Profile_Image/profile";

        binding.userImage.setOnClickListener(v -> {
            if (isStoragePermissonOK()) {
                pickImage();
            }
        });

        binding.btDone.setOnClickListener(v -> {
            if (checkName() && checkStatus() && checkImage()) {
                progressDialog.show();
                uploadData();
            }
        });
    }

    private void uploadData() {
//        imageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/chatsapp-49bea.appspot.com/o/ozUSqX8sAUeuALqDmZA0MP2meE53%2FMedia%2FProfile_Image%2Fprofile?alt=media&token=f3a5d8d4-60a5-4b31-b1c6-a9fb0dab6a44");
        storageReference.child(storagePath).putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
            task.addOnCompleteListener(task1 -> {
                String url = Objects.requireNonNull(task1.getResult()).toString();
                Map<String, Object> map = new HashMap<>();
                map.put("uID", firebaseAuth.getUid());
                map.put("number", firebaseAuth.getCurrentUser().getPhoneNumber());
                map.put("name", name);
                map.put("status", status);
                map.put("image", url);
                databaseReference.child(Objects.requireNonNull(firebaseAuth.getUid())).updateChildren(map).addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(getContext(), DashBoardActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Fail to upload", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    private boolean isStoragePermissonOK() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            new Permissons().requestStorage(UserDataFragment.this);
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AllConstants.STORAGE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                    Toast.makeText(getContext(), "Permisson Accept", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Permisson Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void pickImage() {
        CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(Objects.requireNonNull(getContext()), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                binding.userImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getContext(), error + "", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkName() {
        name = binding.edtUser.getText().toString();
        if (name.isEmpty()) {
            binding.edtUser.setError("Field is required");
            return false;
        } else {
            binding.edtUser.setError(null);
            return true;
        }
    }

    private boolean checkStatus() {
        status = binding.edtUserStatus.getText().toString();
        if (status.isEmpty()) {
            binding.edtUserStatus.setError("Field is required");
            return false;
        } else {
            binding.edtUserStatus.setError(null);
            return true;
        }
    }

    private boolean checkImage() {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Image is required", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}