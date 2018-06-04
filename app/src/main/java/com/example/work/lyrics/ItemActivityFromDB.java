package com.example.work.lyrics;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemActivityFromDB extends AppCompatActivity implements View.OnClickListener{
    WebView YouTView;
    WebView LyricsView;
    ScrollView scroll;
    ImageView Impreview;
    TextView TitleVirew;
    Button backs;
    Button next;
    int ind=0;
    int index = 0;
    int count=0;
    BroadcastReceiver br;
    byte[] ArrImage;
    DBHelper dbHelper = new DBHelper(this);
    @SuppressLint({"ResourceAsColor", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_from_db);
        YouTView = findViewById(R.id.YouTView);
        LyricsView = findViewById(R.id.LyricsView);
        scroll = findViewById(R.id.scrollview);
        Impreview = findViewById(R.id.Impreview);
        TitleVirew = findViewById(R.id.TitleVirew);
        backs = findViewById(R.id.backs);
        backs.setOnClickListener(this);
        next = findViewById(R.id.next);
        next.setOnClickListener(this);
      try {
            Intent intent = getIntent();
            index = intent.getIntExtra("index",0);
          RenderPage(index);
        }catch (Exception e){}

    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", index);
    }
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        index = savedInstanceState.getInt("index");
        RenderPage(index);
    }
    private  void GetPic(ImageView im,byte[] ByteArr) {
        try
        {
                Bitmap bmp = BitmapFactory.decodeByteArray(ByteArr, 0, ByteArr.length);
                im.setImageBitmap(bmp);
        }catch (Exception  e)
        {}
    }
    public  void sendNotif()
    {
        Intent intent = new Intent(this,ItemActivityFromDB.class);
        PendingIntent penint = PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(TitleVirew.getText().toString())
                .setAutoCancel(false).addAction(R.mipmap.ic_launcher,"НАЗАД",penint).addAction(R.mipmap.ic_launcher,"ВПЕРЕД",penint).setSmallIcon(R.mipmap.ic_launcher).setPriority(2).setOngoing(true).setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        NotificationManagerCompat notifi = NotificationManagerCompat.from(this);
        notifi.notify(0,builder.build());
   //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeByteArray(ArrImage, 0, ArrImage.length)))
    }
    private  void delete(SQLiteDatabase database)
    {
        try {
            Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                cursor.moveToPosition(index);
                int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                String idstr = cursor.getString(idIndex);
                database.delete(DBHelper.TABLE_CONTACTS,dbHelper.ID+"="+idstr,null);
                Toast.makeText(this,   "Запись удалена!!!", Toast.LENGTH_LONG).show();
            }
            cursor.close();
            finish();
        }catch  (Throwable t){}
    }
@SuppressLint("ResourceAsColor")
public void RenderPage (int i)
{
    try {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
        count = cursor.getCount();
        cursor.moveToPosition(i);
        int pointBlob = cursor.getColumnIndex(DBHelper.KEY_Bytes_Image);
        int pointArtist = cursor.getColumnIndex(DBHelper.KEY_Artist);
        int pointTitle = cursor.getColumnIndex(DBHelper.KEY_Title);
        int pointURL = cursor.getColumnIndex(DBHelper.KEY_URL);
        int pointYouTube = cursor.getColumnIndex(DBHelper.KEY_YOUTUBE);
        byte[] BytesPics =  cursor.getBlob(pointBlob);
        ArrImage = BytesPics;
        GetPic(Impreview,BytesPics);
        String urlyout = cursor.getString(pointYouTube);
        if(urlyout=="" || urlyout==null)
        {
           String htmlData =  "<body bgcolor=\"black\"><div class=\"running\"></div></body></html>";
            YouTView.loadDataWithBaseURL("", htmlData, "text/html", "UTF-8", null);
        }else
        {
            WebSettings webset1 = YouTView.getSettings();
            webset1.setBuiltInZoomControls(false);
            webset1.setJavaScriptEnabled(true);
            YouTView.setWebViewClient(new MyWebViewClient());
            YouTView.loadUrl(urlyout);
            YouTView.setBackgroundColor(R.color.Black);
        }
        String s =  cursor.getString(pointArtist)+" "+ cursor.getString(pointTitle);
        TitleVirew.setText(s);
        String URl =  cursor.getString(pointURL);
        WebSettings webset = LyricsView.getSettings();
        webset.setBuiltInZoomControls(false);
        webset.setJavaScriptEnabled(true);
        LyricsView.setWebViewClient(new MyWebViewClient());
        LyricsView.loadUrl(URl);
    }catch (Exception e){}
}
    @Override
    public void onClick(View view) {
        if (view == next)
        {
            if(index < count-1)
            {
                index++;
                RenderPage(index);
            }
        }else if(view == backs)
        {
            if(index >0)
            {
                index--;
                RenderPage(index);
            }
        }
    }
}
