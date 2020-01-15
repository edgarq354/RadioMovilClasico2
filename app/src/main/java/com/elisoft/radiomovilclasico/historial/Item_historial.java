package com.elisoft.radiomovilclasico.historial;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.R;

import java.util.List;

/**
 * Created by elisoft on 19-05-17.
 */


public class Item_historial extends ArrayAdapter<CPedido_usuario> {
    public Item_historial(Context context, List<CPedido_usuario> objects) {
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
                    R.layout.item_pedido,
                    parent,
                    false);
        }


        CPedido_usuario ped =  getItem(position);

        TextView tv_nombre= (TextView) convertView.findViewById(R.id.tv_nombre);
        TextView tv_direccion = (TextView) convertView.findViewById(R.id.tv_direccion);
        TextView tv_fecha = (TextView) convertView.findViewById(R.id.tv_fecha);
        TextView tv_estado = (TextView) convertView.findViewById(R.id.tv_estado);
        TextView tv_billetera = (TextView) convertView.findViewById(R.id.tv_billetera);
        TextView tv_clase_vehiculo = (TextView) convertView.findViewById(R.id.tv_clase_vehiculo);
        TextView tv_calificacion_conductor = (TextView) convertView.findViewById(R.id.tv_calificacion_conductor);
        TextView tv_calificacion_vehiculo = (TextView) convertView.findViewById(R.id.tv_calificacion_vehiculo);

/*
* 2=pedido finalizado correctamente.
* 3=pedido cancelado por el usuario
* 4=pedido cancelado por el usuario.
* 5=pedido cancelado por el taxista por alguna razon.
* */

        tv_nombre.setText(ped.getNombre()+" "+ped.getApellido());
        tv_direccion.setText(ped.getIndicacion());
        tv_fecha.setText(ped.getFecha_pedido());
        if(ped.getEstado_billetera()==1){
            tv_billetera.setText("Billetera: BOB "+ped.getMonto_billetera());
        }else
        {
            tv_billetera.setVisibility(View.INVISIBLE);
        }

        tv_calificacion_conductor.setText(String.valueOf(ped.getCalificacion_conductor()));
        tv_calificacion_vehiculo.setText(String.valueOf(ped.getCalificacion_vehiculo()));

        switch (ped.getClase_vehiculo()){
            case 1: tv_clase_vehiculo.setText("MOVIL NORMAL");break;
            case 2: tv_clase_vehiculo.setText("MOVIL DE LUJO");break;
            case 3: tv_clase_vehiculo.setText("MOVIL CON AIRE");break;
            case 4: tv_clase_vehiculo.setText("MOVIL CON MALETERO");break;
            case 5: tv_clase_vehiculo.setText("MOVIL PARA PEDIDOS");break;
            case 6: tv_clase_vehiculo.setText("RESERVA DE UN MOVIL");break;
            case 7: tv_clase_vehiculo.setText("MOTO");break;
            case 8: tv_clase_vehiculo.setText("MOTO PARA PEDIDOS");break;
        }

        if(ped.getEstado_pedido()==2)
        {
            try {
                tv_estado.setText(ped.getMonto_total()+" Bs");
                tv_estado.setBackgroundResource(R.drawable.bk_completado);
                tv_estado.setTextColor(Color.parseColor("#536DFE"));

            }catch (Exception e)
            {
                Log.e("Estado",e.toString());
            }
        }
        else if(ped.getEstado_pedido()==5)
         { try {
                tv_estado.setText(String.valueOf("CANCELO"));
                tv_estado.setBackgroundResource(R.drawable.bk_cancelado);
                tv_estado.setTextColor(Color.parseColor("#fc0101"));
            }catch (Exception e)
            {
                Log.e("Estado cancelo",e.toString());
            }
        }
        else if(ped.getEstado_pedido()==4)
        {
            tv_estado.setText(String.valueOf("CANCELE"));
            tv_estado.setBackgroundResource(R.drawable.bk_cancelado);
            tv_estado.setTextColor(Color.parseColor("#fc0101"));
        }




        return convertView;
    }
}