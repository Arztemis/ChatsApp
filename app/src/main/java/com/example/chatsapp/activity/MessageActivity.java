package com.example.chatsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    private ActivityMessageBinding binding;
    private String hisID, hisImage, myID, chatID, hisName = null;
    private Util util;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<MessageModel, ViewHolder> firebaseRecyclerAdapter;

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

        hisID = getIntent().getStringExtra("hisID");
        hisImage = getIntent().getStringExtra("hisImage");
        hisName = getIntent().getStringExtra("hisName");

        binding.setImage(hisImage);
        binding.setName(hisName);
        binding.setActivity(this);

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
            }
        });


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
            map.put("date", date);
            databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(myID).child(chatID);
            databaseReference.updateChildren(map);

            databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(hisID).child(chatID);
            Map<String, Object> update = new HashMap<>();
            update.put("lastMessage", msg);
            update.put("date", date);
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
                .child(chatID);
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
        binding.recyclerViewMessage.setAdapter(firebaseRecyclerAdapter);
        binding.recyclerViewMessage.smoothScrollToPosition(binding.recyclerViewMessage.getAdapter().getItemCount());
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStart() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerViewMessage.setLayoutManager(linearLayoutManager);
        super.onStart();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding viewDataBinding;

        public ViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.viewDataBinding = viewDataBinding;
        }
    }


}
