package com.example.zhangzhaoxiang.bottle;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class friend_add extends AppCompatActivity {
    private ListView mLvShow;
    private List<Map<String, String>> dataList;
    private SimpleAdapter adapter;
    private List<String> data;
    Dialog dia;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mLvShow = (ListView) findViewById(R.id.LV_add);
            dataList = getDataList();
            addAdapter adapter = new addAdapter(this, dataList, R.layout.add_item//
                    , new String[]{"name", "number"}//
                    , new int[]{R.id.pname, R.id.pnumber});
            mLvShow.setAdapter(adapter);

            ImageButton but1 = (ImageButton) findViewById(R.id.add_back);
            but1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent show = new Intent(friend_add.this, home.class);
                    show.putExtra("id", 1);
                    startActivity(show);
                    finish();
                }
            });
            mLvShow.setAdapter(adapter);
            //ListView item点击事件
            mLvShow.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                }
            });
        }
    }
    private List<Map<String, String>> getDataList() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts._ID, ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY}//
                , null, null, null);
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<String, String>();
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts._ID));// "_id"
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY));// "display_name"
            map.put("name", name);

            // 联系人号码
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI//
                    , new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}// "data1"
                    , "raw_contact_id=?", new String[]{id}, null);
            if (phoneCursor.moveToNext()) {
                String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                map.put("number", number);
            }

            list.add(map);
        }
        return list;
    }
}