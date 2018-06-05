package com.example.work.lyrics;

/**
 * Created by Work on 22.05.2018.
 */



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper{
    private  SQLiteDatabase db;
    public  static  final  int  DATABASE_VERSION = 1;
    public  static final String DATABASE_NAME = "dbase";
    public  static final String TABLE_CONTACTS = "LYRICS";

    public static final String KEY_OriginalTitle = "Оригинальный заголовок";
    public static final String KEY_OriginalArtist ="Оригинальный Артист";

    public   static final String ID = "_ID";
    public   static final String KEY_ID = "id";
    public static final String KEY_Title = "Title";
    public static final String KEY_Artist = "Artist";
    public static final String KEY_URLImage= "URLImage";
    public static final String KEY_Bytes_Image= "Bytes_Image";
    public static final String KEY_URL = "URL";
    public static final String KEY_YOUTUBE = "YOUTUBE";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_CONTACTS+"("+ID + " integer primary key,"+KEY_ID+" text,"+KEY_Title+" text,"+KEY_Artist+" text,"+KEY_URLImage+" text,"+
                KEY_Bytes_Image + " blob,"+ KEY_URL +" text,"+KEY_YOUTUBE+ " text"+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}

