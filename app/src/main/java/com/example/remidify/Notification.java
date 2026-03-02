package com.example.remidify;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Notification extends AppCompatActivity {

    LinearLayout container;
    TextView txtCount;
    DBHelper db;
    int patientId=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        container=findViewById(R.id.notificationContainer);
        txtCount=findViewById(R.id.txtCount);

        db=new DBHelper(this);

        SharedPreferences sp=
                getSharedPreferences("LoginSession",MODE_PRIVATE);

        String username=sp.getString("username",null);

        if(username!=null){

            Cursor c=db.getUser(username);

            if(c.moveToFirst())
                patientId=c.getInt(0);

            c.close();
        }

        loadNotifications();
    }

    private void loadNotifications(){

        container.removeAllViews();

        Cursor cursor=db.getNotifications(patientId);

        int count=0;

        while(cursor.moveToNext()){

            TextView tv=new TextView(this);

            tv.setText(
                    cursor.getString(0)+
                            "\n"+cursor.getString(1)+
                            "\n"+cursor.getString(2));

            tv.setPadding(30,30,30,30);
            container.addView(tv);

            count++;
        }

        txtCount.setText(String.valueOf(count));
        cursor.close();
    }
}