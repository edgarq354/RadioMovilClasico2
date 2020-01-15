package com.elisoft.radiomovilclasico.video_tutorial;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.R;

import java.util.ArrayList;

/**
 * Created by ELIO on 03/07/2017.
 */


public  class item_video extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<Cvideo> items;
    Bundle savedInstanceState;


    public item_video(Activity activity, ArrayList<Cvideo> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<Cvideo> Pedidos) {
        for (int i = 0; i < Pedidos.size(); i++) {
            items.add(Pedidos.get(i));
        }
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_video, null);
        }
        Cvideo video = items.get(position);

        TextView tv_nombre = (TextView) v.findViewById(R.id.tv_nombre);
        TextView tv_descripcion = (TextView) v.findViewById(R.id.tv_descripcion);

        tv_nombre.setText(video.getNombre());
        tv_descripcion.setText(video.getDescripcion());


        return v;
    }


}