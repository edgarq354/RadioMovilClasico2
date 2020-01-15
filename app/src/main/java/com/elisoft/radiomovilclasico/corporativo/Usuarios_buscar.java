package com.elisoft.radiomovilclasico.corporativo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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

public class Usuarios_buscar extends AppCompatActivity implements  SearchView.OnQueryTextListener {


    ListView lista_buscar;
    Suceso suceso;
    JSONArray JS_contactos;



    ArrayList<CContacto> contacto;
    ProgressDialog pDialog;

    private String id_usuario,id_empresa;
    String nombre,celular,id_administrador;
    private int item;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_buscar_usuario, menu);
//se agregar la cabecera. con su busqueda
        //se agregar la cabecera. con su busqueda
        MenuItem searchItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setInputType(InputType.TYPE_CLASS_PHONE);
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.trim().toString().length()>6){
        cargar_json_en_lista( newText);
        }

        return false;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios_buscar);
        lista_buscar=(ListView)findViewById(R.id.lv_lista);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        try{
            Bundle bundle=getIntent().getExtras();
            id_empresa=bundle.getString("id_empresa");
            SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
            id_administrador=perfil.getString("id_usuario","");
        }catch (Exception e){
            finish();
        }




        // evento de onclick en la Lista de Busqueda ...
        lista_buscar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                item=i;
                id_usuario=contacto.get(i).getid();
                nombre=contacto.get(i).getNombre();
                celular=contacto.get(i).getNumero();
                int lon=celular.length();
                if(lon>=8) {
                    celular = celular.substring(lon - 8, lon);
                }

                try {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Usuarios_buscar.this);
                    builder.setTitle(getString(R.string.app_name));
                    builder.setMessage(Html.fromHtml("<font>¿Agregar <b>"+contacto.get(i).getNombre()+"</b>  con número de celular <b>"+celular+"</b> como usuario de su Empresa?</font>"));
                    builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            Servicio servicio=new Servicio();
                            servicio.execute(getString(R.string.servidor)+"frmCorporativo.php?opcion=agregar_usuario_empresa", "1",id_usuario,id_administrador,id_empresa);

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
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }












    // comenzar el servicio con el direcciones....
    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "-1";
            if(isCancelled()==false && pDialog.isShowing()==true) {

                if (params[1] == "1") {
                    devuelve = "";
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
                                devuelve = "3";
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
            pDialog = new ProgressDialog(Usuarios_buscar.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Registrando contacto como usuario de la empresa.");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.cancel();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if(s.equals("2"))
            {
                mensaje_error(suceso.getMensaje());
            }
            else if(s.equals("3"))
            {
                cargar_lista_en_contacto(Integer.parseInt(id_usuario),nombre,celular);
            }
            else if(s.equals("")==true)
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


    // comenzar el servicio de busqueda de usuarios que no estan en ninguna empresa....
    public class Servicio_buscar extends AsyncTask<String,Integer,String> {


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
                        jsonParam.put("celular", params[2]);

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
                                // registrar en el array

                                JSONArray usu = respuestaJSON.getJSONArray("lista_usuario");
                                contacto= new ArrayList<CContacto>();
                                for (int i = 0; i < usu.length(); i++) {
                                    String id =usu.getJSONObject(i).getString("id");
                                    String nombre = usu.getJSONObject(i).getString("nombre") + " " + usu.getJSONObject(i).getString("apellido");
                                    String telefono = usu.getJSONObject(i).getString("celular");

                                    CContacto con = new CContacto(id, nombre, telefono,null);
                                            contacto.add(con);
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
            try{
            pDialog = new ProgressDialog(Usuarios_buscar.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Descargando los usuarios. . .");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
            }catch (Exception e){

            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.cancel();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                Lista_usuarios adaptador = new Lista_usuarios(Usuarios_buscar.this,contacto,getString(R.string.servidor),0);
                lista_buscar.setAdapter(adaptador);
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


    private void cargar_lista_en_contacto(int id, String nombre, String telefono) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(Usuarios_buscar.this,
                getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("id", id);
        registro.put("nombre",nombre);
        registro.put("telefono",telefono);
        bd.insert("usuario_empresa", null, registro);
        bd.close();
        onBackPressed();
    }


    public  void cargar_json_en_lista(String texto)
    {
        if(texto.trim().toString().length()>6) {
            Servicio_buscar servicio = new Servicio_buscar();
            servicio.execute(getString(R.string.servidor) + "frmCorporativo.php?opcion=lista_de_usuarios_sin_empresa", "1", texto);// parametro que recibe el doinbackground
        }
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
