package com.example.enes.myapplication;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class HaberBoxAdapter extends ArrayAdapter<Haber> {
    private LayoutInflater layoutInflater;
    private ArrayList<Haber> haberList;
    private int resource;
    static SparseArray<Bitmap> tablo;

    HaberBoxAdapter(Context context, int resource, ArrayList<Haber> objects) {
        super(context, resource, objects);
        tablo = new SparseArray<>();
        haberList = objects;
        this.resource = resource;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public static void reset(){
        tablo.clear();
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        convertView = layoutInflater.inflate(resource, null);
        ImageView iv = convertView.findViewById(R.id.resim);
        TextView tv = convertView.findViewById(R.id.baslik);
        String resimYolu = "http://"+MainActivity.ip+"/img/" + haberList.get(position).getResim();
        if(tablo.get(position, null) != null)
            iv.setImageBitmap(tablo.get(position));
        else
            new ResimCek(iv, tablo, position).execute(resimYolu);
        tv.setText(haberList.get(position).getBaslik());
        return convertView;
    }
}
