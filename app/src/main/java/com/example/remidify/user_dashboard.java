package com.example.remidify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class user_dashboard extends AppCompatActivity {

    TextView welcome, notify;
    ListView vaccineList;
    LinearLayout recordContainer;

    DBHelper db;
    int loggedPatientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        welcome = findViewById(R.id.txtWelcome);
        vaccineList = findViewById(R.id.listVaccines);
        recordContainer = findViewById(R.id.recordContainer);
        notify = findViewById(R.id.note);
        notify.setOnClickListener(v ->
                startActivity(new Intent(user_dashboard.this, Notification.class)));

        db = new DBHelper(this);

        SharedPreferences sp =
                getSharedPreferences("LoginSession", MODE_PRIVATE);

        String username = sp.getString("username", null);

        if (username != null) {

            Cursor userCursor = db.getUser(username);

            if (userCursor.moveToFirst()) {

                loggedPatientId = userCursor.getInt(0);
                String name = userCursor.getString(1);
                int age = userCursor.getInt(3);

                welcome.setText("Hello, " + name + " 👋");

                loadVaccines(age);
                loadPatientRecords();
            }
        }
    }


    private void loadVaccines(int age){

        Cursor c = db.getVaccinesByAge(age);
        ArrayList<String> list = new ArrayList<>();

        while(c.moveToNext()){
            list.add("💉 " + c.getString(0));
        }

        vaccineList.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        list));
    }

    private void loadPatientRecords(){

        Cursor cursor = db.getPatientRecords(loggedPatientId);

        while(cursor.moveToNext()){

            TextView tv = new TextView(this);

            tv.setText(
                    cursor.getString(0) +
                            "\nTaken: " + cursor.getString(1) +
                            "\nDue: " + cursor.getString(2)
            );

            tv.setPadding(20,20,20,20);
            recordContainer.addView(tv);
        }
    }
}
