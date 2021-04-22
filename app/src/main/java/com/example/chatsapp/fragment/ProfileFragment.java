package com.example.chatsapp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.chatsapp.R;
import com.example.chatsapp.activity.AllConstants;
import com.example.chatsapp.activity.EditNameActivity;
import com.example.chatsapp.databinding.FragmentProfileBinding;
import com.example.chatsapp.model.UserModel;
import com.example.chatsapp.permissons.Permissons;
import com.example.chatsapp.utils.Util;
import com.example.chatsapp.viewmodel.ProfileViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private Uri imageUri;
    private Util util;
    private Permissons permissons;
    private AlertDialog alertDialog;
    private UserModel user;
    private SharedPreferences.Editor sharedPreferences;

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

        util = new Util();
        sharedPreferences = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE).edit();
        permissons = new Permissons();

        //Đăng ký observer theo mô hình MVVM để theo dõi livedata
//        Log.d("DUCKHANH", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber());
        Observer<UserModel> observer = new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                binding.setUserModel(userModel);
                user = userModel;
                String name = userModel.getName();
//                Log.d("DUCKHANH", name);
//                Log.d("DUCKHANH", userModel.getImage());
//                Log.d("DUCKHANH", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                binding.tvProfileNumber.setText(userModel.getNumber());
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
        };
        profileViewModel.getUser().observe(getViewLifecycleOwner(), observer);

        //Check quyền users cho truy cập storage để cập nhật ảnh đại diện
        binding.imgProfile.setOnClickListener(view -> {
            if (permissons.isStorageOk(getContext())) {
                pickImage();
            } else {
                permissons.requestStorage(ProfileFragment.this);
            }
        });

        //Tạo 1 AlertDialog, mục đích hiện thị diaglog_layout để cập nhật status
        binding.imgEditStatus.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View view1 = LayoutInflater.from(getContext()).inflate(R.layout.dialog_layout, null);
            builder.setView(view1);

            EditText editText = view1.findViewById(R.id.edtStatus);
            Button button = view1.findViewById(R.id.buttonDone);

            editText.setText(binding.tvProfileStatus.getText().toString());

            button.setOnClickListener(v -> {
                String status = editText.getText().toString().trim();
                if (!status.isEmpty()) {
                    profileViewModel.editStatus(status);
                    alertDialog.dismiss();
                }

            });

            alertDialog = builder.create();
            alertDialog.show();

        });

        binding.cardName.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), EditNameActivity.class);
            intent.putExtra("name", user.getName());
            startActivityForResult(intent, AllConstants.CODE);
        });

        return binding.getRoot();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AllConstants.STORAGE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                } else {
                    Toast.makeText(getContext(), "Storage permisson rejected", Toast.LENGTH_SHORT).show();
                }
        }
    }


    //Override lại method này để nhận dữ liệu trả về, luôn đi kèm với phương thức startActivityforResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    imageUri = result.getUri();
                    binding.imgProfile.setImageURI(imageUri);
                    uploadImage(imageUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(getContext(), error + "", Toast.LENGTH_SHORT).show();
                }
                break;
            case AllConstants.CODE:
                String name = data.getStringExtra("name");
                profileViewModel.edtUserName(name);
                sharedPreferences.putString("name", name).apply();

                break;
        }

    }

    private void pickImage() {
        CropImage.activity()
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(getContext(), ProfileFragment.this);
    }

    private void uploadImage(Uri imageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(util.getUID())
                .child(AllConstants.IMAGE_PATH);

        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                task.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String uri = task.getResult().toString();
                        profileViewModel.editImage(uri);
                        sharedPreferences.putString("userImage", uri).apply();
                    }
                });
            }
        });

    }
}