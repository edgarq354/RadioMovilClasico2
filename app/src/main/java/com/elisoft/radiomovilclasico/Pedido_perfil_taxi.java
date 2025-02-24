package com.elisoft.radiomovilclasico;


import android.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import android.widget.Toast;

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

public class Pedido_perfil_taxi extends AppCompatActivity implements View.OnClickListener {

    EditText et_nombre, et_apellido, et_celular, et_placa;
    ImageView perfil;
    Suceso suceso;
    ProgressDialog pDialog;
    LinearLayout ll_vehiculoo;


    String id_conductor = "", id_empresa = "", id_vehiculo = "";

    LinearLayout pb_cargando;
    int cantida_servicio=0;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_perfil_taxi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pb_cargando=(LinearLayout)findViewById(R.id.pb_cargando);
        ll_vehiculoo=(LinearLayout)findViewById(R.id.ll_vehiculo);


        try {
            Bundle bundle = getIntent().getExtras();
            id_conductor = bundle.getString("id_conductor");
            id_vehiculo = bundle.getString("id_vehiculo");
            Servicio_conductor hilo_taxi = new Servicio_conductor();

            hilo_taxi.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=get_conductor_panico_por_ci", "1", String.valueOf(id_conductor));// parametro que recibe el doinbackground
        } catch (Exception e) {
        }


        et_nombre = (EditText) findViewById(R.id.nombre);
        et_apellido = (EditText) findViewById(R.id.apellido);
        et_celular = (EditText) findViewById(R.id.celular);
        et_placa = (EditText) findViewById(R.id.placa);
        perfil = (ImageView) findViewById(R.id.perfil);
        ll_vehiculoo.setOnClickListener(this);

        et_placa.setText(id_vehiculo);

        getImage(String.valueOf(id_conductor));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ll_vehiculo) {

                Intent empresa=new Intent(this,Datos_vehiculo.class);

                empresa.putExtra("placa",id_vehiculo);
                startActivity(empresa);
        }
    }


    public class Servicio_conductor extends AsyncTask<String, Integer, String> {

        String nombre, paterno, materno, celular, empresa_id, placa;

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";


            //obtener datos de la empresa..
            if (params[1] == "1") {
                try {
                    cantida_servicio +=1;
                    HttpURLConnection urlConn;

                    url = new URL(cadena);
                    urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.setRequestProperty("Accept", "application/json");
                    urlConn.connect();

                    //se crea el objeto JSON
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("ci", params[2]);
                    //Envio los prametro por metodo post
                    OutputStream os = urlConn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();

                    int respuesta = urlConn.getResponseCode();

                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {

                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }

                        SystemClock.sleep(950);

                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
                            nombre = respuestaJSON.getString("nombre");
                            paterno = respuestaJSON.getString("paterno");
                            materno = respuestaJSON.getString("materno");
                            celular = respuestaJSON.getString("celular");
                            id_conductor = respuestaJSON.getString("ci");
                            devuelve = "1";
                        } else {
                            devuelve = "2";
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return devuelve;
        }

        @Override
        protected void onPreExecute() {
            //para el progres Dialog
            try {
                /*
                pDialog = new ProgressDialog(Pedido_perfil_taxi.this);
                pDialog.setTitle("Taxi Corp");
                pDialog.setMessage("Solicitando datos del conductor.");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(true);
                pDialog.show();
?               */

            } catch (Exception e) {
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);
            try {
                cantida_servicio-=1;
              //  pDialog.dismiss();//ocultamos proggress dialog
            } catch (Exception e) {

            }

            if (s.equals("1") == true) {
                et_nombre.setText(nombre);
                et_apellido.setText(paterno + " " + materno);
                et_celular.setText(celular);



            } else if (s.equals("2") == true) {//cancelado correctamente..
                mensaje_error(suceso.getMensaje());
                finish();
            } else {
                mensaje_error("Error con su conexió de Internet");
            }
            if(cantida_servicio==0){
                pb_cargando.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

    }




    private void getImage(String id)//
    {
        class GetImage extends AsyncTask<String, Void, Bitmap> {
            ImageView bmImage;


            public GetImage(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);


                Drawable dw = new BitmapDrawable(getResources(), bitmap);
                //se edita la imagen para ponerlo en circulo.

                if (bitmap == null) {
                    dw = getResources().getDrawable(R.mipmap.ic_perfil);
                }
                imagen_circulo(dw,bmImage);

//                bmImage.setImageDrawable(dw);

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = getString(R.string.servidor) + "frmTaxi.php?opcion=get_imagen&id_conductor=" + strings[0];//hace consulta ala Bd para recurar la imagen
                Drawable d = getResources().getDrawable(R.mipmap.ic_perfil);
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

        GetImage gi = new GetImage(perfil);
        gi.execute(id);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void mensaje_error(String mensaje) {

        Toast.makeText(this,mensaje, Toast.LENGTH_SHORT).show();
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




    public void imagen_circulo(Drawable id_imagen, ImageView imagen) {
        Bitmap originalBitmap = ((BitmapDrawable) id_imagen).getBitmap();
        if (originalBitmap.getWidth() > originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        } else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }

//creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

//asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getWidth());
        try {
            imagen.setImageDrawable(roundedDrawable);
        }catch (Exception e)
        {

        }
    }
}