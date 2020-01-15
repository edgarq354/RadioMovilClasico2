package com.elisoft.radiomovilclasico.registro_inicio_sesion;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.Constants;
import com.elisoft.radiomovilclasico.Menu_usuario;
import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.Servicio_guardar_contacto_empresa;
import com.elisoft.radiomovilclasico.informacion.Pagina;

import java.util.ArrayList;

public class Opcion_inicio_sesion extends AppCompatActivity {

    private static final String[] SMS_PERMISSIONS = { Manifest.permission.INTERNET,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_NETWORK_STATE };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_opcion_inicio_sesion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(verificar_login_usuario())
        {
            startActivity(new Intent(this, Menu_usuario.class));
            finish();
        }


        try {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } catch (Exception e) {
        }


        verificar_todos_los_permisos();

    }

    public void terminos_condiciones(View v)
    {
        Intent intent=new Intent(this, Pagina.class);
        intent.putExtra("titulo","Terminos y condiciones");
        intent.putExtra("url",getString(R.string.servidor)+"terminos_condiciones.php");
        startActivity(intent);
    }






    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        int per=0;
        switch (requestCode) {
            case 1: {
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
                    Intent intent = new Intent(this, Servicio_guardar_contacto_empresa.class);
                    intent.setAction(Constants.ACTION_RUN_ISERVICE);
                    startService(intent);

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean verificar_login_usuario()
    {
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        return (perfil.getString("login_usuario","").equals("1"));

    }

    public void crear_cuenta(View v)
    {
        Intent registrar = new Intent(this, Autenticar_celular.class);


            startActivity(registrar);

    }
    public  void  ya_tengo(View v)
    {
        startActivity(new Intent( this,Iniciar_sesion.class));
    }


    public void verificar_permiso_sms()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de SMS para realizar la autenficación");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions( Opcion_inicio_sesion.this,
                            SMS_PERMISSIONS,
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
            ActivityCompat.requestPermissions( this,
                    SMS_PERMISSIONS,
                    1);
        }
    }

    public void verificar_todos_los_permisos()
    {
        final String[] SMS_PERMISSIONS1 = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION
        };


        ActivityCompat.requestPermissions( this,
                SMS_PERMISSIONS1,
                1);


    }


}
