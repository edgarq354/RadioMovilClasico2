package com.elisoft.radiomovilclasico.recorrido_compartido;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.R;

import java.util.List;

/**
 * Created by elisoft on 19-05-17.
 */


public class Item_carrera_compartido extends ArrayAdapter<CCompartido> {
    public Item_carrera_compartido(Context context, List<CCompartido> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(
                    R.layout.item_recorrido_carrera,
                    parent,
                    false);
        }


        CCompartido ped =  getItem(position);

        TextView tv_nombre= (TextView) convertView.findViewById(R.id.tv_nombre);
        TextView tv_direccion = (TextView) convertView.findViewById(R.id.tv_direccion);
        ImageView im_ojo = (ImageView) convertView.findViewById(R.id.im_ojo);

        if(ped.getEstado_carrera()==1){
            tv_nombre.setText(ped.getId_carrera() +" - Inicio");
        }else{
            tv_nombre.setText(ped.getId_carrera() +" - Finalizo");
        }
        tv_direccion.setText(ped.getFecha_inicio());

        if(ped.getEstado()==1)
        {
         im_ojo.setVisibility(View.VISIBLE);
        }
        else{
            im_ojo.setVisibility(View.INVISIBLE);
        }




        return convertView;
    }
}