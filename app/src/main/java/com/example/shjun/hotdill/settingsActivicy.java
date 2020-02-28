package com.example.shjun.hotdill;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

public class settingsActivicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_activicy);
        final SharedPreferences pref=getSharedPreferences("pref",MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();


        Switch mainSwitch=(Switch) findViewById(R.id.mainSwitch);
        if (pref.getBoolean("mainService",true)==true){
            mainSwitch.setChecked(true);
        }else{
            mainSwitch.setChecked(false);
        }

        //mainSwitch.setChecked(true);

        mainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(pref.getBoolean("mainService",true)==true){
                    editor.putBoolean("mainService",false);

                }else{
                    editor.putBoolean("mainService",true);
                }
                editor.commit();

            }
        });

        Switch alarmSwitch=(Switch) findViewById(R.id.alarmSwitch);
        if (pref.getBoolean("alarmService",true)==true){
            alarmSwitch.setChecked(true);
        }else{
            alarmSwitch.setChecked(false);
        }

        //mainSwitch.setChecked(true);

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(pref.getBoolean("alarmService",true)==true){
                    editor.putBoolean("alarmService",false);

                }else{
                    editor.putBoolean("alarmService",true);
                }
                editor.commit();

            }
        });




        Button recommandSettingButton=(Button)findViewById(R.id.recommandSettingButton);
        recommandSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(settingsActivicy.this);
                d.setTitle("NumberPicker");
                d.setContentView(R.layout.dialog);
                TextView dialogTitleTextView=(TextView) d.findViewById(R.id.dialogTitleTextView);
                dialogTitleTextView.setText("오늘의 핫딜 추천횟수");
                TextView dialogUnitTextView=(TextView) d.findViewById(R.id.dialogUnitTextView);
                dialogUnitTextView.setText(" 이상");

                Button dialogSetButton = (Button) d.findViewById(R.id.dialogSetButton);
                Button dialogCancelButton = (Button) d.findViewById(R.id.dialogCancelButton);
                final NumberPicker dialogNumberPicker = (NumberPicker) d.findViewById(R.id.dialogNumberPicker);

                dialogNumberPicker.setMaxValue(100); // max value 100
                dialogNumberPicker.setMinValue(0);   // min value 0
                dialogNumberPicker.setValue(pref.getInt("recommandSet",7));//저장된값 불러와서 표시 Minmax설정 위에 잇으면 동작안함
                dialogNumberPicker.setWrapSelectorWheel(false); //반대로 돌아갈수 있는지
                //np.setOnValueChangedListener(this);
                dialogSetButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        //tv.setText(String.valueOf(np.getValue())); //set the value to textview
                        editor.putInt("recommandSet",dialogNumberPicker.getValue());
                        editor.commit();
                        d.dismiss();
                    }
                });
                dialogCancelButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        d.dismiss(); // dismiss the dialog
                    }
                });
                d.show();
            }
        });


        Button crawlingCycleSettingButton=(Button)findViewById(R.id.crawlingCycleSettingButton);
        crawlingCycleSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(settingsActivicy.this);
                d.setTitle("NumberPicker");
                d.setContentView(R.layout.dialog);
                TextView dialogTitleTextView=(TextView) d.findViewById(R.id.dialogTitleTextView);
                dialogTitleTextView.setText("데이터 수집 주기");
                TextView dialogUnitTextView=(TextView) d.findViewById(R.id.dialogUnitTextView);
                dialogUnitTextView.setText("분");
                Button dialogSetButton = (Button) d.findViewById(R.id.dialogSetButton);
                Button dialogCancelButton = (Button) d.findViewById(R.id.dialogCancelButton);
                final NumberPicker dialogNumberPicker = (NumberPicker) d.findViewById(R.id.dialogNumberPicker);


                dialogNumberPicker.setMaxValue(60); // max value 100
                dialogNumberPicker.setMinValue(1);   // min value 0
                dialogNumberPicker.setWrapSelectorWheel(false); //반대로 돌아갈수 있는지
                dialogNumberPicker.setValue(pref.getInt("cycleSet",1));//저장된 설정값 불러와서 표시
                //np.setOnValueChangedListener(this);
                dialogSetButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                       // tv.setText(String.valueOf(np.getValue())); //set the value to textview
                        editor.putInt("cycleSet",dialogNumberPicker.getValue());
                        editor.commit();
                        d.dismiss();
                    }
                });
                dialogCancelButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        d.dismiss(); // dismiss the dialog
                    }
                });
                d.show();
            }
        });


        Button findListSettingButton=(Button)findViewById(R.id.findListSettingButton);
        findListSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),findItemSetting.class);
                startActivity(intent);

            }
        });

        Button resetFoundHotdillButton=(Button)findViewById(R.id.resetFoundHotDillButton);
        resetFoundHotdillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(settingsActivicy.this);

                alert.setTitle("포착된 핫딜 초기화");
                alert.setMessage("정말 초기화 하시겠습니까?");


                alert.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // String username = name.getText().toString();
                        return;
                    }
                });


                alert.setNegativeButton("확인",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DatabaseHelper databaseHelper=new DatabaseHelper(settingsActivicy.this,"crawling",null,1);
                        SQLiteDatabase database;
                        database=databaseHelper.getWritableDatabase();
                        databaseHelper.deleteFound(database);


                    }
                });

                alert.show();
            }
        });


    }
}
