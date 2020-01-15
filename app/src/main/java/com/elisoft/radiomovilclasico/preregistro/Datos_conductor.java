package com.elisoft.radiomovilclasico.preregistro;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

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

public class Datos_conductor extends AppCompatActivity  implements View.OnClickListener{

    Button bt_siguiente;
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
    String ci="";
    String direccion_imagen="";
    String id_vehiculo="";



    String direccion_imagen_ruat="";
    String direccion_imagen_soat="";
    String v_direccion_imagen_inspeccion_tecnica="";


    String direccion_imagen_carnet_1="";
    String direccion_imagen_carnet_2="";
    String direccion_imagen_licencia_1="";
    String direccion_imagen_licencia_2="";



    String v_placa="";
    String v_marca="";
    String v_tipo="";
    String v_clase="";
    String v_modelo="";
    String v_color= "";
    String v_direccion_imagen_1="";
    String v_direccion_imagen_2= "";
    String v_direccion_imagen_3= "";
    String v_direccion_imagen_4= "";
    String v_ci_pro="";
    String v_nombre_pro= "";
    String v_paterno_pro= "";
    String v_materno_pro= "";
    String v_expedido_pro= "";
    String v_moto_pro= "";
    String v_movil_pro= "";
    String v_estado= "3";

    Suceso suceso;
    ProgressDialog pDialog;

