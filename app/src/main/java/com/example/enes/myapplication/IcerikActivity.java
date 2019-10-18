package com.example.enes.myapplication;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IcerikActivity extends AppCompatActivity {
    ImageView iv;
    ScrollView sv;
    TextView tvIcerik, sGrntlnme, sBegenme, sBegenmeme;
    FloatingActionButton fab1;
    FloatingActionButton fab2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icerik);
        iv = findViewById(R.id.resimIcerik);
        sv = findViewById(R.id.scrollView);
        tvIcerik = findViewById(R.id.txIcerik);
        sBegenme = findViewById(R.id.txBegenmeSayisi);
        sBegenmeme = findViewById(R.id.txBegenmemeSayisi);
        sGrntlnme = findViewById(R.id.txGoruntulenmeSayisi);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        final int id = getIntent().getIntExtra("ID",0);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(preferences.contains(""+id)){
            fab1.setEnabled(preferences.getInt(""+id,-1) != 1);
            fab2.setEnabled(preferences.getInt(""+id,1) != -1);
        }
        String icerik = getIntent().getStringExtra("tumIcerik");
        tvIcerik.setText(icerik);
        String strResim = "http://"+MainActivity.ip+"/img/" + getIntent().getStringExtra("resimIcerik");
        new ResimCek(iv).execute(strResim);
        sBegenme.setText(""+getIntent().getIntExtra("begenme",0));
        sBegenmeme.setText(""+getIntent().getIntExtra("begenmeme",0));
        sGrntlnme.setText(""+getIntent().getIntExtra("goruntulenme",0));
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!preferences.contains(""+id))
                    new Sorgu().execute("http://"+MainActivity.ip+":8080/haberler/begen?id="+id+"&onay=true");
                else
                    new Sorgu().execute("http://"+MainActivity.ip+":8080/haberler/begenme?id="+id+"&onay=false",
                            "http://"+MainActivity.ip+":8080/haberler/begen?id="+id+"&onay=true");
                preferences.edit().putInt(""+id,1).apply();
                fab1.setEnabled(false);
                fab2.setEnabled(true);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!preferences.contains(""+id))
                    new Sorgu().execute("http://"+MainActivity.ip+":8080/haberler/begenme?id="+id+"&onay=true");
                else
                    new Sorgu().execute("http://"+MainActivity.ip+":8080/haberler/begen?id="+id+"&onay=false",
                            "http://"+MainActivity.ip+":8080/haberler/begenme?id="+id+"&onay=true");
                preferences.edit().putInt(""+id,-1).apply();
                fab2.setEnabled(false);
                fab1.setEnabled(true);
            }
        });

    }

    private class Sorgu extends AsyncTask<String, String, Void > {

        private Haber haber = new Haber();

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection;
            BufferedReader br;
            String link = params[0];
            try {
                if(params.length > 1){
                    URL url = new URL(link);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    connection.getResponseMessage();
                    connection.disconnect();
                    link = params[1];
                }

                URL url = new URL(link);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                String satir;
                String dosya = "";
                while ((satir = br.readLine()) != null) {
                    dosya += satir;
                    Log.d("Haber", dosya);
                }
                try {
                    JSONObject haberJSO = new JSONObject(dosya);
                    haber.setBegenme(haberJSO.getInt("begenme"));
                    haber.setBegenmeme(haberJSO.getInt("begenmeme"));
                    haber.setGoruntulenme(haberJSO.getInt("goruntulenme"));
                } catch (Exception e) {
                    Log.d("HATA", "JSON HATASI 2");
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.d("HATA", "SORGU SINIFI HATASI");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            sBegenme.setText(String.valueOf(haber.getBegenme()));
            sBegenmeme.setText(String.valueOf(haber.getBegenmeme()));
            sGrntlnme.setText(String.valueOf(haber.getGoruntulenme()));
        }
    }
}
