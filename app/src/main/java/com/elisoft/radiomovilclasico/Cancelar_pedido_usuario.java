package com.elisoft.radiomovilclasico;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

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

public class Cancelar_pedido_usuario extends AppCompatActivity implements View.OnClickListener {

    Button bt_cancelar;
    EditText et_otro;
    RadioButton rb_uno,rb_dos,rb_tres,rb_cuatro,rb_cinco,rb_otro;
    Suceso suceso;
    ProgressDialog pDialog;
    int id_pedido=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelar_pedido_usuario);
        bt_cancelar=(Button)findViewById(R.id.bt_cancelar);
        rb_uno=(RadioButton)findViewById(R.id.rb_uno);
        rb_dos=(RadioButton)findViewById(R.id.rb_dos);
        rb_tres=(RadioButton)findViewById(R.id.rb_tres);
        rb_cuatro=(RadioButton)findViewById(R.id.rb_cuatro);
        rb_cinco=(RadioButton)findViewById(R.id.rb_cinco);
        rb_otro=(RadioButton)findViewById(R.id.rb_otro);
        et_otro=(EditText)findViewById(R.id.et_otro);
        bt_cancelar.setOnClickListener(this);
        try{
            Bundle bundle=getIntent().getExtras();
            id_pedido= Integer.parseInt(bundle.getString("id_pedido"));
        }catch (Exception e)
        {
            finish();
        }

    }

    public  int getDistancia(double lat_a,double lon_a, double lat_b, double lon_b){
        long  Radius = 6371000;
        double dLat = Math.toRadians(lat_b-lat_a);
        double dLon = Math.toRadians(lon_b-lon_a);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) * Math.sin(dLon /2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return (int) (Radius * c);
    }

    @Override
    public void onClick(View view) {

        if(R.id.bt_cancelar==view.getId()) {
            String detalle = et_otro.getText().toString();
            if (rb_uno.isChecked()) {
                detalle = rb_uno.getText().toString().trim();
            } else if (rb_dos.isChecked()) {
                detalle = rb_dos.getText().toString().trim();
            } else if (rb_tres.isChecked()) {
                detalle = rb_tres.getText().toString().trim();
            } else if (rb_cuatro.isChecked()) {
                detalle = rb_cuatro.getText().toString().trim();
            } else if (rb_cinco.isChecked()) {
                detalle = rb_cinco.getText().toString().trim();
            } else if(rb_otro.isChecked()){
                detalle=et_otro.getText().toString().trim();
            }
            SharedPreferences perfil = getSharedPreferences("perfil", Context.MODE_PRIVATE);
            Servicio_taxi hilo = new Servicio_taxi();
             hilo.execute(getString(R.string.servidor) + "frmPedido.php?opcion=detalle_cancelar_pedido_usuario", "1", perfil.getString("id_usuario", ""), String.valueOf(id_pedido),detalle);
        }
    }



    // comenzar el servicio con el motista....
    public class Servicio_taxi extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
         //CANCELAREL PEDIDO....
            if (params[1] == "1") { //mandar JSON metodo post ENVIAR LA CONFIRMACION LA CANCELACIONN DE PEDIDO.,,,
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
                    jsonParam.put("id_pedido", params[3]);
                    jsonParam.put("detalle", params[4]);


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
            pDialog = new ProgressDialog(Cancelar_pedido_usuario.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Cargando detalle");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();//ocultamos proggress dialog
            // Log.e("respuesta del servidor=", "" + s);
           finish();



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
        builder.setTitle("Taxi ELitex");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }
    private void eliminar_pedido() {

        SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        SharedPreferences.Editor editar=pedido.edit();
        editar.putString("nombre_taxi", "");
        editar.putString("celular", "");
        editar.putString("marca", "");
        editar.putString("placa", "");
        editar.putString("color", "");
        editar.putString("id_pedido", "");
        editar.commit();
    }

    public void mensaje(String mensaje)
    {
        Toast toast = Toast.makeText(this,mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }



}
