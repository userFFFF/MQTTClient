package com.user.mqttclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import static com.user.mqttclient.RecycleViewDivider.VERTICAL_LIST;

public class ContactsActivity extends AppCompatActivity {
    private final String TAG = "ContactsActivity";
    private RecyclerView mContactsRecycleView;
    private ContactsListAdapter mContactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mContactsRecycleView = findViewById(R.id.contacts_list);

        mContactsRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mContactsAdapter = new ContactsListAdapter();
        mContactsRecycleView.setAdapter(mContactsAdapter);
        mContactsAdapter.setOnItemClickListener(new ContactsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(ContactsActivity.this, ChatActivity.class));
                Toast.makeText(getApplicationContext(), "click: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mContactsAdapter.setOnItemLongClickListener(new ContactsListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "long click: " + position, Toast.LENGTH_SHORT).show();
            }
        });
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

    public void updateMessage(String imageSrc, String nickName, String userID) {
        contactsDataModel mContactsDataModel = new contactsDataModel();
        mContactsDataModel.mImageSrc = imageSrc;
        mContactsDataModel.mNickname = nickName;
        mContactsDataModel.mUserID = userID;

        mContactsAdapter.setData(mContactsDataModel);
        mContactsAdapter.notifyDataSetChanged();
        mContactsRecycleView.smoothScrollToPosition(mContactsAdapter.getItemCount());
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
