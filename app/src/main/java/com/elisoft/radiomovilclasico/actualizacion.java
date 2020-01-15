package com.elisoft.radiomovilclasico;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class actualizacion extends AppCompatActivity implements View.OnClickListener {
Button actualizar;
    TextView mensaje;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizacion);
        actualizar=(Button)findViewById(R.id.actualizar);
        mensaje=(TextView)findViewById(R.id.mensaje);

        SharedPreferences actual=getSharedPreferences("actualizacion",MODE_PRIVATE);
        mensaje.setText(actual.getString("mensaje",""));

        actualizar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
