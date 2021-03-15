package com.example.chatsapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsapp.R;
import com.example.chatsapp.databinding.ChatItemLayoutBinding;
import com.example.chatsapp.databinding.FragmentChatBinding;
import com.example.chatsapp.model.ChatListModel;
import com.example.chatsapp.model.ChatModel;
import com.example.chatsapp.utils.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private Util util;
    private FirebaseRecyclerAdapter<ChatListModel, ViewHolder> firebaseRecyclerAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);
        util = new Util();
        readChat();
        return binding.getRoot();
    }

    private void readChat() {
        Query query = FirebaseDatabase.getInstance().getReference("ChatList").child(util.getUID());

        FirebaseRecyclerOptions<ChatListModel> options = new FirebaseRecyclerOptions.Builder<ChatListModel>()
                .setQuery(query, ChatListModel.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatListModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatListModel model) {
                String userID = model.getMember();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(userID);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Date time = null;
                            String name = snapshot.child("name").getValue().toString();
                            String image = snapshot.child("image").getValue().toString();
                            Calendar calendar = Calendar.getInstance();
                            try {
                                time = Util.sdf().parse(model.getDateTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            assert time != null;
                            calendar.setTime(time);
                            String date = Util.getTimeAgo(calendar.getTimeInMillis());
                            ChatModel chatModel = new ChatModel(model.getChatListID(), name,
                                    model.getLastMessage(), image, date);
                            holder.binding.setChatModel(chatModel);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ChatItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                        R.layout.chat_item_layout, parent, false);
                return new ViewHolder(binding);
            }
        };

        binding.recycleViewChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycleViewChat.setHasFixedSize(false);
        binding.recycleViewChat.setAdapter(firebaseRecyclerAdapter);
//        binding.recycleViewChat.setVisibility(View.VISIBLE);
//        binding.shimmerlayout.shimmerFrameLayout.stopShimmer();
//        binding.shimmerlayout.shimmerFrameLayout.setVisibility(View.GONE);


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ChatItemLayoutBinding binding;

        public ViewHolder(@NonNull ChatItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    @Override
    public void onResume() {
        firebaseRecyclerAdapter.startListening();
        super.onResume();
    }

    @Override
    public void onPause() {
        firebaseRecyclerAdapter.stopListening();
        super.onPause();
    }
}