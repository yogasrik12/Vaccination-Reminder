package com.example.remidify;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername,etPassword;
    Button btnSignIn,btnDoc;
    TextView txtForgot;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername=findViewById(R.id.etUsername);
        etPassword=findViewById(R.id.etPassword);
        btnSignIn=findViewById(R.id.btnSignIn);
        btnDoc=findViewById(R.id.btnDoctor);
        txtForgot=findViewById(R.id.txtForgot);

        db=new DBHelper(this);

        btnSignIn.setOnClickListener(v->loginUser());

        btnDoc.setOnClickListener(v ->
                startActivity(new Intent(
                        LoginActivity.this,
                        Doctor_login.class)));

        // FORGOT PASSWORD CLICK
        txtForgot.setOnClickListener(v -> showVerifyDialog());
    }

    // ================= LOGIN =================
    private void loginUser(){

        String u=etUsername.getText().toString();
        String p=etPassword.getText().toString();

        if(TextUtils.isEmpty(u)||TextUtils.isEmpty(p)){

            Toast.makeText(this,
                    "Enter all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(db.checkUser(u,p)){

            SharedPreferences sp=
                    getSharedPreferences(
                            "LoginSession",MODE_PRIVATE);

            sp.edit()
                    .putString("username",u)
                    .putBoolean("isLoggedIn",true)
                    .apply();

            Toast.makeText(this,
                    "Login Success",
                    Toast.LENGTH_SHORT).show();

            startActivity(new Intent(
                    LoginActivity.this,
                    user_dashboard.class));

            finish();

        }else{

            Toast.makeText(this,
                    "Invalid Credentials",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ================= VERIFY USER =================
    private void showVerifyDialog(){

        LinearLayout layout=new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40,30,40,10);

        EditText etUser=new EditText(this);
        etUser.setHint("Enter Username (Gmail)");

        EditText etMobile=new EditText(this);
        etMobile.setHint("Enter Mobile Number");

        layout.addView(etUser);
        layout.addView(etMobile);

        new AlertDialog.Builder(this)
                .setTitle("Verify Account")
                .setView(layout)
                .setPositiveButton("Verify",(dialog,which)->{

                    String user=etUser.getText().toString();
                    String mobile=etMobile.getText().toString();

                    if(db.verifyUserForReset(user,mobile)){
                        showResetDialog(user);
                    }else{
                        Toast.makeText(this,
                                "Invalid Details",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel",null)
                .show();
    }

    // ================= RESET PASSWORD =================
    private void showResetDialog(String username){

        LinearLayout layout=new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40,30,40,10);

        EditText newPass=new EditText(this);
        newPass.setHint("New Password");

        EditText confirmPass=new EditText(this);
        confirmPass.setHint("Confirm Password");

        layout.addView(newPass);
        layout.addView(confirmPass);

        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setView(layout)
                .setPositiveButton("Reset",(dialog,which)->{

                    String p1=newPass.getText().toString();
                    String p2=confirmPass.getText().toString();

                    if(TextUtils.isEmpty(p1)||TextUtils.isEmpty(p2)){
                        Toast.makeText(this,
                                "Enter all fields",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!p1.equals(p2)){
                        Toast.makeText(this,
                                "Passwords do not match",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean updated=
                            db.updatePassword(username,p1);

                    Toast.makeText(this,
                            updated ?
                                    "Password Updated" :
                                    "Update Failed",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel",null)
                .show();
    }
}