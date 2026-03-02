package com.example.remidify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Doctor_dashboard extends AppCompatActivity {

    DBHelper db;
    TextView txtDoctorName, txtPatientCount;
    LinearLayout patientContainer;
    Button btnAddVaccine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        db = new DBHelper(this);

        txtDoctorName = findViewById(R.id.txtDoctorName);
        txtPatientCount = findViewById(R.id.txtPatientCount);
        patientContainer = findViewById(R.id.patientContainer);
        btnAddVaccine = findViewById(R.id.btnAddVaccine);

        // Add Vaccine Button
        btnAddVaccine.setOnClickListener(v ->
                startActivity(new Intent(
                        Doctor_dashboard.this,
                        Add_vaccine.class)));

        loadDoctorName();
        loadPatients();
    }


    private void loadDoctorName(){

        Cursor cursor = db.getDoctor("doc123");

        if(cursor.moveToFirst()){
            txtDoctorName.setText("Dr. " + cursor.getString(0));
        }

        cursor.close();
    }

    private void loadPatients(){

        Cursor cursor = db.getAllPatients();
        int count = 0;

        while(cursor.moveToNext()){

            count++;

            int id   = cursor.getInt(0);
            String name = cursor.getString(1);
            int age  = cursor.getInt(2);

            CardView card = new CardView(this);
            card.setRadius(20);
            card.setCardElevation(8);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

            params.setMargins(20,20,20,20);
            card.setLayoutParams(params);

            TextView tv = new TextView(this);
            tv.setPadding(30,30,30,30);
            tv.setText(name + "  |  Age: " + age);
            tv.setTextSize(16);

            card.addView(tv);
            patientContainer.addView(card);


            card.setOnClickListener(v -> {

                if(id == -1){
                    Toast.makeText(this,
                            "Invalid Patient",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent(
                        Doctor_dashboard.this,
                        Patient_detail.class);

                i.putExtra("patient_id", id);
                startActivity(i);
            });
        }

        txtPatientCount.setText(String.valueOf(count));
        cursor.close();
    }
}
