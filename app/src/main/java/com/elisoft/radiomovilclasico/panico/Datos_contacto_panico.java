package com.elisoft.radiomovilclasico.panico;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.R;

public class Datos_contacto_panico extends AppCompatActivity {

    TextView tv_nombre_conductor,tv_nombre_usuario,tv_celular_c,tv_celular_u,tv_placa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_contacto_panico);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

}
