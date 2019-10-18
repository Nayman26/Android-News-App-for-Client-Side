package com.example.enes.myapplication;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.widget.ImageView;

import java.io.InputStream;

public class ResimCek extends AsyncTask<String, Void, Bitmap> {
    @SuppressLint("StaticFieldLeak")
    private ImageView iv;
    private SparseArray<Bitmap> tablo;
    private int position;

    ResimCek(ImageView iv) {
        this.iv = iv;
    }

    ResimCek(ImageView iv, SparseArray<Bitmap> tablo, int position) {
        this.iv = iv;
        this.tablo = tablo;
        this.position = position;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bmp = null;
        try (InputStream is = new java.net.URL(params[0]).openStream()) {
            bmp = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(tablo != null)
            tablo.put(position,bmp);

        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        iv.setImageBitmap(bitmap);
    }
}
