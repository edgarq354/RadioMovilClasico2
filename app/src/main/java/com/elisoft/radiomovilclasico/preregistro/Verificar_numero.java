package com.elisoft.radiomovilclasico.preregistro;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.Menu_usuario;
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

public class Verificar_numero extends AppCompatActivity implements View.OnClickListener {

    Button bt_verificar;
    EditText et_cedula;
    String ci="";
    String imei="";
    String nombre="";
    String paterno="";
    String materno="";
    String genero="";
    String correo="";
    String celular="";
    String direccion="";
    String categoria="";
    String expedido="";
    String estado="";
    String direccion_imagen="";
    String id_vehiculo="";
    Suceso suceso=new Suceso();




    String direccion_imagen_carnet_1="";
    String direccion_imagen_carnet_2="";
    String direccion_imagen_licencia_1="";
    String direccion_imagen_licencia_2="";


    ProgressDialog pDialog;
    AlertDialog alert2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_numero);
        et_cedula=(EditText)findViewById(R.id.et_cedula);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);

        bt_verificar=(Button)findViewById(R.id.bt_verificar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        bt_verificar.setOnClickListener(this);

        aceptar_condiciones();

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
            case R.id.bt_verificar:
                ci=et_cedula.getText().toString().trim();
                if(ci.length()>4 && ci.length()<10){
                    verificar_ci_conductor(ci);
                }else{
                    mensaje("Ingrese datos mayor a 4 numero y menor que 10.   ej.(8213529)");
                }
                break;
        }
    }

    public void verificar_ci_conductor(String s_ci)
    {
        Servicio servicio = new Servicio();
        servicio.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=verificar_ci_conductor", "1", ci,imei);// parametro que recibe el doinbackground

    }

    public void saltar_cargar_datos_conductor()
    {
        Intent siguiente=new Intent(this, Datos_conductor.class);
        siguiente.putExtra("ci",ci);
        siguiente.putExtra("nombre",nombre);
        siguiente.putExtra("paterno",paterno);
        siguiente.putExtra("materno",materno);
        siguiente.putExtra("expedido",expedido);
        siguiente.putExtra("correo",correo);
        siguiente.putExtra("celular",celular);
        siguiente.putExtra("estado",estado);
        siguiente.putExtra("direccion_imagen",direccion_imagen);
        siguiente.putExtra("genero",genero);
        siguiente.putExtra("categoria",categoria);
        siguiente.putExtra("direccion",direccion);
        siguiente.putExtra("id_vehiculo",id_vehiculo);


        siguiente.putExtra("direccion_imagen_carnet_1", direccion_imagen_carnet_1);
        siguiente.putExtra("direccion_imagen_carnet_2",direccion_imagen_carnet_2);
        siguiente.putExtra("direccion_imagen_licencia_1", direccion_imagen_licencia_1);
        siguiente.putExtra("direccion_imagen_licencia_2", direccion_imagen_licencia_2);

        startActivity(siguiente);
    }

    public class Servicio extends AsyncTask<String,Integer,String> {


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

                        //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        // StringBuilder pasando a cadena.                    }

                        SystemClock.sleep(950);

                        //Accedemos a vector de resultados.
                        String error = respuestaJSON.getString("suceso");// suceso es el campo en el Json
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                        if (error.equals("1")) {
                            JSONArray dato=respuestaJSON.getJSONArray("perfil_conductor");
                            nombre= dato.getJSONObject(0).getString("nombre");
                            paterno=dato.getJSONObject(0).getString("paterno") ;
                            materno=dato.getJSONObject(0).getString("materno") ;
                            correo= dato.getJSONObject(0).getString("correo") ;
                            celular= dato.getJSONObject(0).getString("celular") ;
                            ci= dato.getJSONObject(0).getString("ci") ;
                            direccion= dato.getJSONObject(0).getString("direccion") ;
                            categoria= dato.getJSONObject(0).getString("categoria_licencia") ;
                            genero= dato.getJSONObject(0).getString("sexo") ;
                            expedido= dato.getJSONObject(0).getString("expedido") ;
                            direccion_imagen=dato.getJSONObject(0).getString("direccion_imagen");
                            id_vehiculo=dato.getJSONObject(0).getString("id_vehiculo");


                            direccion_imagen_carnet_1=respuestaJSON.getString("direccion_imagen_carnet_1");
                            direccion_imagen_carnet_2=respuestaJSON.getString("direccion_imagen_carnet_2");
                            direccion_imagen_licencia_1=respuestaJSON.getString("direccion_imagen_licencia_1");
                            direccion_imagen_licencia_2=respuestaJSON.getString("direccion_imagen_licencia_2");


                            devuelve="1";
                        } else if(suceso.getSuceso().equals("3")) {
                            devuelve = "3";
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
            pDialog = new ProgressDialog(Verificar_numero.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Verificando la Cedula de Identidad en nuestra base de datos.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();//ocultamos proggress dialog

            estado=suceso.getSuceso();
            if (s.equals("1")) {
                saltar_cargar_datos_conductor();
            } else if(s.equals("2")) {
                mensaje(suceso.getMensaje());
            }else if(s.equals("3"))
            {
                imei="";
                nombre="";
                paterno="";
                materno="";
                genero="";
                correo="";
                celular="";
                direccion="";
                categoria="";
                expedido="";
                direccion_imagen="";
                id_vehiculo="";

                saltar_cargar_datos_conductor();
            }
            else
            {
                mensaje("Falla en tu conexión a Internet.");
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

    public void mensaje(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create();
        builder.show();
    }


    public void  aceptar_condiciones()
    {


        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.aceptar_terminos, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);


        final Button bt_aceptar_continuar= (Button) promptView.findViewById(R.id.bt_aceptar_continuar);
        final CheckBox cb_terminos_condiciones= (CheckBox) promptView.findViewById(R.id.cb_terminos_condiciones);

        bt_aceptar_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_terminos_condiciones.isChecked())
                {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    alert2.cancel();
                }else
                {
                    Toast.makeText(Verificar_numero.this,"Aun no a aceptado los terminos y condiciones",Toast.LENGTH_SHORT).show();
                }

            }
        });

        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();
    }





}
