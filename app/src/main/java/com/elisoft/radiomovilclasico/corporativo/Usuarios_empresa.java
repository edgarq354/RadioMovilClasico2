package com.elisoft.radiomovilclasico.corporativo;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.Menu_usuario;
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

public class Usuarios_empresa extends AppCompatActivity implements SearchView.OnQueryTextListener{
    String id_empresa="",id_usuario="";

    ListView lista_buscar;
    Suceso suceso;
    ProgressDialog pDialog;
    ArrayList<CContacto> contacto ;

    @Override
    protected void onStart() {
        cargar_contacto_en_la_lista("");
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios_empresa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent usuarios=new Intent(getApplicationContext(),Usuarios_buscar.class);
                usuarios.putExtra("id_empresa",id_empresa);
                startActivity(usuarios);

            }
        });

        try{
            Bundle bundle=getIntent().getExtras();
            id_empresa=bundle.getString("id_empresa");
            SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
            id_usuario=perfil.getString("id_usuario","");
            actualizar();
        }catch (Exception e){
            finish();
        }



        lista_buscar=(ListView)findViewById(R.id.lv_lista );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

// evento de onclick en la Lista de Busqueda ...
        lista_buscar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CContacto hi=new CContacto();
                hi=contacto.get(i);
                mensaje(hi);

            }
        });




    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                return super.onOptionsItemSelected(item);
    }

    private void actualizar() {

        Servicio servicio= new Servicio();
        servicio.execute(getString(R.string.servidor)+"frmCorporativo.php?opcion=lista_de_usuarios_por_id_empresa", "1",id_empresa);// parametro que recibe el doinbackground

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.trim().toString().length()>2) {
            cargar_contacto_en_la_lista(newText);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_buscar_usuario, menu);
        //se agregar la cabecera. con su busqueda
        MenuItem searchItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setInputType(InputType.TYPE_CLASS_PHONE);
        searchView.setOnQueryTextListener(this);



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void mensaje(final CContacto contacto)
    {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.app_name));
            builder.setMessage("¿Eliminar de la lista de usuario?");
            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    Servicio_cambio_estado servicio= new Servicio_cambio_estado();
                    servicio.execute(getString(R.string.servidor)+"frmCorporativo.php?opcion=eliminar_usuario_empresa", "1",contacto.getid(),id_usuario,id_empresa);// parametro que recibe el doinbackground

                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {

                }
            });
            builder.create();
            builder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    public void cargar_contacto_en_la_lista(String nombre)
    {
        contacto= new ArrayList<CContacto>();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(Usuarios_empresa.this,
                getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from usuario_empresa where nombre LIKE '%"+nombre+"%' or telefono LIKE '%"+nombre+"%' ORDER BY nombre ASC", null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

            do {
                Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_perfil);
                CContacto hi =new CContacto(fila.getString(0),fila.getString(1),fila.getString(2),myDrawable);
                contacto.add(hi);
            } while(fila.moveToNext());

        } else
            //   Toast.makeText(this, "No hay registrados",Toast.LENGTH_SHORT).show();

            bd.close();
        Lista_usuarios adaptador = new Lista_usuarios(this,contacto,getString(R.string.servidor),1);
        lista_buscar.setAdapter(adaptador);
    }


    // comenzar el servicio con el direcciones....
    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
            if(pDialog.isShowing()==true && isCancelled()==false) {
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
                        jsonParam.put("id_empresa", params[2]);

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
                                // vacia los datos que estan registrados en nuestra base de datos SQLite..
                                vaciar_contacto();

                                JSONArray usu = respuestaJSON.getJSONArray("lista_usuario");
                                for (int i = 0; i < usu.length(); i++) {
                                    int id = Integer.parseInt(usu.getJSONObject(i).getString("id"));
                                    String nombre = usu.getJSONObject(i).getString("nombre") + " " + usu.getJSONObject(i).getString("apellido");
                                    String telefono = usu.getJSONObject(i).getString("celular");

                                    cargar_lista_en_contacto(id, nombre, telefono);
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

            pDialog = new ProgressDialog(Usuarios_empresa.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Descargando los usuarios. . .");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.cancel();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                cargar_contacto_en_la_lista("");
            }
            else if(s.equals("2"))
            {
                Toast.makeText(getApplicationContext(),suceso.getMensaje(),Toast.LENGTH_SHORT).show();
                cargar_contacto_en_la_lista("");
            }
            else
            {
                mensaje_error("Error Al conectar con el servidor.");
                cargar_contacto_en_la_lista("");
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

    public class Servicio_cambio_estado extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
            if(pDialog.isShowing()==true && isCancelled()==false) {
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
                        jsonParam.put("id_administrador", params[3]);
                        jsonParam.put("id_empresa", params[4]);

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
                                devuelve = "1";
                                eliminar_contacto(params[2]);
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


            pDialog = new ProgressDialog(Usuarios_empresa.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Eliminando contacto. . .");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.cancel();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                Toast.makeText(getApplicationContext(),suceso.getMensaje().toString(),Toast.LENGTH_SHORT).show();
                cargar_contacto_en_la_lista("");
            }
            else if(s.equals("2"))
            { mensaje_error(suceso.getMensaje());
                cargar_contacto_en_la_lista("");
            }
            else
            {
                mensaje_error("Error Al conectar con el servidor.");
                cargar_contacto_en_la_lista("");
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

    private void eliminar_contacto(String param) {
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                    "radiomovilclasico", null, 4);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.delete("usuario_empresa", "id="+param, null);
            db.close();
            Log.e("sqlite ", "vaciar usuario_empresa con id=" + param);
        } catch (Exception e)
        {
            Log.e("sqlite ", "error vaciar contacto con id=" + param);
        }
    }

    private void cargar_lista_en_contacto(int id, String nombre, String telefono) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "radiomovilclasico", null, 4);
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("id", id);
        registro.put("nombre",nombre);
        registro.put("telefono",telefono);
        bd.insert("usuario_empresa", null, registro);
        bd.close();
    }

    private void vaciar_contacto() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
        SQLiteDatabase db = admin.getWritableDatabase();
        db.execSQL("delete from usuario_empresa");
        db.close();
        Log.e("sqlite ", "vaciar contacto");
    }

    public void mensaje_error(String mensaje)
    {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.app_name));
            builder.setMessage(mensaje);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {

                }
            });

            builder.create();
            builder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
