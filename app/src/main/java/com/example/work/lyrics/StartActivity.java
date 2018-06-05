package com.example.work.lyrics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcel;
import android.os.StrictMode;
import android.preference.ListPreference;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class StartActivity extends AppCompatActivity implements View.OnClickListener {
Button favorites;
    TextView textView;
    TextView textView2;
    WebView webView;
    EditText editText;
    SearchView searchvie;
    TableLayout SongsTable;
    ScrollView scroller;
    Button ab;
    ArrayList<lyr> LyrList = new ArrayList<lyr>();
    public static final String GENIUS = "ql8SWCFqYCKKvYo9FB5FUBtaSMmHgogB75hb-PHVz6SWeZz_OhZNpXATVHHK-Mnb";
    public static  String USER_AGENT ;//"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
private static final String your_client_id = "q-DXjq_xiZVDvgW80kCEmrlIeRrv6zTdoHyOc-IhoVPDZp2r_uJ05TJRjG3ODxT9";
    private static final String your_client_secret = "MKILtCj9rMGHIvQU6BMciNIURbAiZ8GV3A3DXhiYmOv90zr92e3DBCLadCqj_T54Lm91CFxZMs4wYrCpHJiVHw";
            lyrics l=new lyrics(lyrics.SEARCH_ITEM);
private static  String Texs ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (getResources().getConfiguration().orientation==2)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        scroller =findViewById(R.id.scrollview);
        setContentView(R.layout.activity_start);
        SongsTable = findViewById(R.id.SongsTable);
        searchvie = findViewById(R.id.searchvie);
        favorites = findViewById(R.id.favorites);
        ab = findViewById(R.id.about);
      favorites.setOnClickListener(this);
      ab.setOnClickListener(this);
        USER_AGENT = WebSettings.getDefaultUserAgent(StartActivity.this);
        searchvie.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String  query = searchvie.getQuery().toString();
                search(query);
                SongsList(LyrList.size());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    public void SongsList(int size){
        SongsTable.removeAllViews();
        for(int i = 0; i<size; i++){
            TableRow SongsRaw = new TableRow(this);
            TableRow.LayoutParams pr = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            SongsRaw.setId(i);
            SongsRaw.setOnClickListener(this);
            ImageView SongsImage = new ImageView(this);
          new DownloadImageTask(SongsImage).execute(LyrList.get(i).mURLImage);
           SongsImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
             TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(270, 270);
            TextView SongsName = new TextView(this);
            //////////////////////////////////////////////////////////////////////////////////////////
            TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(800,TableLayout.LayoutParams.WRAP_CONTENT);
            //////////////////////////////////////////////////////////////////////////////////////////
            SongsImage.setLayoutParams(layoutParams);
            SongsName.setLayoutParams(layoutParams1);
            SongsName.setText(LyrList.get(i).mArtist+" " + LyrList.get(i).mTitle);
            SongsName.setTextSize(16);
            SongsName.setTextColor(getResources().getColor(R.color.Yellow));
            SongsRaw.addView(SongsImage,0);
            SongsRaw.addView(SongsName,1);
            SongsRaw.setBackgroundColor(getResources().getColor(R.color.Black));
            SongsTable.addView(SongsRaw,i);

        }
    }

    public   ArrayList<lyrics> search(String query) {
        LyrList.clear();
        ArrayList<lyrics> results = new ArrayList<>();
        query = Normalizer.normalize(query, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        JsonObject response = null;
        try {
            URL queryURL = new URL(String.format("http://api.genius.com/search?q=%s", URLEncoder.encode(query, "UTF-8")));
            Connection connection = Jsoup.connect(queryURL.toExternalForm())
                    .header("Authorization", "Bearer " + GENIUS)
                    .ignoreContentType(true);
            Document document = connection.userAgent(USER_AGENT).get();
            response = new JsonParser().parse(document.text()).getAsJsonObject();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null || response.getAsJsonObject("meta").get("status").getAsInt() != 200) {
            Toast.makeText(this, "Ничего не найдено", Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            JsonArray hits = response.getAsJsonObject("response").getAsJsonArray("hits");
            if (hits == null) {
                Toast.makeText(this, "nuul hits", Toast.LENGTH_SHORT).show();
                return null;
            }
            int processed =0;
            while (processed<hits.size())
            {
            JsonObject song = hits.get(processed).getAsJsonObject().getAsJsonObject("result");
            String sc = song.toString();
            String artist = song.getAsJsonObject("primary_artist").get("name").getAsString();
            String title = song.get("title").getAsString();
            String URLImage = song.get("header_image_url").getAsString();
            String url = "http://genius.com/songs/" + song.get("id").getAsString();
            String id =  song.get("id").getAsString();
                lyr l1 = new lyr();
                l1.mArtist = artist;
                l1.id = id;
                l1.mURLImage =  URLImage;
                l1.mTitle =  title;
                l1.mURL = url;
                String n = l1.id;
                LyrList.add(l1);
                processed++;
        }
        } catch (Exception e)
        {
            Toast.makeText(this, "Ошибка в search", Toast.LENGTH_SHORT).show();
        }
     return  results;
    }

    @Override
    public void onClick(View view) {
     int itemClick = view.getId() ;
     if (view == favorites)
     {
         Intent intent = new Intent(this,ListSongs.class);
         startActivity(intent);
     }else  if(view ==ab)
     {
         Intent intent = new Intent(this,AboutTeemActivity.class);
         startActivity(intent);
     }
     else
     {
         Intent intent = new Intent(this,ItemActivity.class);
         intent.putExtra("Artist",LyrList.get(itemClick).mArtist);
         intent.putExtra("Title",LyrList.get(itemClick).mTitle);
         intent.putExtra("URLImage",LyrList.get(itemClick).mURLImage);
         intent.putExtra("idsong",LyrList.get(itemClick).id);
         intent.putExtra("LyricsURL",LyrList.get(itemClick).mURL);
         startActivity(intent);
     }
    }
}
class lyr
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
  class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    Context context;
    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
      //      Toast.makeText(context, "Error Downloaded Image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
