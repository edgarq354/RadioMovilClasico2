package com.elisoft.radiomovilclasico.reserva;

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
 * Created by ELIO on 24/12/2017.
 */

public class Item_reserva  extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<CReserva> items;
    private Context mContext;

    public Item_reserva(Context c, Activity activity, ArrayList<CReserva> items) {
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
            v = inf.inflate(R.layout.item_reserva, null);
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        final CReserva  ped = items.get(position);

        TextView numero_casa= (TextView) v.findViewById(R.id.tv_numero_casa);
        TextView fecha= (TextView) v.findViewById(R.id.tv_fecha);
        TextView referencia= (TextView) v.findViewById(R.id.tv_referencia);
        TextView estado= (TextView) v.findViewById(R.id.tv_estado);

        numero_casa.setText("#:"+ped.getId());
        fecha.setText(ped.getFecha());
        referencia.setText(ped.getReferencia());
        if(ped.getEstado()==0){
            estado.setText("En proceso");
        }else if(ped.getEstado()==1){
            estado.setText("Aceptado");
        }else{
            estado.setText("Cancelado");
        }





        return v;

    }



}