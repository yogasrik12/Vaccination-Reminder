package com.example.remidify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Patient_detail extends AppCompatActivity {

    DBHelper db;
    LinearLayout recordsContainer;
    TextView txtInitials, txtName, txtSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        db = new DBHelper(this);
        recordsContainer = findViewById(R.id.recordsContainer);
        txtInitials = findViewById(R.id.txtInitials);
        txtName = findViewById(R.id.txtName);
        txtSub = findViewById(R.id.txtSub);

        int patientId =
                getIntent().getIntExtra("patient_id",-1);
        loadPatientHeader(patientId);
        loadRecords(patientId);
    }

    private void loadRecords(int id){

        Cursor cursor = db.getPatientRecords(id);

        while(cursor.moveToNext()){

            int recordId = cursor.getInt(0);
            String vName = cursor.getString(3);
            String taken = cursor.getString(5);
            String due = cursor.getString(6);

            TextView tv = new TextView(this);
            tv.setPadding(30,20,30,20);
            tv.setTextSize(16);

            tv.setText(
                    "💉 " + vName +
                            "\nTaken : " + taken +
                            "\nDue : " + due
            );

            // CLICK → UPDATE PAGE
            tv.setOnClickListener(v -> {

                Intent i = new Intent(
                        Patient_detail.this,
                        Update_Delete_Vaccine.class);

                i.putExtra("record_id", recordId);
                startActivity(i);
            });

            recordsContainer.addView(tv);
        }

        cursor.close();
    }

    private void loadPatientHeader(int id){

        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT name, age FROM users WHERE id=?",
                new String[]{String.valueOf(id)}
        );

        if(cursor.moveToFirst()){

            String name = cursor.getString(0);
            int age = cursor.getInt(1);

            txtName.setText(name);
            txtSub.setText("Age: " + age);

            // Generate initials
            String[] parts = name.split(" ");
            String initials = "";

            for(String p : parts){
                if(p.length() > 0){
                    initials += p.charAt(0);
                }
            }

            txtInitials.setText(initials.toUpperCase());
        }

        cursor.close();
    }

}