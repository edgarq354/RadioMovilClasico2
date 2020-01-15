package com.elisoft.radiomovilclasico.guia_turistica;

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
 * Created by ELIO on 23/12/2017.
 */

public class Item_guia_comercial_categoria extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<CCategoria> items;
    private Context mContext;

    public Item_guia_comercial_categoria(Context c, Activity activity, ArrayList<CCategoria> items) {
        this.activity = activity;
        this.items = items;
        this.mContext=c;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
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
            v = inf.inflate(R.layout.item_categoria, null);
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        CCategoria ped = items.get(position);

        TextView titulo= (TextView) v.findViewById(R.id.tv_nombre);
        titulo.setText(ped.getNombre());


        return v;
    }



}