package com.example.chatsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatsapp.databinding.ActivityEditNameBinding;

public class EditNameActivity extends AppCompatActivity {

    ActivityEditNameBinding binding;
    private String fName, lName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNameBinding.inflate(getLayoutInflater());

        binding.imgBack.setOnClickListener(v -> onBackPressed());

        String name = getIntent().getStringExtra("name").trim();
        Log.d("DUCKHANH", name);
        if (name.contains(" ")) {
            String[] split = name.split(" ");
            binding.edtFName.setText(split[0]);
            StringBuilder lastName = new StringBuilder();
            for (int i = 1; i < split.length; i++) {
                lastName.append(split[i]).append(" ");
            }
            binding.edtLName.setText(lastName.toString().trim());
        } else {
            binding.edtFName.setText("");
            binding.edtLName.setText(name);
        }

        binding.buttonEditName.setOnClickListener(v -> senDataNameBack());

        setContentView(binding.getRoot());

    }

    @Override
    public void onBackPressed() {
        senDataNameBack();
    }

    private void senDataNameBack() {
        if (checkFname() && checkLname()) {
            Intent intent = new Intent();
            intent.putExtra("name", fName + " " + lName);
            setResult(AllConstants.CODE, intent);
            finish();
        }
    }

    private boolean checkFname() {
        fName = binding.edtFName.getText().toString().trim();
        if (binding.edtFName.getText().toString().isEmpty()) {
            binding.edtFName.setError("Field is required");
            return false;
        } else {
            binding.edtFName.setError(null);
            return true;
        }
    }

    private boolean checkLname() {
        lName = binding.edtLName.getText().toString().trim();
        if (binding.edtLName.getText().toString().isEmpty()) {
            binding.edtLName.setError("Field is required");
            return false;
        } else {
            binding.edtLName.setError(null);
            return true;
        }
    }
}