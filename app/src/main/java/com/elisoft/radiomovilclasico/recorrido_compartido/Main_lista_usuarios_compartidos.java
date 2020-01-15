package com.elisoft.radiomovilclasico.recorrido_compartido;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class Main_lista_usuarios_compartidos extends AppCompatActivity {
    ListView lista;
    ArrayList<CCompartido> compartido=new ArrayList<CCompartido>();
    Suceso suceso;
    private ProgressDialog pDialog;
    TextView tv_mensaje;


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lista_carrera_compartida);
        lista = (ListView) findViewById(R.id.lv_lista);
        tv_mensaje=(TextView)findViewById(R.id.tv_mensaje);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        actualizar();
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CCompartido hi=new CCompartido();
                hi=compartido.get(i);
                Intent intent=new Intent(getApplicationContext(),Carreras_recorrido_usuario.class);
                intent.putExtra("id_usuario",hi.getId());
                startActivity(intent);
            }
        });



    }

    public void actualizar()
    {
        SharedPreferences prefe=getSharedPreferences("perfil", Context.MODE_PRIVATE);
        String id=prefe.getString("id_usuario","");


        Servicio hilo = new Servicio();
        hilo.execute(getString(R.string.servidor) + "frmCompartir_carrera.php?opcion=lista_de_sub_usuarios_por_id_usuario", "1", id);// parametro que recibe el doinbackground
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
                                String id=usu.getJSONObject(i).getString("id_usuario");
                                String nombre=usu.getJSONObject(i).getString("nombre");
                                String direccion=usu.getJSONObject(i).getString("direccion");
                                String estado=usu.getJSONObject(i).getString("estado");
                                CCompartido hi = new CCompartido(Integer.parseInt(id),nombre,direccion, Integer.parseInt(estado));
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
                pDialog = new ProgressDialog(Main_lista_usuarios_compartidos.this);
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

        Item_compartido adaptador = new Item_compartido(Main_lista_usuarios_compartidos.this, compartido);
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
