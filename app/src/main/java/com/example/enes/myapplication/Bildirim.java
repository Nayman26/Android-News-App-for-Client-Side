package com.example.enes.myapplication;;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Bildirim extends Service {

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
        new arkaPlan().execute("http://"+MainActivity.ip+":8080/haberler/filtrele?limit=1");
        handler.postDelayed(runnable, 60000);
        }
    };

    @Override
    public int onStartCommand(Intent _intent, int flags, int startId) {
        handler.post(runnable);
        return super.onStartCommand(_intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("StaticFieldLeak")
    public class arkaPlan extends AsyncTask<String, Void, Haber> {
        @Override
        protected Haber doInBackground(String... params) {
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
                    haberJSO = haberler.getJSONObject(0);

                    haber = new Haber();
                    haber.setId(haberJSO.getInt("id"));
                    Log.e("qqqq",""+haber.getId());
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Bildirim.this);
                    int bildirimID = preferences.getInt("BildirimID", -1);
                    Log.e("qqqq",""+bildirimID);
                    if(bildirimID < 0 || bildirimID >= haber.getId())
                        return null;

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("BildirimID",haber.getId());
                    editor.apply();

                    haber.setBaslik(haberJSO.getString("baslik"));
                    haber.setIcerik(haberJSO.getString("icerik"));
                    haber.setResim(haberJSO.getString("resim"));
                    haber.setTur(haberJSO.getString("tur"));
                    haber.setTarih(haberJSO.getString("tarih"));
                    haber.setBegenme(haberJSO.getInt("begenme"));
                    haber.setBegenmeme(haberJSO.getInt("begenmeme"));
                    haber.setGoruntulenme(haberJSO.getInt("goruntulenme") + 1);
                    HttpURLConnection connection2;
                    URL url2 = new URL("http://"+MainActivity.ip+":8080/haberler/goruntulenme?id=" + haber.getId());
                    connection2 = (HttpURLConnection) url2.openConnection();
                    connection2.connect();
                    connection2.getResponseMessage();

                    return haber;
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
        protected void onPostExecute (Haber haber){
            super.onPostExecute(haber);

            if(haber != null){
                Intent intent = new Intent(Bildirim.this, IcerikActivity.class);
                intent.putExtra("tumIcerik", haber.getIcerik());
                intent.putExtra("resimIcerik", haber.getResim());
                intent.putExtra("ID", haber.getId());
                intent.putExtra("begenme", haber.getBegenme());
                intent.putExtra("begenmeme", haber.getBegenmeme());
                intent.putExtra("goruntulenme", haber.getGoruntulenme());
                intent.putExtra("tarih", haber.getTarih());

                PendingIntent contentIntent = PendingIntent.getActivity(Bildirim.this, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder b = new NotificationCompat.Builder(Bildirim.this);

                b.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(haber.getBaslik())
                        .setContentText(haber.getIcerik())
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                        .setContentIntent(contentIntent);

                NotificationManager notificationManager = (NotificationManager) Bildirim.this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(6, b.build());
            }


        }
    }
}
