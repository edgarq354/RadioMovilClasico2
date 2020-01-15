package com.elisoft.radiomovilclasico.perfil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.Suceso;
import com.elisoft.radiomovilclasico.informacion.Pagina;

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

public class Billetera extends AppCompatActivity implements  View.OnClickListener{


    TextView tv_monto,tv_como_funciona;
    EditText et_codigo;
    Button bt_agregar;

    Suceso suceso;
    ProgressDialog pDialog;

    @Override
    protected void onRestart() {
        actualizar_billetera();
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billetera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_monto=(TextView)findViewById(R.id.tv_monto);
        tv_como_funciona=(TextView)findViewById(R.id.tv_como_funciona);
        et_codigo=(EditText)findViewById(R.id.et_codigo);
        bt_agregar=(Button)findViewById(R.id.bt_agregar);

        bt_agregar.setOnClickListener(this);
        tv_como_funciona.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actualizar_billetera();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.bt_agregar:
                SharedPreferences sperfil=getSharedPreferences("perfil",MODE_PRIVATE);
                Servicio hilo = new Servicio();
                hilo.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=get_cargar_codigo", "1",sperfil.getString("id_usuario",""),et_codigo.getText().toString().trim());// parametro que recibe el doinbackground

                break;
            case R.id.tv_como_funciona:
                Intent intent=new Intent(this, Pagina.class);
                intent.putExtra("titulo","¿Cómo funciona?");
                intent.putExtra("url",getString(R.string.servidor)+"como_funciona.php");
                startActivity(intent);
                break;
        }
    }


    public class Servicio extends AsyncTask<String,Integer,String> {
String s_monto="";

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";



//enviar codigo para aumentar a billetera
            if (params[1] == "1") { //mandar JSON metodo post ENVIAR LA CONFIRMACION DEL LLEGADO DEL MOTISTA.,,,
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
                    jsonParam.put("codigo", params[3]);


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


            if (params[1] == "2") {
                //actualizar billetera
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
                        if (suceso.getSuceso().equals("1")) {
                            devuelve="3";
                            s_monto=respuestaJSON.getString("monto");
                        } else  {
                            devuelve = "4";
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
            pDialog = new ProgressDialog(Billetera.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Autenticando ....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();//ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);
            if(s.equals("1"))
            {
                mensaje(suceso.getMensaje());
                actualizar_billetera();
            }
            else if(s.equals("2"))
            {
                mensaje(suceso.getMensaje());
                actualizar_billetera();
            }else  if(s.equals("3")){
                tv_monto.setText("BOB "+s_monto);
            }else if(s.equals("4"))
            {
                tv_monto.setText("BOB 0.0");
            }
            else
            {

                mensaje_error("No pudimos conectarnos al servidor.\nVuelve a intentarlo.");
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


    private void actualizar_billetera() {
        SharedPreferences sperfil=getSharedPreferences("perfil",MODE_PRIVATE);
        Servicio hilo = new Servicio();
        hilo.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=get_monto_billetera", "2",sperfil.getString("id_usuario",""));// parametro que recibe el doinbackground

    }

    public void mensaje(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
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
