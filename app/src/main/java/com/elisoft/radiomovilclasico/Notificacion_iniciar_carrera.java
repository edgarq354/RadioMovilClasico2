package com.elisoft.radiomovilclasico;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Notificacion_iniciar_carrera extends AppCompatActivity implements  View.OnClickListener{
    TextView tv_mensaje;
    Button bt_si,bt_no;
    Suceso suceso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_iniciar_carrera);

        tv_mensaje=(TextView)findViewById(R.id.tv_mensaje);
        bt_si=(Button)findViewById(R.id.bt_si);
        bt_no=(Button)findViewById(R.id.bt_no);

        try{
            Bundle bundle=getIntent().getExtras();
            String mensaje="";
            mensaje=bundle.getString("mensaje","");
            tv_mensaje.setText(mensaje);

        }catch (Exception e)
        {
            finish();
        }

        bt_si.setOnClickListener(this);
        bt_no.setOnClickListener(this);
        getSupportActionBar().hide();

    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bt_no)
        {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle(getString(R.string.app_name));
            dialogo1.setMessage("¿Desea cancelar el Pedido?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    //cargamos los datos
                    Servicio_abordar_vehiculo hilo_taxi = new Servicio_abordar_vehiculo();
                    SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                    SharedPreferences usuario = getSharedPreferences("perfil", MODE_PRIVATE);
                    String id_usuario = usuario.getString("id_usuario", "");
                    //dibuja en el mapa las taxi que estan cerca...
                    //hilo_taxi.execute(getString(R.string.servidor)+"frmTaxi.php?opcion=get_taxis_en_rango", "1","64.455","-18.533");// parametro que recibe el doinbackground
                    if(pedido.getString("id_pedido","").equals("")==false && pedido.getString("id_pedido","0").equals("0")==false) {
                        try {
                            hilo_taxi.execute(getString(R.string.servidor) + "frmPedido.php?opcion=cancelar_abordo_carrera", "1", id_usuario,pedido.getString("id_pedido",""));// parametro que recibe el doinbackground
                        } catch (Exception e) {

                        }
                    }else
                    {
                        finish();
                    }

                }
            });
            dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {

                }
            });
            dialogo1.show();
        }
        else if(view.getId()==R.id.bt_si)
        {
            Servicio_abordar_vehiculo hilo_taxi = new Servicio_abordar_vehiculo();
            SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
            SharedPreferences usuario = getSharedPreferences("perfil", MODE_PRIVATE);
            String id_usuario = usuario.getString("id_usuario", "");
            //dibuja en el mapa las taxi que estan cerca...
            //hilo_taxi.execute(getString(R.string.servidor)+"frmTaxi.php?opcion=get_taxis_en_rango", "1","64.455","-18.533");// parametro que recibe el doinbackground
            if(pedido.getString("id_pedido","").equals("")==false && pedido.getString("id_pedido","0").equals("0")==false) {
                try {
                    hilo_taxi.execute(getString(R.string.servidor) + "frmPedido.php?opcion=aceptar_abordo_carrera", "2", id_usuario,pedido.getString("id_pedido",""));// parametro que recibe el doinbackground
                } catch (Exception e) {

                }
            }else
            {
                finish();
            }
        }
    }

    public class Servicio_abordar_vehiculo extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";


            //1: CANCELAR ABIRDAR CARRERA.
            if (params[1] == "1") {
                try {
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
                    jsonParam.put("id_usuario", params[2]);
                    jsonParam.put("id_pedido", params[3]);
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

                        SystemClock.sleep(1500);
                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));
                        if (suceso.getSuceso().equals("1")) {
                            SharedPreferences pedido = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pedido.edit();
                            editor.putString("id_pedido", "");
                            editor.commit();

                            SharedPreferences pedido2 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = pedido2.edit();
                            editor2.putString("abordo", "2");
                            editor2.commit();

                            devuelve = "6";
                        } else

                        {

                            devuelve = "7";
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


            //2: ACEPTAR ABORDO DE LA CARRERA

            if (params[1] == "2") {
                try {
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
                    jsonParam.put("id_usuario", params[2]);
                    jsonParam.put("id_pedido", params[3]);
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

                        SystemClock.sleep(1500);
                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));
                        if (suceso.getSuceso().equals("1")) {
                            SharedPreferences pedido = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pedido.edit();
                            editor.putString("id_pedido", "");
                            editor.commit();

                            SharedPreferences pedido2 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = pedido2.edit();
                            editor2.putString("abordo", "1");
                            editor2.commit();

                            devuelve = "1";
                        } else

                        {

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
            //   tv_mensaje_pedido.setText("Buscando el Taxi mas Próximo.");

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);


            if (s.equals("1")) {
             finish();
            } else if (s.equals("2") == true) {
                mensaje_error_final(suceso.getMensaje());

            }  else if(s.equals("500")==true)
            {

            }else if(s.equals("6"))
            {
                SharedPreferences pedido2 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                String id_pedido=pedido2.getString("id_pedido","");

                SharedPreferences.Editor editor2 = pedido2.edit();
                editor2.putString("id_pedido", "");
                editor2.putString("estado", "4");
                editor2.commit();
                Intent cancelar_pedido=new Intent(getApplicationContext(),Cancelar_pedido_usuario.class);
                cancelar_pedido.putExtra("id_pedido",id_pedido);
                startActivity(cancelar_pedido);
                finish();


            }else if(s.equals("7"))
            {
                mensaje_error_final(suceso.getMensaje());
            }
            else {
                mensaje_error_final("Error al conectar con el servidor");
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

    public void mensaje_error_final(String mensaje)
    {   try {
        AlertDialog.Builder builder = new AlertDialog.Builder(Notificacion_iniciar_carrera.this);
        builder.setTitle("Atención");
        builder.setCancelable(false);
        builder.setMessage(mensaje);
        builder.create();
        builder.setPositiveButton("OK",null);
        builder.show();
    }catch (Exception e)
    {

    }

    }

}
