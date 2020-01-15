package com.elisoft.radiomovilclasico;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.notificaciones.SharedPrefManager;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Registrar_usuario_facebook extends AppCompatActivity implements View.OnClickListener{
    Button siguiente;
    Suceso suceso;
    ProgressDialog pDialog;
    EditText et_codigo,et_contrasenia1,et_contrasenia2;
    String celular,email,nombre,apellido;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario_facebook);



        siguiente=(Button)findViewById(R.id.siguiente);
        et_codigo = (EditText) findViewById(R.id.et_codigo);
        et_contrasenia1 = (EditText) findViewById(R.id.et_contrasenia1);
        et_contrasenia2 = (EditText) findViewById(R.id.et_contrasenia2);

        siguiente.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle =getIntent().getExtras();
        try{
            nombre=bundle.getString("nombre");
            celular=bundle.getString("celular");
            apellido=bundle.getString("apellido");
            email=bundle.getString("email");
        }catch (Exception e)
        {
            finish();
        }


    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.siguiente) {
            if (et_codigo.getText().toString().toUpperCase().trim().length() >= 6) {
                if (validar_contrasenia(et_contrasenia1.getText().toString().toUpperCase().trim(), et_contrasenia2.getText().toString().toUpperCase().trim())==true) {
                    final String token = SharedPrefManager.getInstance(this).getDeviceToken();
                    if (token != null || token == "") {
                        Servicio servicio = new Servicio();
                        servicio.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=registrar_usuario_autenticar", "1", nombre, apellido, celular, email, et_contrasenia1.getText().toString().toUpperCase(), token,et_codigo.getText().toString().toUpperCase().trim());// parametro que recibe el doinbackground

                    } else {

                        mensaje_error("No se a podido generar el Token. porfavor active sus datos de Red e instale Google Pay Service");
                    }

                } else {
                    mensaje("Contraseña incorrecta. \nLa contraseña debe ser mayor a 6 caracteres.");
                }
            }
            else
            {
                mensaje("Nombre de Usuario muy corto. \nIntente con mas caracteres.");
            }
        }
    }
    private void iniciar_login() {
        Intent intent=new Intent(this,Inicio.class);
        startActivity(intent);
        finish();
    }
    public void Registrate()
    {

    }



    public void mensaje(String mensaje)
    {
        Toast toast = Toast.makeText(this,mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public boolean validar_contrasenia(String contrasenia1, String contrasenia2)
    { boolean sw=false;
        if(contrasenia1.length()>6)
        {
            sw=true;
        }
        if((contrasenia1.equals(contrasenia2) && sw==true)){
            return true;
        }else
            return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        LoginManager.getInstance().logOut();
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
//Registrar usuario.
            if (params[1] == "1") {
                try {
                    HttpURLConnection urlConn;

                    DataOutputStream printout;
                    DataOutputStream input;

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
                    jsonParam.put("nombre", params[2]);
                    jsonParam.put("apellido", params[3]);
                    jsonParam.put("celular", params[4]);
                    jsonParam.put("email", params[5]);
                    jsonParam.put("contrasenia", params[6]);
                    jsonParam.put("token", params[7]);
                    jsonParam.put("codigo", params[8]);

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

                        //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        // StringBuilder pasando a cadena.                    }

                        SystemClock.sleep(950);

                        //Accedemos a vector de resultados.
                        String error = respuestaJSON.getString("suceso");// suceso es el campo en el Json
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));
                        if (error.equals("1")) {
                            String id=respuestaJSON.getString("id_usuario");
                            cargar_datos(id);
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
            pDialog = new ProgressDialog(Registrar_usuario_facebook.this);
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
                inicio_principal();
            } else if(s.equals("2")) {
                errorRegistro();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Falla en tu conexión a Internet.",
                        Toast.LENGTH_SHORT).show();
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

    public void cargar_datos(String id)
    {
        SharedPreferences usuario=getSharedPreferences("perfil",MODE_PRIVATE);
        SharedPreferences.Editor editar=usuario.edit();
        editar.putString("nombre",nombre);
        editar.putString("apellido",apellido);
        editar.putString("email",email);
        editar.putString("codigo",et_codigo.getText().toString().toUpperCase().trim());
        editar.putString("celular",celular);
        editar.putString("id_usuario",id);
        editar.putString("login_usuario","1");
        editar.commit();
    }

    private void inicio_principal() {
        SharedPreferences usuario=getSharedPreferences("perfil",MODE_PRIVATE);
        if(usuario.getString("id_usuario","").equals("")==false  && usuario.getString("id_usuario","").equals("null")==false) {


            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle(getString(R.string.app_name));
            dialogo1.setMessage("Muchas gracias por registrate en "+ Html.fromHtml("<b>"+getString(R.string.app_name)+"</b>")+".\n" +
                    "Ahora podés pedir tu Taxi  desde tu celular y el Taxi llegara donde tu estes.\nSin " +
                    "llamadas, sin moverte.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    saltar_principal();
                }
            });

            dialogo1.show();


        }
    }
    public void saltar_principal()
    {
        startActivity(new Intent(this, Menu_usuario.class));
    }
    private void errorRegistro() {
        mensaje_error(suceso.getMensaje());
        /*mensaje("Usuario Incorrecto.. puede intentar iniciar sesion con su cuenta de facebook.");
        finish();*/
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
