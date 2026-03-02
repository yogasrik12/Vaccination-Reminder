package com.example.remidify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;

import java.util.Calendar;

public class Update_Delete_Vaccine extends AppCompatActivity {

    DBHelper db;

    EditText name,type,taken,due,notes;
    Button btnUpdate,btnDelete;

    int recordId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete);

        db = new DBHelper(this);

        name = findViewById(R.id.editName);
        type = findViewById(R.id.editType);
        taken = findViewById(R.id.editTaken);
        due = findViewById(R.id.editDue);
        notes = findViewById(R.id.editNotes);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);


        recordId = getIntent().getIntExtra("record_id",-1);

        if(recordId == -1){
            Toast.makeText(this,
                    "Error loading record",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadRecord();

        taken.setOnClickListener(v -> calendar(taken));
        due.setOnClickListener(v -> calendar(due));

        btnUpdate.setOnClickListener(v -> updateRecord());
        btnDelete.setOnClickListener(v -> deleteRecord());
    }


    private void loadRecord(){

        Cursor c = db.getSingleRecord(recordId);

        if(c.moveToFirst()){

            name.setText(c.getString(3));
            type.setText(c.getString(4));
            taken.setText(c.getString(5));
            due.setText(c.getString(6));
            notes.setText(c.getString(7));
        }

        c.close();
    }

    private void calendar(EditText e){

        Calendar c = Calendar.getInstance();

        new DatePickerDialog(this,
                (view,y,m,d)->
                        e.setText(d+"/"+(m+1)+"/"+y),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)).show();
    }


    private void updateRecord(){

        boolean ok = db.updatePatientRecord(
                recordId,
                name.getText().toString(),
                type.getText().toString(),
                taken.getText().toString(),
                due.getText().toString(),
                notes.getText().toString()
        );

        Toast.makeText(this,
                ok ? "Updated Successfully" : "Update Failed",
                Toast.LENGTH_SHORT).show();

        if(ok) finish();
    }


    private void deleteRecord(){

        boolean ok = db.deletePatientRecord(recordId);

        Toast.makeText(this,
                ok ? "Deleted Successfully" : "Delete Failed",
                Toast.LENGTH_SHORT).show();

        if(ok) finish();
    }
}
