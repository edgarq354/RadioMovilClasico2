package com.elisoft.radiomovilclasico;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Empresa_radio_taxi extends AppCompatActivity implements View.OnClickListener {

    ImageView im_logo;

    LinearLayout ll_whatsapp,ll_llamar;

    Suceso suceso;
    ProgressDialog pDialog;


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_radio_taxi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        im_logo=(ImageView)findViewById(R.id.im_logo);

        ll_whatsapp=(LinearLayout) findViewById(R.id.ll_whatsapp);
        ll_llamar=(LinearLayout) findViewById(R.id.ll_llamar);

        try {
            Bundle bundle= getIntent().getExtras();
            int id_empresa= Integer.parseInt(bundle.getString("id_empresa"));
            getImage(String.valueOf(id_empresa));
        }catch (Exception e) {
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ll_whatsapp.setOnClickListener(this);
        ll_llamar.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.ll_whatsapp){
                    boolean isWhatsapp = appInstalledOrNot("com.whatsapp");
                    if (isWhatsapp)
                        AbrirWhatsApp("+591 76633339");
         }else if(view.getId()==R.id.ll_llamar){
            Intent llamada = new Intent(Intent.ACTION_DIAL);
            llamada.setData(Uri.parse("tel: 33474444" ));
            if (ActivityCompat.checkSelfPermission(Empresa_radio_taxi.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                verificar_permiso_llamada();
            }else{
                startActivity(llamada);
            }
        }
    }




    private void getImage(String id)//
    {
        class GetImage extends AsyncTask<String,Void,Bitmap> {
            ImageView bmImage;


            public GetImage(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);


                Drawable dw = new BitmapDrawable(getResources(), bitmap);
                //se edita la imagen para ponerlo en circulo.

                if( bitmap==null)
                { dw = getResources().getDrawable(R.drawable.ic_empresa);}

                bmImage.setImageDrawable(dw);

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = getString(R.string.servidor)+"frmTaxi.php?opcion=get_logo_empresa&id_empresa="+strings[0];//hace consulta ala Bd para recurar la imagen
                Drawable d = getResources().getDrawable(R.drawable.ic_empresa);
                Bitmap mIcon = drawableToBitmap(d);
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
                return mIcon;
            }
        }

        GetImage gi = new GetImage(im_logo);
        gi.execute(id);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void mensaje_error(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show()  ;
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

    void AbrirWhatsApp(String number) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(i, ""));
    }

    public void verificar_permiso_llamada()
    {
        final String[] PERMISSIONS = {
                android.Manifest.permission.CALL_PHONE};

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a LLAMADA.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Empresa_radio_taxi.this,
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
            ActivityCompat.requestPermissions(Empresa_radio_taxi.this,
                    PERMISSIONS,
                    1);
        }
    }

}
