package com.example.work.lyrics;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class ListSongs extends AppCompatActivity implements  ViewGroup.OnClickListener{
        DBHelper dbHelper  = new DBHelper(this);
        TableLayout SongsTable;
        int CONTEXTMENUPOSITION=0;
        SearchView search;
          View DelView =null;
        ArrayList<String> listnamesSongs  = new ArrayList<String>();
    final int D=0;
    int minus=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_songs);
        SongsTable = findViewById(R.id.SongsTable1);
        search = findViewById(R.id.search);
   runOnUiThread(new Runnable() {
       @Override
       public void run() {
           SongsList();
       }
   });
        minus=0;
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
              //  Search
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuinfo)
    {
        menu.add(0,D,0,"Удалить запись");
        CONTEXTMENUPOSITION = v.getId();
        DelView = v;
    }
private  void Search(SearchView sea)
{
    SongsTable.removeAllViews();
    String query = sea.getQuery().toString();
    List<Integer> indexes = new ArrayList<Integer>();
    for (String item : listnamesSongs)
    {
        if (item.contains(query.toLowerCase()))
        {
            indexes.add(listnamesSongs.indexOf(item));

        }
    }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
        int size = cursor.getCount();
        for (int i = 0; i < size; i++) {
            cursor.moveToPosition(indexes.get(i));
            TableRow SongsRaw = new TableRow(this);
            TableRow.LayoutParams pr = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            SongsRaw.setId(i);
            SongsRaw.setOnClickListener(this);
            ImageView SongsImage = new ImageView(this);
            GetPic(indexes.get(i), SongsImage);
            SongsImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(270, 270);
            TextView SongsName = new TextView(this);
            //////////////////////////////////////////////////////////////////////////////////////////
            if (getResources().getConfiguration().orientation==1)
            {
                TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(800, TableLayout.LayoutParams.WRAP_CONTENT);
                SongsName.setLayoutParams(layoutParams1);
            }else  if(getResources().getConfiguration().orientation==2)
            {
                TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(1200, TableLayout.LayoutParams.WRAP_CONTENT);
                SongsName.setLayoutParams(layoutParams1);
            }
            //////////////////////////////////////////////////////////////////////////////////////////
            SongsImage.setLayoutParams(layoutParams);
            int piccolind = cursor.getColumnIndex(DBHelper.KEY_Artist);
            int piccolind1 = cursor.getColumnIndex(DBHelper.KEY_Title);
            SongsName.setText(cursor.getString(piccolind) + cursor.getString(piccolind1));
            SongsName.setTextSize(16);
            listnamesSongs.add(SongsName.getText().toString());
            SongsName.setTextColor(getResources().getColor(R.color.Yellow));
            SongsRaw.addView(SongsImage, 0);
            SongsRaw.addView(SongsName, 1);
            SongsRaw.setBackgroundColor(getResources().getColor(R.color.Black));
            SongsRaw.setOnClickListener(this);
            registerForContextMenu(SongsRaw);
            SongsTable.addView(SongsRaw, i);
        }
}
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case D:
                delete(CONTEXTMENUPOSITION,DelView);
                Toast.makeText(this, "Запись удалена", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private  void delete(int index,View v)
    {
        try {
            SQLiteDatabase  db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                cursor.moveToPosition(index);
                int idIndex = cursor.getColumnIndex(DBHelper.ID);
                String str = cursor.getString(idIndex);
                db.delete(DBHelper.TABLE_CONTACTS,dbHelper.ID+"="+str,null);
                SongsTable.removeView(v);
            }
            cursor.close();
        SongsTable.removeAllViews();
             SongsList();
       }catch  (Exception e){
            Toast.makeText(this, "Ошибка в удалении, повторите попытку", Toast.LENGTH_SHORT).show();
        }

    }

    public void SongsList(){
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                TableRow SongsRaw = new TableRow(this);
                TableRow.LayoutParams pr = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                SongsRaw.setId(i);
                SongsRaw.setOnClickListener(this);
                ImageView SongsImage = new ImageView(this);
                GetPic(i, SongsImage);
                SongsImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(270, 270);
                TextView SongsName = new TextView(this);
                //////////////////////////////////////////////////////////////////////////////////////////
                if (getResources().getConfiguration().orientation==1)
                {
                    TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(800, TableLayout.LayoutParams.WRAP_CONTENT);
                    SongsName.setLayoutParams(layoutParams1);
                }else  if(getResources().getConfiguration().orientation==2)
                {
                    TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(1200, TableLayout.LayoutParams.WRAP_CONTENT);
                    SongsName.setLayoutParams(layoutParams1);
                }
                //////////////////////////////////////////////////////////////////////////////////////////
                SongsImage.setLayoutParams(layoutParams);
                int piccolind = cursor.getColumnIndex(DBHelper.KEY_Artist);
                int piccolind1 = cursor.getColumnIndex(DBHelper.KEY_Title);
                SongsName.setText(cursor.getString(piccolind) + cursor.getString(piccolind1));
                SongsName.setTextSize(16);
                listnamesSongs.add(SongsName.getText().toString());
                SongsName.setTextColor(getResources().getColor(R.color.Yellow));
                SongsRaw.addView(SongsImage, 0);
                SongsRaw.addView(SongsName, 1);
                SongsRaw.setBackgroundColor(getResources().getColor(R.color.Black));
                SongsRaw.setOnClickListener(this);
                registerForContextMenu(SongsRaw);
                SongsTable.addView(SongsRaw, i);
            }
        }catch (Exception e)
        {}
    }
    private  void GetPic(int index,ImageView im) {
        try
        {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                cursor.moveToPosition(index);
                int piccolind = cursor.getColumnIndex(DBHelper.KEY_Bytes_Image);
                byte[] bytepics = cursor.getBlob(piccolind);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytepics, 0, bytepics.length);
                im.setImageBitmap(bmp);
            }
        }catch (Exception  e)
        {}
    }
    @Override
    public void onClick(View view) {
        int indexforcursor = view.getId();
        Intent intent = new Intent(this,ItemActivityFromDB.class);
       intent.putExtra("index",indexforcursor);
        startActivity(intent);
    }
}


