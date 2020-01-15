package com.elisoft.radiomovilclasico.preregistro;

import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.Suceso;

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

public class Datos_vehiculo_pre extends AppCompatActivity  implements View.OnClickListener{


    Button bt_siguiente;

    EditText et_placa,et_marca,et_tipo,et_clase,et_modelo,et_color;
    EditText et_ci_pro,et_nombre_pro,et_paterno_pro,et_materno_pro;
   Spinner sp_expedido_pro,sp_radicatoria;
   String ci="",direccion_imagen="",estado="" ;

    String direccion_imagen_ruat="";
    String direccion_imagen_soat="";
    String v_direccion_imagen_inspeccion_tecnica="";


    String direccion_imagen_carnet_1="";
    String direccion_imagen_carnet_2="";
    String direccion_imagen_licencia_1="";
    String direccion_imagen_licencia_2="";


    RadioButton rb_movil,rb_moto,rb_moto_torito;


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
    String v_radicatoria= "";

    Suceso suceso;
    ProgressDialog pDialog;
    AlertDialog alert2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_vehiculo_pre);
        et_placa=(EditText)findViewById(R.id.et_placa);
        et_marca=(EditText)findViewById(R.id.et_marca);
        et_tipo=(EditText)findViewById(R.id.et_tipo);
        et_clase=(EditText)findViewById(R.id.et_clase);
        et_modelo=(EditText)findViewById(R.id.et_modelo);
        et_color=(EditText)findViewById(R.id.et_color);
        et_ci_pro=(EditText)findViewById(R.id.et_ci_pro);
        et_nombre_pro=(EditText)findViewById(R.id.et_nombre_pro);
        et_paterno_pro=(EditText)findViewById(R.id.et_paterno_pro);
        et_materno_pro=(EditText)findViewById(R.id.et_materno_pro);
        sp_expedido_pro=(Spinner)findViewById(R.id.sp_departamento);
        sp_radicatoria=(Spinner)findViewById(R.id.sp_radicatoria);
        rb_moto=(RadioButton)findViewById(R.id.rb_moto);
        rb_moto_torito=(RadioButton)findViewById(R.id.rb_moto_torito);
        rb_movil=(RadioButton)findViewById(R.id.rb_movil);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bt_siguiente=(Button)findViewById(R.id.bt_siguiente);

        try{
            Bundle bundle=getIntent().getExtras();
            ci=bundle.getString("ci");
            direccion_imagen=bundle.getString("direccion_imagen");


            et_placa.setText(bundle.getString("placa"));
            et_marca.setText(bundle.getString("marca"));
            et_modelo.setText(bundle.getString("modelo"));
            et_tipo.setText(bundle.getString("tipo"));
            et_clase.setText(bundle.getString("clase"));
            et_color.setText(bundle.getString("color"));

            v_direccion_imagen_1=bundle.getString("direccion_imagen_1");
            v_direccion_imagen_2=bundle.getString("direccion_imagen_2");
            v_direccion_imagen_3=bundle.getString("direccion_imagen_3");
            v_direccion_imagen_4=bundle.getString("direccion_imagen_4");

            direccion_imagen_ruat=bundle.getString("direccion_imagen_ruat");
            direccion_imagen_soat=bundle.getString("direccion_imagen_soat");
            v_direccion_imagen_inspeccion_tecnica=bundle.getString("direccion_imagen_inspeccion_tecnica");



            direccion_imagen=bundle.getString("direccion_imagen");
            direccion_imagen_carnet_1=bundle.getString("direccion_imagen_carnet_1");
            direccion_imagen_carnet_2=bundle.getString("direccion_imagen_carnet_2");
            direccion_imagen_licencia_1=bundle.getString("direccion_imagen_licencia_1");
            direccion_imagen_licencia_2=bundle.getString("direccion_imagen_licencia_2");
            et_ci_pro.setText(bundle.getString("ci_pro"));
            et_nombre_pro.setText(bundle.getString("nombre_pro"));
            et_paterno_pro.setText(bundle.getString("paterno_pro"));
            et_materno_pro.setText(bundle.getString("materno_pro"));



            v_moto_pro=bundle.getString("moto_pro","0");
            v_movil_pro=bundle.getString("movil_pro","0");
            estado=bundle.getString("estado");



            elegir_propietario(bundle.getString("ci"), bundle.getString("expedido"),bundle.getString("nombre"),bundle.getString("paterno"),bundle.getString("materno"));

            if(v_moto_pro.equals("1")==true)
            {
                rb_moto.setChecked(true);
            }else if(v_moto_pro.equals("2")==true)
            {
                rb_moto_torito.setChecked(true);
            }
            if(v_movil_pro.equals("1")==true)
            {
                rb_movil.setChecked(true);
            }
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
                    jsonParam.put("placa", et_placa.getText().toString().trim());
                    jsonParam.put("marca", v_marca);
                    jsonParam.put("tipo", v_tipo);
                    jsonParam.put("clase", v_clase);
                    jsonParam.put("modelo", v_modelo);
                    jsonParam.put("color", v_color);
                    jsonParam.put("ci_pro", v_ci_pro);
                    jsonParam.put("nombre_pro", v_nombre_pro);
                    jsonParam.put("paterno_pro", v_paterno_pro);
                    jsonParam.put("materno_pro", v_materno_pro);
                    jsonParam.put("expedido_pro", v_expedido_pro);
                    jsonParam.put("radicatoria", v_radicatoria);
                    jsonParam.put("estado", estado);
                    jsonParam.put("movil",v_movil_pro);
                    jsonParam.put("moto", v_moto_pro);

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
                            devuelve="1";
                            estado="1";
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
            //para el progres Dialog
            pDialog = new ProgressDialog(Datos_vehiculo_pre.this);
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
                saltar_imagen_preregistro();
            } else if(s.equals("2")) {
                mensaje(suceso.getMensaje());
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


    public void guardar_datos_conductor()
    {

        v_marca=et_marca.getText().toString().trim();
        v_tipo=et_tipo.getText().toString().trim();
        v_clase=et_clase.getText().toString().trim();
        v_modelo=et_modelo.getText().toString().trim();
        v_color= et_color.getText().toString().trim();
        v_ci_pro=et_ci_pro.getText().toString().trim();
        v_nombre_pro= et_nombre_pro.getText().toString().trim();
        v_paterno_pro= et_paterno_pro.getText().toString().trim();
        v_materno_pro= et_materno_pro.getText().toString().trim();


        switch (sp_expedido_pro.getSelectedItem().toString().trim())
        {
            case "SANTA CRUZ":
                v_expedido_pro="SC";
                break;
            case "COCHABAMBA":
                v_expedido_pro="CB";
                break;
            case "LA PAZ":
                v_expedido_pro="CB";
                break;
            case "TARIJA":
                v_expedido_pro="TJ";
                break;
            case "BENI":
                v_expedido_pro="BN";
                break;
            case "ORURO":
                v_expedido_pro="OR";
                break;
            case "POTOSI":
                v_expedido_pro="PT";
                break;
            case "CHUQUISACA":
                v_expedido_pro="CH";
                break;
            case "PANDO":
                v_expedido_pro="PD";
                break;
        }


        switch (sp_radicatoria.getSelectedItem().toString().trim())
        {
            case "SANTA CRUZ":
                v_radicatoria="SC";
                break;
            case "COCHABAMBA":
                v_radicatoria="CB";
                break;
            case "LA PAZ":
                v_radicatoria="CB";
                break;
            case "TARIJA":
                v_radicatoria="TJ";
                break;
            case "BENI":
                v_radicatoria="BN";
                break;
            case "ORURO":
                v_radicatoria="OR";
                break;
            case "POTOSI":
                v_radicatoria="PT";
                break;
            case "CHUQUISACA":
                v_radicatoria="CH";
                break;
            case "PANDO":
                v_radicatoria="PD";
                break;
        }

        if(v_ci_pro.length()>4 && v_ci_pro.length()<14) {
            if(rb_moto.isChecked()==true)
            {
                v_moto_pro="1";
            }else  if(rb_moto_torito.isChecked()==true)
            {
                v_moto_pro="2";
            }else
            {
                v_moto_pro="0";
            }
            if(rb_movil.isChecked()==true)
            {
                v_movil_pro="1";
            }else
            {
                v_movil_pro="0";
            }

            v_marca=et_marca.getText().toString().trim();
            v_tipo=et_tipo.getText().toString().trim();
            v_clase=et_clase.getText().toString().trim();
            v_modelo=et_modelo.getText().toString().trim();
            v_color= et_color.getText().toString().trim();
            v_ci_pro=et_ci_pro.getText().toString().trim();
            v_nombre_pro= et_nombre_pro.getText().toString().trim();
            v_paterno_pro= et_paterno_pro.getText().toString().trim();
            v_materno_pro= et_materno_pro.getText().toString().trim();
            if(v_marca.length()>2) {
                if(v_tipo.length()>2){
                    if(v_clase.length()>2){
                        if(v_modelo.length()>2){
                            if (v_color.length()>2){
                                if (v_nombre_pro.length()>2){
                                    if (v_materno_pro.length()>2&&v_paterno_pro.length()>2){
            Servicio servicio = new Servicio();
            servicio.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=guardar_vehiculo_pre_registro", "1");// parametro que recibe el doinbackground
                                    }else {
                                        mensaje("Ingrese su apellidos");
                                    }
                                }else{
                                    mensaje("Ingrese su nombre");
                                }
                            }else{
                                mensaje("Ingrese el color");
                            }
                        }else{
                            mensaje("Ingrese su modelo");
                        }
                    }else{
                        mensaje("Ingrese su clase de vehículo");
                    }
                }else{
                    mensaje("Ingrese su tipo de vehículo");
                }
            }else{
                mensaje("Ingrese su marca de vehículo");
            }

        }else
        {
            mensaje("Por favor ingrese la cedula de identidad del Propietario del vehiculo.");
        }

    }


    public void saltar_imagen_preregistro()
    {

            Intent siguiente = new Intent(this, Menu_fotos_preregistro.class);
            siguiente.putExtra("ci", ci);
            siguiente.putExtra("direccion_imagen", direccion_imagen);
            siguiente.putExtra("placa", et_placa.getText().toString().trim());
            siguiente.putExtra("direccion_imagen_1", v_direccion_imagen_1);
            siguiente.putExtra("direccion_imagen_2", v_direccion_imagen_2);
            siguiente.putExtra("direccion_imagen_3", v_direccion_imagen_3);
            siguiente.putExtra("direccion_imagen_4", v_direccion_imagen_4);
            siguiente.putExtra("direccion_imagen_soat", direccion_imagen_soat);
            siguiente.putExtra("direccion_imagen_ruat", direccion_imagen_ruat);
            siguiente.putExtra("direccion_imagen_inspeccion_tecnica", v_direccion_imagen_inspeccion_tecnica);


            siguiente.putExtra("direccion_imagen_carnet_1", direccion_imagen_carnet_1);
            siguiente.putExtra("direccion_imagen_carnet_2",direccion_imagen_carnet_2);
            siguiente.putExtra("direccion_imagen_licencia_1", direccion_imagen_licencia_1);
            siguiente.putExtra("direccion_imagen_licencia_2", direccion_imagen_licencia_2);

        startActivity(siguiente);

    }

    public void mensaje(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK",null);
        builder.create();
        builder.show();
    }


    public void  elegir_propietario(final String c,final String ex,final String nom,final String pat,final String mat)
    {


        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.elegir_propietario, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);


        final LinearLayout ll_propietario= (LinearLayout) promptView.findViewById(R.id.ll_propietario);
        final LinearLayout ll_conductor= (LinearLayout) promptView.findViewById(R.id.ll_conductor);

        ll_conductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    alert2.cancel();
            }
        });
        ll_propietario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_ci_pro.setText(c);
                et_nombre_pro.setText(nom);
                et_paterno_pro.setText(pat);
                et_materno_pro.setText(mat);
                v_expedido_pro=ex;
                switch (ex)
                {
                    case "SC":
                        sp_expedido_pro.setSelection(0);

                        break;
                    case "CB":
                        sp_expedido_pro.setSelection(1);
                        break;
                    case "LP":
                        sp_expedido_pro.setSelection(2);
                        break;
                    case "TJ":
                        sp_expedido_pro.setSelection(3);
                        break;
                    case "BN":
                        sp_expedido_pro.setSelection(4);
                        break;
                    case "OR":
                        sp_expedido_pro.setSelection(5);
                        break;
                    case "PT":
                        sp_expedido_pro.setSelection(6);
                        break;
                    case "CH":
                        sp_expedido_pro.setSelection(7);
                        break;
                    case "PD":
                        sp_expedido_pro.setSelection(8);
                        break;

                }
                alert2.cancel();
            }
        });

        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();
    }


}
