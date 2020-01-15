package com.elisoft.radiomovilclasico;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

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

/**
 * Un {@link IntentService} que simula un proceso en primer plano
 * <p>
 */

public class Servicio_pedido extends IntentService {
    Suceso suceso;
    String sid_pedido,sid_pedido2,sdetalle="";
    int estado=0;
    String monto_total="0",distancia="0";
    boolean sw_monto_total=false;
    boolean sw_condulta_monto=true;
    private static final String TAG = Servicio_pedido.class.getSimpleName();


    public Servicio_pedido() {
        super("Servicio_pedido");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Constants.ACTION_RUN_ISERVICE.equals(action)) {
                handleActionRun();
            }
        }
    }

    /**
     * Maneja la acción de ejecución del servicio
     */
    private void handleActionRun() {
        try {

            SharedPreferences prefe=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);

            sid_pedido=prefe.getString("id_pedido","0");

            // Bucle de simulación de pedido cuando tiene estado del pedido=0
            for (int i = 1; estado==0 || estado==1 && sid_pedido.equals("0")==false; i++) {
                    if(estaConectado()) {
                        Servicio_punto servicio_punto = new Servicio_punto();
                        servicio_punto.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=obtener_ubicacion_por_id_pedido", "1", sid_pedido);
                        // parametro que recibe el doinbackground
                        //Log.d(TAG, i + ""); // Logueo
                        // Retardo de 1 segundo en la iteración
                        Thread.sleep(2000);
                    }else
                    {
                        Thread.sleep(1000);
                    }

            }


            sw_monto_total=true;
            if(estado==2 && sw_condulta_monto==true) {
                sw_condulta_monto=false;
                sid_pedido2=sid_pedido;
                do{
                    if(estaConectado()) {
                        Servicio_monto_total servicio_monto_total = new Servicio_monto_total();
                        servicio_monto_total.execute(getString(R.string.servidor) + "frmPedido.php?opcion=monto_total_por_id_pedido", "1", sid_pedido2);
                    }    Thread.sleep(1000);
                }while (sw_monto_total==true);

            }else
            {
                sw_monto_total=false;
            }

            do{
                Thread.sleep(1000);
            }while (sw_monto_total==true);
            // Quitar de primer plano
            stopForeground(true);
            // si nuestro estado esta en 2 o mayor .. quiere decir que no nuestro pedido se finalizo o sino se cancelo... sin nninguna carrera...
            if(estado>1)
            {
                stopService(new Intent(this,Servicio_pedido.class));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {


        SharedPreferences pro=getSharedPreferences("pedido_en_proceso",MODE_PRIVATE);
        SharedPreferences.Editor editar=pro.edit();
        editar.putString("id_pedido","");
        editar.commit();

        SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
        SharedPreferences.Editor edit=pedido.edit();
        edit.putString("id_pedido","");
        edit.commit();


        if(estado==2) {

            Toast.makeText(this, "Pedido Finalizado.", Toast.LENGTH_LONG).show();
            Intent dialogIntent = new Intent(getApplicationContext(), Pedido_finalizado.class);
            dialogIntent.putExtra("monto_total", monto_total);
            dialogIntent.putExtra("distancia", distancia);
            dialogIntent.putExtra("id_pedido", sid_pedido2);
            dialogIntent.putExtra("detalle", sdetalle);
            dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogIntent);
            sid_pedido2="";
        }


        // Emisión para avisar que se terminó el servicio
        Intent localIntent = new Intent(Constants.ACTION_PROGRESS_EXIT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }


    // comenzar el servicio. para obtener el punto de la ultima ubicacion..
    public class Servicio_punto extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
//obtener datos del pedido en curso.....
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


                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
                            JSONArray dato = respuestaJSON.getJSONArray("punto");
                            if (dato.getString(0).toString().length() > 8) {
                                double latitud = Double.parseDouble(dato.getJSONObject(0).getString("latitud"));
                                double longitud = Double.parseDouble(dato.getJSONObject(0).getString("longitud"));
                                int rotacion = Integer.parseInt(dato.getJSONObject(0).getString("rotacion"));
                                int clase_vehiculo=Integer.parseInt(dato.getJSONObject(0).getString("clase_vehiculo"));
                                estado= Integer.parseInt(dato.getJSONObject(0).getString("estado"));

                                cargar_puntos_en_tabla(latitud, longitud,rotacion,clase_vehiculo);
                                devuelve = "1";
                            } else
                            {
                                devuelve = "2";
                            }


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

            //obtener el estado del pedido......
            if (params[1] == "3") { //mandar JSON metodo post para login
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
                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));
                        if (suceso.getSuceso().equals("1")) {
                            estado= Integer.parseInt(respuestaJSON.getString("estado"));
                            devuelve="5";
                        }
                        else  {
                            devuelve = "6";
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

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("onPostExcute=", "" + s);
            if(s.equals("1"))
            {
                SharedPreferences prefe=getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=prefe.edit();
                editor.putString("punto_optenido","true");
                editor.commit();

            }
            else
            if(s.equals("4"))
            {
                if(estaConectado()) {
                    Servicio_punto servicio_estado = new Servicio_punto();
                    servicio_estado.execute(getString(R.string.servidor) + "frmPedido.php?opcion=get_estado_pedido", "3", sid_pedido);
                    // parametro que recibe el doinbackground
                }
            }
            else
            {
                SharedPreferences prefe=getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=prefe.edit();
                editor.putString("punto_optenido","false");
                editor.commit();
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

    public class Servicio_monto_total extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
//obtener datos del pedido en curso.....
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


                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
                            monto_total=respuestaJSON.getString("monto_total");
                            distancia=respuestaJSON.getString("distancia");
                            try{
                                sdetalle=respuestaJSON.getString("detalle");
                            }catch (Exception e)
                            {

                            }

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

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("onPostExcute=", "" + s);
            if(s.equals("1"))
            {

            }
            else
            {

            }
            sw_monto_total=false;
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

    private void cargar_puntos_en_tabla(double latitud,double longitud,int rotacion,int clase_vehiculo) {
try {
    SharedPreferences punto_pedido=getSharedPreferences("punto taxi",MODE_PRIVATE);
    SharedPreferences.Editor editor=punto_pedido.edit();
    editor.putString("latitud", String.valueOf(latitud));
    editor.putString("longitud", String.valueOf(longitud));
    editor.putString("rotacion", String.valueOf(rotacion));
    editor.putInt("clase_vehiculo", clase_vehiculo);
    editor.commit();
/*
    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
            "taxicorp", null, 1);
    SQLiteDatabase bd = admin.getWritableDatabase();
    ContentValues registro = new ContentValues();
    registro.put("id_pedido", sid_pedido);
    registro.put("latitud", String.valueOf(latitud));
    registro.put("longitud", String.valueOf(longitud));
    bd.insert("puntos_pedido", null, registro);
    bd.close();*/
}catch (Exception e)
{

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

    //VERIFICAR SI ESTA CON CONEXION WIFI
    protected Boolean conectadoWifi(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
    //VERIFICAR SI ESTA CON CONEXION DE DATOS
    protected Boolean conectadoRedMovil(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Boolean estaConectado(){
        if(conectadoWifi()){
            return true;
        }else{
            if(conectadoRedMovil()){
                return true;
            }else{
                return false;
            }
        }
    }
}

