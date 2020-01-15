package com.elisoft.radiomovilclasico;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.SqLite.AdminSQLiteOpenHelper;

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

public class Pedido_finalizado extends AppCompatActivity implements View.OnClickListener{

    TextView tv_mensaje,tv_distancia,tv_detalle ;
    Button bt_aceptar;
    RatingBar rb_conductor,rb_vehiculo;
    int id_pedido=0;
    Suceso suceso;

    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_finalizado);


        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tv_mensaje=(TextView)findViewById(R.id.tv_mensaje);
        tv_distancia=(TextView)findViewById(R.id.tv_distancia);
        bt_aceptar=(Button)findViewById(R.id.bt_aceptar);
        rb_conductor=(RatingBar)findViewById(R.id.rb_conductor);
        rb_vehiculo=(RatingBar)findViewById(R.id.rb_vehiculo);
        tv_detalle=(TextView)findViewById(R.id.tv_detalle);
        try{
            Bundle bundle=getIntent().getExtras();
            id_pedido= Integer.parseInt(bundle.getString("id_pedido"));
            tv_distancia.setText("Distancia recorrido "+bundle.getString("distancia")+" Mtrs.");
            tv_mensaje.setText("Total "+bundle.getString("monto_total","")+" BOB.");
            if(bundle.getString("detalle","").equals("")==false){
                tv_detalle.setText(bundle.getString("detalle",""));
            }
        }catch (Exception e)
        {
            finish();
        }

        bt_aceptar.setOnClickListener(this);
        getSupportActionBar().hide();

    }


    @Override
    protected void onStop() {
        guardar_calificacion();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        guardar_calificacion();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        SharedPreferences mis_datos = getSharedPreferences(getString(R.string.finalizo), this.MODE_PRIVATE);
        if (mis_datos.getInt("id_pedido", 0) == id_pedido)
        {
            finish();
        }else{
            mostrar_calificacion();
        }

        super.onStart();
    }

    @Override
    protected void onPause() {
        guardar_calificacion();
        super.onPause();
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bt_aceptar)
        {
            String puntuacion_conductor= String.valueOf(rb_conductor.getRating());
            String puntuacion_vehiculo= String.valueOf(rb_vehiculo.getRating());
            Servicio hilo = new Servicio();
            hilo.execute(getString(R.string.servidor) + "frmPedido.php?opcion=cargar_puntuacion", "1", String.valueOf(id_pedido),puntuacion_conductor,puntuacion_vehiculo);
        }
    }
    // comenzar el servicio para la conexion con la base de datos.....
    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
// busca taxi dentro de su rango
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
                    jsonParam.put("id_pedido", params[2]);
                    jsonParam.put("punto_conductor", params[3]);
                    jsonParam.put("punto_vehiculo", params[4]);
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

                        SystemClock.sleep(950);

                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
                            devuelve="1";

                            JSONArray usu=respuestaJSON.getJSONArray("historial");
                            for (int i=0;i<usu.length();i++)
                            {
                                int id_pedido= Integer.parseInt(usu.getJSONObject(i).getString("id"));
                                int id_taxi= Integer.parseInt(usu.getJSONObject(i).getString("id_conductor"));
                                int estado_pedido= Integer.parseInt(usu.getJSONObject(i).getString("estado"));
                                String indicacion=usu.getJSONObject(i).getString("direccion");
                                String fecha_pedido=usu.getJSONObject(i).getString("fecha_pedido");
                                double latitud= Double.parseDouble(usu.getJSONObject(i).getString("longitud"));
                                double longitud= Double.parseDouble(usu.getJSONObject(i).getString("longitud"));
                                String nombre=usu.getJSONObject(i).getString("nombre");
                                String apellido=usu.getJSONObject(i).getString("apellido");
                                String celular=usu.getJSONObject(i).getString("celular");
                                String marca=usu.getJSONObject(i).getString("marca");
                                String placa=usu.getJSONObject(i).getString("placa");
                                String descripcion=usu.getJSONObject(i).getString("detalle");
                                String monto_total=usu.getJSONObject(i).getString("monto_total");


                                cargar_lista_en_historial( id_pedido,
                                        id_taxi,
                                        estado_pedido,
                                        fecha_pedido,
                                        nombre,
                                        apellido,
                                        celular,
                                        marca,
                                        placa,
                                        indicacion,
                                        descripcion,
                                        latitud,
                                        longitud,
                                        monto_total);
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
                pDialog = new ProgressDialog(Pedido_finalizado.this);
                pDialog.setTitle(getString(R.string.app_name));
                pDialog.setMessage("Subiendo la calificación");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }catch (Exception e)
            {

            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                pDialog.dismiss();//ocultamos proggress dialog
            }catch (Exception e)
            {

            }
            //ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                startActivity(new Intent(getApplicationContext(),Menu_usuario.class));
                finish();
            }

            else
            {
                finish();
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

    private void cargar_lista_en_historial( int id,
                                            int id_taxi,
                                            int estado_pedido,
                                            String fecha_pedido,
                                            String nombre,
                                            String apellido,
                                            String celular,
                                            String marca,
                                            String placa,
                                            String indicacion,
                                            String descripcion,
                                            double latitud,
                                            double longitud,
                                            String monto_total)
    {
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                    getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
            SQLiteDatabase bd = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("id", String.valueOf(id));
            registro.put("id_conductor", String.valueOf(id_taxi));
            registro.put("estado_pedido", String.valueOf(estado_pedido));
            registro.put("fecha_pedido", fecha_pedido);
            registro.put("latitud", String.valueOf(latitud));
            registro.put("longitud", String.valueOf(longitud));
            registro.put("nombre", nombre);
            registro.put("apellido", apellido);
            registro.put("celular", celular);
            registro.put("marca", marca);
            registro.put("placa", placa);
            registro.put("indicacion", indicacion);
            registro.put("descripcion", descripcion);
            registro.put("monto_total", monto_total);
            bd.insert("pedido_usuario", null, registro);
            bd.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void guardar_calificacion()
    {
        SharedPreferences mis_datos;
        mis_datos=getSharedPreferences(getString(R.string.finalizo),MODE_PRIVATE);

        SharedPreferences.Editor editor=mis_datos.edit();
        editor.putInt("conductor",rb_conductor.getNumStars());
        editor.putInt("vehiculo",rb_vehiculo.getNumStars());
        editor.putInt("id_pedido",id_pedido);
        editor.commit();

    }

    public void mostrar_calificacion()
    {
        SharedPreferences mis_datos;
        mis_datos=getSharedPreferences(getString(R.string.finalizo),MODE_PRIVATE);
        rb_conductor.setRating(0);
        rb_vehiculo.setRating(0);

    }


}
