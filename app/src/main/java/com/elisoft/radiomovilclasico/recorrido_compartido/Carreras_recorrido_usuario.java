package com.elisoft.radiomovilclasico.recorrido_compartido;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

public class Carreras_recorrido_usuario extends AppCompatActivity {
    ListView lista;
    ArrayList<CCompartido> compartido=new ArrayList<CCompartido>();
    Suceso suceso;
    private ProgressDialog pDialog;
    TextView tv_mensaje;
    int id_usuario_compartido;


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carreras_recorrido_usuario);
        lista = (ListView) findViewById(R.id.lv_lista);
        tv_mensaje=(TextView)findViewById(R.id.tv_mensaje);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        try{
            Bundle bundle=getIntent().getExtras();
            id_usuario_compartido=bundle.getInt("id_usuario");
        }catch (Exception e)
        {
            finish();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        actualizar();

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CCompartido hi=new CCompartido();
                hi=compartido.get(i);
                Intent intent=new Intent(getApplicationContext(),Ver_carrera_compartida.class);
                intent.putExtra("id_usuario",hi.getId());
                intent.putExtra("id_pedido",hi.getId_pedido());
                intent.putExtra("id_carrera",hi.getId_carrera());
                intent.putExtra("id_conductor",hi.getId_conductor());
                intent.putExtra("fecha_inicio",hi.getFecha_inicio());
                intent.putExtra("estado_pedido",hi.getEstado());
                intent.putExtra("estado_carrera",hi.getEstado_carrera());


                intent.putExtra("nombre_pasajero",hi.getNombre_pasajero());
                intent.putExtra("apellido_pasajero",hi.getApellido_pasajero());
                intent.putExtra("conductor",hi.getConductor());
                intent.putExtra("celular_conductor",hi.getCelular_conductor());
                intent.putExtra("celular_pasajero",hi.getCelular_pasajero());
                intent.putExtra("marca",hi.getMarca());
                intent.putExtra("color",hi.getColor());
                intent.putExtra("razon_social",hi.getRazon_social());
                intent.putExtra("placa",hi.getPlaca());
                intent.putExtra("id_empresa",hi.getId_empresa());
                intent.putExtra("url",hi.getUrl());
                startActivity(intent);
            }
        });
    }

    public void actualizar()
    {
        SharedPreferences prefe=getSharedPreferences("perfil", Context.MODE_PRIVATE);
        String id=prefe.getString("id_usuario","");


        Servicio hilo = new Servicio();
        hilo.execute(getString(R.string.servidor) + "frmCompartir_carrera.php?opcion=lista_de_carreras_compartidas_por_id_usuario", "1", id, String.valueOf(id_usuario_compartido));// parametro que recibe el doinbackground


    }

    // comenzar el servicio con el motista....
    public class Servicio extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

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
                    jsonParam.put("id_usuario",params[2]);
                    jsonParam.put("id_usuario_compartido",params[3]);


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
                        // vacia los datos que estan registrados en nuestra base de datos SQLite..
                        compartido = new ArrayList<CCompartido>();

                        if (suceso.getSuceso().equals("1")) {
                            JSONArray usu = respuestaJSON.getJSONArray("usuario");
                            for (int i = 0; i < usu.length(); i++) {
                                String id_conductor=usu.getJSONObject(i).getString("id_conductor");
                                String fecha_inicio=usu.getJSONObject(i).getString("fecha_inicio");
                                String id_usuario=usu.getJSONObject(i).getString("id_usuario");
                                String id_pedido=usu.getJSONObject(i).getString("id_pedido");
                                String id_carrera=usu.getJSONObject(i).getString("id_carrera");
                                String estado_carrera=usu.getJSONObject(i).getString("estado_carrera");
                                String estado_pedido=usu.getJSONObject(i).getString("estado_pedido");

                                String nombre_pasajero=usu.getJSONObject(i).getString("nombre_usuario");
                                String apellido_pasajero=usu.getJSONObject(i).getString("apellido_usuario");
                                String conductor=usu.getJSONObject(i).getString("conductor");
                                String celular_conductor=usu.getJSONObject(i).getString("celular_conductor");
                                String celular_pasajero=usu.getJSONObject(i).getString("celular_usuario");
                                String marca=usu.getJSONObject(i).getString("marca");
                                String color=usu.getJSONObject(i).getString("color");
                                String razon_social=usu.getJSONObject(i).getString("razon_social");
                                String placa=usu.getJSONObject(i).getString("placa");
                                String id_empresa=usu.getJSONObject(i).getString("id_empresa");
                                String imagen=usu.getJSONObject(i).getString("url");


                                CCompartido hi = new CCompartido(Integer.parseInt(id_usuario), Integer.parseInt(id_pedido), Integer.parseInt(id_carrera), Integer.parseInt(estado_pedido), Integer.parseInt(id_conductor), Integer.parseInt(estado_carrera),fecha_inicio,nombre_pasajero
                                        ,apellido_pasajero
                                        ,conductor
                                        ,celular_conductor
                                        ,celular_pasajero
                                        ,marca
                                        ,color
                                        ,razon_social,placa,id_empresa,imagen);

                                compartido.add(hi);
                            }
                            Log.e("Carrera","Finalizo de cargar las carreras.");


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
                pDialog = new ProgressDialog(Carreras_recorrido_usuario.this);
                pDialog.setMessage("Descargando los usuarios. .");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(true);
                pDialog.show();
            }catch (Exception e)
            {
                mensaje_error("Por favor actualice la aplicación.");
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                actualizar_lista();
                tv_mensaje.setText("");
            } else if(s.equals("2"))
            {
                tv_mensaje.setText("Lista vacia.");
            }
            else
            {
                mensaje_error("Error: Al conectar con el servidor.");
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
    public void actualizar_lista() {

        Item_carrera_compartido adaptador = new Item_carrera_compartido(Carreras_recorrido_usuario.this, compartido);
        lista.setAdapter(adaptador);

    }
    public void mensaje_error(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }



}
