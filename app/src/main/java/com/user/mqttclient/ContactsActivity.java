package com.user.mqttclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.user.mqttclient.RecycleViewDivider.VERTICAL_LIST;

public class ContactsActivity extends AppCompatActivity {
    private final String TAG = "ContactsActivity";
    private RecyclerView mContactsRecycleView;
    private contactsAdapter mContactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mContactsRecycleView = findViewById(R.id.contacts_list);

        mContactsRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mContactsAdapter = new contactsAdapter();
        mContactsRecycleView.setAdapter(mContactsAdapter);
        mContactsRecycleView.addItemDecoration(new RecycleViewDivider(this, VERTICAL_LIST));
        initList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getApplicationContext().sendBroadcast(new Intent("finish"));
    }

    private class contactsAdapter extends RecyclerView.Adapter {
        private List<contactsDataModel> mMessageList = new ArrayList<>();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater mInflater = LayoutInflater.from(ContactsActivity.this);
            RecyclerView.ViewHolder viewHolder;
            View mView = mInflater.inflate(R.layout.item_contacts, parent, false);
            viewHolder = new contactsHolder(mView);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((contactsHolder) holder).mNickname.setText(this.mMessageList.get(position).mNickname);
            ((contactsHolder) holder).mUserID.setText(this.mMessageList.get(position).mUserID);
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

        public void setData(contactsDataModel model) {
            this.mMessageList.add(model);
        }
    }


    public void updateMessage(String imageSrc, String nickName, String userID) {
        contactsDataModel mContactsDataModel = new contactsDataModel();
        mContactsDataModel.mImageSrc = imageSrc;
        mContactsDataModel.mNickname = nickName;
        mContactsDataModel.mUserID = userID;

        mContactsAdapter.setData(mContactsDataModel);
        mContactsAdapter.notifyDataSetChanged();
        //mContactsRecycleView.smoothScrollToPosition(mContactsAdapter.getItemCount());
    }

    public class contactsDataModel {
        public String mImageSrc;
        public String mNickname;
        public String mUserID;
    }

    public void initList() {
        int i = 0;
        while (i < 20) {
            String mNickname = "User-" + i;
            int num = (int) ((Math.random() * 9 + 1) * 100000);
            updateMessage("icon", mNickname, String.valueOf(num));
            i++;
        }
    }
}
