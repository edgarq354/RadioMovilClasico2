package com.elisoft.radiomovilclasico;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.menu_otra_direccion.Otra_direccion;

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
import java.util.List;

public class Datos_de_pedido extends AppCompatActivity implements View.OnClickListener {

    EditText indicacion,et_direccion;
    Button pedir_taxi_ahora, cancelar_pedido;
    CheckBox cb_pedir_otra_direccion;
    double latitud = 0, longitud = 0;
    int otra_ubicacion=0;

    Suceso suceso;
    ProgressDialog pDialog;

    LinearLayout ll_pedir;
    LinearLayout ll_cancelar, ll_progress;
    ProgressBar cargando;
    Handler handle = new Handler();
    Thread pro_pedido;
    int i = 0;

    LinearLayout.LayoutParams cero = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
    LinearLayout.LayoutParams parent = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_de_pedido);
        indicacion = (EditText) findViewById(R.id.indicacion);
        et_direccion = (EditText) findViewById(R.id.et_direccion);
        pedir_taxi_ahora = (Button) findViewById(R.id.pedir_movil_ahora);
        cancelar_pedido = (Button) findViewById(R.id.cancelar_pedido);
        cargando = (ProgressBar) findViewById(R.id.cargando);
        ll_pedir = (LinearLayout) findViewById(R.id.ll_pedir);
        ll_cancelar = (LinearLayout) findViewById(R.id.ll_cancelar);
        ll_progress = (LinearLayout) findViewById(R.id.ll_progress);
        cb_pedir_otra_direccion=(CheckBox)findViewById(R.id.cb_pedir_otra_direccion);


        try {
            Bundle bundle = getIntent().getExtras();
            latitud = bundle.getDouble("latitud", 0);
            longitud = bundle.getDouble("longitud", 0);
            otra_ubicacion=bundle.getInt("otra_ubicacion",0);

            if(otra_ubicacion==1)
            {
              cb_pedir_otra_direccion.setVisibility(View.INVISIBLE);
            }

            obtener_direccion(latitud,longitud);
            if (latitud == 0 || longitud == 0) {
                finish();
            }

        } catch (Exception e) {
            finish();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pedir_taxi_ahora.setOnClickListener(this);
        cancelar_pedido.setOnClickListener(this);
        cb_pedir_otra_direccion.setOnClickListener(this);
        cambiar_boton(true, false);
    }

    @Override
    public void onClick(View v) {
        if (R.id.pedir_movil_ahora == v.getId()) {
            Servicio_pedir_taxi hilo_taxi = new Servicio_pedir_taxi();
            SharedPreferences usuario = getSharedPreferences("perfil", MODE_PRIVATE);
            String id = usuario.getString("id_usuario", "");
            String nombre = usuario.getString("nombre", "");
            nombre = nombre + " " + usuario.getString("apellido", "");
            //dibuja en el mapa las taxi que estan cerca...
            //hilo_taxi.execute(getString(R.string.servidor)+"frmTaxi.php?opcion=get_taxis_en_rango", "1","64.455","-18.533");// parametro que recibe el doinbackground
            try {
                hilo_taxi.execute(getString(R.string.servidor) + "frmPedido.php?opcion=pedir_taxi", "7", id, String.valueOf(latitud), String.valueOf(longitud), nombre, indicacion.getText().toString());// parametro que recibe el doinbackground
            } catch (Exception e) {
                mensaje("Por favor active su GPS para realizar pedidos.");
            }


        } else if (R.id.cancelar_pedido == v.getId()) {
            Servicio_cancelar_taxi hilo_taxi = new Servicio_cancelar_taxi();
            SharedPreferences ultimo = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
            String id = ultimo.getString("id_pedido", "");
            try {
                hilo_taxi.execute(getString(R.string.servidor) + "frmPedido.php?opcion=cancelar_pedido", "1", id);// parametro que recibe el doinbackground
            } catch (Exception e) {
                mensaje("Tuvimos problemas al obtener tus datos.");
            }
        }
        else if(R.id.cb_pedir_otra_direccion==v.getId())
        {
            if(cb_pedir_otra_direccion.isChecked()==true) {
                startActivity(new Intent(this, Otra_direccion.class));
            }
        }

    }

    //verifica si llego la notificacion con el pedido aceptado..
    public void pedido_aceptado() {
        SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        if (pedido.getString("id_pedido", "").equals("") == false) {
            i = 100;
            startActivity(new Intent(this, Pedido_usuario.class));

            finish();
        }
    }

    public void progress_en_proceso() {

        i = 0;
        pro_pedido = new Thread(new Runnable() {
            @Override
            public void run() {
//1800 es 3 minutos.
                while (i < 110) {
                    i = i + 1;

                    handle.post(new Runnable() {
                        @Override
                        public void run() {
                            cargando.setProgress(i);
                        }
                    });
                    try {
                        if (i < 95) {
                            pedido_aceptado();
                        } else if (i == 107) {
                            verificar_pedido();
                        } else if (i > 108) {
                            ll_progress.setVisibility(View.INVISIBLE);
                        }

                        try {
                            Thread.sleep(1800);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
        pro_pedido.start();
    }

    public void verificar_pedido() {
        SharedPreferences prefe = getSharedPreferences("pedido_en_proceso", Context.MODE_PRIVATE);
        try {
            int id_pedido = Integer.parseInt(prefe.getString("id_pedido", ""));
            Servicio_pedir_taxi hilo_taxi = new Servicio_pedir_taxi();
            hilo_taxi.execute(getString(R.string.servidor) + "frmPedido.php?opcion=get_pedido_por_id_pedido", "2", String.valueOf(id_pedido));// parametro que recibe el doinbackground
        } catch (Exception e) {

        }
    }

    public class Servicio_pedir_taxi extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

if(pDialog.isShowing()==true) {
    //verificar si alguien aceptotu pedido..
    // verificar si tiene un pedido que aun no ha finalizado....
    //obtener datos del pedido en curso.....
    if (params[1] == "2") { //mandar JSON metodo post para login
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
                suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));

                if (suceso.getSuceso().equals("1")) {
                    JSONArray dato = respuestaJSON.getJSONArray("pedido");
                    String snombre = dato.getJSONObject(0).getString("nombre_taxi");
                    String scelular = dato.getJSONObject(0).getString("celular");
                    String sid_taxi = dato.getJSONObject(0).getString("id_taxi");
                    String smarca = dato.getJSONObject(0).getString("marca");
                    String splaca = dato.getJSONObject(0).getString("placa");
                    String scolor = dato.getJSONObject(0).getString("color");
                    String sid_pedido = dato.getJSONObject(0).getString("id_pedido");
                    SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                    SharedPreferences.Editor editar = pedido.edit();
                    editar.putString("nombre_taxi", snombre);
                    editar.putString("celular", scelular);
                    editar.putString("id_taxi", sid_taxi);
                    editar.putString("marca", smarca);
                    editar.putString("placa", splaca);
                    editar.putString("color", scolor);
                    editar.putString("latitud", dato.getJSONObject(0).getString("latitud"));
                    editar.putString("longitud", dato.getJSONObject(0).getString("longitud"));
                    editar.putString("id_pedido", sid_pedido);
                    editar.commit();

                    SharedPreferences ped = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
                    SharedPreferences.Editor editor = ped.edit();
                    editor.putString("id_pedido", "");
                    editor.commit();

                    devuelve = "8";
                } else {
                    devuelve = "9";
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

    //enviar pedir taxi..
    if (params[1] == "7") {
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
            jsonParam.put("latitud", params[3]);
            jsonParam.put("longitud", params[4]);
            jsonParam.put("nombre", params[5]);
            jsonParam.put("indicacion", params[6]);
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
                suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));
                if (suceso.getSuceso().equals("1")) {
                    SharedPreferences pedido = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pedido.edit();
                    editor.putString("id_pedido", respuestaJSON.getString("id_pedido"));
                    editor.commit();
                    devuelve = "3";
                } else {
                    SharedPreferences pedido = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pedido.edit();
                    editor.putString("id_pedido", "");
                    editor.commit();
                    devuelve = "5";
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
            pDialog = new ProgressDialog(Datos_de_pedido.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Se esta Realizando su pedido.");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);
            try {
                pDialog.dismiss();//ocultamos proggress dialog
            }catch (Exception e)
            {

            }

            if (s.equals("3")) {

                progress_en_proceso();
                cambiar_boton(false, true);
            } else if (s.equals("5") == true) {
                mensaje_error_2(suceso.getMensaje());

            } else if (s.equals("8") == true) {
                //verificar si alguien acepto el pedido.
                i = 100;
                Intent intent = new Intent(Datos_de_pedido.this, Servicio_pedido.class);
                intent.setAction(Constants.ACTION_RUN_ISERVICE);
                startService(intent);

                startActivity(new Intent(Datos_de_pedido.this, Pedido_usuario.class));
                finish();
            } else if (s.equals("9") == true) {
                mensaje_error_2("Porfavor vuelve a pedir tu Taxi.");
                cambiar_boton(true, false);
            } else {
                mensaje_error();
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

    private void cambiar_boton(boolean pedido, boolean cancelar) {

        ll_pedir.setLayoutParams(cero);
        ll_cancelar.setLayoutParams(cero);
        ll_progress.setVisibility(View.INVISIBLE);
        if (pedido == true) {
            ll_pedir.setLayoutParams(parent);
        } else if (cancelar == true) {
            ll_progress.setVisibility(View.VISIBLE);
            ll_cancelar.setLayoutParams(parent);
        }
    }

    public class Servicio_cancelar_taxi extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";


            //cancelar el pedido del taxi..
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
                        suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));
                        if (suceso.getSuceso().equals("1")) {
                            SharedPreferences pedido = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pedido.edit();
                            editor.putString("id_pedido", "");
                            editor.commit();

                            devuelve = "6";
                        } else {
                            devuelve = "5";
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
            pDialog = new ProgressDialog(Datos_de_pedido.this);
            pDialog.setTitle("Taxi Elitex");
            pDialog.setMessage("Cancelando el pedido.");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);
           try {
               pDialog.dismiss();//ocultamos proggress dialog
           }catch (Exception e)
           {

           }

            if (s.equals("5") == true) {
                Toast.makeText(Datos_de_pedido.this, suceso.getMensaje(), Toast.LENGTH_SHORT).show();
                verificar_pedido();
            } else if (s.equals("6") == true) {//cancelado correctamente..
                Toast.makeText(Datos_de_pedido.this, suceso.getMensaje(), Toast.LENGTH_SHORT).show();
                cambiar_boton(true, false);
                verificar_pedido();
                finish();
            } else {
               mensaje_error();
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


    public void mensaje(String mensaje) {
        Toast toast = Toast.makeText(this, mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public void mensaje_error()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Taxi Elitex");
        builder.setMessage("No pudimos conectarnos al servidor.\nVuelve a intentarlo.");
        builder.setPositiveButton("OK",null);
        builder.create();
        builder.show();
    }

    public void mensaje_error_2(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Taxi Elitex");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK",null);
        builder.create();
        builder.show();
    }

    public void obtener_direccion(double lat, double lon) {
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.

        } catch (IllegalArgumentException illegalArgumentException) {

        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            //error. o no tiene datos recolectados...
        } else {

// Funcion que determina si se obtuvo resultado o no

                // Creamos el objeto address
                Address address = addresses.get(0);

                // Creamos el string a partir del elemento direccion
               String direccionText = String.format("%s, %s, %s",
                       address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                       address.getFeatureName(),
                       address.getLocality());
            et_direccion.setText(direccionText);

          //  et_direccion.setText(address.getFeatureName()+" | "+address.getSubAdminArea ()+" | "+address.getSubLocality ()+" | "+address.getLocality ()+" | "+address.getSubLocality ()+" | "+address.getPremises ()+" | "+addresses.get(0).getThoroughfare()+" | "+address.getAddressLine(0));
        }
    }



}
