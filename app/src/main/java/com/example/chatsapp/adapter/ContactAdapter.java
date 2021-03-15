package com.example.chatsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsapp.R;
import com.example.chatsapp.activity.MessageActivity;
import com.example.chatsapp.activity.UserInfoActivity;
import com.example.chatsapp.databinding.ContactItemLayoutBinding;
import com.example.chatsapp.model.UserModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private List<UserModel> list, filterArrayList;
    private ContactItemLayoutBinding binding;

    public ContactAdapter(Context mContext, List<UserModel> list) {
        this.mContext = mContext;
        this.list = list;
        filterArrayList = new ArrayList<>();
        filterArrayList.addAll(list);
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
        holder.layoutBinding.imgContact.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, UserInfoActivity.class);
            intent.putExtra("userID", userModel.getuID());
            mContext.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, MessageActivity.class);
            intent.putExtra("hisID", userModel.getuID());
            intent.putExtra("hisName", userModel.getName());
            intent.putExtra("hisImage", userModel.getImage());
//            Log.d("DUCKHANH", userModel.getImage());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return contactFilter;
    }

    private final Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<UserModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(filterArrayList);
            } else {
                String filter = constraint.toString().toLowerCase().trim();
                for (UserModel user : filterArrayList) {
                    if (user.getName().toLowerCase().contains(filter)) {
                        filteredList.add(user);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends UserModel>) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ContactItemLayoutBinding layoutBinding;

        public ViewHolder(ContactItemLayoutBinding layoutBinding) {
            super(layoutBinding.getRoot());
            this.layoutBinding = layoutBinding;
        }
    }
}
