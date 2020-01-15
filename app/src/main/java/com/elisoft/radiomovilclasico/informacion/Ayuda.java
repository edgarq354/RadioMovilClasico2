package com.elisoft.radiomovilclasico.informacion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.chat.Chat;

public class Ayuda extends AppCompatActivity implements View.OnClickListener {

    LinearLayout ll_bloqueo,ll_sin_solicitud,ll_que_hago_luego,ll_calificacion,ll_reporte;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ll_que_hago_luego=(LinearLayout)findViewById(R.id.ll_que_hago_luego);
        ll_sin_solicitud=(LinearLayout)findViewById(R.id.ll_sin_solicitud);
        ll_bloqueo=(LinearLayout)findViewById(R.id.ll_bloqueos);
        ll_calificacion=(LinearLayout)findViewById(R.id.ll_calificacion);
        ll_reporte=(LinearLayout)findViewById(R.id.ll_reporte);

        ll_que_hago_luego.setOnClickListener(this);
        ll_sin_solicitud.setOnClickListener(this);
        ll_bloqueo.setOnClickListener(this);
        ll_calificacion.setOnClickListener(this);
        ll_reporte.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ll_que_hago_luego:
                Intent intent=new Intent(this, Pagina.class);
                intent.putExtra("titulo","Mi registro");
                intent.putExtra("url",getString(R.string.servidor)+"web_que_hago_luego_de_registrarme_pasajero.php");
                startActivity(intent);
                break;
            case R.id.ll_sin_solicitud:
                Intent intent2=new Intent(this, Pagina.class);
                intent2.putExtra("titulo","Sin Solicitud");
                intent2.putExtra("url",getString(R.string.servidor)+"web_sin_solicitud_pasajero.php");
                startActivity(intent2);
                break;
            case R.id.ll_bloqueos:
                Intent intent3=new Intent(this, Pagina.class);
                intent3.putExtra("titulo","Bloqueos");
                intent3.putExtra("url",getString(R.string.servidor)+"web_bloqueo_pasajero.php");
                startActivity(intent3);
                break;
            case R.id.ll_calificacion:
                Intent intent4=new Intent(this, Pagina.class);
                intent4.putExtra("titulo","Calificación");
                intent4.putExtra("url",getString(R.string.servidor)+"web_calificacion_pasajero_conductor_pasajero.php");
                startActivity(intent4);
                break;
            case R.id.ll_reporte:
                Intent it_chat=new Intent(getApplicationContext(), Chat.class);
                it_chat.putExtra("id_conductor","0");
                it_chat.putExtra("titulo","Administración");
                startActivity(it_chat);
                break;
        }
    }
}
