package com.elisoft.radiomovilclasico.guia_turistica;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.menu_otra_direccion.Otra_direccion;
import com.elisoft.radiomovilclasico.R;

import java.util.ArrayList;

/**
 * Created by ELIO on 23/12/2017.
 */

public class Item_guia_comercial extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<CLugar> items;
    private Context mContext;

    public Item_guia_comercial(Context c, Activity activity, ArrayList<CLugar> items) {
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
            v = inf.inflate(R.layout.item_lugar, null);
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        final CLugar  ped = items.get(position);

        TextView nombre= (TextView) v.findViewById(R.id.tv_nombre);
        TextView direccion= (TextView) v.findViewById(R.id.tv_direccion);
        FloatingActionButton fb_llamar= (FloatingActionButton) v.findViewById(R.id.fb_llamar);
        FloatingActionButton fb_whatsapp= (FloatingActionButton) v.findViewById(R.id.fb_whatsapp);
        FloatingActionButton fb_ver_mapa= (FloatingActionButton) v.findViewById(R.id.fb_ver_mapa);
        nombre.setText(ped.getNombre());
        direccion.setText(ped.getDireccion());

        fb_llamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ped.getTelefono().toString().length()>3){
                    Intent llamada = new Intent(Intent.ACTION_DIAL);
                    llamada.setData(Uri.parse("tel:" +ped.getTelefono().toString().trim()));

                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        verificar_permiso_llamada();
                    }else{
                        mContext.startActivity(llamada);
                    }

                }else {
                    Toast.makeText(mContext,"No tiene telefono",Toast.LENGTH_SHORT).show();
                }


            }
        });

        fb_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ped.getWhatsapp().toString().length()>5){
                String formattedNumber = ped.getWhatsapp().toString().trim();
                try{
                    Intent sendIntent =new Intent("android.intent.action.MAIN");
                    sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT,"hola");
                    sendIntent.putExtra("sms_body", "Hola que tal");
                    sendIntent.putExtra("jid", formattedNumber +"@s.whatsapp.net");
                    sendIntent.setPackage("com.whatsapp");
                    mContext.startActivity(sendIntent);
                }
                catch(Exception e)
                {
                    Toast.makeText(mContext,"Error/n"+ e.toString(),Toast.LENGTH_SHORT).show();
                }
                }else {
                    Toast.makeText(mContext,"No tiene WhatsApp",Toast.LENGTH_SHORT).show();
                }
            }
        });

        fb_ver_mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapa=new Intent(mContext, Otra_direccion.class);
                mapa.putExtra("id",String.valueOf(ped.getId()));
                mapa.putExtra("nombre",ped.getNombre());
                mapa.putExtra("direccion",ped.getDireccion());
                mapa.putExtra("latitud",ped.getLatitud());
                mapa.putExtra("longitud",ped.getLongitud());
                mapa.putExtra("id_categoria",String.valueOf(ped.getId_categoria()));
               mContext.startActivities(new Intent[]{mapa});

            }
        });




        return v;

    }

    public void verificar_permiso_llamada()
    {
        final String[] PERMISSIONS = {Manifest.permission.CALL_PHONE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(mContext);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a LLAMADA.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(activity,
                            PERMISSIONS,
                            1);

                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();

                }
            });
            dialogo1.show();
        } else {
            ActivityCompat.requestPermissions(activity,
                    PERMISSIONS,
                    1);
        }
    }


}