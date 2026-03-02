package com.example.remidify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Add_vaccine extends AppCompatActivity {

    DBHelper db;

    ListView listPatients,listRecords;
    EditText vaccineName,vaccineType,takenDate,dueDate,notes;
    Button btnSave,btnUpdate,btnDelete;

    int selectedPatientId=-1;
    int selectedRecordId=-1;
    String selectedPatientName="";

    ArrayList<String> names=new ArrayList<>();
    ArrayList<Integer> ids=new ArrayList<>();

    ArrayList<String> records=new ArrayList<>();
    ArrayList<Integer> recordIds=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vaccine);

        db=new DBHelper(this);

        listPatients=findViewById(R.id.listPatients);
        listRecords=findViewById(R.id.listRecords);

        vaccineName=findViewById(R.id.editVaccineName);
        vaccineType=findViewById(R.id.editVaccineType);
        takenDate=findViewById(R.id.editTakenDate);
        dueDate=findViewById(R.id.editDueDate);
        notes=findViewById(R.id.editNotes);

        btnSave=findViewById(R.id.btnSaveRecord);
        btnUpdate=findViewById(R.id.btnUpdateRecord1);
        btnDelete=findViewById(R.id.btnDeleteRecord);

        loadPatients();

        listPatients.setOnItemClickListener((p,v,pos,id)->{
            selectedPatientId=ids.get(pos);
            selectedPatientName=names.get(pos);
            loadRecords(selectedPatientId);
        });

        takenDate.setOnClickListener(v->showCalendar(takenDate));
        dueDate.setOnClickListener(v->showCalendar(dueDate));

        btnSave.setOnClickListener(v->saveRecord());
    }

    private void loadPatients(){

        names.clear();
        ids.clear();

        Cursor c=db.getAllPatients();

        while(c.moveToNext()){
            ids.add(c.getInt(0));
            names.add(c.getString(1));
        }
        c.close();

        listPatients.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        names));
    }

    private void loadRecords(int id){

        records.clear();
        recordIds.clear();

        Cursor c=db.getPatientRecords(id);

        while(c.moveToNext()){
            recordIds.add(c.getInt(0));
            records.add(c.getString(3));
        }
        c.close();

        listRecords.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        records));
    }

    private void showCalendar(EditText e){

        Calendar c=Calendar.getInstance();

        new DatePickerDialog(this,
                (view,year,month,day)->
                        e.setText(day+"/"+(month+1)+"/"+year),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)).show();
    }

    // ================= SAVE =================
    private void saveRecord(){

        if(selectedPatientId==-1){
            Toast.makeText(this,"Select Patient First",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted=db.insertPatientRecord(
                selectedPatientId,
                selectedPatientName,
                vaccineName.getText().toString(),
                vaccineType.getText().toString(),
                takenDate.getText().toString(),
                dueDate.getText().toString(),
                notes.getText().toString()
        );

        if(inserted){

            String dateTime=new SimpleDateFormat(
                    "dd-MM-yyyy HH:mm",
                    Locale.getDefault())
                    .format(new Date());

            String title="New Vaccine Added";

            String message=
                    vaccineName.getText().toString()+
                            " scheduled on "+
                            dueDate.getText().toString();

            db.insertNotification(
                    selectedPatientId,
                    title,
                    message,
                    dateTime
            );

            showTopNotification(title,message);
        }

        Toast.makeText(this,
                inserted?"Saved":"Failed",
                Toast.LENGTH_SHORT).show();

        loadRecords(selectedPatientId);
    }

    // ================= PHONE NOTIFICATION =================
    private void showTopNotification(String title,String message){

        String channelId="remidify_channel";

        NotificationManager manager=
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            NotificationChannel channel=
                    new NotificationChannel(
                            channelId,
                            "Remidify Alerts",
                            NotificationManager.IMPORTANCE_HIGH);

            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder=
                new NotificationCompat.Builder(this,channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        manager.notify(
                (int)System.currentTimeMillis(),
                builder.build());
    }
}