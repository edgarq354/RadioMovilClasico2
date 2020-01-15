package com.elisoft.radiomovilclasico.perfil;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.Suceso;

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

public class Modificar_contrasenia extends AppCompatActivity implements View.OnClickListener {
    EditText contrasenia_antigua, nueva_contrasenia, repetir_contrasenia;
    Button confirmar;

    Suceso suceso;
    ProgressDialog pDialog;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_contrasenia);
        contrasenia_antigua = (EditText) findViewById(R.id.contrasenia_antigua);
        nueva_contrasenia = (EditText) findViewById(R.id.nueva_contrasenia);
        repetir_contrasenia = (EditText) findViewById(R.id.confirmar_contrasenia);
        confirmar = (Button) findViewById(R.id.confirmar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        confirmar.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirmar) {
            if (nueva_contrasenia.getText().toString().equals(repetir_contrasenia.getText().toString())) {
                if (nueva_contrasenia.getText().toString().trim().length() >= 6) {
                    Servicio servicio = new Servicio();
                    SharedPreferences perfil = getSharedPreferences("perfil", MODE_PRIVATE);
                    servicio.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=actualizar_contrasenia", "1", perfil.getString("id_usuario", ""), contrasenia_antigua.getText().toString(), nueva_contrasenia.getText().toString());
                } else {
                    mensaje_error("La constraseña es muy corta.");
                }
            } else {
                mensaje_error("No coincide tus contraseña.");
                nueva_contrasenia.setText("");
                repetir_contrasenia.setText("");
                contrasenia_antigua.setText("");
            }
        }
    }


    // comenzar el servicio con el motista....
    public class Servicio extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";


//Enviamos un correo m¡para confirmar la modificacion de su cuenta..
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
                    jsonParam.put("antigua", params[3]);
                    jsonParam.put("nueva", params[4]);


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

            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog
            pDialog = new ProgressDialog(Modificar_contrasenia.this);
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
            if (s.equals("1")) {
                nueva_contrasenia.setText("");
                repetir_contrasenia.setText("");
                contrasenia_antigua.setText("");
                mensaje_error_final(suceso.getMensaje());
            } else if (s.equals("2")) {
                mensaje_error(suceso.getMensaje());
                nueva_contrasenia.setText("");
                repetir_contrasenia.setText("");
                contrasenia_antigua.setText("");
            } else {
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

    public void mensaje_error(String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }
    public void mensaje_error_final(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setCancelable(false);
        builder.create();
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                finish();
            }
        });
        builder.show();
    }

}
