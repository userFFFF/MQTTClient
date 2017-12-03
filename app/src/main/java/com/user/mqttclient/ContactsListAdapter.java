package com.user.mqttclient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user0 on 2017/12/3.
 */

public class ContactsListAdapter extends RecyclerView.Adapter {
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private List<ContactsActivity.contactsDataModel> mMessageList = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;
        View mView = mInflater.inflate(R.layout.item_contacts, parent, false);
        viewHolder = new contactsHolder(mView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((contactsHolder) holder).mNickname.setText(this.mMessageList.get(position).mNickname);
        ((contactsHolder) holder).mUserID.setText(this.mMessageList.get(position).mUserID);

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemLongClickListener.onItemLongClick(holder.itemView, position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.mMessageList.size();
    }

    private class contactsHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mNickname;
        TextView mUserID;

        public contactsHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.icon);
            mNickname = itemView.findViewById(R.id.nickname);
            mUserID = itemView.findViewById(R.id.userID);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public void setData(ContactsActivity.contactsDataModel model) {
        this.mMessageList.add(model);
    }
}
