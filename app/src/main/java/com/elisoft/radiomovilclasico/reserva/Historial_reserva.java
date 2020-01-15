package com.elisoft.radiomovilclasico.reserva;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.Suceso;

import org.json.JSONArray;
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
import java.util.ArrayList;

public class Historial_reserva extends AppCompatActivity {

    ListView lista ;
    ArrayList<CReserva> reserva;
    Suceso suceso;
    private ProgressDialog pDialog;

    android.support.v7.app.AlertDialog alert2 = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_reserva);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        lista  = (ListView) findViewById(R.id.lista);

        lista .setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(reserva.get(i).getEstado()<=1){
                    cancelar_reserva(reserva.get(i));
                }
            }
        });

        actualizar();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void cancelar_reserva(CReserva pedido)
    {
        Intent intent=new Intent(this,Detalle_reserva.class);
        intent.putExtra("latitud",Double.parseDouble(pedido.getLatitud()));
        intent.putExtra("longitud",Double.parseDouble(pedido.getLongitud()));
        intent.putExtra("referencia",pedido.getReferencia());
        intent.putExtra("id_pedido",pedido.getId());
        startActivity(intent);
        finish();

    }



    private void actualizar() {
        SharedPreferences prefe=getSharedPreferences("perfil", Context.MODE_PRIVATE);
        String id=prefe.getString("id_usuario","");
        Servicio servicio= new  Servicio();
        servicio.execute(getString(R.string.servidor)+"frmPedido.php?opcion=get_reservas", "1",id);// parametro que recibe el doinbackground

    }

    public class Servicio_pedir_reserva extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";


            //verificar si alguien aceptotu pedido..
            // verificar si tiene un pedido que aun no ha finalizado....
            //obtener datos del pedido en curso.....


            //2: CANCELAR EL PEDIDO RESERVA
            if (params[1] == "1") {
                if(!isCancelled()){
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
                        jsonParam.put("detalle", params[4]);
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
            }

            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog
            try {
                pDialog = new ProgressDialog(Historial_reserva.this);
                pDialog.setMessage("Descargando la lista de reservas. . .");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.show();
            }catch (Exception e)
            {
                mensaje_error("Por favor actualice la aplicación.");
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.cancel();//ocultamos proggress dialog
            //ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);

             if(s.equals("6"))
            {
                actualizar();
            }else if(s.equals("7"))
            {
                mensaje_error(suceso.getMensaje());
            }
            else {
                mensaje_error_final("Falla en tu conexión a Internet.Si esta seguro que tiene conexión a internet.Actualice la aplicación.");
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

    public void mensaje_error(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }

    public void mensaje_error_final(String mensaje) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(Historial_reserva.this);
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
        } catch (Exception e) {
            Log.e("mensaje_error", e.toString());
        }
    }


    // comenzar el servicio con el direcciones....
    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
            if( isCancelled()==false) {
                devuelve = "-1";
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


                            JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                            suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));

                            if (suceso.getSuceso().equals("1")) {
                                reserva= new ArrayList<CReserva>();
                                // vacia los datos que estan registrados en nuestra base de datos SQLite..

                                JSONArray usu = respuestaJSON.getJSONArray("lista");
                                for (int i = 0; i < usu.length(); i++) {

                                     int id=Integer.parseInt(usu.getJSONObject(i).getString("id"));
                                     String referencia=usu.getJSONObject(i).getString("direccion");
                                     String numero=usu.getJSONObject(i).getString("numero_casa");
                                     String latitud=usu.getJSONObject(i).getString("latitud");
                                     String longitud=usu.getJSONObject(i).getString("longitud");
                                     String fecha=usu.getJSONObject(i).getString("fecha_reserva");
                                     int estado=Integer.parseInt(usu.getJSONObject(i).getString("estado_reserva"));

                                    CReserva hi =new CReserva( id,  referencia, numero,  latitud,  longitud, fecha, estado);
                                    reserva.add(hi);
                                }







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
            }
            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog
            try {
                pDialog = new ProgressDialog(Historial_reserva.this);
                pDialog.setMessage("Descargando la lista de reservas. . .");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.show();
            }catch (Exception e)
            {
                mensaje_error("Por favor actualice la aplicación.");
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("onPostExcute=", "" + s);
            pDialog.cancel();

            if (s.equals("1")) {
                Item_reserva adaptador = new Item_reserva(Historial_reserva.this,Historial_reserva.this,reserva);

                lista.setAdapter(adaptador);
            }
            else if(s.equals("2"))
            {
                Toast.makeText(getApplicationContext(),suceso.getMensaje(),Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Error Al conectar con el servidor.",Toast.LENGTH_SHORT).show();
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
