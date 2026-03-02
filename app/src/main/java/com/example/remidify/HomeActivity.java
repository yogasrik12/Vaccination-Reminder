package com.example.remidify;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    Button btnSignup, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnSignup = findViewById(R.id.btnSignup);
        btnLogin = findViewById(R.id.btnLogin);

        btnSignup.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, SignupActivity.class)));

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, LoginActivity.class)));
    }
}