package com.example.remidify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "VaccinationDB.db";
    private static final int DB_VERSION = 13;   // Increased for new update

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "dob TEXT," +
                "age INTEGER," +
                "mobile TEXT," +
                "username TEXT UNIQUE," +
                "password TEXT)");

        db.execSQL("CREATE TABLE doctors(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "doctor_name TEXT," +
                "username TEXT UNIQUE," +
                "password TEXT," +
                "clinic_code TEXT," +
                "patient_total INTEGER)");

        db.execSQL("CREATE TABLE vaccines(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "min_age INTEGER," +
                "max_age INTEGER," +
                "vaccine_name TEXT)");

        db.execSQL("CREATE TABLE patient_records(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "patient_id INTEGER," +
                "patient_name TEXT," +
                "vaccine_name TEXT," +
                "vaccine_type TEXT," +
                "taken_date TEXT," +
                "due_date TEXT," +
                "notes TEXT)");

        // NOTIFICATIONS
        db.execSQL("CREATE TABLE notifications(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "patient_id INTEGER," +
                "title TEXT," +
                "message TEXT," +
                "date_time TEXT)");

        insertVaccines(db);
        insertDefaultDoctor(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

        db.execSQL("CREATE TABLE IF NOT EXISTS notifications(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "patient_id INTEGER," +
                "title TEXT," +
                "message TEXT," +
                "date_time TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS patient_records(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "patient_id INTEGER," +
                "patient_name TEXT," +
                "vaccine_name TEXT," +
                "vaccine_type TEXT," +
                "taken_date TEXT," +
                "due_date TEXT," +
                "notes TEXT)");
    }


    private void insertDefaultDoctor(SQLiteDatabase db) {

        db.execSQL(
                "INSERT INTO doctors(doctor_name,username,password,clinic_code,patient_total) " +
                        "VALUES('Dr. Smith','doc123','1234','CLINIC01',25)"
        );
    }
    private void insertVaccines(SQLiteDatabase db){

        db.execSQL("INSERT INTO vaccines(min_age,max_age,vaccine_name) VALUES(0,1,'BCG')");
        db.execSQL("INSERT INTO vaccines(min_age,max_age,vaccine_name) VALUES(1,5,'DTP')");
        db.execSQL("INSERT INTO vaccines(min_age,max_age,vaccine_name) VALUES(9,15,'HPV')");
        db.execSQL("INSERT INTO vaccines(min_age,max_age,vaccine_name) VALUES(18,59,'Influenza')");
        db.execSQL("INSERT INTO vaccines(min_age,max_age,vaccine_name) VALUES(60,120,'Pneumococcal')");
    }

    public Cursor getVaccinesByAge(int age){

        SQLiteDatabase db=this.getReadableDatabase();

        return db.rawQuery(
                "SELECT vaccine_name FROM vaccines " +
                        "WHERE ? BETWEEN min_age AND max_age",
                new String[]{String.valueOf(age)});
    }


    public boolean insertUser(String name,String dob,String age,
                              String mobile,String username,String password){

        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues cv=new ContentValues();
        cv.put("name",name);
        cv.put("dob",dob);
        cv.put("age",Integer.parseInt(age));
        cv.put("mobile",mobile);
        cv.put("username",username);
        cv.put("password",password);

        return db.insert("users",null,cv)!=-1;
    }

    public boolean checkUsername(String username){

        SQLiteDatabase db=this.getReadableDatabase();

        Cursor cursor=db.rawQuery(
                "SELECT id FROM users WHERE username=?",
                new String[]{username});

        boolean exists=cursor.getCount()>0;
        cursor.close();
        return exists;
    }
    public boolean checkUser(String username,String password){

        SQLiteDatabase db=this.getReadableDatabase();

        Cursor cursor=db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username,password});

        boolean exists=cursor.getCount()>0;
        cursor.close();
        return exists;
    }

    public Cursor getUser(String username){

        SQLiteDatabase db=this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM users WHERE username=?",
                new String[]{username});
    }


    public boolean checkDoctor(String username,String password,String clinicCode){

        SQLiteDatabase db=this.getReadableDatabase();

        Cursor cursor=db.rawQuery(
                "SELECT * FROM doctors WHERE username=? AND password=? AND clinic_code=?",
                new String[]{username,password,clinicCode});

        boolean exists=cursor.getCount()>0;
        cursor.close();
        return exists;
    }

    public Cursor getDoctor(String username){

        SQLiteDatabase db=this.getReadableDatabase();

        return db.rawQuery(
                "SELECT doctor_name, patient_total FROM doctors WHERE username=?",
                new String[]{username});
    }

    public Cursor getAllPatients(){

        SQLiteDatabase db=this.getReadableDatabase();

        return db.rawQuery(
                "SELECT id,name,age FROM users",
                null);
    }
    public boolean insertPatientRecord(int patientId,String patientName,
                                       String vaccineName,String vaccineType,
                                       String takenDate,String dueDate,
                                       String notes){

        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues cv=new ContentValues();
        cv.put("patient_id",patientId);
        cv.put("patient_name",patientName);
        cv.put("vaccine_name",vaccineName);
        cv.put("vaccine_type",vaccineType);
        cv.put("taken_date",takenDate);
        cv.put("due_date",dueDate);
        cv.put("notes",notes);

        return db.insert("patient_records",null,cv)!=-1;
    }
    public Cursor getPatientRecords(int patientId){

        SQLiteDatabase db=this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM patient_records WHERE patient_id=?",
                new String[]{String.valueOf(patientId)});
    }
    public Cursor getSingleRecord(int recordId){

        SQLiteDatabase db=this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM patient_records WHERE id=?",
                new String[]{String.valueOf(recordId)});
    }
    public boolean updatePatientRecord(int recordId,
                                       String vaccineName,
                                       String vaccineType,
                                       String takenDate,
                                       String dueDate,
                                       String notes){

        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues cv=new ContentValues();
        cv.put("vaccine_name",vaccineName);
        cv.put("vaccine_type",vaccineType);
        cv.put("taken_date",takenDate);
        cv.put("due_date",dueDate);
        cv.put("notes",notes);

        int result=db.update(
                "patient_records",
                cv,
                "id=?",
                new String[]{String.valueOf(recordId)});

        return result>0;
    }
    public boolean deletePatientRecord(int recordId){

        SQLiteDatabase db=this.getWritableDatabase();

        int result=db.delete(
                "patient_records",
                "id=?",
                new String[]{String.valueOf(recordId)});

        return result>0;
    }
    public void insertNotification(int patientId,
                                   String title,
                                   String message,
                                   String dateTime){

        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues cv=new ContentValues();
        cv.put("patient_id",patientId);
        cv.put("title",title);
        cv.put("message",message);
        cv.put("date_time",dateTime);

        db.insert("notifications",null,cv);
    }

    public Cursor getNotifications(int patientId){

        SQLiteDatabase db=this.getReadableDatabase();

        return db.rawQuery(
                "SELECT title,message,date_time FROM notifications " +
                        "WHERE patient_id=? ORDER BY id DESC",
                new String[]{String.valueOf(patientId)});
    }

    public boolean verifyUserForReset(
            String username,
            String mobile){

        SQLiteDatabase db=this.getReadableDatabase();

        Cursor cursor=db.rawQuery(
                "SELECT id FROM users " +
                        "WHERE username=? AND mobile=?",
                new String[]{username,mobile});

        boolean exists=cursor.getCount()>0;
        cursor.close();

        return exists;
    }


    public boolean updatePassword(
            String username,
            String newPassword){

        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues cv=new ContentValues();
        cv.put("password",newPassword);

        int result=db.update(
                "users",
                cv,
                "username=?",
                new String[]{username});

        return result>0;
    }

}