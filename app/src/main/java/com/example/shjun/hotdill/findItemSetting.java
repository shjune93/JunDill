package com.example.shjun.hotdill;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class findItemSetting extends AppCompatActivity {
    ListView findItemListView;
    DatabaseHelper databaseHelper=new DatabaseHelper(this,"crawling",null,1);
    SQLiteDatabase database;
    int removePosition;
    ArrayList<String> findItems=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_item_setting);
        findItemListView=(ListView)findViewById(R.id.findItemListView);


        database=databaseHelper.getWritableDatabase();

        findItems=databaseHelper.selectFind(database);


        //기본 어뎁터뷰 사용
        final ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,findItems);
        findItemListView.setAdapter(adapter);

        Button findItemCancelButton=(Button)findViewById(R.id.findItemCancelButton);
        findItemCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





        Button findItemAddButton=(Button)findViewById(R.id.findItemAddButton);
        findItemAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(findItemSetting.this);

                alert.setTitle("찾을항목 추가");
                alert.setMessage("찾을 상품의 이름을 추가해주세요");


                final EditText finditem = new EditText(findItemSetting.this);
                finditem.setSingleLine();
                alert.setView(finditem);

                alert.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // String username = name.getText().toString();
                        return;
                    }
                });


                alert.setNegativeButton("확인",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        databaseHelper.insertFind(database,finditem.getText().toString());
                        //findItems=databaseHelper.selectFind(database);
                        adapter.add(finditem.getText().toString());
                        adapter.notifyDataSetChanged();


                    }
                });

                alert.show();

            }
        });



        findItemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(findItemSetting.this);
                removePosition = position;
                alert_confirm.setMessage("삭제 하시겠습니까?").setCancelable(false).setPositiveButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        }).setNegativeButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'YES'
                                //클릭한 아이템의 문자열을 가져옴
                                String selected_item = (String)adapter.getItem(removePosition);
                                adapter.remove(selected_item);
                                databaseHelper.deleteFind(database,selected_item);
                                adapter.notifyDataSetChanged();

                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
                return true;
            }
        });

    }
}
