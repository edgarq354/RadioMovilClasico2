package com.elisoft.radiomovilclasico;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Notificacion_mensaje extends AppCompatActivity implements  View.OnClickListener{
    TextView tv_mensaje;
    Button bt_aceptar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_mensaje);

        tv_mensaje=(TextView)findViewById(R.id.tv_mensaje);
        bt_aceptar=(Button)findViewById(R.id.bt_aceptar);

        try{
            Bundle bundle=getIntent().getExtras();
            String mensaje="";
            mensaje=bundle.getString("mensaje","");
            tv_mensaje.setText(mensaje);

        }catch (Exception e)
        {
            finish();
        }

        bt_aceptar.setOnClickListener(this);
        getSupportActionBar().hide();

    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bt_aceptar)
        {
            finish();
        }
    }

}
