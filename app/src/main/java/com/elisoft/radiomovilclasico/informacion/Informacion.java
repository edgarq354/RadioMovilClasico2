package com.elisoft.radiomovilclasico.informacion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.R;

public class Informacion extends AppCompatActivity implements View.OnClickListener{
    LinearLayout ll_sobre_nosotros,ll_contactanos,ll_facebook,ll_terminos,ll_web,ll_reglas_servicio,ll_ayuda;
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ll_sobre_nosotros=(LinearLayout)findViewById(R.id.ll_sobre_nosotros);
        ll_contactanos=(LinearLayout)findViewById(R.id.ll_contactanos);
        ll_facebook=(LinearLayout)findViewById(R.id.ll_facebook);
        ll_terminos=(LinearLayout)findViewById(R.id.ll_termino);
        ll_web=(LinearLayout)findViewById(R.id.ll_web);
        ll_reglas_servicio=(LinearLayout)findViewById(R.id.ll_reglas);
        ll_ayuda=(LinearLayout)findViewById(R.id.ll_ayuda);


        ll_sobre_nosotros.setOnClickListener(this);
        ll_contactanos.setOnClickListener(this);
        ll_facebook.setOnClickListener(this);
        ll_terminos.setOnClickListener(this);
        ll_web.setOnClickListener(this);
        ll_reglas_servicio.setOnClickListener(this);
        ll_ayuda.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ll_facebook)
        {
            Intent intent=new Intent(this, Pagina.class);
            intent.putExtra("titulo","Facebook");
            intent.putExtra("url","https://www.facebook.com/clasico_torito");
            startActivity(intent);
        }
        else if(v.getId()==R.id.ll_termino)
        {
            Intent intent=new Intent(this, Pagina.class);
            intent.putExtra("titulo","Terminos y condiciones");
            intent.putExtra("url",getString(R.string.servidor)+"terminos_condiciones_pasajero.php");
            startActivity(intent);
        }
        else if(v.getId()==R.id.ll_reglas)
        {
            Intent intent=new Intent(this, Pagina.class);
            intent.putExtra("titulo","Reglas del servicio");
            intent.putExtra("url",getString(R.string.servidor)+"web_regla_de_servicio_pasajero.php");
            startActivity(intent);
        }
        else if(v.getId()==R.id.ll_ayuda)
        {
            startActivity(new Intent(this,Ayuda.class));
        }
        else if(v.getId()==R.id.ll_sobre_nosotros)
        {
            Intent intent=new Intent(this, Pagina.class);
            intent.putExtra("titulo","Sobre nosotros");
            intent.putExtra("url",getString(R.string.servidor)+"web_sobre_nosotros_pasajero.php");
            startActivity(intent);
        }
        else if(v.getId()==R.id.ll_web)
        {
            Intent intent=new Intent(this, Pagina.class);
            intent.putExtra("titulo","Facebook");
            intent.putExtra("url","https://www.facebook.com/clasico_torito");
            startActivity(intent);
        }
        else if(v.getId()==R.id.ll_contactanos)
        {
            boolean isWhatsapp = appInstalledOrNot("com.whatsapp");
            if (isWhatsapp)
                AbrirWhatsApp();
        }
    }



    void AbrirWhatsApp() {

        Uri uri = Uri.parse("smsto: "+getString(R.string.whatsapp));
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        i.putExtra("sms_body", "Un movil por favor");
        startActivity(Intent.createChooser(i, "Radio Movil Clasico"));

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}
