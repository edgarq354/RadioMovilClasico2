package com.elisoft.radiomovilclasico.corporativo;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.R;

import java.util.ArrayList;

public class Lista_usuarios extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<CContacto> items;
    String nombre;
    String url="";
    int usuario=0;



    public Lista_usuarios(Activity activity, ArrayList<CContacto> items, String url, int usuario ) {
        this.activity = activity;
        this.items = items;
        this.url=url;
        this.usuario=usuario;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<CContacto> Contacto) {
        for (int i = 0; i < Contacto.size(); i++) {
            items.add(Contacto.get(i));
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
            v = inf.inflate(R.layout.lista_usuarios, null);
        }

        CContacto dir = items.get(position);

        TextView nombre = (TextView) v.findViewById(R.id.category);
        nombre.setText(dir.getNombre());

        TextView celular = (TextView) v.findViewById(R.id.texto);
        celular.setText(dir.getNumero());



        return v;
    }





}