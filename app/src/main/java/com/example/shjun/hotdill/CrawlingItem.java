package com.example.shjun.hotdill;

import android.os.Parcel;
import android.os.Parcelable;


import java.text.DateFormat;
import java.util.Date;


public class CrawlingItem implements Parcelable {
    String title;
    String link;
    int recommand;
    String date;



    DateFormat format3 = DateFormat.getDateInstance(DateFormat.MEDIUM);



    public CrawlingItem(String title, String link, int recommand, String date) {
        this.title = title;
        this.link = link;
        this.recommand = recommand;
        this.date = date;
    }

    public CrawlingItem(Parcel src){
        title=src.readString();
        link=src.readString();
        recommand=src.readInt();
        date=src.readString();


    }

    public static final Parcelable.Creator CREATOR=new Parcelable.Creator(){
        @Override
        public Object createFromParcel(Parcel source) {
            return new CrawlingItem(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new CrawlingItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeInt(recommand);
        dest.writeString(date);

    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getRecommand() {
        return recommand;
    }

    public void setRecommand(int recommand) {
        this.recommand = recommand;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
