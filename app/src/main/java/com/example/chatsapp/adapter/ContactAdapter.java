package com.example.chatsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsapp.R;
import com.example.chatsapp.activity.UserInfoActivity;
import com.example.chatsapp.databinding.ContactItemLayoutBinding;
import com.example.chatsapp.model.UserModel;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context mContext;
    private List<UserModel> list;
    private ContactItemLayoutBinding binding;

    public ContactAdapter(Context mContext, List<UserModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.contact_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel userModel = list.get(position);
        //Set data
        holder.layoutBinding.setUserModel(userModel);
        //set sự kiện click cho imagaview Info
        holder.layoutBinding.container.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, UserInfoActivity.class);
            intent.putExtra("userID", userModel.getuID());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ContactItemLayoutBinding layoutBinding;

        public ViewHolder(ContactItemLayoutBinding layoutBinding) {
            super(layoutBinding.getRoot());
            this.layoutBinding = layoutBinding;
        }
    }
}
