package com.elisoft.radiomovilclasico;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.notificaciones.SharedPrefManager;
import com.facebook.AccessToken;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registrar_nombre extends AppCompatActivity implements View.OnClickListener {

    Button siguiente;
    Suceso suceso;
    EditText celular,et_codigo,et_contrasenia1,et_contrasenia2;
    String email;
    String nombre,apellido,id_facebook;

    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_nombre);

        if (AccessToken.getCurrentAccessToken() == null) {
            iniciar_login();
        }

        siguiente = (Button) findViewById(R.id.siguiente);
        et_codigo = (EditText) findViewById(R.id.et_codigo);
        et_contrasenia1 = (EditText) findViewById(R.id.et_contrasenia1);
        et_contrasenia2 = (EditText) findViewById(R.id.et_contrasenia2);
        celular = (EditText) findViewById(R.id.celular);

        siguiente.setOnClickListener(this);

        getSupportActionBar().hide();

        Bundle bundle = getIntent().getExtras();
        try {
            String nombre_completo=bundle.getString("name");
            nombre=obtener_nombre(nombre_completo);
            apellido=obtener_apellido(nombre_completo);
            email = bundle.getString("email");
            id_facebook = bundle.getString("id_facebook");
        } catch (Exception e) {
            finish();
        }


        celular.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (verificar_celular(s)) {
                    siguiente.setEnabled(true);
                    celular.setTextColor(Color.BLACK);
                } else {
                    siguiente.setEnabled(false);
                    celular.setTextColor(Color.RED);
                }
            }
        });
    }

    private void iniciar_login() {
        finish();
    }

    public String obtener_nombre(String nombre)
    {int contador=0;
        String nom="";try{
        for (int i=0;i<nombre.length();i++)
        {
           if(nombre.charAt(i)==(int)32)
           {
               contador++;
           }
        }

        if(contador==1)
        {

            for (int i=0;i<nombre.length();i++)
             {
                 if(nombre.charAt(i)==(int)32)
                {
                    nom=nombre.substring(0,i);
                    i=nombre.length();
                }
            }
        }
        else if(contador>=2)
        {int c=0;
            for (int i=nombre.length()-1;i>0;i--)
            {
                if(nombre.charAt(i)==(int)32)
                {
                   c++;
                }
                if(c==2)
                {
                    nom=nombre.substring(0,i);
                    i=0;
                }
            }
        }
        else
        {
            nom=nombre;
        }
    }
    catch (Exception e)
    {
        nom=nombre;
    }
        return nom;
    }

    public String obtener_apellido(String nombre)
    {int contador=0;
        String ape="";
        try {
            for (int i = 0; i < nombre.length(); i++) {
                if (nombre.charAt(i) == (int) 32) {
                    contador++;
                }
            }

            if (contador == 1) {

                for (int i = 0; i < nombre.length(); i++) {
                    if (nombre.charAt(i) == (int) 32) {
                        ape = nombre.substring(i + 1, nombre.length());
                        i = nombre.length();
                    }
                }
            } else if (contador >= 2) {
                int c = 0;
                for (int i=nombre.length()-1;i>0;i--) {
                    if (nombre.charAt(i) == (int) 32) {
                        c++;
                    }
                    if (c == 2) {
                        ape = nombre.substring(i + 1, nombre.length());
                        i = 0;
                    }
                }
            } else {
                ape = nombre;
            }
        }catch (Exception e)
        {
            ape=nombre;
        }
        return ape;
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.siguiente)
        {

            if( nombre.toString().trim().length()>=3  && apellido.toString().trim().length()>=3) {
                if ( verificar_celular(celular.getText().toString().trim())) {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle("Vamos a verificar el número de telefono");
                    dialogo1.setMessage("" + celular.getText().toString().trim() + " \n¿Es Correcto este número o quieres modificarlo?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            //cargamos los datos.
                            registrar_usuario();
                        }
                    });
                    dialogo1.setNegativeButton("EDITAR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {

                        }
                    });
                    dialogo1.show();

                }
                else{

                  mensaje_error("Número Invalido.\n" +
                          "Por favor ingrese un numero valido.");
                }
            }
            else
            {
                mensaje("Por Favor acepte los terminos de privacidad de Facebook.");
                finish();
            }
        }
    }

    public void registrar_usuario()
    {
        validacion();
    }

    private boolean verificar_celular(CharSequence s) {
        boolean sw=false;
        try{
            int numero= Integer.parseInt(s.toString());
            if(numero>=60000000 && numero<=79999999)
            {
                sw=true;
            }
        }catch (Exception e)
        {
            sw=false;
        }
        return sw;
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

    public boolean validar_contrasenia(String contrasenia1, String contrasenia2)
    { boolean sw=false;
        if(contrasenia1.length()>6&&contrasenia2.length()>6 )
        {
            sw=true;
        }

        return (contrasenia1.equals(contrasenia2) && sw==true);
    }

    private void errorRegistro() {
        mensaje_error(suceso.getMensaje());
        /*mensaje("Usuario Incorrecto.. puede intentar iniciar sesion con su cuenta de facebook.");
        finish();*/
    }

    public void mensaje(String mensaje)
    {
        Toast toast = Toast.makeText(this,mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
    public void validacion()
    {
        if(et_codigo.getText().toString().trim().length()>5) {
            if (validar_contrasenia(et_contrasenia1.getText().toString().trim(), et_contrasenia2.getText().toString().trim())) {
                try {
                    int numero = Integer.parseInt(celular.getText().toString());

                    final String token = SharedPrefManager.getInstance(this).getDeviceToken();
                    if (token != null || token == "") {
                        if (numero >= 60000000 && numero <= 79999999) {

                            Servicio servicio = new Servicio();
                            servicio.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=registrar_usuario_facebook", "1", nombre, apellido, celular.getText().toString(), email, et_contrasenia1.getText().toString(), token,et_codigo.getText().toString().trim(),id_facebook);// parametro que recibe el doinbackground

                        } else {

                            mensaje_error("Número Invalido.\n" +
                                    "Por favor ingrese un numero valido.");
                        }
                    } else {

                        mensaje_error("No se a podido generar el Token. porfavor active sus datos de Red e instale Google Pay Service");
                    }
                } catch (Exception e) {

                }
            } else {
                mensaje("Contraseña incorrecta. \nLa contraseña debe ser mayor a 6 caracteres.");
            }
        }else
        {
            mensaje("Por favor ingrese su USUARIO.\nMayor a 6 caracteres.");
        }
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
                    jsonParam.put("id_facebook", params[9]);

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
            pDialog = new ProgressDialog(Registrar_nombre.this);
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
        editar.putString("codigo",et_codigo.getText().toString().trim());
        editar.putString("celular",celular.getText().toString());
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
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

}
