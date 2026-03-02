package com.example.remidify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class Doctor_login extends AppCompatActivity {

    TextInputEditText username, password, clinicCode;
    MaterialButton accessPortal;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_login);

        username = findViewById(R.id.docUsername);
        password = findViewById(R.id.docPassword);
        clinicCode = findViewById(R.id.docClinicCode);
        accessPortal = findViewById(R.id.btnAccessPortal);

        db = new DBHelper(this);

        accessPortal.setOnClickListener(v -> {

            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String clinic = clinicCode.getText().toString().trim();

            if(user.isEmpty() || pass.isEmpty() || clinic.isEmpty()){
                Toast.makeText(this,
                        "All fields required",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            boolean check = db.checkDoctor(user, pass, clinic);

            if(check){

                Toast.makeText(this,
                        "Login Successful",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(
                        Doctor_login.this,
                        Doctor_dashboard.class);

                intent.putExtra("doctor_username", user);
                startActivity(intent);
                finish();

            }else{
                Toast.makeText(this,
                        "Invalid Credentials",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
