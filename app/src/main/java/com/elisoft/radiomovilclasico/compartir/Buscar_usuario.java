package com.elisoft.radiomovilclasico.compartir;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.SqLite.AdminSQLiteOpenHelper;
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

public class Buscar_usuario extends AppCompatActivity {
   EditText et_buscar;
    Suceso suceso;

    private ProgressDialog pDialog;
    ArrayList<CUsuario> historial=new ArrayList<CUsuario>();
    ListView lv_lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_usuario);
        et_buscar=(EditText)findViewById(R.id.et_buscar);
        lv_lista=(ListView)findViewById(R.id.lv_lista);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        lv_lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CUsuario hi=new CUsuario();
                hi=historial.get(i);
                compartir_carreras(hi.getId());
            }
        });


        et_buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()>2){
                    SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
                    int id_usuario= Integer.parseInt(perfil.getString("id_usuario","0"));
                    Servicio hilo_pedido = new Servicio();
                    String ip=getString(R.string.servidor);
                    hilo_pedido.execute(ip+"frmCompartir_carrera.php?opcion=buscar_usuario", "1", String.valueOf(id_usuario),charSequence.toString());// parametro que recibe el doinbackground
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    public void compartir_carreras(final int id_usuario_compartir)
    {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Compartir Carrera");
        dialogo1.setMessage("¿Desea Compartir sus carreras con este Contacto?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                compartir_mis_carreras(id_usuario_compartir);
            }
        });
        dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
            }
        });

        dialogo1.show();
    }

    private void compartir_mis_carreras(int id_usuario_compartir) {
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        int id_usuario= Integer.parseInt(perfil.getString("id_usuario","0"));
        Servicio_compartir hilo_pedido = new Servicio_compartir();
        String ip=getString(R.string.servidor);
        hilo_pedido.execute(ip+"frmCompartir_carrera.php?opcion=insertar_compartir_carreras", "1", String.valueOf(id_usuario), String.valueOf(id_usuario_compartir));// parametro que recibe el doinbackground
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
                    jsonParam.put("id_usuario", params[2]);
                    jsonParam.put("celular", params[3]);

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
                            JSONArray usu=respuestaJSON.getJSONArray("usuario");
                            historial=new ArrayList<CUsuario>();
                            for (int i=0;i<usu.length();i++)
                            {
                                String id_usuario=usu.getJSONObject(i).getString("id");
                                String nombre=usu.getJSONObject(i).getString("nombre");
                                String apellido=usu.getJSONObject(i).getString("apellido");
                                String celular=usu.getJSONObject(i).getString("celular");
                                String correo=usu.getJSONObject(i).getString("correo");

                                historial.add(new CUsuario(Integer.parseInt(id_usuario),nombre,apellido,correo,celular));
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
            //para el progres Dialog

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                UsuarioAdapter adaptador = new UsuarioAdapter(Buscar_usuario.this,historial);
                lv_lista.setAdapter(adaptador);
            }
            else if(s.equals("2"))
            {
                historial.clear();
                UsuarioAdapter adaptador = new UsuarioAdapter(Buscar_usuario.this,historial);
                lv_lista.setAdapter(adaptador);
            }
            else
            {
                historial.clear();
                UsuarioAdapter adaptador = new UsuarioAdapter(Buscar_usuario.this,historial);
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



    private void insertar_usuarios(String id, String nombre, String apellido, String correo, String celular) {

        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
            SQLiteDatabase bd = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("id", id);
            registro.put("nombre", nombre);
            registro.put("apellido", apellido);
            registro.put("correo", correo);
            registro.put("celular", celular);
            bd.insert("usuario", null, registro);
            bd.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void vaciar_usuario() {

        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                    getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
            SQLiteDatabase db = admin.getWritableDatabase();
            db.delete("usuario", "", null);
            db.close();
            Log.e("sqlite ", "vaciar todas las usuario");
        } catch (Exception e)
        {
            Log.e("sqlite ", "Error : vaciar todas los usuario "+e);
        }
    }

    public class Servicio_compartir extends AsyncTask<String,Integer,String> {


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
                    jsonParam.put("id_usuario", params[2]);
                    jsonParam.put("id_usuario_compartir", params[3]);

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
                        vaciar_usuario();
                        if (suceso.getSuceso().equals("1")) {
                            JSONArray usu=respuestaJSON.getJSONArray("usuario");
                            for (int i=0;i<usu.length();i++)
                            {
                                String id_usuario=usu.getJSONObject(i).getString("id");
                                String nombre=usu.getJSONObject(i).getString("nombre");
                                String apellido=usu.getJSONObject(i).getString("apellido");
                                String celular=usu.getJSONObject(i).getString("celular");
                                String correo=usu.getJSONObject(i).getString("correo");


                                insertar_usuarios( id_usuario,nombre,apellido,correo,celular);
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
            //para el progres Dialog
            try {
                pDialog = new ProgressDialog(Buscar_usuario.this);
                pDialog.setMessage("Agregando contacto para compartir mis carreras..");
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
            super.onPostExecute(s);  pDialog.dismiss();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                mensaje_error(suceso.getMensaje());
            }
            else if(s.equals("2"))
            {
                mensaje_error(suceso.getMensaje());
            }
            else
            {
                mensaje_error("Falla en tu conexión a Internet.");
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
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("TaxiCorp");
        dialogo1.setMessage(mensaje);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                finish();
            }
        });
        dialogo1.show();
    }

}
