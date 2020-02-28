package com.example.shjun.hotdill;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;


import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {




    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists crawling(id integer primary key autoincrement,title text unique, link text, recommand integer,date date)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    ///////////////////////////////////////////////////////////크롤링한 데이터///////////////////////////////////////////////////

    public void createTable(SQLiteDatabase db){
        String sql = "create table if not exists crawling(id integer primary key autoincrement,title text unique, link text, recommand integer,date date)";
        db.execSQL(sql);
    }

    public void insertData(SQLiteDatabase db, String title, String link, int recommand, String date) {
        //replace
        String sql = "insert or ignore into crawling(title,link,recommand,date) values(?,?,?,?)";
        Object[] params = {title, link, recommand, date};

        db.execSQL(sql, params);
        sql="update crawling set recommand = ? where title= ?";
        Object[] params2={recommand,title};
        db.execSQL(sql,params2);

    }

    public ArrayList<CrawlingItem> selectData(SQLiteDatabase db,String where) {

        ArrayList<CrawlingItem> crawlingItems=new ArrayList<CrawlingItem>();

        String sql = "select title,link,recommand,date from crawling "+where;
        Cursor cursor = db.rawQuery(sql, null);


        //while(cursor.moveToNext()){}
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();

            String title=cursor.getString(0);
            String link = cursor.getString(1);
            int recommand = cursor.getInt(2);
            String date = cursor.getString(3);

            CrawlingItem item=new CrawlingItem(title,link,recommand,date);
            crawlingItems.add(item);



        }

        cursor.close();
        return crawlingItems;
    }

    public int selectIdData(SQLiteDatabase db,String where) {


        String sql = "select id from crawling "+where;
        Cursor cursor = db.rawQuery(sql, null);

        cursor.moveToFirst();
        int id=cursor.getInt(0);
        cursor.close();
        return id;
}

    public ArrayList<String> selectTopData(SQLiteDatabase db,int top) {

        ArrayList<String> tops=new ArrayList<String>();

        String sql = "select title from crawling order by id desc limit "+top;
        Cursor cursor = db.rawQuery(sql, null);


        //while(cursor.moveToNext()){}
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();

            String title=cursor.getString(0);

            tops.add(title);


        }

        cursor.close();
        return tops;
    }

    public int selectCount(SQLiteDatabase db){
        String sql="select Count(*) from crawling";
        Cursor cursor= db.rawQuery(sql, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public void dropTable(SQLiteDatabase db){
        String sql="drop table crawling";
        db.execSQL(sql);
    }


    /////////////////////////////////찾을 항목/////////////////////////////////////
    public void createFindTable(SQLiteDatabase db){
        String sql = "create table if not exists finditem(item text unique)";
        db.execSQL(sql);
    }


    public void insertFind(SQLiteDatabase db,String item){
        String sql = "insert or ignore into finditem(item) values('"+item+"')";
        db.execSQL(sql);
    }

    public ArrayList<String> selectFind(SQLiteDatabase db) {
        ArrayList<String> findItems = new ArrayList<String>();

        String sql = "select item from finditem";
        Cursor cursor = db.rawQuery(sql, null);


        //while(cursor.moveToNext()){}
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();

            String item = cursor.getString(0);
            findItems.add(item);

        }
        return findItems;
    }



    public void deleteFind(SQLiteDatabase db,String item){
        String sql = "delete from finditem where item='"+item+"'";
        db.execSQL(sql);
    }



    //////////////////////////////////////////////////////찾은 항목////////////////////////////////
    public void createFoundTable(SQLiteDatabase db){
        String sql = "create table if not exists founditem(itemId integer unique)";
        db.execSQL(sql);
    }


    public void insertFound(SQLiteDatabase db,int id){
        String sql = "insert or ignore into founditem(itemId) values("+id+")";
        db.execSQL(sql);
    }

    public ArrayList<Integer> selectFound(SQLiteDatabase db) {
        ArrayList<Integer> foundItems = new ArrayList<Integer>();

        String sql = "select itemId from founditem order by itemId desc";
        Cursor cursor = db.rawQuery(sql, null);


        //while(cursor.moveToNext()){}
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();

            int itemId = cursor.getInt(0);
            foundItems.add(itemId);

        }
        return foundItems;
    }



    public void deleteFound(SQLiteDatabase db){
        String sql = "delete from founditem";
        db.execSQL(sql);
    }

}
