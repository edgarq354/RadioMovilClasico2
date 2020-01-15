package com.elisoft.radiomovilclasico.guia_turistica;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

public class Guia_comercial extends AppCompatActivity {
int id_categoria=0;

    ListView lista ;
    ArrayList<CLugar> lugar;
    private ProgressDialog pDialog;
    int id_pedido;
    Suceso suceso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guia_comercial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lista  = (ListView) findViewById(R.id.lista);


        try {
            Bundle bundle = getIntent().getExtras();
            id_categoria = Integer.parseInt(bundle.getString("id_categoria","0"));
            if(id_categoria==0){
                finish();
            }
           actualizar();
            cargar_contacto_en_la_lista("",id_categoria);

        }catch (Exception e){
            finish();
        }






    }

    private void actualizar() {

        Servicio servicio= new Servicio();
        servicio.execute(getString(R.string.servidor)+"frmGuia_turistica.php?opcion=lista_de_lugar", "1");// parametro que recibe el doinbackground

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }



    public void cargar_contacto_en_la_lista(String nombre,int id_categoria)
    {
        lugar= new ArrayList<CLugar>();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper( this,
                getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from lugar where nombre LIKE '%"+nombre+"%' and id_categoria='"+id_categoria+"' ORDER BY nombre ASC", null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

            do {
                CLugar hi =new CLugar(fila.getInt(0),fila.getString(1),fila.getString(2),fila.getString(3),
                        fila.getString(4),fila.getString(5),fila.getString(6),fila.getInt(7),fila.getInt(8));
                lugar.add(hi);
            } while(fila.moveToNext());

        } else
            //   Toast.makeText(this, "No hay registrados",Toast.LENGTH_SHORT).show();

            bd.close();
        Item_guia_comercial adaptador = new Item_guia_comercial(this,this,lugar);

        lista.setAdapter(adaptador);
    }

    private void cargar_lista_en_categoria(int id,
                                           String nombre,
                                           String direccion,
                                           String telefono,
                                           String whatsapp,
                                           String latitud,
                                           String longitud,
                                           int estado,
                                           int id_categoria) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("id", id);
        registro.put("nombre",nombre);
        registro.put("direccion",direccion);
        registro.put("telefono",telefono);
        registro.put("whatsapp",whatsapp);
        registro.put("latitud",latitud);
        registro.put("longitud",longitud);
        registro.put("estado",estado);
        registro.put("id_categoria",id_categoria);
        bd.insert("lugar", null, registro);
        bd.close();
    }

    private void vaciar_categoria() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
        SQLiteDatabase db = admin.getWritableDatabase();
        db.execSQL("delete from lugar");
        db.close();
        Log.e("sqlite ", "vaciar contacto");
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
                        jsonParam.put("id_categoria", String.valueOf(id_categoria));

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
                                vaciar_categoria();

                                JSONArray usu = respuestaJSON.getJSONArray("lista");
                                for (int i = 0; i < usu.length(); i++) {
                                    int id = Integer.parseInt(usu.getJSONObject(i).getString("id"));
                                    String nombre = usu.getJSONObject(i).getString("nombre");
                                    String direccion = usu.getJSONObject(i).getString("direccion");
                                    String telefono = usu.getJSONObject(i).getString("telefono");
                                    String whatsapp = usu.getJSONObject(i).getString("whatsapp");
                                    String latitud = usu.getJSONObject(i).getString("latitud");
                                    String longitud = usu.getJSONObject(i).getString("longitud");
                                    int estado = Integer.parseInt(usu.getJSONObject(i).getString("estado"));
                                    int id_categoria = Integer.parseInt(usu.getJSONObject(i).getString("id_categoria"));
                                    cargar_lista_en_categoria(id, nombre,direccion,telefono,whatsapp,latitud,longitud,estado,id_categoria);
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


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                cargar_contacto_en_la_lista("",id_categoria);
            }
            else if(s.equals("2"))
            {
                Toast.makeText(getApplicationContext(),suceso.getMensaje(),Toast.LENGTH_SHORT).show();
                cargar_contacto_en_la_lista("",id_categoria);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Error Al conectar con el servidor.",Toast.LENGTH_SHORT).show();
                cargar_contacto_en_la_lista("",id_categoria);
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
