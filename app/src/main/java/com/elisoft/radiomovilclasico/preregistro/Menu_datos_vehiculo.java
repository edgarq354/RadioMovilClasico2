package com.elisoft.radiomovilclasico.preregistro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.Suceso;

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

public class Menu_datos_vehiculo extends AppCompatActivity implements View.OnClickListener{


    String v_direccion_imagen_1="";

    String direccion_imagen_ruat="";
    String direccion_imagen_soat="";
    String v_direccion_imagen_inspeccion_tecnica="";
    String placa="";


    Button bt_vehiculo,bt_soat,bt_ruat,bt_inspeccion_tecnica;
    ImageView im_vehiculo,im_soat,im_ruat,im_inspeccion_tecnica;

    Suceso suceso;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_datos_vehiculo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bt_vehiculo=(Button)findViewById(R.id.bt_vehiculo);
        bt_soat=(Button)findViewById(R.id.bt_soat);
        bt_ruat=(Button)findViewById(R.id.bt_ruat);
        bt_inspeccion_tecnica=(Button)findViewById(R.id.bt_inspeccion_tecnica);


        im_vehiculo=(ImageView)findViewById(R.id.im_vehiculo);
        im_soat=(ImageView)findViewById(R.id.im_soat);
        im_ruat=(ImageView)findViewById(R.id.im_ruat);
        im_inspeccion_tecnica=(ImageView)findViewById(R.id.im_inspeccion_tecnica);

        bt_vehiculo.setOnClickListener(this);
        bt_soat.setOnClickListener(this);
        bt_ruat.setOnClickListener(this);
        bt_inspeccion_tecnica.setOnClickListener(this);


        try{
            Bundle bundle=getIntent().getExtras();
            placa=bundle.getString("placa");
            v_direccion_imagen_1=bundle.getString("direccion_imagen_1");
            direccion_imagen_ruat=bundle.getString("direccion_imagen_ruat");
            direccion_imagen_soat=bundle.getString("direccion_imagen_soat");
            v_direccion_imagen_inspeccion_tecnica=bundle.getString("direccion_imagen_inspeccion_tecnica");



            verificar_todas_las_fotos();

        }catch (Exception e)
        {
            finish();
        }

    }

    @Override
    protected void onRestart() {
        verificar_datos();
        super.onRestart();
    }

    public  void verificar_datos()
    {
        Servicio servicio = new Servicio();
        servicio.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=get_direccion_vehiculo", "1");// parametro que recibe el doinbackground

    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void verificar_todas_las_fotos()
    {
        if(v_direccion_imagen_1.length()>5) {
            im_vehiculo.setBackgroundResource(R.drawable.ic_ok);
        }
        if(direccion_imagen_soat.length()>5)
        {
            im_soat.setBackgroundResource(R.drawable.ic_ok);
        }
        if(direccion_imagen_ruat.length()>5)
        {
            im_ruat.setBackgroundResource(R.drawable.ic_ok);
        }
        if(v_direccion_imagen_inspeccion_tecnica.length()>5) {
            im_inspeccion_tecnica.setBackgroundResource(R.drawable.ic_ok);
        }




    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_vehiculo:
                Intent iv=new Intent(this,Foto_vehiculo.class);
                iv.putExtra("placa", placa);
                iv.putExtra("direccion_imagen_1", v_direccion_imagen_1);
                startActivity(iv);
                break;
            case R.id.bt_soat:
                Intent is=new Intent(this,Foto_soat.class);
                is.putExtra("placa", placa);
                is.putExtra("direccion_imagen_soat", direccion_imagen_soat);
                startActivity(is);
                break;
            case R.id.bt_ruat:
                Intent ir=new Intent(this,Foto_rua.class);
                ir.putExtra("placa", placa);
                ir.putExtra("direccion_imagen_ruat", direccion_imagen_ruat);
                startActivity(ir);
                break;
            case R.id.bt_inspeccion_tecnica:
                Intent iin=new Intent(this,Foto_inspeccion_tecnica.class);
                iin.putExtra("placa", placa);
                iin.putExtra("direccion_imagen_inspeccion_tecnica", v_direccion_imagen_inspeccion_tecnica);
                startActivity(iin);
                break;


        }
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
            //para el progres Dialog
            pDialog = new ProgressDialog(Menu_datos_vehiculo.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Verificando . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();//ocultamos proggress dialog


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
}