    EditText et_placa,et_nombre,et_paterno,et_materno,et_correo,et_celular,et_direccion,et_ci;
    RadioButton rb_hombre,rb_mujer;
    Spinner sp_categoria,sp_expedido;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_conductor);
        bt_siguiente=(Button)findViewById(R.id.bt_siguiente);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        et_ci=(EditText)findViewById(R.id.et_ci);
        et_nombre=(EditText)findViewById(R.id.et_nombre);
        et_paterno=(EditText)findViewById(R.id.et_paterno);
        et_materno=(EditText)findViewById(R.id.et_materno);
        et_correo=(EditText)findViewById(R.id.et_correo);
        et_celular=(EditText)findViewById(R.id.et_celular);
        et_direccion=(EditText)findViewById(R.id.et_direccion);
        et_placa=(EditText)findViewById(R.id.et_placa);

        sp_categoria=(Spinner)findViewById(R.id.sp_categoria);
        sp_expedido=(Spinner)findViewById(R.id.sp_departamento);
        rb_hombre=(RadioButton)findViewById(R.id.rb_hombre);
        rb_mujer=(RadioButton)findViewById(R.id.rb_mujer);





        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try{
            Bundle bundle=getIntent().getExtras();
            ci=bundle.getString("ci");
            nombre=bundle.getString("nombre");
            paterno=bundle.getString("paterno");
            materno=bundle.getString("materno");
            genero=bundle.getString("genero");
            correo=bundle.getString("correo");
            celular=bundle.getString("celular");
            direccion=bundle.getString("direcion");
            categoria=bundle.getString("categoria");
            expedido=bundle.getString("expedido");
            estado=bundle.getString("estado");
            direccion_imagen=bundle.getString("direccion_imagen");
            et_placa.setText(bundle.getString("id_vehiculo"));


            direccion_imagen_carnet_1=bundle.getString("direccion_imagen_carnet_1");
            direccion_imagen_carnet_2=bundle.getString("direccion_imagen_carnet_2");
            direccion_imagen_licencia_1=bundle.getString("direccion_imagen_licencia_1");
            direccion_imagen_licencia_2=bundle.getString("direccion_imagen_licencia_2");

            et_ci.setText(ci);
            et_nombre.setText(nombre);
            et_paterno.setText(paterno);
            et_materno.setText(materno);
            et_correo.setText(correo);
            et_celular.setText(celular);
            et_direccion.setText(direccion);


        }catch (Exception e)
        {

        }

        bt_siguiente.setOnClickListener(this);
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
            case R.id.bt_siguiente:
              guardar_datos_conductor();
                break;
        }

    }


    public void guardar_datos_conductor()
    {
        imei="";
        nombre=et_nombre.getText().toString().trim();
        paterno=et_paterno.getText().toString().trim();
        materno=et_materno.getText().toString().trim();
        if(rb_hombre.isChecked()==true)
        {
            genero="M";
        }else
        {
            genero="F";
        }
        correo=et_correo.getText().toString().trim();
        celular=et_celular.getText().toString().trim();
        direccion=et_direccion.getText().toString().trim();
        categoria=sp_categoria.getSelectedItem().toString();

        ci=et_ci.getText().toString().trim();
        v_placa=et_placa.getText().toString().trim();
        switch (sp_expedido.getSelectedItem().toString().trim())
        {
            case "SANTA CRUZ":
                expedido="SC";
                break;
            case "COCHABAMBA":
                expedido="CB";
                break;
            case "LA PAZ":
                expedido="LP";
                break;
            case "TARIJA":
                expedido="TJ";
                break;
            case "BENI":
                expedido="BN";
                break;
            case "ORURO":
                expedido="OR";
                break;
            case "POTOSI":
                expedido="PT";
                break;
            case "CHUQUISACA":
                expedido="CH";
                break;
            case "PANDO":
                expedido="PD";
                break;
        }
        if(nombre.length()>2) {
            if (materno.length() > 2){
                if (direccion.length()>5){
                    if (correo.length()>10){
        Servicio servicio = new Servicio();
        servicio.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=guardar_conductor_pre_registro", "1");// parametro que recibe el doinbackground
                    }else
                    {
                        mensaje("Ingrese su correo electronico");
                    }
                }else{
                    mensaje("Ingrese su direccion de domicilio");
                }
            }else{
                mensaje("Ingrese su apellido materno");
            }
        }else
        {
            mensaje("Ingrese su nombre");
        }

    }


    public void saltar_datos_vehiculo()
    {
        Intent siguiente=new Intent(this, Datos_vehiculo_pre.class);
        siguiente.putExtra("ci",ci);
        siguiente.putExtra("placa",et_placa.getText().toString().trim());
        startActivity(siguiente);
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
                    jsonParam.put("ci", ci);
                    jsonParam.put("nombre", nombre);
                    jsonParam.put("paterno", paterno);
                    jsonParam.put("materno", materno);
                    jsonParam.put("expedido", expedido);
                    jsonParam.put("categoria", categoria);
                    jsonParam.put("direccion", direccion);
                    jsonParam.put("celular", celular);
                    jsonParam.put("genero", genero);
                    jsonParam.put("correo", correo);
                    jsonParam.put("estado", estado);
                    jsonParam.put("placa", v_placa);

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
                        estado=suceso.getSuceso();

                        if (error.equals("1")) {
                            JSONArray dato=respuestaJSON.getJSONArray("perfil_vehiculo");
                            v_marca= dato.getJSONObject(0).getString("marca");
                            v_modelo=dato.getJSONObject(0).getString("modelo") ;
                            v_tipo=dato.getJSONObject(0).getString("tipo") ;
                            v_clase= dato.getJSONObject(0).getString("clase") ;
                            v_color= dato.getJSONObject(0).getString("color") ;
                            v_direccion_imagen_1= dato.getJSONObject(0).getString("direccion_imagen_adelante") ;
                            v_direccion_imagen_2= dato.getJSONObject(0).getString("direccion_imagen_atras") ;
                            v_direccion_imagen_3= dato.getJSONObject(0).getString("direccion_imagen_interior_adelante") ;
                            v_direccion_imagen_4= dato.getJSONObject(0).getString("direccion_imagen_interior_atras") ;
                            v_ci_pro= dato.getJSONObject(0).getString("ci") ;
                            v_nombre_pro= dato.getJSONObject(0).getString("nombre") ;
                            v_paterno_pro= dato.getJSONObject(0).getString("paterno") ;
                            v_materno_pro= dato.getJSONObject(0).getString("materno") ;
                            v_expedido_pro= dato.getJSONObject(0).getString("expedido") ;
                            v_moto_pro= dato.getJSONObject(0).getString("moto");
                            v_movil_pro= dato.getJSONObject(0).getString("movil");
                            estado="1";
                            v_estado="1";
                            devuelve="1";



                            direccion_imagen_ruat=respuestaJSON.getString("direccion_imagen_ruat");
                            direccion_imagen_soat=respuestaJSON.getString("direccion_imagen_soat");
                            v_direccion_imagen_inspeccion_tecnica=respuestaJSON.getString("direccion_imagen_inspeccion_tecnica");

                        } else if(suceso.getSuceso().equals("3")) {
                            devuelve = "3";
                            estado="1";
                            v_estado="3";
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
            pDialog = new ProgressDialog(Datos_conductor.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Guardando los datos en el sistema. . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();//ocultamos proggress dialog


            if (s.equals("1")) {
                saltar_cargar_datos_vehiculo();
            } else if(s.equals("2")) {
                mensaje(suceso.getMensaje());
            }else if(s.equals("3"))
            {
                v_marca="";
                v_modelo="";
                v_tipo="";
                v_clase="";
                v_color= "";
                v_direccion_imagen_1="";
                v_direccion_imagen_2= "";
                v_direccion_imagen_3= "";
                v_direccion_imagen_4= "";
                v_ci_pro="";
                v_nombre_pro= "";
                v_paterno_pro= "";
                v_materno_pro= "";
                v_expedido_pro= "";
                v_moto_pro= "";
                v_movil_pro= "";
                v_estado= "";

                saltar_cargar_datos_vehiculo();
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

    public void saltar_cargar_datos_vehiculo()
    {
        if(v_placa.length()>4 && v_placa.length()<10) {
            Intent siguiente = new Intent(this, Datos_vehiculo_pre.class);
            siguiente.putExtra("ci", ci);
            siguiente.putExtra("direccion_imagen", direccion_imagen);
            siguiente.putExtra("placa", v_placa);
            siguiente.putExtra("marca", v_marca);
            siguiente.putExtra("modelo", v_modelo);
            siguiente.putExtra("tipo", v_tipo);
            siguiente.putExtra("clase", v_clase);
            siguiente.putExtra("color", v_color);
            siguiente.putExtra("direccion_imagen_1", v_direccion_imagen_1);
            siguiente.putExtra("direccion_imagen_2", v_direccion_imagen_2);
            siguiente.putExtra("direccion_imagen_3", v_direccion_imagen_3);
            siguiente.putExtra("direccion_imagen_4", v_direccion_imagen_4);
            siguiente.putExtra("ci_pro", v_ci_pro);
            siguiente.putExtra("expedido_pro", v_expedido_pro);
            siguiente.putExtra("nombre_pro", v_nombre_pro);
            siguiente.putExtra("paterno_pro", v_paterno_pro);
            siguiente.putExtra("materno_pro", v_materno_pro);
            siguiente.putExtra("moto_pro", v_moto_pro);
            siguiente.putExtra("movil_pro", v_movil_pro);
            siguiente.putExtra("estado", v_estado);

            siguiente.putExtra("expedido",expedido);
            siguiente.putExtra("nombre", nombre);
            siguiente.putExtra("paterno", paterno);
            siguiente.putExtra("materno", materno);


            siguiente.putExtra("direccion_imagen_soat", direccion_imagen_soat);
            siguiente.putExtra("direccion_imagen_ruat", direccion_imagen_ruat);
            siguiente.putExtra("direccion_imagen_inspeccion_tecnica", v_direccion_imagen_inspeccion_tecnica);


            siguiente.putExtra("direccion_imagen_carnet_1", direccion_imagen_carnet_1);
            siguiente.putExtra("direccion_imagen_carnet_2",direccion_imagen_carnet_2);
            siguiente.putExtra("direccion_imagen_licencia_1", direccion_imagen_licencia_1);
            siguiente.putExtra("direccion_imagen_licencia_2", direccion_imagen_licencia_2);

        startActivity(siguiente);
        }else
        {
            mensaje_sin_final("Necesita ingresar la placa de su vehiculo");
        }
    }


    public void mensaje(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK",  null);
        builder.create();
        builder.show();
    }
    public void mensaje_sin_final(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK",null);
        builder.create();
        builder.show();
    }

}
