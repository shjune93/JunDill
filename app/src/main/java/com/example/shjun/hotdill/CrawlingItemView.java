package com.example.shjun.hotdill;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CrawlingItemView extends LinearLayout {

    TextView titleTextView;
    TextView recommandCountTextView;
    public CrawlingItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.crawling_list_view,this,true);


        titleTextView=(TextView)findViewById(R.id.titleTextView);
        recommandCountTextView=(TextView)findViewById(R.id.recommandCountTextView);

        //editButton=(Button)findViewById(R.id.editButton);
        //deleteButton=(Button)findViewById(R.id.deleteButton);
    }

    public void setTitle(String title){
        titleTextView.setText(title);
    }

    public void setRecommand(int recommand){
        recommandCountTextView.setText(String.valueOf(recommand));
    }

}
