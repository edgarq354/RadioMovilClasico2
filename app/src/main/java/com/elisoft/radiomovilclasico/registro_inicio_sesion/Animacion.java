package com.elisoft.radiomovilclasico.registro_inicio_sesion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.elisoft.radiomovilclasico.Constants;
import com.elisoft.radiomovilclasico.Menu_usuario;
import com.elisoft.radiomovilclasico.R;

public class Animacion extends AppCompatActivity implements View.OnClickListener{
    ProgressBar cargando;
    Handler handle=new Handler();
    int i;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        if(verificar_version()==false) {

            if (verificar_login_usuario()) {
                startActivity(new Intent(this, Menu_usuario.class));
                finish();
            }else{

                getSupportActionBar().hide();
                progress_en_proceso();

            }

        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_animacion);

        cargando=(ProgressBar)findViewById(R.id.cargando);




        try{
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }catch (Exception e)
        {}



    }

    public  void progress_en_proceso()
    {

        i=0;
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (i<2)
                {
                    i=i+1;

                    handle.post(new Runnable() {
                        @Override
                        public void run() {
                            cargando.setProgress(i);



                        }
                    });
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                startActivity(new Intent(getApplicationContext(),Opcion_inicio_sesion.class));
            }
        }).start();


    }



    public boolean verificar_login_usuario()
    {
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        return (perfil.getString("login_usuario","").equals("1"));

    }
    public boolean verificar_login_taxi()
    {
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        return (perfil.getString("login_taxi","").equals("1"));

    }

    @Override
    public void onClick(View v) {

    }

    public boolean verificar_version()
    {boolean sw=false;
        // notificacion para verificar la actualizacion nueva
        SharedPreferences act = getSharedPreferences("actualizacion_elitex", Context.MODE_PRIVATE);
        int nueva=act.getInt("version",0);
        if(nueva>28)
        {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Actualización");
            dialogo1.setMessage("Hay una nueva version.Por favor actualice la aplicación desde Play Store.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    finish();
                }
            });

            dialogo1.show();
            sw=true;
        }
        return sw;
    }
}
