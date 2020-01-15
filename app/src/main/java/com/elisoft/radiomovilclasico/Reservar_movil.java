package com.elisoft.radiomovilclasico;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

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
import java.util.Calendar;

public class Reservar_movil extends AppCompatActivity {

    private static final int Date_id = 0;
    Button bt_cancelar,bt_pedir,bt_fecha,bt_hora;
    EditText et_referencia;
    String fecha="";
    Calendar c_hoy;
    int dia,mes,anio,hora,minuto;
    CheckBox cb_tipo_pedido_empresa;

    Double latitud,longitud;
    Suceso suceso;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_movil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // get prompts.xml view


        bt_cancelar = (Button) findViewById(R.id.bt_cancelar);
        bt_pedir = (Button) findViewById(R.id.bt_pedir);
        bt_fecha = (Button) findViewById(R.id.bt_fecha);
        bt_hora = (Button) findViewById(R.id.bt_hora);
        et_referencia = (EditText) findViewById(R.id.et_referencia);
        cb_tipo_pedido_empresa= (CheckBox)findViewById(R.id.cb_tipo_pedido_empresa);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Calendar c = Calendar.getInstance();
        c_hoy = Calendar.getInstance();
        anio = c.get(Calendar.YEAR);
        mes = c.get(Calendar.MONTH)+1;
        dia = c.get(Calendar.DAY_OF_MONTH);
        hora = c.get(Calendar.HOUR_OF_DAY);
        minuto = c.get(Calendar.MINUTE)+5;

        bt_fecha.setText(dia+ "  de  " + mes + "  del " + anio);
        bt_hora.setText(hora+":"+minuto);

        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bt_pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int tipo_pedido_empresa=0;
                if(cb_tipo_pedido_empresa.isChecked()){
                    tipo_pedido_empresa=1;
                }
                String fecha_reserva=anio+"-"+mes+"-"+dia+" "+hora+":"+minuto+":00";

                if(verificar_fecha_aceptable( dia, mes,  anio)==true&&verificar_hora_aceptable(hora,minuto)==true){
                    SharedPreferences usuario = getSharedPreferences("perfil", MODE_PRIVATE);
                    String id = usuario.getString("id_usuario", "");
                    String imei = usuario.getString("imei", "");
                    String nombre = usuario.getString("nombre", "");
                    nombre = nombre + " " + usuario.getString("apellido", "");
                    Servicio_pedir_taxi hilo_pedir_taxi=new Servicio_pedir_taxi();
                    hilo_pedir_taxi.execute(getString(R.string.servidor) + "frmPedido.php?opcion=reservar_movil", "7", id,
                            String.valueOf(latitud),
                            String.valueOf(longitud),
                            nombre,
                            et_referencia.getText().toString().trim(),
                            "0",
                            imei,String.valueOf(1),String.valueOf(tipo_pedido_empresa),fecha_reserva);// parametro que recibe el doinbackground
                }else {
                    mensaje_de_fecha_incalida();
                }


            }
        });
        bt_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ver_fecha();
            }
        });

        bt_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ver_hora();
            }
        });

        try{
            Bundle bundle=getIntent().getExtras();
            latitud=bundle.getDouble("latitud",0);
            longitud=bundle.getDouble("longitud",0);
            if(latitud==0 && longitud==0)
            {
                finish();
            }
        }catch (Exception e){
            finish();
        }
    }

    private void ver_fecha() {
        // Date picker dialog
        DatePickerDialog  date_listener = new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                // store the data in one string and set it to text


                if(verificar_fecha_aceptable( day,  month+1,  year)==true){
                    dia=day;
                    mes=month+1;
                    anio=year;
                    ver_hora();
                }else
                {
                    mensaje_de_fecha_incalida();
                }
                bt_fecha.setText(dia+ "  de  " + mes+"  del  "+anio );



            }
        },anio,mes-1,dia);
        date_listener.show();
    }


    private void ver_hora() {
        // Date picker dialog
        TimePickerDialog date_listener = new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int horas, int minutos) {
                // store the data in one string and set it to text


                if(verificar_hora_aceptable( horas,minutos)==true)
                {
                    hora=horas;
                    minuto=minutos;
                }else{
                    mensaje_de_fecha_incalida();
                }


                bt_hora.setText( hora+ " : " + minuto);
            }
        },hora,minuto+8,true);
        date_listener.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public boolean verificar_fecha_aceptable(int dia, int mes, int anio){
        boolean sw=false;
        Calendar c = Calendar.getInstance();
        int anio_actual = c.get(Calendar.YEAR);
        int mes_actual = c.get(Calendar.MONTH)+1;
        int dia_actual = c.get(Calendar.DAY_OF_MONTH);
        if(anio==anio_actual){
            if(mes==mes_actual){
                if(dia>=dia_actual){
                    sw=true;
                }
            }else if(mes>mes_actual){
                sw=true;
            }
        }
        else if(anio>=anio_actual){
            sw=true;
        }
        return sw;
    }

    public boolean verificar_hora_aceptable(int hora, int minuto){
        boolean sw=false;
        Calendar c = Calendar.getInstance();
        int anio_actual = c.get(Calendar.YEAR);
        int mes_actual = c.get(Calendar.MONTH)+1;
        int dia_actual = c.get(Calendar.DAY_OF_MONTH);
        int hora_actual = c.get(Calendar.HOUR_OF_DAY);
        int minuto_actual = c.get(Calendar.MINUTE);
        if(dia==dia_actual && mes==mes_actual && anio==anio_actual){
             if(hora_actual<hora){
                sw=true;
            }
        }else{
            sw=true;
        }

        return sw;
    }

    public void mensaje_de_fecha_incalida()
    {
        try {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Tiempo de recogida no valida");
            dialogo1.setMessage("Por favor, asegurese de que el tiempo de recogida es después de al menos una hora a partir de ahora.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    ver_fecha();
                }
            });
            dialogo1.show();
        }catch (Exception e){
        }

    }





    public class Servicio_pedir_taxi extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

                //enviar pedir taxi..
                if (params[1] == "7") {
                    if(!isCancelled()) {
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
                            jsonParam.put("numero_casa", params[7]);
                            jsonParam.put("imei", params[8]);
                            jsonParam.put("clase_vehiculo", params[9]);
                            jsonParam.put("tipo_pedido_empresa", params[10]);
                            jsonParam.put("fecha_reserva", params[11]);
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

                                SystemClock.sleep(1500);
                                JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                                suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));
                                if (suceso.getSuceso().equals("1")) {
                                    devuelve = "3";
                                } else
                                {

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
            pDialog = new ProgressDialog(Reservar_movil.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Reservando movil.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.cancel();//ocultamos proggress dialog


            if (s.equals("3")) {
                mensaje_error_final(suceso.getMensaje());
            } else if (s.equals("5") == true) {
                mensaje_error(suceso.getMensaje());
            }
            else {
                mensaje_error_final("Falla en tu conexión a Internet.Si esta seguro que tiene conexión a internet.Actualice la aplicación.");
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

    public void mensaje_error_final(String mensaje)
    {   try {
            AlertDialog.Builder builder = new AlertDialog.Builder(Reservar_movil.this);
            builder.setTitle(getString(R.string.app_name));
            builder.setCancelable(false);
            builder.setMessage(mensaje);
            builder.create();
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    finish();
                }
            });
            builder.show();

    }catch (Exception e)
    {   Log.e("mensaje_error",e.toString());
    }

    }





}
