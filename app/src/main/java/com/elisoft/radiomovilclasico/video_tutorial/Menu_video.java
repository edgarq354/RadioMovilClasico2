package com.elisoft.radiomovilclasico.video_tutorial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class Menu_video extends AppCompatActivity {
    Suceso suceso;
    private ProgressDialog pDialog;
    ArrayList<Cvideo> cvideo=new ArrayList<Cvideo>();
    ListView lv_lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lv_lista=(ListView)findViewById(R.id.lv_lista);

        lv_lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cvideo hi=new Cvideo();
                hi=cvideo.get(i);
                ver_video(hi);
            }
        });

        Servicio hilo_pedido = new Servicio();
        String ip=getString(R.string.servidor);
        hilo_pedido.execute(ip+"frmUsuario.php?opcion=lista_video", "1");// parametro que recibe el doinbackground


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void ver_video(Cvideo video) {

        Uri uri = Uri.parse(video.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    public class Servicio extends AsyncTask<String,Integer,String> {


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
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));
                        // vacia los datos que estan registrados en nuestra base de datos SQLite..
                        if (suceso.getSuceso().equals("1")) {
                            JSONArray usu=respuestaJSON.getJSONArray("video");
                            cvideo=new ArrayList<Cvideo>();
                            for (int i=0;i<usu.length();i++)
                            {
                                String id=usu.getJSONObject(i).getString("id");
                                String nombre=usu.getJSONObject(i).getString("nombre");
                                String descripcion=usu.getJSONObject(i).getString("descripcion");
                                String s_url=usu.getJSONObject(i).getString("url");
                                String id_empresa=usu.getJSONObject(i).getString("id_empresa");

                                cvideo.add(new Cvideo(Integer.parseInt(id), Integer.parseInt(id_empresa),nombre,descripcion,s_url));
                            }

                            devuelve="1";
                        } else  {
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
            try {
                pDialog = new ProgressDialog(Menu_video.this);
                pDialog.setMessage("Descargando la lista de videos .");
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
            Log.e("onPostExcute=", "" + s);
            pDialog.dismiss();//ocultamos proggress dialog
            if (s.equals("1")) {
                item_video adaptador = new item_video(Menu_video.this,cvideo);
                lv_lista.setAdapter(adaptador);
            }
            else if(s.equals("2"))
            {
                cvideo.clear();
                item_video adaptador = new item_video(Menu_video.this,cvideo);
                lv_lista.setAdapter(adaptador);
            }
            else
            {
                cvideo.clear();
                item_video adaptador = new item_video(Menu_video.this,cvideo);
                lv_lista.setAdapter(adaptador);
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
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }

}