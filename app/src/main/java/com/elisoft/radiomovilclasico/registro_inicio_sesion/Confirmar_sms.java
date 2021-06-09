package com.elisoft.radiomovilclasico.registro_inicio_sesion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.Menu_usuario;
import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.Registrar_nombre_completo;
import com.elisoft.radiomovilclasico.Suceso;
import com.elisoft.radiomovilclasico.notificaciones.SharedPrefManager;


import org.json.JSONArray;
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


public class Confirmar_sms extends AppCompatActivity implements View.OnClickListener {
    private String celular;
    Button codeInputButton;
    TextView enviar_mensaje;
    EditText inputCode;
    TextView mensaje;
    String token="",imei="";
    JSONArray perfil;
    Suceso suceso;
    ProgressDialog pDialog;


    private static final String TAG = "VerificationActivity";

    private boolean mShouldFallback = true;
    private static final String[] SMS_PERMISSIONS = { android.Manifest.permission.INTERNET,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.ACCESS_NETWORK_STATE };
    ProgressBar cargando;
    Handler handle=new Handler();

    int i=0;
    private boolean mIsSmsVerification;
    public static final String SMS = "sms";
    private String mPhoneNumber;
    TextView tv_tiempo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_sms);
        mensaje=(TextView)findViewById(R.id.mensaje);
        codeInputButton=(Button)findViewById(R.id.codeInputButton);
        inputCode=(EditText)findViewById(R.id.inputCode);
        cargando=(ProgressBar)findViewById(R.id.cargando);
        enviar_mensaje=(TextView)findViewById(R.id.enviar_mensaje);
        tv_tiempo=(TextView)findViewById(R.id.tv_tiempo);


        inputCode.addTextChangedListener(new TextWatcher() {

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
                if (verificar_codigo(s)) {
                    codeInputButton.setEnabled(true);
                    inputCode.setTextColor(Color.BLACK);
                } else {
                    codeInputButton.setEnabled(false);
                    inputCode.setTextColor(Color.RED);
                }
            }
        });


        try{
            Bundle bundle=getIntent().getExtras();
            celular=""+bundle.getString("celular");
            mPhoneNumber = "+591"+celular;
            final String method ="sms";
            mIsSmsVerification = method.equalsIgnoreCase("sms");
            progress_en_proceso();
            enviar_sms();
            TextView tv_titulo=(TextView)findViewById(R.id.tv_titulo);
            tv_titulo.setText("Verificar "+mPhoneNumber);

        }catch (Exception e)
        {
            finish();
        }


        codeInputButton.setOnClickListener(this);
        enviar_mensaje.setOnClickListener(this);




    }

    private boolean verificar_codigo(CharSequence s) {
        boolean sw=false;
        try{

            if(s.toString().trim().toString().length()>=4)
            {
                sw=true;
            }
        }catch (Exception e)
        {
            sw=false;
        }
        return sw;
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.codeInputButton) {
            verificar_codigo();

            /*
            token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
            if (token != null && token != "") {

                Servicio hilo_cargar = new Servicio();
                hilo_cargar.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=iniciar_sesion_con_celular", "1", celular, token);// parametro que recibe el doinbackground
            }
            else
            {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getApplicationContext());
                dialogo1.setTitle("Vamos a verificar el número de telefono");
                dialogo1.setMessage("No tiene token de acceso.  \n por favor vuelva a intentar mas tarde. \n para generar el Token ncesita tener instalado el Google Play Service.");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                    }
                });
                dialogo1.show();
            }
            */
        }
        else if(v.getId()==R.id.enviar_mensaje)
        {
            enviar_mensaje.setVisibility(View.INVISIBLE);
            progress_en_proceso();

            enviar_sms();
        }
    }



    //USUARIO
    public void verificar_codigo()
    {
        token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();



        if (token != null && token != "") {

            Servicio hilo_cargar = new Servicio();
            hilo_cargar.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=iniciar_sesion_con_celular", "1", celular, token,imei,inputCode.getText().toString().trim());// parametro que recibe el doinbackground
        }
        else
        {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Confirmar_sms.this);
            dialogo1.setTitle("Vamos a verificar el número de telefono");
            dialogo1.setMessage("No tiene token de acceso.  \n por favor vuelva a intentar mas tarde. \n para generar el Token ncesita tener instalado el Google Play Service.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {

                }
            });
            dialogo1.show();
        }
    }

    public void enviar_sms()
    {
        Servicio_mensaje hilo_moto = new Servicio_mensaje();
        hilo_moto.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=enviar_sms", "1",celular);// parametro que recibe el doinbackground

    }


    //USUARIO
    public void obtener_codigo()
    {
        try {

            Uri smsUri = Uri.parse("content://sms/inbox");
            Cursor cursor = getContentResolver().query(smsUri, null, null, null, null);
             /* Moving To First */
            if (!cursor.moveToFirst()) { /* false = cursor is empty */
                return;
            }
            for (int k = 0; k < cursor.getColumnCount() && !cursor.getString(2).equals("+46769446575"); k++) {
                cursor.moveToNext();
            }
            if (cursor.getString(2).equals("+46769446575")) {
                inputCode.setText(obtener_codigo(cursor.getString(12)));
            }
            cursor.close();
        }catch (Exception e)
        {

        }
    }
    //USUARIO
    public String obtener_codigo(String texto)
    {String codigo="";
        for (int i=0;i<texto.length();i++)
        {
            if(es_numero(String.valueOf(texto.charAt(i))))
            {
                codigo+=texto.charAt(i);
            }
        }
        return codigo;
    }
    //USUARIO
    public boolean es_numero(String numero)
    {
        try{
            Long.parseLong(numero);
        }catch (Exception e)
        {
            return false;
        }
        return true;
    }

    //USUARIO
    public  void progress_en_proceso()
    {

        i=0;
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (i<59)
                {
                    i=i+1;

                    handle.post(new Runnable() {
                        @Override
                        public void run() {
                            cargando.setProgress(i);

                            if(i==60)
                            {
                                tv_tiempo.setText("01:00");
                            }
                            else
                            {
                                tv_tiempo.setText("00:"+i);
                            }

                            if( i==10||i==30||i==50)
                            {
                                obtener_codigo();

                            }
                            if(i>=55)
                            {
                                enviar_mensaje.setVisibility(View.VISIBLE);
                            }

                        }
                    });
                    try{

                        Thread.sleep(1000);
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }
    //USUARIO
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }






    //servicio de verificar si ya esta registrado el cellular.
    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
//Iniciar sesion
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
                    jsonParam.put("celular", params[2]);
                    jsonParam.put("token", params[3]);
                    jsonParam.put("imei", params[4]);
                    jsonParam.put("codigo", params[5]);

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
                            JSONArray dato=respuestaJSON.getJSONArray("perfil");
                            String snombre= dato.getJSONObject(0).getString("nombre");
                            String sapellido=dato.getJSONObject(0).getString("apellido") ;
                            String semail= dato.getJSONObject(0).getString("correo") ;
                            String scelular= dato.getJSONObject(0).getString("celular") ;
                            String sid= dato.getJSONObject(0).getString("id") ;
                            String scodigo= dato.getJSONObject(0).getString("codigo") ;
                            cargar_datos(snombre,sapellido,semail,scelular,sid,scodigo);

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
            try{
                pDialog = new ProgressDialog(Confirmar_sms.this);
                pDialog.setTitle(getString(R.string.app_name));
                pDialog.setMessage("Verificando el Codigo");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }catch (Exception e)
            {}
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                pDialog.dismiss();//ocultamos proggress dialog
            }catch (Exception e){}
            // Log.e("onPostExcute=", "" + s);

            if (suceso.getSuceso().equals("1")) {
                iniciar_sesion();
            }else if(suceso.getSuceso().equals("3")) {
                Intent registrar=new Intent(getApplicationContext(),Registrar_nombre_completo.class);
                registrar.putExtra("celular",celular);
                startActivity(registrar);
                finish();
            }else if(suceso.getSuceso().equals("2")) {

                mensaje_error(suceso.getMensaje());
            }
            else
            {
                mensaje("Falla en tu conexión a Internet.");
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

    public void cargar_datos(String nombre, String apellido, String email, String celular, String id, String codigo)
    {
        SharedPreferences usuario=getSharedPreferences("perfil",MODE_PRIVATE);
        SharedPreferences.Editor editar=usuario.edit();
        editar.putString("nombre",nombre);
        editar.putString("apellido",apellido);
        editar.putString("email",email);
        editar.putString("celular",celular);
        editar.putString("id_usuario",id);
        editar.putString("codigo",codigo);
        editar.putString("imei",imei);
        editar.putString("login_usuario","1");
        editar.commit();
    }
    public void saltar_principal()
    {
        startActivity(new Intent(this, Menu_usuario.class));
        finish();
    }
    private void iniciar_sesion() {
        SharedPreferences usuario=getSharedPreferences("perfil",MODE_PRIVATE);
        if(usuario.getString("id_usuario","").equals("")==false  && usuario.getString("id_usuario","").equals("null")==false) {

            saltar_principal();

        }
    }
    public void mensaje(String mensaje)
    {
        Toast toast = Toast.makeText(this,mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }



    public class Servicio_mensaje extends AsyncTask<String,Integer,String> {
        //para el usuario

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "-1";
            if(pDialog.isShowing()) {//borre el ==true
                devuelve = "";
                if (params[1] == "1") { //mandar JSON metodo post para login
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

                            //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                            JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                            // StringBuilder pasando a cadena.                    }

                            SystemClock.sleep(950);

                            //Accedemos a vector de resultados.
                            suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));// suceso es el campo en el Json
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
            }
            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog

            //para el progres Dialog
            pDialog = new ProgressDialog(Confirmar_sms.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Enviando sms de verificación.");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);
            pDialog.cancel();//ocultamos proggress dialog

            if (s.equals("1")) {

                obtener_codigo();
            }
            else if(s.equals("2"))
            {
                mensaje_error(suceso.getMensaje());
            }
            else if(s.equals(""))
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

    public void mensaje_error(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }




}



