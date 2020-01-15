package com.elisoft.radiomovilclasico.preregistro;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.Inicio;
import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.Suceso;
import com.elisoft.radiomovilclasico.registro_inicio_sesion.Animacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Menu_fotos_preregistro extends AppCompatActivity implements View.OnClickListener{

    Button bt_conductor,bt_vehiculo,bt_finalizar;


    String v_direccion_imagen_1="";

    String direccion_imagen_ruat="";
    String direccion_imagen_soat="";
    String v_direccion_imagen_inspeccion_tecnica="";
    String ci="",placa="";


    String direccion_imagen="";
    String direccion_imagen_carnet_1="";
    String direccion_imagen_carnet_2="";
    String direccion_imagen_licencia_1="";
    String direccion_imagen_licencia_2="";

    int cant=0;
    int total=7;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_fotos_preregistro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bt_conductor=(Button)findViewById(R.id.bt_conductor);
        bt_vehiculo=(Button)findViewById(R.id.bt_vehiculo);
        bt_finalizar=(Button)findViewById(R.id.bt_finalizar);

        bt_conductor.setOnClickListener(this);
        bt_vehiculo.setOnClickListener(this);
        bt_finalizar.setOnClickListener(this);

        try{
            Bundle bundle=getIntent().getExtras();
            ci=bundle.getString("ci");
            placa=bundle.getString("placa");
            v_direccion_imagen_1=bundle.getString("direccion_imagen_1");
            direccion_imagen_ruat=bundle.getString("direccion_imagen_ruat");
            direccion_imagen_soat=bundle.getString("direccion_imagen_soat");
            v_direccion_imagen_inspeccion_tecnica=bundle.getString("direccion_imagen_inspeccion_tecnica");



            direccion_imagen=bundle.getString("direccion_imagen");
            direccion_imagen_carnet_1=bundle.getString("direccion_imagen_carnet_1");
            direccion_imagen_licencia_1=bundle.getString("direccion_imagen_licencia_1");

        }catch (Exception e)
        {
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_conductor:
                if(validaPermisos()){

                    Intent icon=new Intent(this,Menu_datos_conductor.class);
                    icon.putExtra("ci", ci);
                    icon.putExtra("direccion_imagen", direccion_imagen);
                    icon.putExtra("direccion_imagen_carnet_1", direccion_imagen_carnet_1);
                    icon.putExtra("direccion_imagen_licencia_1", direccion_imagen_licencia_1);
                    startActivity(icon);
                }else{
                    cargarDialogoRecomendacion();
                }
                break;
            case R.id.bt_vehiculo:

                if(validaPermisos()){
                Intent ive=new Intent(this,Menu_datos_vehiculo.class);
                ive.putExtra("placa", placa);
                ive.putExtra("direccion_imagen_1", v_direccion_imagen_1);
                ive.putExtra("direccion_imagen_soat", direccion_imagen_soat);
                ive.putExtra("direccion_imagen_ruat", direccion_imagen_ruat);
                ive.putExtra("direccion_imagen_inspeccion_tecnica", v_direccion_imagen_inspeccion_tecnica);
                startActivity(ive);
                }else{
                    cargarDialogoRecomendacion();
                }
                break;
            case R.id.bt_finalizar:
                mensaje_final("Gracias por Completar tu registro. Ahora puedes descargar la aplicacion del conductor y ingresar.");
                /*
                verificar_todas_las_fotos();
                if(cant==total)
                {
                    mensaje_final("Gracias por Completar tu registro. Ahora puedes descargar la aplicacion del conductor y ingresar.");
                }else{
                    mensaje("Aun te falta completar las imagenes.");
                }
                */
                break;
        }
    }

    @Override
    protected void onRestart() {
        verificar_datos();
        super.onRestart();
    }

    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public  void verificar_datos()
    {
        Servicio_conductor servicio = new Servicio_conductor();
        servicio.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=get_direccion_conductor", "1");

        Servicio servicio2 = new Servicio();
        servicio2.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=get_direccion_vehiculo", "1");// parametro que recibe el doinbackground


    }

    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
//guardar datos del conductor
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
                    jsonParam.put("placa", placa);

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


                        if (error.equals("1")) {
                            JSONArray dato=respuestaJSON.getJSONArray("perfil_vehiculo");
                            v_direccion_imagen_1= dato.getJSONObject(0).getString("direccion_imagen_adelante") ;

                            devuelve="1";

                            direccion_imagen_ruat=respuestaJSON.getString("direccion_imagen_ruat");
                            direccion_imagen_soat=respuestaJSON.getString("direccion_imagen_soat");
                            v_direccion_imagen_inspeccion_tecnica=respuestaJSON.getString("direccion_imagen_inspeccion_tecnica");


                        }else
                        {
                            devuelve="2";
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

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);



            if (s.equals("1")) {
                verificar_todas_las_fotos();
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


    public class Servicio_conductor extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
//verificar si los datos en la base de datos con su cedula de identidad.
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
                    jsonParam.put("ci",ci);

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


                        if (error.equals("1")) {
                            JSONArray dato=respuestaJSON.getJSONArray("perfil_conductor");

                            direccion_imagen=dato.getJSONObject(0).getString("direccion_imagen");
                            direccion_imagen_carnet_1=respuestaJSON.getString("direccion_imagen_carnet_1");
                            direccion_imagen_licencia_1=respuestaJSON.getString("direccion_imagen_licencia_1");


                            devuelve="1";
                        }  else
                        {
                            devuelve="2";
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

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("1")) {
                verificar_todas_las_fotos();
            }
            else
            {

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


    public void verificar_todas_las_fotos()
    {
        cant=0;
        if(direccion_imagen.length()>5)
        {
            cant++;
        }

       if(direccion_imagen_carnet_1.length()>5)
       {
           cant++;
        }

    if(direccion_imagen_licencia_1.length()>5)
    {
        cant++;
    }

        if(v_direccion_imagen_1.length()>5) {
            cant++;
        }
        if(direccion_imagen_soat.length()>5)
        {
            cant++;
        }
        if(direccion_imagen_ruat.length()>5)
        {
            cant++;
        }
        if(v_direccion_imagen_inspeccion_tecnica.length()>5) {
            cant++;
        }

    }

    public void mensaje(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }
    public void mensaje_final(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.elisoft.radiomovilclasico_conductor&hl=es");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        builder.create();
        builder.show();
    }

    public void verificar_permiso_almacenamiento()
    {
        final String[] PERMISSIONS = { android.Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a ALMACENAMIENTO.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Menu_fotos_preregistro.this,
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
            ActivityCompat.requestPermissions(Menu_fotos_preregistro.this,
                    PERMISSIONS,
                    1);
        }
    }
    public void verificar_permiso_camara()
    {
        final String[] PERMISSIONS = { android.Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a CAMARA.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Menu_fotos_preregistro.this,
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
            ActivityCompat.requestPermissions(Menu_fotos_preregistro.this,
                    PERMISSIONS,
                    1);
        }
    }


}
