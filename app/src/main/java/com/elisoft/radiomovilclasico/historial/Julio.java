package com.elisoft.radiomovilclasico.historial;

/**
 * Created by elisoft on 19-05-17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.carreras.Carreras;

import java.util.ArrayList;

public class Julio  extends Fragment {
    ArrayList<CPedido_usuario> historial=new ArrayList<CPedido_usuario>();
    ListView lv_lista;
    TextView tv_error;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tabs_julio, container, false);
        tv_error = (TextView) rootView.findViewById(R.id.tv_error);
        lv_lista = (ListView) rootView.findViewById(R.id.lv_lista);
        // Instancia del ListView.

        Item_historial adaptador = new Item_historial(getActivity(),historial);
        lv_lista.setAdapter(adaptador);

        if(historial.size()>0)
        {
            tv_error.setText("");
        }
        else
        {
            tv_error.setText("No tienes ningun pedido.");
        }
        lv_lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(historial.get(i).getEstado_pedido()==2){
                    CPedido_usuario hi=new CPedido_usuario();
                    hi=historial.get(i);
                    mensaje(hi);
                }

            }
        });

        return rootView;
    }
    public void cargar_lista(ArrayList<CPedido_usuario> cPedido_taxis)
    {
        historial=cPedido_taxis;

    }
    public void mensaje(CPedido_usuario historial)
    {
        try {
            Intent i = new Intent(getActivity(), Carreras.class);

            i.putExtra("id_pedido", String.valueOf(historial.getId()));
            i.putExtra("id_vehiculo", String.valueOf(historial.getPlaca()));
            startActivity(i);
        }catch (Exception e)
        {
            Log.e("carrera",e.toString());
        }
    }
}