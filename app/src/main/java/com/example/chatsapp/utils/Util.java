package com.example.chatsapp.utils;

import com.google.firebase.auth.FirebaseAuth;

public class Util {
    private FirebaseAuth firebaseAuth;

    public String getUID() {
        firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getUid();
    }
}
