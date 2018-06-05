package com.example.work.lyrics;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.Timer;
import java.util.TimerTask;

public class ItemActivity extends AppCompatActivity implements View.OnClickListener{
    Song song = new Song();
    public static final String GENIUS = "ql8SWCFqYCKKvYo9FB5FUBtaSMmHgogB75hb-PHVz6SWeZz_OhZNpXATVHHK-Mnb";
    public static  String USER_AGENT;
    WebView YouTView;
    WebView LyricsView;
    ScrollView scroll;
    ImageView Impreview;
    TextView TitleVirew;
    Button btnplus;
    boolean flag = false;
 public   boolean flagIm =false;
    DBHelper dbhelper = new DBHelper(this);
    SQLiteDatabase sdb;
    byte [] bytespic;
   @SuppressLint("SetJavaScriptEnabled")
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        YouTView = findViewById(R.id.YouTView);
        LyricsView = findViewById(R.id.LyricsView);
       scroll = findViewById(R.id.scrollview);
       Impreview = findViewById(R.id.Impreview);
       TitleVirew = findViewById(R.id.TitleVirew);
       btnplus = findViewById(R.id.btnplus);
       btnplus.setOnClickListener(this);
       Intent intent = getIntent();
       song.id = intent.getStringExtra("idsong");
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
         try {
             NEWThread();
             USER_AGENT = WebSettings.getDefaultUserAgent(ItemActivity.this);
           song.mArtist =  intent.getStringExtra("Artist");
              song.mTitle =  intent.getStringExtra("Title");
             song.mURLImage = intent.getStringExtra("URLImage");
             song.mURL = intent.getStringExtra("LyricsURL");
             new DownloadImageTask1(Impreview).execute(song.mURLImage);
             String s="";
             s = song.mArtist + " " + song.mTitle;
              TitleVirew.setText(s);
             WebSettings webset = LyricsView.getSettings();
             webset.setBuiltInZoomControls(false);
             webset.setJavaScriptEnabled(true);
             LyricsView.setWebViewClient(new MyWebViewClient());
            LyricsView.loadUrl(song.mURL);
         }catch (Exception e){}
       flagIm=true;
    }

    @SuppressLint({"SetJavaScriptEnabled", "ResourceAsColor"})
    private  void GetVideoYoutube() {
        try {
            WebSettings webset = YouTView.getSettings();
            webset.setBuiltInZoomControls(false);
            webset.setJavaScriptEnabled(true);
            YouTView.setWebViewClient(new MyWebViewClient());
          /*  String frameVideo = "<html><body align='center' ><iframe width=\"300\" height=\"150\" src=\"https://www.youtube.be/embed/";
            String frameVideo1 = "\"; frameborder=\"0\" allowfullscreen></iframe></body></html>";*/
            String frameVideo = "";
            if (song.mURLYoutube != null || song.mURLYoutube=="") {
                String urly = song.mURLYoutube;
                frameVideo = urly;
            }
          // YouTView.loadData(frameVideo, "text/html", "utf-8");
            if (frameVideo!="" || frameVideo!=null)
            {
              YouTView.loadUrl(frameVideo);
            }
            YouTView.setBackgroundColor(android.R.color.transparent);
        }catch (Exception e){}
    }

    private  void NEWThread()
    {
       new Thread(new Runnable() {
            @Override
            public void run() {
                song.mURLYoutube =  GetYoutube(song.id);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GetVideoYoutube();
                        flag = true;
                    }
                });
            }
        }).start();
    }
    public   String GetYoutube(String id) {
        String Res="";
        id = Normalizer.normalize(id, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        JsonObject response = null;
        try {
            URL queryURL = new URL(String.format("http://api.genius.com/songs/%s", URLEncoder.encode(id, "UTF-8")));
            Connection connection = Jsoup.connect(queryURL.toExternalForm())
                    .header("Authorization", "Bearer " + GENIUS)
                    .ignoreContentType(true);
            Document document = connection.userAgent(USER_AGENT).get();
            response = new JsonParser().parse(document.text()).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null || response.getAsJsonObject("meta").get("status").getAsInt() != 200) {
            Toast.makeText(this, "nullResp", Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            JsonObject song = response.getAsJsonObject("response").getAsJsonObject("song");
            JsonObject hits = response.getAsJsonObject("response").getAsJsonObject("song");
            JsonArray songs = hits.getAsJsonArray("media");
                JsonElement provider   =  songs.get(0).getAsJsonObject().get("provider");
                String pro = provider.toString();
                if (pro==null || pro=="")
                {
                    return null;
                }else {
                    pro = pro.substring(1, pro.length() - 1);
                    if (pro.equals("youtube"))
                    {
                        JsonElement url =    songs.get(0).getAsJsonObject().get("url");
                        String u = url.toString();
                        Res = u.substring(1,u.length()-1);
                    }else return null;
                }
        } catch (Exception e)
        {
        }
        return  Res ;
    }
    private   void BDINSERT(View view)
    {
        sdb = dbhelper.getWritableDatabase();
        SavePic();
        ContentValues cv = new ContentValues();
        cv.put(dbhelper.KEY_ID, song.id);
        cv.put(dbhelper.KEY_Artist, song.mArtist);
        cv.put(dbhelper.KEY_Title, song.mTitle);
        cv.put(dbhelper.KEY_Bytes_Image, bytespic);
        cv.put(dbhelper.KEY_URLImage, song.mURLImage);
        cv.put(dbhelper.KEY_URL, song.mURL);
        cv.put(dbhelper.KEY_YOUTUBE, song.mURLYoutube);
        sdb.insert(DBHelper.TABLE_CONTACTS, null, cv);
        Toast.makeText(this, "Закладка добавлена", Toast.LENGTH_LONG).show();
        dbhelper.close();
    }
    private  void SavePic()
    {
            Bitmap bmp =  ((BitmapDrawable)Impreview.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
            bmp.compress(Bitmap.CompressFormat.PNG,100,stream);
        int size = stream.toByteArray().length;
        bytespic = new byte[size];
        bytespic = stream.toByteArray();
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, String.valueOf(flagIm), Toast.LENGTH_SHORT).show();
        if (view ==btnplus)
        {
            if(flag==true && flagIm==true)
          {
            BDINSERT(btnplus);
          }
        }
    }
}
class Song
{
    public String mTitle;
    public String id;
    public  String urlimage;
    public String mArtist;
    public String mOriginalTitle;
    public String mOriginalArtist;
    public String mSourceUrl;
    public String mCoverURL;
    public String mLyrics;
    public String mSource;
    public String mURLImage;
    public String mURL;
    public String mURLYoutube;
    public String getOriginalTrack() {
        if (mOriginalTitle != null)
            return mOriginalTitle;
        else
            return mTitle;
    }
}
class DownloadImageTask1 extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    Context context;
    public DownloadImageTask1(ImageView bmImage) {
        this.bmImage = bmImage;
    }
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }
    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
class MyWebViewClient extends WebViewClient
{
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        view.loadUrl(url);
        return true;
    }
}
