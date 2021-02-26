package com.example.chatsapp.fragment;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatsapp.R;
import com.example.chatsapp.activity.AllConstants;
import com.example.chatsapp.adapter.ContactAdapter;
import com.example.chatsapp.databinding.FragmentContactBinding;
import com.example.chatsapp.model.UserModel;
import com.example.chatsapp.permissons.Permissons;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ContactFragment extends Fragment {

    private FragmentContactBinding binding;
    private DatabaseReference databaseReference;
    private Permissons permissons;
    private ArrayList<UserModel> userContacts, appContacts;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact, container, false);

        permissons = new Permissons();
        userContacts = new ArrayList<>();


        binding.recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycleView.setHasFixedSize(true);

        getUserContact();

        return binding.getRoot();
    }

    private void getUserContact() {
        if (permissons.isContactOk(getContext())) {
            String[] projection = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };

            ContentResolver cr = Objects.requireNonNull(getActivity()).getContentResolver();
            Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
                    null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    number = number.replaceAll("\\s", "");
                    String num = String.valueOf(number.charAt(0));
                    if (num.equals("0")) {
                        number = number.replaceFirst("(?:0)+", "+84");

                        UserModel userModel = new UserModel();
                        userModel.setName(name);
                        userModel.setNumber(number);
                        userContacts.add(userModel);
                    }
                }
                cursor.close();

                getAppContacts(userContacts);
            }

        } else {
            permissons.requestContact(getActivity());
        }
    }

    private void getAppContacts(ArrayList<UserModel> mobileContacts) {
        appContacts = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = databaseReference.orderByChild("number");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    appContacts.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String number = ds.child("number").getValue().toString();

                        for (UserModel userModel : mobileContacts) {

                            if (userModel.getNumber().equals(number)) {

                                String status = ds.child("status").getValue().toString();
                                String uID = ds.child("uID").getValue().toString();
                                String image = ds.child("image").getValue().toString();
                                String name = ds.child("name").getValue().toString();
                                UserModel registeredUser = new UserModel();

                                registeredUser.setName(name);
                                registeredUser.setStatus(status);
                                registeredUser.setImage(image);
                                registeredUser.setuID(uID);
                                appContacts.add(registeredUser);
                                break;
                            }
                        }
                    }
                    ContactAdapter adapter = new ContactAdapter(getContext(), appContacts);
                    binding.recycleView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AllConstants.CONTACT_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserContact();
                } else {
                    Toast.makeText(getContext(), "Contact permisson denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}