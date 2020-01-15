package com.elisoft.radiomovilclasico;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Datos_vehiculo extends AppCompatActivity {

    String id_vehiculo;
    ImageView im_uno,im_dos,im_tres,im_cuatro;
    TextView tv_placa;
    String url1="",url2="",url3="",url4="";
    Suceso suceso;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_vehiculo);
        im_uno=(ImageView)findViewById(R.id.im_uno);
        im_dos=(ImageView)findViewById(R.id.im_dos);
        im_tres=(ImageView)findViewById(R.id.im_tres);
        im_cuatro=(ImageView)findViewById(R.id.im_cuatro);
        tv_placa=(TextView)findViewById(R.id.tv_placa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try{
            Bundle bundle=getIntent().getExtras();
            id_vehiculo= bundle.getString("placa","");
            tv_placa.setText(String.valueOf("Placa:"+id_vehiculo));
            Servicio_taxi hilo = new Servicio_taxi();
            hilo.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=get_vehiculo_por_placa", "1",id_vehiculo);// parametro que recibe el doinbackground

        }catch (Exception e){
            finish();
        }
    }

    private void getImage(String id,ImageView im)//
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
                { dw = getResources().getDrawable(R.mipmap.ic_perfil);}
                else
                {
                    bmImage.setImageDrawable(dw);
                }


            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = strings[0];//hace consulta ala Bd para recurar la imagen
                Bitmap mIcon = null;
                try {
                    InputStream in = new URL(url).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
                return mIcon;
            }
        }

        GetImage gi = new GetImage(im);
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


    public class Servicio_taxi extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
//Iniciar sesion
            if (params[1] == "1") {
                try {
                    HttpURLConnection urlConn;

                    DataOutputStream printout;
                    DataOutputStream input;

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
                    jsonParam.put("placa", params[2]);

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

                        //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        // StringBuilder pasando a cadena.                    }

                        SystemClock.sleep(950);

                        //Accedemos a vector de resultados.
                        String error = respuestaJSON.getString("suceso");// suceso es el campo en el Json
                        String mensaje = respuestaJSON.getString("mensaje");
                        suceso = new Suceso(error, mensaje);

                        if (error.equals("1")) {
                            url1=respuestaJSON.getString("direccion_imagen_adelante");
                            url2=respuestaJSON.getString("direccion_imagen_atras");
                            url3=respuestaJSON.getString("direccion_imagen_interior_adelante");
                            url4=respuestaJSON.getString("direccion_imagen_interior_atras");

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
            pDialog = new ProgressDialog(Datos_vehiculo.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Descargando datos..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.cancel();//ocultamos proggress dialog
            //  Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                cargar_imagenes();
            } else if (s.equals("2")) {
                mensaje_error(suceso.getMensaje());

            } else {
                mensaje_error_final("Error: Al conectar con el Servidor.");
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    private void cargar_imagenes() {
        if(url1.length()>5){
            url1=url1.replaceFirst("../","");
            getImage(getString(R.string.servidor_web)+url1,im_uno);
        }
        if(url2.length()>5){
            url2=url2.replaceFirst("../","");
            getImage(getString(R.string.servidor_web)+url2,im_dos);
        }
        if(url3.length()>5){
            url3=url3.replaceFirst("../","");
            getImage(getString(R.string.servidor_web)+url3,im_tres);
        }
        if(url4.length()>5){
            url4=url4.replaceFirst("../","");
            getImage(getString(R.string.servidor_web)+url4,im_cuatro);
        }

    }

    public void mensaje_error(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }

    public void mensaje_error_final(String mensaje)
    {   try {
            AlertDialog.Builder builder = new AlertDialog.Builder(Datos_vehiculo.this);
            builder.setTitle(getString(R.string.app_name));
            builder.setCancelable(false);
            builder.setMessage(mensaje);
            builder.create();
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    finish();
                }
            });
            builder.show();
    }catch (Exception e)
    {   Log.e("mensaje_error",e.toString());
    }

    }

}
