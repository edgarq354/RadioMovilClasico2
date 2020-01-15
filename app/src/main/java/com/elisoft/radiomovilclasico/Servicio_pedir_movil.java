package com.elisoft.radiomovilclasico;

import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

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

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Servicio_pedir_movil extends IntentService {
    public static final String ACTION_PROGRESO="servicio_pedido_proceso";
    public static final String ACTION_FINAL="servicio_pedido_finalizo";
   int  id_pedido=0;
   int estado_pedido=0;
   Suceso suceso;
   Intent padre;
    private static final String TAG = Servicio_pedido.class.getSimpleName();


    public Servicio_pedir_movil( ) {

        super("Servicio_pedir_movil");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            id_pedido= intent.getIntExtra("id_pedido", 0);
            final String action = intent.getAction();
            handleActionRun();
            padre=intent;

        }
    }

    /**
     * Maneja la acción de ejecución del servicio
     */
    private void handleActionRun() {
        try {
            int i=0;

            int tiempo=0;
            int tiempo_notificacion=0;
            int diametro_maximo=2000;
            int diametro_minimo=1000;

            do{

                tiempo++;
                if(tiempo%4==0 )
                {
                    if(diametro_maximo==2000)
                    {
                        diametro_minimo=1000;
                        verificar_pedido( diametro_minimo,  diametro_maximo,  1);
                        diametro_maximo=5000;
                    }else if(diametro_maximo==5000)
                    {
                        diametro_minimo=3000;
                        verificar_pedido( diametro_minimo,  diametro_maximo,  1);
                        diametro_maximo+=1000;
                    }else{
                        verificar_pedido( diametro_minimo,  diametro_maximo,  0);
                    }
                }

               // /Comunicamos el progreso
                Intent bcIntent = new Intent();
                bcIntent.setAction(ACTION_PROGRESO);
                bcIntent.putExtra("id_pedido", id_pedido);
                bcIntent.putExtra("estado", estado_pedido);
                sendBroadcast(bcIntent);

                SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                try{
                    id_pedido=Integer.parseInt(pedido.getString("id_pedido","0"));

                }catch (Exception e){
                    try{
                        SharedPreferences prefe = getSharedPreferences("pedido_en_proceso", Context.MODE_PRIVATE);
                        id_pedido = Integer.parseInt(prefe.getString("id_pedido", "0"));
                    }catch (Exception ee){
                       id_pedido=0;
                    }

                }

                tareaLarga();
            }while( tiempo<60 &&  estado_pedido==0 && id_pedido!=0);


            if(estado_pedido==1)
            {
                SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                Intent bcIntent = new Intent();
                bcIntent.setAction(ACTION_FINAL);
                bcIntent.putExtra("estado", 1);
                sendBroadcast(bcIntent);
                Intent numero = new Intent(this, Pedido_usuario.class);
                numero.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                numero.putExtra("id_pedido",pedido.getString("id_pedido",""));
                numero.putExtra("latitud",Double.parseDouble(pedido.getString("latitud","0")));
                numero.putExtra("longitud",Double.parseDouble(pedido.getString("longitud","0")));
                startActivity(numero);
            }else {
                try {
                    Servicio_pedir_cancelar hilo_taxi_cancelar=new Servicio_pedir_cancelar();
                    SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                    SharedPreferences pedido_proceso=getSharedPreferences("pedido_en_proceso",MODE_PRIVATE);
                    SharedPreferences usuario = getSharedPreferences("perfil", MODE_PRIVATE);
                    String id = usuario.getString("id_usuario", "");
                    //dibuja en el mapa las taxi que estan cerca...
                    //hilo_taxi.execute(getString(R.string.servidor)+"frmTaxi.php?opcion=get_taxis_en_rango", "1","64.455","-18.533");// parametro que recibe el doinbackground
                    int id_pedido=0;
                    try {
                        id_pedido=Integer.parseInt(pedido.getString("id_pedido","0"));
                    }catch (Exception e){
                        try {
                            id_pedido=Integer.parseInt(pedido_proceso.getString("id_pedido","0"));
                        }catch (Exception ee){
                            id_pedido=0;
                        }
                    }
                    if(id_pedido!=0) {
                        try {
                            hilo_taxi_cancelar = new Servicio_pedir_cancelar();
                            hilo_taxi_cancelar.execute(getString(R.string.servidor) + "frmPedido.php?opcion=cancelar_pedido_usuario", "1", id,String.valueOf(id_pedido));// parametro que recibe el doinbackground
                        } catch (Exception e) {
                            saltar_menu_principal();
                        }
                    }else
                    {
                        saltar_menu_principal();
                    }



                }catch (Exception e)
                {
                    Log.e("mensaje_error",e.toString());
                    saltar_menu_principal();
                }



            }
            // Quitar de primer plano
            stopForeground(true);
            // si nuestro estado esta en 2 o mayor .. quiere decir que no nuestro pedido se finalizo o sino se cancelo... sin nninguna carrera...

            stopService(new Intent(this,Servicio_pedir_movil.class));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void  saltar_menu_principal()
    {
        Intent bcIntent = new Intent();
        bcIntent.setAction(ACTION_FINAL);
        bcIntent.putExtra("estado", 1);
        sendBroadcast(bcIntent);
        Intent numero = new Intent(this, Menu_usuario.class);
        numero.putExtra("finalizo_solicitud_pedido",1);
        numero.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(numero);
    }

    private void tareaLarga()
    {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {}
    }

    @Override
    public void onDestroy() {
        Log.e("servicio notificacion:","Servicio destruido...");

        // Emisión para avisar que se terminó el servicio
        Intent localIntent = new Intent(Constants.ACTION_PROGRESS_EXIT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    public void verificar_pedido(int diametro_minimo, int diametro_maximo, int enviar_notificacion) {
        try {
            Servicio_pedir_taxi hilo_taxi_obtener_dato=new Servicio_pedir_taxi();
            hilo_taxi_obtener_dato.execute(getString(R.string.servidor) + "frmPedido.php?opcion=verificar_si_acepto_pedido_2", "2", String.valueOf(id_pedido),String.valueOf(diametro_minimo),String.valueOf(diametro_maximo),String.valueOf(enviar_notificacion));
        } catch (Exception e) {
        }
    }


    public class Servicio_pedir_taxi extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";


            //verificar si alguien aceptotu pedido..
            // verificar si tiene un pedido que aun no ha finalizado....
            //obtener datos del pedido en curso.....

                if (params[1] == "2") { //mandar JSON metodo post para login
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
                            jsonParam.put("id_pedido", params[2]);
                            jsonParam.put("diametro_minimo", params[3]);
                            jsonParam.put("diametro_maximo", params[4]);
                            jsonParam.put("enviar_notificacion", params[5]);

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
                                    String sempresa=dato.getJSONObject(0).getString("empresa");
                                    String s_id_empresa=dato.getJSONObject(0).getString("id_empresa");
                                    String scalificacion_conductor = dato.getJSONObject(0).getString("calificacion_conductor");
                                    String scalificacion_vehiculo = dato.getJSONObject(0).getString("calificacion_vehiculo");

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
                                    editar.putString("abordo", dato.getJSONObject(0).getString("abordo"));
                                    editar.putString("estado", dato.getJSONObject(0).getString("estado"));
                                    editar.putString("id_pedido", sid_pedido);
                                    editar.putString("calificacion_conductor", scalificacion_conductor);
                                    editar.putString("calificacion_vehiculo", scalificacion_vehiculo);
                                    editar.putString("empresa",sempresa);
                                    editar.putString("id_empresa",s_id_empresa);
                                    editar.commit();

                                    SharedPreferences proceso = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = proceso.edit();
                                    editor.putString("id_pedido", "");
                                    editor.commit();
                                    estado_pedido=1;


                                    devuelve = "8";
                                } else if (suceso.getSuceso().equals("2")) {
                                    devuelve = "9";


                                } else {
                                    devuelve = "10";
                                    estado_pedido=3;
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
            //   tv_mensaje_pedido.setText("Buscando el Taxi mas Próximo.");

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);
             if (s.equals("8") == true) {
                //verificar si alguien acepto el pedido.
                 SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                 Intent servicio_contacto = new Intent(Servicio_pedir_movil.this, Servicio_guardar_contacto.class);
                 servicio_contacto.setAction(Constants.ACTION_RUN_ISERVICE);
                 servicio_contacto.putExtra("nombre",pedido.getString("nombre_taxi", ""));
                 servicio_contacto.putExtra("telefono",pedido.getString("celular", ""));
                 startService(servicio_contacto);
            } else if (s.equals("9") == true) {


            }
            else {

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



    public class Servicio_pedir_cancelar extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";


            //verificar si alguien aceptotu pedido..
            // verificar si tiene un pedido que aun no ha finalizado....
            //obtener datos del pedido en curso.....


            //2: CANCELAR EL PEDIDO
            if (params[1] == "1") {
                if(!isCancelled()){
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
                        jsonParam.put("id_pedido", params[3]);
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
                                SharedPreferences pedido = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pedido.edit();
                                editor.putString("id_pedido", "");
                                editor.commit();

                                devuelve = "6";
                            } else

                            {

                                devuelve = "7";
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
            //   tv_mensaje_pedido.setText("Buscando el Taxi mas Próximo.");

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);
            if(s.equals("500")==true)
            {

            }else if(s.equals("6"))
            {
                SharedPreferences pedido2 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = pedido2.edit();
                editor2.putString("id_pedido", "");
                editor2.putString("estado", "4");
                editor2.commit();



            }else if(s.equals("7"))
            {

            }
            else {

            }

            saltar_menu_principal();

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

