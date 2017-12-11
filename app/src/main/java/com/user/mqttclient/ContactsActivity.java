package com.user.mqttclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import static com.user.mqttclient.RecycleViewDivider.VERTICAL_LIST;

public class ContactsActivity extends AppCompatActivity {
    private final String TAG = "ContactsActivity";
    private RecyclerView mContactsRecycleView;
    private ContactsListAdapter mContactsAdapter;
    private SharedPreferences mSharedPre;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mContactsRecycleView = findViewById(R.id.contacts_list);

        mSharedPre = getSharedPreferences(ShareConfig.NAME, Activity.MODE_PRIVATE);
        mEditor = mSharedPre.edit();

        mContactsRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mContactsAdapter = new ContactsListAdapter();
        mContactsRecycleView.setAdapter(mContactsAdapter);
        mContactsAdapter.setOnItemClickListener(new ContactsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                contactsDataModel mContactsInfo;
                mContactsInfo = mContactsAdapter.getContactsInfo(position);
                Log.i(TAG, "mContactsInfo.mImageSrc: " + mContactsInfo.mImageSrc + " mContactsInfo.mNickname:"
                        + mContactsInfo.mNickname + " mContactsInfo.mUserID: " + mContactsInfo.mUserID);
                mEditor.putString(ShareConfig.IMAGESRC, mContactsInfo.mImageSrc);
                mEditor.putString(ShareConfig.NICKNAME, mContactsInfo.mNickname);
                mEditor.putString(ShareConfig.USERID, mContactsInfo.mUserID);
                mEditor.commit();
                startActivity(new Intent(ContactsActivity.this, ChatActivity.class));
            }
        });
        mContactsAdapter.setOnItemLongClickListener(new ContactsListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {

                alertDialogDeleteContacts(position);
//                mContactsAdapter.removeContacts(position);
//                contactsDataModel mContactsDataModel = new contactsDataModel();
//                mContactsDataModel.mImageSrc = "FFF";
//                mContactsDataModel.mNickname = "FFFHHH";
//                mContactsDataModel.mUserID = "999999";
//                mContactsAdapter.changeContacts(position - 2, mContactsDataModel);

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

    public void updateMessage(String imageSrc, String nickName, String userID, int unRead) {
        contactsDataModel mContactsDataModel = new contactsDataModel();
        mContactsDataModel.mImageSrc = imageSrc;
        mContactsDataModel.mNickname = nickName;
        mContactsDataModel.mUserID = userID;
        mContactsDataModel.mUnRead = unRead;

        mContactsAdapter.setData(mContactsDataModel);
        mContactsAdapter.notifyDataSetChanged();
        mContactsRecycleView.smoothScrollToPosition(mContactsAdapter.getItemCount());
    }

    public static class contactsDataModel {
        public String mImageSrc;
        public String mNickname;
        public String mUserID;
        public int mUnRead;
    }

    public void alertDialogDeleteContacts(final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(R.string.txt_contactsDel);
        dialog.setPositiveButton(R.string.txt_contactsDel_yes,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mContactsAdapter.removeContacts(position);
                    }
                });
        dialog.setNegativeButton(R.string.txt_contactsDel_no,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    public void initList() {
        int i = 0;
        while (i < 20) {
            String mNickname = "User-" + i;
            int num = (int) ((Math.random() * 9 + 1) * 100000);
            updateMessage("icon", mNickname, String.valueOf(num), 0);
            i++;
        }
    }
}
