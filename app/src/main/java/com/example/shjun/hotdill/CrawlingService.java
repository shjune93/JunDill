package com.example.shjun.hotdill;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Thread.sleep;


public class CrawlingService extends Service {

    private String htmlContentInStringFormat="";
    private String ppomppu="http://www.ppomppu.co.kr/zboard/zboard.php?id=ppomppu";
    private String coolenjoy="http://www.coolenjoy.net/bbs/jirum/p";

    SQLiteDatabase database;
    DatabaseHelper databaseHelper;
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    JsoupAsyncTask jsoupAsyncTask;
    ArrayList<String> searchCrawlings;//검색되어질 목록
    ArrayList<String> findCrawlings;//찾을목록
    int checkCount;
    int p;



    final Handler mHandler = new Handler();

    //DatabaseHelper databaseHelper;
    public CrawlingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        final SharedPreferences pref=getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();// editor에 put 하기

        //p=5;
        //Log.d("시작","서비스시작됨");
        p=pref.getInt("page",8);
        editor.putInt("page",2);
        editor.commit();
        databaseHelper=new DatabaseHelper(this,"crawling",null,1);
        database=databaseHelper.getWritableDatabase();


        Runnable doVibe = new Runnable() {

            @Override
            public void run() {
                checkCount=databaseHelper.selectCount(database);
                Log.d("실행전갯수",String.valueOf(checkCount));

                //
                jsoupAsyncTask=new JsoupAsyncTask();
                jsoupAsyncTask.p=p;
                jsoupAsyncTask.execute();
                Log.d("서비스 실행중","실행중 p:"+p);


                //3초후 실행
                mHandler.postDelayed(new Runnable()
                {
                    @Override     public void run()
                    {
                        //추가됬다면
                        if(checkCount!=databaseHelper.selectCount(database)){

                            int addCount=databaseHelper.selectCount(database)-checkCount;//추가됬으면 1보다큼큼
                            searchCrawlings=databaseHelper.selectTopData(database,addCount);//위에서 추가한 만큼 가져옴
                            findCrawlings=databaseHelper.selectFind(database);//


                            //검색리스트에 포함되있는지 확인후 알림창띄워줌
                            for(int j=0;j<searchCrawlings.size();j++){
                                for(int i=0;i<findCrawlings.size();i++){

                                    if(searchCrawlings.get(j).toUpperCase().contains(findCrawlings.get(i).toUpperCase())){

                                        //String url=databaseHelper.selectData(database,"where title='"+searchCrawlings.get(j)+"'").get(0).getLink();
                                        CrawlingItem findItem=databaseHelper.selectData(database,"where title='"+searchCrawlings.get(j)+"'").get(0);

                                        int id=databaseHelper.selectIdData(database,"where title='"+searchCrawlings.get(j)+"'");
                                        databaseHelper.insertFound(database,id);
                                        if(pref.getBoolean("alarmService",true)){//알림이 켜져잇으면
                                            String url=findItem.getLink();
                                            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                            PendingIntent pendingIntent = PendingIntent.getActivity(CrawlingService.this,0,intent,PendingIntent.FLAG_ONE_SHOT);
                                            String CHANNEL_ID = "default";

                                            if (Build.VERSION.SDK_INT >= 26) {
//
                                                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                                        "Channel human readable title",
                                                        NotificationManager.IMPORTANCE_DEFAULT);
                                                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                                        .build();

                                                channel.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +getPackageName()+"/"+R.raw.ball),audioAttributes);

                                                //((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
                                                NotificationManager nfm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                                nfm.createNotificationChannel(channel);
                                                Notification notification = new NotificationCompat.Builder(CrawlingService.this, CHANNEL_ID)
                                                        .setContentTitle("포착")
                                                        .setContentText(searchCrawlings.get(j))
                                                        .setSmallIcon(R.drawable.androidshopping2)
                                                        .setContentIntent(pendingIntent)
                                                        .setAutoCancel(true)
                                                        .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +getPackageName()+"/"+R.raw.ball))
                                                        .build();
                                                notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;

                                                //startForeground(id, notification);
                                                nfm.notify(id,notification);


                                            }else{


                                                NotificationManager nfm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                                Notification notification = new NotificationCompat.Builder(CrawlingService.this, CHANNEL_ID)
                                                        .setContentTitle("포착")
                                                        .setContentText(searchCrawlings.get(j))
                                                        .setSmallIcon(R.drawable.androidshopping2)
                                                        .setContentIntent(pendingIntent)
                                                        .setAutoCancel(true)
                                                        .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +getPackageName()+"/"+R.raw.ball))
                                                        .build();

                                                notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;

                                                nfm.notify(id,notification);
                                                //onStartCommand(id,);
                                                //startForeground(id,notification);
                                            }

                                            Log.d("포착",findCrawlings.get(i));
                                            break;

                                        }

                                    }


                                }
                            }
                            //갯수 갱신
                            checkCount=databaseHelper.selectCount(database);


                        }
                        Log.d("실행후갯수",String.valueOf(checkCount));

                    }
                }, 3000);

                //p=1;

                //딜레이주고 다시run불러서 실행
                int cycleSet=pref.getInt("cycleSet",1)*60000;
                mHandler.postDelayed(this,cycleSet);





            }
        };
        doVibe.run();


        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //super.onCreate();


