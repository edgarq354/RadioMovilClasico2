package com.elisoft.radiomovilclasico.carreras;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.Pedido_perfil_taxi;
import com.elisoft.radiomovilclasico.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * Created by ELIO on 28/10/2016.
 */

public  class Items_carreras extends BaseAdapter {

    String url_pagina;
    String nombre;
    protected Activity activity;
    protected ArrayList<CCarrera> items;
    private Context mContext;
    Bundle savedInstanceState;
    ImageView im_mapa;

    CCarrera ped;
    TextView tv_numero;
    String id_carrera="";

    public Items_carreras(Context c, Bundle b, Activity activity, ArrayList<CCarrera> items) {
        this.activity = activity;
        this.items = items;
        this.savedInstanceState = b;
        this.mContext=c;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<CCarrera> Pedidos) {
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
            v = inf.inflate(R.layout.item_carreras, null);
        }
        ped = items.get(position);



        TextView fecha = (TextView) v.findViewById(R.id.fecha);
        TextView monto = (TextView) v.findViewById(R.id.monto);
        ImageView ib_conductor=(ImageView)v.findViewById(R.id.ib_conductor);
        TextView tv_distancia=(TextView)v.findViewById(R.id.tv_distancia);
        tv_numero = (TextView) v.findViewById(R.id.tv_numero);
        ImageView im_mapa=(ImageView)v.findViewById(R.id.im_mapa);
        WebView wv_mapa = (WebView)v.findViewById(R.id.wv_mapa);
        LinearLayout ll_ver_datos=(LinearLayout)v.findViewById(R.id.ll_ver_datos);
        String url=mContext.getString(R.string.servidor)+"ver_carrera.php?id_carrera="+ped.getId()+"&id_pedido="+ped.getId_pedido();
        // wv_mapa.loadUrl(url);
        //wv_mapa.loadUrl("https://universoandroidstudio.blogspot.com/2016/");
        wv_mapa.getSettings().setJavaScriptEnabled(true);
        wv_mapa.setWebViewClient(new WebViewClient());
        wv_mapa.loadUrl(url);
        this.im_mapa=im_mapa;

        //poner en el String todos los puntos registrados.....
        boolean sw=true;

        try {
            int id = ped.getId_pedido();
        }catch (Exception e)
        {
            sw=false;
        }
        final View finalV = v;
        String  url1=  "public/Imagen_Conductor/Perfil-"+String.valueOf(ped.getId_conductor())+".png";
        Picasso.with(mContext).load(mContext.getString(R.string.servidor_web)+url1).placeholder(R.mipmap.ic_perfil).into(ib_conductor);

        ll_ver_datos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ee = new Intent(mContext, Pedido_perfil_taxi.class);
                ee.putExtra("id_conductor",  String.valueOf(ped.getId_conductor()));
                ee.putExtra("id_vehiculo", String.valueOf(ped.getPlaca()));
                finalV.getContext().startActivity( ee);
            }
        });


        if(sw==true) {
            fecha.setText(ped.getFecha_fin());
            monto.setText( ped.getMonto()+" Bs.");
            tv_distancia.setText( ped.getDistancia()+" mt.");

        }






        return v;
    }





}