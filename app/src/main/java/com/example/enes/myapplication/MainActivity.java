package com.example.enes.myapplication;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static String ip = "192.168.43.192";
    ListView listView;
    Button gundem,spor,egitim,ekonomi;
    public static ArrayList<Haber> haberList;
    static HaberBoxAdapter adapter;
    Haber hbr;
    boolean a =true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list);
        gundem = findViewById(R.id.Gundem);
        spor = findViewById(R.id.Spor);
        egitim = findViewById(R.id.Egitim);
        ekonomi = findViewById(R.id.Ekonomi);
        haberList = new ArrayList<>();
        adapter = new HaberBoxAdapter(getApplicationContext(), R.layout.haberbox, haberList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hbr = haberList.get(position);
                hbr.setGoruntulenme(hbr.getGoruntulenme()+1);
                final String urlGoruntu="http://"+ip+":8080/haberler/goruntulenme?id="+hbr.getId();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpURLConnection connection;
                            URL url = new URL(urlGoruntu);
                            connection = (HttpURLConnection) url.openConnection();
                            connection.connect();
                            connection.getResponseMessage();

                            Intent intent = new Intent(MainActivity.this,IcerikActivity.class);
                            intent.putExtra("tumIcerik",hbr.getIcerik());
                            intent.putExtra("resimIcerik",hbr.getResim());
                            intent.putExtra("ID",hbr.getId());
                            intent.putExtra("begenme",hbr.getBegenme());
                            intent.putExtra("begenmeme",hbr.getBegenmeme());
                            intent.putExtra("goruntulenme",hbr.getGoruntulenme());
                            intent.putExtra("tarih",hbr.getTarih());

                            startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        if(a=true)
        new arkaPlan().execute("http://"+ip+":8080/haberler/filtrele");
        gundem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                haberList.removeAll(haberList);
                HaberBoxAdapter.reset();
                new arkaPlan().execute("http://"+ip+":8080/haberler/filtrele?tur=gundem");
                gundem.setEnabled(false);
                spor.setEnabled(true);
                egitim.setEnabled(true);
                ekonomi.setEnabled(true);
                a=false;
            }
        });

        spor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                haberList.removeAll(haberList);
                HaberBoxAdapter.reset();
                new arkaPlan().execute("http://"+ip+":8080/haberler/filtrele?tur=spor");
                spor.setEnabled(false);
                egitim.setEnabled(true);
                gundem.setEnabled(true);
                ekonomi.setEnabled(true);
                a=false;
            }
        });

        egitim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                haberList.removeAll(haberList);
                HaberBoxAdapter.reset();
                new arkaPlan().execute("http://"+ip+":8080/haberler/filtrele?tur=egitim");
                egitim.setEnabled(false);
                spor.setEnabled(true);
                gundem.setEnabled(true);
                ekonomi.setEnabled(true);
                a=false;
            }
        });

        ekonomi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                haberList.removeAll(haberList);
                HaberBoxAdapter.reset();
                new arkaPlan().execute("http://"+ip+":8080/haberler/filtrele?tur=ekonomi");
                ekonomi.setEnabled(false);
                spor.setEnabled(true);
                egitim.setEnabled(true);
                gundem.setEnabled(true);
                a=false;
            }
        });
        Intent servis = new Intent(getApplicationContext(),Bildirim.class);
        startService(servis);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listView.invalidateViews();
    }

    @SuppressLint("StaticFieldLeak")
    public class arkaPlan extends AsyncTask<String, Void, ArrayList<Haber>> {
        @Override
        protected ArrayList<Haber> doInBackground(String... params) {
            HttpURLConnection connection;
            BufferedReader br;
            try {
                URL url = new URL(params[0]);
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
                    JSONObject reader = new JSONObject(dosya);
                    JSONArray haberler = reader.getJSONArray("haberler");
                    JSONObject haberJSO;
                    Haber haber;
                    for (int i = 0; i < haberler.length(); i++) {
                        haberJSO = haberler.getJSONObject(i);
                        haber = new Haber();
                        haber.setId(haberJSO.getInt("id"));
                        haber.setBaslik(haberJSO.getString("baslik"));
                        haber.setIcerik(haberJSO.getString("icerik"));
                        haber.setResim(haberJSO.getString("resim"));
                        haber.setTur(haberJSO.getString("tur"));
                        haber.setTarih(haberJSO.getString("tarih"));
                        haber.setBegenme(haberJSO.getInt("begenme"));
                        haber.setBegenmeme(haberJSO.getInt("begenmeme"));
                        haber.setGoruntulenme(haberJSO.getInt("goruntulenme"));
                        haberList.add(haber);
                    }
                    return haberList;
                } catch (Exception e) {
                    Log.d("HATA", "JSON HATASI");
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.d("HATA", "ARKAPLAN HATASI");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Haber> hList) {
            super.onPostExecute(hList);
            listView.invalidateViews();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            if(hList.get(0).getId() > preferences.getInt("BildirimID", -1)){
                editor.putInt("BildirimID",hList.get(0).getId());
                editor.apply();
            }
            if (hList == null)
                Toast.makeText(MainActivity.this, "Haber Listesi BOŞ", Toast.LENGTH_LONG).show();
        }
    }
}
