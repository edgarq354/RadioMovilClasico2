package com.elisoft.radiomovilclasico.compartir;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.R;

import java.util.ArrayList;


/**
 * Created by ELIO on 28/10/2016.
 */

public class Usuario_select_adapter extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<CUsuario> items;

    CUsuario ped;

    public Usuario_select_adapter(Activity activity, ArrayList<CUsuario> items) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_seleccionar_usuario, null);
        }
        ped = items.get(position);



        TextView nombre = (TextView) v.findViewById(R.id.tv_nombre);
        TextView celular = (TextView) v.findViewById(R.id.tv_celular);
        final CheckBox cb_seleccionar = (CheckBox) v.findViewById(R.id.cb_seleccionar);

        //poner en el String todos los puntos registrados.....
        nombre.setText(ped.getNombre()+" "+ped.getApellido());
        celular.setText(ped.getCelular());


        cb_seleccionar.setChecked(ped.isEstado());

        cb_seleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_seleccionar.isChecked() == true) {
                    ped = items.get(position);
                    CUsuario nuevo = new CUsuario(ped.getId(), ped.getNombre(),ped.getApellido(),ped.getCorreo(),ped.getCelular());
                    items.remove(position);
                    nuevo.setEstado(true);
                    items.add(position,nuevo);
                } else
                {
                    ped = items.get(position);
                    CUsuario nuevo = new CUsuario(ped.getId(), ped.getNombre(),ped.getApellido(),ped.getCorreo(),ped.getCelular());
                    items.remove(position);
                    nuevo.setEstado(false);
                    items.add(position,nuevo);
                }

            }
        });


        return v;
    }

}