package com.elisoft.radiomovilclasico.corporativo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.R;

public class Empresa extends AppCompatActivity {
String id_empresa,nit,razon_social,direccion,monto_deuda;

EditText et_razon_social,et_nit,et_direccion;
TextView tv_monto_deuda;
Button ib_usuarios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);
        et_razon_social=(EditText)findViewById(R.id.et_razon_social);
        et_direccion=(EditText)findViewById(R.id.et_direccion);
        et_nit=(EditText)findViewById(R.id.et_nit);
        tv_monto_deuda=(TextView)findViewById(R.id.tv_monto_deuda);
        ib_usuarios=(Button)findViewById(R.id.ib_usuarios);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try{
            Bundle bundle=getIntent().getExtras();
            id_empresa=bundle.getString("id_empresa");
            nit=bundle.getString("nit");
            razon_social=bundle.getString("razon_social");
            direccion=bundle.getString("direccion");
            monto_deuda=bundle.getString("monto_deuda");

            tv_monto_deuda.setText(monto_deuda);
            et_razon_social.setText(razon_social);
            et_direccion.setText(direccion);
            et_nit.setText(nit);


            ib_usuarios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent usuarios=new Intent(getApplicationContext(),Usuarios_empresa.class);
                    usuarios.putExtra("id_empresa",id_empresa);
                    startActivity(usuarios);
                }
            });

        }catch (Exception e){
            finish();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}
