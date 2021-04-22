package com.example.chatsapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatsapp.BR;
import com.example.chatsapp.R;
import com.example.chatsapp.databinding.ActivityMessageBinding;
import com.example.chatsapp.databinding.LeftItemLayoutBinding;
import com.example.chatsapp.databinding.RightItemLayoutBinding;
import com.example.chatsapp.model.ChatListModel;
import com.example.chatsapp.model.MessageModel;
import com.example.chatsapp.utils.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    private ActivityMessageBinding binding;
    private String hisID, hisImage, myID, chatID, hisName, myImage = null;
    private Util util;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<MessageModel, ViewHolder> firebaseRecyclerAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_message, null, false);
        initView();
        setContentView(binding.getRoot());
    }

    private void initView() {
        util = new Util();
        myID = util.getUID();
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        myImage = sharedPreferences.getString("userImage", "");


        if (getIntent().hasExtra("chatID")) {
            chatID = getIntent().getStringExtra("chatID");
            hisImage = getIntent().getStringExtra("hisImage");
            hisName = getIntent().getStringExtra("hisName");
            hisID = getIntent().getStringExtra("hisID");
            //Curios : Xài checkchat thì layout sẽ đọc setStackFromEnd, còn readMess thì không được
            checkChat(hisID);
        } else {
            hisID = getIntent().getStringExtra("hisID");
            hisName = getIntent().getStringExtra("hisName");
            hisImage = getIntent().getStringExtra("hisImage");
        }

        binding.setImage(hisImage);
        binding.setName(hisName);
        binding.setActivity(MessageActivity.this);

        if (chatID == null) {
            checkChat(hisID);
        }

        binding.imgSend.setOnClickListener(v -> {
            String message = binding.msgText.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(MessageActivity.this, "Write message..", Toast.LENGTH_SHORT).show();
            } else {
                sendMessage(message);
                binding.msgText.setText("");
                getToken(message, myID, myImage, hisName, chatID);
            }
        });

        binding.msgText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0)
                    updateTypingStatus("false");
                else updateTypingStatus(hisID);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        checkOnline(hisID);
    }

    private void checkChat(final String hisID) {
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(myID);
        Query query = databaseReference.orderByChild("member").equalTo(hisID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String id = ds.child("member").getValue().toString();
                        if (id.equals(hisID)) {
                            chatID = ds.getKey();
                            readMessages(chatID);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createChat(String msg) {
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(myID);
        chatID = databaseReference.push().getKey();
        ChatListModel chatListModel = new ChatListModel(chatID, util.currentDate(), msg, hisID);
        databaseReference.child(chatID).setValue(chatListModel);

        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(hisID);
        ChatListModel chatList = new ChatListModel(chatID, util.currentDate(), msg, myID);
        databaseReference.child(chatID).setValue(chatList);

        databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(chatID);
        MessageModel messageModel = new MessageModel(myID, hisID, msg, util.currentDate(), "text");
        databaseReference.push().setValue(messageModel);


    }

    private void sendMessage(String msg) {
        if (chatID == null) {
            createChat(msg);

        } else {
            String date = util.currentDate();
            MessageModel messageModel = new MessageModel(myID, hisID, msg, date, "text");
            databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(chatID);
            databaseReference.push().setValue(messageModel);

            Map<String, Object> map = new HashMap<>();
            map.put("lastMessage", msg);
            map.put("dateTime", date);
            databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(myID).child(chatID);
            databaseReference.updateChildren(map);

            databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(hisID).child(chatID);
            Map<String, Object> update = new HashMap<>();
            update.put("lastMessage", msg);
            update.put("dateTime", date);
            databaseReference.updateChildren(update);
        }
    }

    public void userInfo() {
        Intent intent = new Intent(MessageActivity.this, UserInfoActivity.class);
        intent.putExtra("userID", hisID);
        startActivity(intent);
    }

    private void readMessages(String chatID) {
        Query query = FirebaseDatabase
                .getInstance().getReference().child("Chat")
                .child(chatID)
                .limitToLast(50);
        FirebaseRecyclerOptions<MessageModel> options = new FirebaseRecyclerOptions.Builder<MessageModel>()
                .setQuery(query, MessageModel.class).build();
        query.keepSynced(true);


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MessageModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull MessageModel messageModel) {
                switch (getItemViewType(position)) {
                    case 0:
                        viewHolder.viewDataBinding.setVariable(BR.messageImage, hisImage);
                        viewHolder.viewDataBinding.setVariable(BR.message, messageModel);
                        break;
                    case 1:
                        viewHolder.viewDataBinding.setVariable(BR.messageImage, hisImage);
                        viewHolder.viewDataBinding.setVariable(BR.message, messageModel);
                        break;
                }
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ViewDataBinding viewDataBinding = null;
                switch (viewType) {
                    case 0:
                        viewDataBinding = RightItemLayoutBinding.inflate(
                                LayoutInflater.from(getBaseContext()), parent, false);
                        break;
                    case 1:
                        viewDataBinding = LeftItemLayoutBinding.inflate(
                                LayoutInflater.from(getBaseContext()), parent, false);
                        break;
                }
                return new ViewHolder(viewDataBinding);

            }

            @Override
            public int getItemViewType(int position) {
                MessageModel messageModel = getItem(position);
                if (myID.equals(messageModel.getSender())) {
                    return 0;
                } else {
                    return 1;
                }
            }
        };

        binding.recyclerViewMessage.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerViewMessage.setLayoutManager(linearLayoutManager);
        binding.recyclerViewMessage.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding viewDataBinding;

        public ViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.viewDataBinding = viewDataBinding;
        }
    }

    @Override
    protected void onResume() {
        util.updateOnlineStatus("online");
        super.onResume();
    }

    @Override
    protected void onPause() {
        updateTypingStatus("false");
        super.onPause();
    }

    private void checkOnline(String hisID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(hisID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String online = snapshot.child("online").getValue().toString();
                    binding.setStatus(online);
                    String typing = snapshot.child("typing").getValue().toString();
                    if (typing.equals(myID)) {
                        binding.typingStatus.setVisibility(View.VISIBLE);
                        binding.typingStatus.playAnimation();
                    } else {
                        binding.typingStatus.setVisibility(View.GONE);
                        binding.typingStatus.cancelAnimation();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateTypingStatus(String status) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(myID);
        Map<String, Object> map = new HashMap<>();
        map.put("typing", status);
        databaseReference.updateChildren(map);
    }

    private void getToken(String message, String myID, String hisImage, String hisName, String chatID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(hisID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();
                String name = snapshot.child("name").getValue().toString();

                JSONObject to = new JSONObject();
                JSONObject data = new JSONObject();
                try {
                    data.put("title", name);
                    data.put("message", message);
                    data.put("hisID", myID);
                    data.put("hisName", hisName);
                    data.put("hisImage", hisImage);
                    data.put("chatID", chatID);

                    to.put("to", token);
                    to.put("data", data);

                    sendNotification(to);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(JSONObject to) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AllConstants.NOTIFICATION_URL, to,
                response -> {
                    Log.d("DUCKHANH", "sendNotifcation" + response);
                },
                error -> {
                    Log.d("DUCKHANH", "sendNotifcation" + error);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //Chung ta se add SERVERKEY va APLLICATION/TYPE vao project
                Map<String, String> map = new HashMap<>();
                map.put("Authorization", "key=" + AllConstants.SERVER_KEY);
                map.put("Content-Type", "application/json");
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Retry Policy dung de xu ly timeouts cua Volley,
        // cho truong hop request khong duoc hoan thanh do ket noi mang hoac case khac
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }
}
