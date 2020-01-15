package com.elisoft.radiomovilclasico.perfil;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.informacion.Pagina;

import java.io.File;

public class Compartir_amigo extends AppCompatActivity implements  View.OnClickListener{
TextView tv_como_funciona,tv_codigo;
Button bt_invitar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartir_amigo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_como_funciona=(TextView)findViewById(R.id.tv_como_funciona);
        tv_codigo=(TextView)findViewById(R.id.tv_codigo);
        bt_invitar=(Button)findViewById(R.id.bt_invitar);

        tv_como_funciona.setOnClickListener(this);
        bt_invitar.setOnClickListener(this);
        tv_codigo.setOnClickListener(this);


        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int codigo= Integer.parseInt("1999"+perfil.getString("id_usuario",""));
        String s_c= Integer.toString(codigo, 16);
        tv_codigo.setText(s_c);





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
            case R.id.tv_codigo:
                ClipData clip = ClipData.newPlainText("text", tv_codigo.getText().toString());
                ClipboardManager clipboard = (ClipboardManager)Compartir_amigo.this.getSystemService(CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(),"Codigo Copiado",Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_invitar:

                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("text/plain");
                intent1.putExtra(Intent.EXTRA_TEXT, "He usado "+getString(R.string.app_name)+" y es una aplicacion genial para pedir Taxi. Te recomiendo usarlo. Usa el codigo "+ tv_codigo.getText()+" para agregarte un bono en tu Billetera. https://play.google.com/store/apps/details?id=com.elisoft.radiomovilclasico&hl=es");
                startActivity(Intent.createChooser(intent1, "Recomendar aplicación."));
                break;
            case R.id.tv_como_funciona:
                Intent intent=new Intent(this, Pagina.class);
                intent.putExtra("titulo","¿Cómo funciona?");
                intent.putExtra("url",getString(R.string.servidor)+"como_funciona.php");
                startActivity(intent);
                break;
        }
    }
}
