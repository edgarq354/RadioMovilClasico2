package com.elisoft.radiomovilclasico.compartir;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

public class Compartir_principal extends AppCompatActivity {
    ArrayList<CUsuario> historial=new ArrayList<CUsuario>();
    ListView lv_lista;

    private ProgressDialog pDialog;
    Suceso suceso;
    TextView tv_mensaje;

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefe=getSharedPreferences("perfil", Context.MODE_PRIVATE);
        String id=prefe.getString("id_usuario","");
        Servicio hilo_pedido = new Servicio();
        String ip=getString(R.string.servidor);
        hilo_pedido.execute(ip+"frmCompartir_carrera.php?opcion=lista_de_usuarios_compartir", "1",id);// parametro que recibe el doinbackground
        Log.i("Item", "actualizar!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartir_principal);
        lv_lista=(ListView)findViewById(R.id.lv_lista);
        tv_mensaje=(TextView)findViewById(R.id.tv_mensaje);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startActivity(new Intent(getApplicationContext(),Buscar_usuario.class));
            }
        });

        UsuarioAdapter adaptador = new UsuarioAdapter(this,historial);
        lv_lista.setAdapter(adaptador);


        lv_lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CUsuario hi=new CUsuario();
                hi=historial.get(i);
                aliminar_compatimiento(hi.getId());
            }
        });


// Crear un nuevo adaptador
        cargar_usuarios();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    public void aliminar_compatimiento(final int id_usuario_compartir)
    {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Compartir Carrera");
        dialogo1.setMessage("¿Desea eliminar el Compartimiento de carreras con este Contacto?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                eliminnar_compartir(id_usuario_compartir);
            }
        });
        dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
            }
        });

        dialogo1.show();
    }

    private void eliminnar_compartir(int id_usuario_compartir) {
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        int id_usuario= Integer.parseInt(perfil.getString("id_usuario","0"));
        Servicio_compartir hilo_pedido = new Servicio_compartir();
        String ip=getString(R.string.servidor);
        hilo_pedido.execute(ip+"frmCompartir_carrera.php?opcion=eliminar_compartir_carrera", "1", String.valueOf(id_usuario), String.valueOf(id_usuario_compartir));// parametro que recibe el doinbackground
    }
    public void cargar_usuarios() {
        historial=new ArrayList<CUsuario>();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select id,nombre,apellido,correo,celular from usuario  ORDER BY nombre ASC ", null);
        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
            do {
                int id= Integer.parseInt(fila.getString(0));
                String nombre= fila.getString(1);
                String apellido= fila.getString(2);
                String correo= fila.getString(3);
                String celular= fila.getString(4);

                historial.add(new CUsuario(id,nombre,apellido,correo,celular));
            } while (fila.moveToNext());
            tv_mensaje.setText("");
        } else
            tv_mensaje.setText("No hay registrados");

        UsuarioAdapter adaptador = new UsuarioAdapter(this,historial);
        lv_lista.setAdapter(adaptador);

        bd.close();
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
                pDialog = new ProgressDialog(Compartir_principal.this);
                pDialog.setMessage("Descargando la lista de contactos . . .");
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
                cargar_usuarios();
            }
            else if(s.equals("2"))
            {
                cargar_usuarios();
            }
            else
            {
                cargar_usuarios();
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
        dialogo1.setTitle("Importante");
        dialogo1.setMessage(mensaje);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.show();
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
                pDialog = new ProgressDialog(Compartir_principal.this);
                pDialog.setMessage("Eliminando contacto para compartir mis carreras..");
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
                finish();
            }
            else if(s.equals("2"))
            {
                mensaje_error(suceso.getMensaje());
                finish();
            }
            else
            {
                mensaje_error("Falla en tu conexiñon a Internet.");
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
