package com.example.remidify;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class SignupActivity extends AppCompatActivity {

    EditText name,dob,age,mobile,username,password;
    Button register;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name=findViewById(R.id.name);
        dob=findViewById(R.id.dob);
        age=findViewById(R.id.age);
        mobile=findViewById(R.id.mobile);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        register=findViewById(R.id.register);

        db=new DBHelper(this);

        dob.setOnClickListener(v->showDatePicker());

        register.setOnClickListener(v->registerUser());
    }

    private void showDatePicker(){

        Calendar c=Calendar.getInstance();

        new DatePickerDialog(this,
                (view,year,month,day)->{

                    dob.setText(day+"/"+(month+1)+"/"+year);

                    int ageCalc=
                            Calendar.getInstance().get(Calendar.YEAR)-year;

                    age.setText(String.valueOf(ageCalc));

                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void registerUser(){

        String n=name.getText().toString();
        String d=dob.getText().toString();
        String a=age.getText().toString();
        String m=mobile.getText().toString();
        String u=username.getText().toString();
        String p=password.getText().toString();

        if(TextUtils.isEmpty(n)||
                TextUtils.isEmpty(d)||
                TextUtils.isEmpty(m)||
                TextUtils.isEmpty(u)||
                TextUtils.isEmpty(p)){

            Toast.makeText(this,
                    "Fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(db.checkUsername(u)){
            Toast.makeText(this,
                    "Username exists",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean insert=db.insertUser(n,d,a,m,u,p);

        if(insert){

            Toast.makeText(this,
                    "Registered Successfully",
                    Toast.LENGTH_SHORT).show();

            startActivity(new Intent(
                    SignupActivity.this,
                    LoginActivity.class));

            finish();
        }
    }
}