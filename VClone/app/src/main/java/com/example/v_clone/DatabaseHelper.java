package com.example.v_clone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DatabaseName = "ECommerceDB";
    SQLiteDatabase ECommerceDB;

    public DatabaseHelper(Context context) {
        super(context, DatabaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user(UserID integer primary key autoincrement, Name text, Username text not null," +
                "Password text not null,Email text not null);");
        db.execSQL("create table audios(audioID integer primary key autoincrement, Name text, username text not null," +
                "FOREIGN KEY(username) REFERENCES user(Username))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user");
        db.execSQL("drop table if exists audios");
        onCreate(db);
    }
    public void addUser(String name, String username, String password, String Email) {
        ContentValues row = new ContentValues();
        row.put("Name", name);
        row.put("Username", username);
        row.put("Password", password);
        row.put("Email", Email);
        ECommerceDB = getWritableDatabase();
        ECommerceDB.insert("user", null, row);
        ECommerceDB.close();
    }
    public void addAudio(String name, String username) {
        ContentValues row = new ContentValues();
        row.put("Name", name);
        row.put("username", username);
        ECommerceDB = getWritableDatabase();
        ECommerceDB.insert("audios", null, row);
        ECommerceDB.close();
    }
    public Cursor getAudios(String username) {
        ECommerceDB = getReadableDatabase();
        String[] arg = {username};
        Cursor cursor = ECommerceDB.rawQuery("select Name from audios where Username = ?", arg);
        cursor.moveToFirst();
        return cursor;
    }
    public Cursor checkUsername(String username) {
        ECommerceDB = getReadableDatabase();
        String[] arg = {username};
        Cursor cursor = ECommerceDB.rawQuery("select Username from user where Username = ?", arg);
        cursor.moveToFirst();
        return cursor;
    }

    public boolean checkPassword(String username, String pass) {
        ECommerceDB = getReadableDatabase();
        String[] arg = {username};
        Cursor cursor = ECommerceDB.rawQuery("select Password from user where Username = ?", arg);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.getString(0).equals(pass))
                return true;
            else
                return false;
        } else
            return false;
    }
    public void updatePassword(String username, String newPass) {
        ECommerceDB = getWritableDatabase();
        ContentValues row = new ContentValues();
        row.put("Username", username);
        row.put("Password", newPass);
        ECommerceDB.update("user", row, "Username = ?", new String[]{username});
        ECommerceDB.close();
    }
    public Cursor getUser(String username) {
        ECommerceDB = getReadableDatabase();
        String[] arg = {username};
        Cursor cursor = ECommerceDB.rawQuery("select Name,Username,Password,Email from user where Username = ?", arg);
        cursor.moveToFirst();
        return cursor;
    }
    public void updateInfo(String email,String name,String username){
        ECommerceDB = getWritableDatabase();
        ContentValues row = new ContentValues();
        row.put("Username", username);
        row.put("Email", email);
        row.put("Name",name);
        ECommerceDB.update("user", row, "Username = ?", new String[]{username});
        ECommerceDB.close();
    }
}