//        ArrayList<CrawlingItem> crawlingItems=jsoupAsyncTask.crawlingItems;
//        int count=crawlingItems.size();
//        Log.d("count",String.valueOf(count));
//        for(int i=0;i<count;i++){
//            databaseHelper.insertData(database,
//                    crawlingItems.get(i).getTitle(),
//                    crawlingItems.get(i).getLink(),
//                    crawlingItems.get(i).getRecommand(),
//                    crawlingItems.get(i).getDate());
//
//            System.out.println(crawlingItems.get(i).getTitle());
//            }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.d("서버실행종료"," 종료");
        super.onCreate();
    }


    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
       // DatabaseHelper databaseHelper=new DatabaseHelper(this,"crawling",null,1);
        ArrayList<CrawlingItem> crawlingItems=new ArrayList<CrawlingItem>();
        DatabaseHelper databaseHelper=new DatabaseHelper(CrawlingService.this,"crawling",null,1);
        SQLiteDatabase database=databaseHelper.getWritableDatabase();
        int p;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            CrawlingItem crawlingItem;

            Date now=new Date();


            try {
                for(int page=p;page>0;page--){//뒤에서 부터 받도록
                    Document doc = Jsoup.connect(ppomppu+"&page="+page).get();
                    for(int l=0;l<2;l++){
                        Elements titles= doc.select("tr.list"+l+" td.list_vspace font");
                        Elements links=doc.select("tr.list"+l+" td.list_vspace a");
                        Elements recommands=doc.select("tr.list"+l+" td.eng.list_vspace");

                        for(int i=9;i>=0;i--){//밑에서부터 순서대로 받도록
                            String title=titles.get(i).text();
                            String link="http://www.ppomppu.co.kr/zboard/"+links.get(3*i+1).attr("href");
                            int recommand=0;
                            if(TextUtils.isEmpty(recommands.get(4*i+2).text())){
                                recommand=0;
                            }else{
                                recommand=Integer.parseInt(recommands.get(4*i+2).text().split("\\s")[0]);
                             }
                            databaseHelper.insertData(database,title,link,recommand,format.format(now));

                        }

                    }
                    doc=Jsoup.connect(coolenjoy +String.valueOf(page)).get();

                    Elements titles= doc.select("tbody tr td.td_subject a");
                    //Elements links=doc.select("tr.list");
                    Elements recommands=doc.select("div.list_good2");
                    int count=titles.size();
                    for(int i=count-1;i>=0;i--){
                        String title=titles.get(i).text().trim();
                        title=title.split("댓글")[0];
                        String link=titles.get(i).attr("href");

                        int realRecommand=Integer.parseInt(recommands.get(i).text());
                        databaseHelper.insertData(database,title,link,realRecommand,format.format(now));
                        //}
                    }
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
}
