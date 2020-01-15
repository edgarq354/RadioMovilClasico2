package com.elisoft.radiomovilclasico.compartir;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.R;

import java.util.ArrayList;


/**
 * Created by ELIO on 28/10/2016.
 */

public class UsuarioAdapter extends BaseAdapter {

    String nombre;
    protected Activity activity;
    protected ArrayList<CUsuario> items;

    CUsuario ped;

    public UsuarioAdapter(Activity activity, ArrayList<CUsuario> items) {
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

    public void addAll(ArrayList<CUsuario> Pedidos) {
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
            v = inf.inflate(R.layout.usuario_card, null);
        }
        ped = items.get(position);



        TextView nombre = (TextView) v.findViewById(R.id.tv_nombre);
        TextView celular = (TextView) v.findViewById(R.id.tv_celular);

        //poner en el String todos los puntos registrados.....
        nombre.setText(ped.getNombre()+" "+ped.getApellido());
        celular.setText(ped.getCelular());

        return v;
    }

}