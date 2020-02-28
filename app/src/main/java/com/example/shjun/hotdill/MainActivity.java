package com.example.shjun.hotdill;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {


    EditText editText;
    ListView hotDillListView;
    CrawlingAdapter crawlingAdapter;
    //AlarmManager alarmManager;
    DatabaseHelper databaseHelper;
    SQLiteDatabase database;
    ArrayList<CrawlingItem> crawlingItems;
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");


    String now=format.format(new Date());

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");




        setSupportActionBar(toolbar);
        final SharedPreferences pref=getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();// editor에 put 하기

        Boolean firstStart=pref.getBoolean("firstStart",true);
        if(firstStart==true){
            editor.putBoolean("mainService",true);
            editor.putBoolean("alarmService",true);
            editor.putBoolean("firstStart",false);
            editor.commit();
        }






        editText=(EditText) toolbar.findViewById(R.id.menu_text);
        hotDillListView=findViewById(R.id.hotDillListView);
        databaseHelper=new DatabaseHelper(this,"crawling",null,1);
        database=databaseHelper.getWritableDatabase();

        //databaseHelper.dropTable(database);
        //databaseHelper.deleteFound(database);

        databaseHelper.createTable(database);
        databaseHelper.createFindTable(database);
        databaseHelper.createFoundTable(database);



        ArrayList<Integer> ids=databaseHelper.selectFound(database);
        crawlingAdapter=new CrawlingAdapter();
        int count=ids.size();
        for(int i=0;i<count;i++){

            CrawlingItem crawlingItem=databaseHelper.selectData(database,"where id="+ids.get(i)+" order by id desc").get(0);
            crawlingAdapter.addItem(crawlingItem);

        }


        crawlingAdapter.notifyDataSetChanged();
        hotDillListView.setAdapter(crawlingAdapter);


//        Intent serviceIntent=new Intent(MainActivity.this, CrawlingService.class);
//        if(pref.getBoolean("mainService",true)){
////            if (Build.VERSION.SDK_INT >= 26) {
////                startForegroundService(serviceIntent);
////            }
////            else {
////                startService(serviceIntent);
////            }
//            startService(serviceIntent);
//            //서버시작
//            //getApplicationContext().startForegroundService(serviceIntent);
//            Log.d("서버실행상태","시작");
//        }else{
//            stopService(serviceIntent);
//            Log.d("서버실행상태","종료");
//        }


//        Intent serviceIntent=new Intent(MainActivity.this, CrawlingService.class);
//        if(pref.getBoolean("mainService",true)){
//
//            //서버시작
//            startService(serviceIntent);
//        }else{
//            stopService(serviceIntent);
//        }




        TabLayout tabs=(TabLayout)findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("포착된 핫딜"));
        tabs.addTab(tabs.newTab().setText("오늘의 핫딜"));
        tabs.addTab(tabs.newTab().setText("전체 핫딜"));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position=tab.getPosition();

                if(position==0){


                    ArrayList<Integer> ids=databaseHelper.selectFound(database);
                    crawlingAdapter=new CrawlingAdapter();
                    int count=ids.size();
                    for(int i=0;i<count;i++){

                        CrawlingItem crawlingItem=databaseHelper.selectData(database,"where id="+ids.get(i)).get(0);
                        crawlingAdapter.addItem(crawlingItem);

                    }


                    crawlingAdapter.notifyDataSetChanged();
                    hotDillListView.setAdapter(crawlingAdapter);

                }else if(position ==1){
                    //디비조회해서 crawlingItem받아오기
                    //받아온 데이터 adapter에 추가하고 데이터셋
                    crawlingAdapter=new CrawlingAdapter();
                    crawlingItems=databaseHelper.selectData(database,"where recommand>="+pref.getInt("recommandSet",7)+" and date ="+now+" order by id desc");//
                    int count =crawlingItems.size();
                    for(int i=0;i<count;i++){
                        crawlingAdapter.addItem(crawlingItems.get(i));
                    }
                    crawlingAdapter.notifyDataSetChanged();
                    hotDillListView.setAdapter(crawlingAdapter);


                }else if(position ==2){
                    crawlingAdapter=new CrawlingAdapter();
                    crawlingItems=databaseHelper.selectData(database,"order by id desc");//
                    int count =crawlingItems.size();
                    for(int i=0;i<count;i++){
                        crawlingAdapter.addItem(crawlingItems.get(i));
                    }
                    crawlingAdapter.notifyDataSetChanged();
                    hotDillListView.setAdapter(crawlingAdapter);
                }

               // getSupportFragmentManager().beginTransaction().replace()
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        hotDillListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CrawlingItem item = (CrawlingItem) crawlingAdapter.getItem(position);
                Log.d("주소:",item.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLink()));
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        SharedPreferences pref=getSharedPreferences("pref",MODE_PRIVATE);
        Intent serviceIntent=new Intent(MainActivity.this, CrawlingService.class);
        if(pref.getBoolean("mainService",true)){
//            if (Build.VERSION.SDK_INT >= 26) {
//                startForegroundService(serviceIntent);
//            }
//            else {
//                startService(serviceIntent);
//            }
            //서버시작
            startService(serviceIntent);
            //getApplicationContext().startForegroundService(serviceIntent);
            Log.d("서버상태","시작");
        }else{
            stopService(serviceIntent);
            Log.d("서버상태","종료");
        }
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int curId=item.getItemId();
        switch(curId){


            case R.id.menu_search:
                editText=(EditText)findViewById(R.id.searchEditText);
                crawlingAdapter=new CrawlingAdapter();
                Log.d("검색:",editText.getText().toString());
                crawlingItems=databaseHelper.selectData(database," order by id desc");//
                int count =crawlingItems.size();
                for(int i=0;i<count;i++){
                    if(crawlingItems.get(i).title.toUpperCase().contains(editText.getText().toString().toUpperCase()))
                    crawlingAdapter.addItem(crawlingItems.get(i));
                }
                crawlingAdapter.notifyDataSetChanged();
                hotDillListView.setAdapter(crawlingAdapter);

                Toast.makeText(this,String.valueOf(crawlingAdapter.getCount())+" 개 검색됨",Toast.LENGTH_LONG).show();
                break;

            case R.id.menu_settings:

                Intent intent=new Intent(getApplicationContext(),settingsActivicy.class);
                startActivity(intent);

               // Toast.makeText(this," 삼 메뉴클릭됨",Toast.LENGTH_LONG).show();
                break;

            default:
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    class CrawlingAdapter extends BaseAdapter {

        ArrayList<CrawlingItem> crawlingItems=new ArrayList<CrawlingItem>();
        public void addItem(CrawlingItem crawlingItem){
            crawlingItems.add(crawlingItem);
        }


        @Override
        public int getCount() {
            return crawlingItems.size();
        }

        @Override
        public Object getItem(int position) {
            return crawlingItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CrawlingItemView crawlingItemView=null;
            //ScheduleItem schedule= scheduleItems.get(position);
            if(convertView==null){
                crawlingItemView=new CrawlingItemView(getApplication());
            }else{
                crawlingItemView=(CrawlingItemView)convertView;
            }//재사용하는 함수 데이터가 많아졋을때 화면에 보여지는것만 객체화(메모리에 올려서) 보여줌


            CrawlingItem crawlingItem= crawlingItems.get(position);
            crawlingItemView.setTitle(crawlingItem.getTitle());
            crawlingItemView.setRecommand(crawlingItem.getRecommand());


            return crawlingItemView;
        }
    }




}
