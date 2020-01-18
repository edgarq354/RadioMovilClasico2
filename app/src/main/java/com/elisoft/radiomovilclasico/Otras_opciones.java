package com.elisoft.radiomovilclasico;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.Scanear.QR_verificar_vehiculo;
import com.elisoft.radiomovilclasico.guia_turistica.Guia_comercial_catergoria;
import com.elisoft.radiomovilclasico.menu_otra_direccion.Otra_direccion;
import com.elisoft.radiomovilclasico.perfil.Perfil_pasajero;
import com.elisoft.radiomovilclasico.video_tutorial.Menu_video;
import com.google.android.gms.location.LocationServices;

public class Otras_opciones extends AppCompatActivity  implements View.OnClickListener{


    TextView tv_guia_comercial,tv_video,tv_qr_movil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otras_opciones);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_guia_comercial=(TextView)findViewById(R.id.tv_guia_comercial);
        tv_video=(TextView)findViewById(R.id.tv_video);
        tv_qr_movil=(TextView)findViewById(R.id.tv_qr_movil);

        tv_guia_comercial.setOnClickListener(this);
        tv_video.setOnClickListener(this);
        tv_qr_movil.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tv_guia_comercial:
                startActivity(new Intent(this, Guia_comercial_catergoria.class));
                break;

            case R.id.tv_video:
                startActivity(new Intent(this, Menu_video.class));
                break;
            case R.id.tv_qr_movil:
                Intent qr_vehiculo = new Intent(this,   QR_verificar_vehiculo.class);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_camara();
                }
                else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_almacenamiento();
                } else {  startActivity(qr_vehiculo);   }

                break;

        }

    }

    public void verificar_permiso_almacenamiento()
    {
        final String[] PERMISSIONS = { android.Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a ALMACENAMIENTO.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Otras_opciones.this,
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
            ActivityCompat.requestPermissions(Otras_opciones.this,
                    PERMISSIONS,
                    1);
        }
    }

    public void verificar_permiso_camara()
    {
        final String[] CAMERA_PERMISSIONS = { android.Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a CAMARA.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Otras_opciones.this,
                            CAMERA_PERMISSIONS,
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
            ActivityCompat.requestPermissions(Otras_opciones.this,
                    CAMERA_PERMISSIONS,
                    1);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 1000)
        {
            int per=0;
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 ) {
                for (int i=0;i<grantResults.length;i++){
                    if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        per++;
                    }
                }

                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {

                finish();
            }

            if(per<grantResults.length){
                finish();
            }else{
                //tiene todos los permisos...

            }
            return;

        }
    }

}
