package com.elisoft.radiomovilclasico.tarifario;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.Suceso;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Calcular_Tarifa extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String[] direccion = {"",""};
    JSONObject rutas=null;
    LatLngBounds AUSTRALIA;
    double latitud_inicio,longitud_inicio,latitud_fin,longitud_fin;
    TextView tv_punto_inicio,tv_punto_fin,tv_;
    ProgressDialog pDialog;
    Suceso suceso;
    TextView tv_monto_distancia,tv_monto_tiempo,tv_tarifa_normal,tv_tarifa_de_lujo,tv_tarifa_con_aire,tv_tarifa_maletero,tv_tarifa_con_pedido, tv_tarifa_con_reserva,tv_tarifa_moto,tv_tarifa_moto_pedido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcular__tarifa);
        tv_punto_inicio=(TextView)findViewById(R.id.tv_punto_inicio);
        tv_punto_fin=(TextView)findViewById(R.id.tv_punto_final);
        tv_monto_distancia=(TextView)findViewById(R.id.tv_monto_distancia);
        tv_monto_tiempo=(TextView)findViewById(R.id.tv_monto_tiempo);
        tv_tarifa_normal=(TextView)findViewById(R.id.tv_tarifa_normal);
        tv_tarifa_de_lujo=(TextView)findViewById(R.id.tv_tarifa_de_lujo);
        tv_tarifa_con_aire=(TextView)findViewById(R.id.tv_tarifa_con_aire);
        tv_tarifa_maletero=(TextView)findViewById(R.id.tv_tarifa_maletero);
        tv_tarifa_con_pedido=(TextView)findViewById(R.id.tv_tarifa_con_pedido);
        tv_tarifa_con_reserva=(TextView)findViewById(R.id.tv_tarifa_con_reserva);
        tv_tarifa_moto=(TextView)findViewById(R.id.tv_tarifa_moto);
        tv_tarifa_moto_pedido=(TextView)findViewById(R.id.tv_tarifa_moto_pedido);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        try{
            Bundle bundle=getIntent().getExtras();
            latitud_inicio=bundle.getDouble("latitud_inicio");
            longitud_inicio=bundle.getDouble("longitud_inicio");
            latitud_fin=bundle.getDouble("latitud_fin");
            longitud_fin=bundle.getDouble("longitud_fin");
            marcar_ruta();

            Servicio servicio = new Servicio();
            servicio.execute(getString(R.string.servidor) + "frmCarrera.php?opcion=calcular_tarifa", "1", String.valueOf(latitud_inicio), String.valueOf(longitud_inicio),String.valueOf(latitud_fin),String.valueOf(longitud_fin));// parametro que recibe el doinbackground

        }catch (Exception e){

        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



                String direccion1 =obtener_direccion( latitud_inicio,longitud_inicio);
                String direccion2 =obtener_direccion( latitud_fin,longitud_fin);
                tv_punto_inicio.setText(String.valueOf(direccion1));
                tv_punto_fin.setText(String.valueOf(direccion2));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng inicio = new LatLng(latitud_inicio, longitud_inicio);
        LatLng fin = new LatLng(latitud_fin, longitud_fin);
        try {

            Marker marker1= this.mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_punto_inicio_1))
                    .anchor((float) 0.5, (float) 0.8)
                    .flat(true)
                    .position(inicio)
                    .title("Inicio"));

        } catch (Exception e) {
        }

        try {

            Marker marker= this.mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_punto_fin_1))
                    .anchor((float) 0.5, (float) 0.8)
                    .flat(true)
                    .position(fin)
                    .title("fin"));

        } catch (Exception e) {

        }
        marcar_ruta();





// Set the camera to the greatest possible zoom level that includes the
// bounds

        //agregaranimacion al mover la camara...
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(inicio)      // Sets the center of the map to Mountain View
                .zoom(12)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



    }






    public String obtener_direccion(double lat, double lon) {
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses = null;
        String s_direccion= "";

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


            if (addresses.size() > 0) {
                int cantidad=addresses.get(0).getMaxAddressLineIndex();
                for (int i = 0; i <= cantidad; i++)
                {
                    s_direccion+= addresses.get(0).getAddressLine(i) + ",";
                }
            }


            // Creamos el string a partir del elemento direccion
            String direccionText = String.format("%s, %s",
                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                    address.getFeatureName());
         }
        return s_direccion;
    }

    public void marcar_ruta( )
    {
        try{
            Servicio_taxi_ruta hilo = new Servicio_taxi_ruta();
            hilo.execute("https://maps.googleapis.com/maps/api/directions/json?origin=" + latitud_inicio + "," + longitud_inicio + "&destination=" + latitud_fin + "," + longitud_fin + "&mode=driving&key=AIzaSyB1h4N5nfpkF1Hg30P88c_1MvH9qG9Tcvs", "4");// parametro que recibe el doinbackground

        }catch (Exception e)
        {

        }
    }


    public class Servicio extends AsyncTask<String,Integer,String> {
         String metros,minuto,normal,de_lujo,con_aire,maletero,pedido,reserva,moto,moto_pedido;
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
                    jsonParam.put("latitud_inicio", params[2]);
                    jsonParam.put("longitud_inicio", params[3]);
                    jsonParam.put("latitud_fin", params[4]);
                    jsonParam.put("longitud_fin", params[5]);

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
                        String mensaje = respuestaJSON.getString("mensaje");// suceso es el campo en el Json
                        suceso=new Suceso(error,mensaje);

                        if (error.equals("1")) {

                            metros=respuestaJSON.getString("metros");
                            minuto=respuestaJSON.getString("minutos");
                            normal=respuestaJSON.getString("normal");
                            de_lujo=respuestaJSON.getString("de_lujo");
                            con_aire=respuestaJSON.getString("con_aire");
                            maletero=respuestaJSON.getString("maletero");
                            pedido=respuestaJSON.getString("pedido");
                            reserva=respuestaJSON.getString("reserva");
                            moto=respuestaJSON.getString("moto");
                            moto_pedido=respuestaJSON.getString("moto_pedido");



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
            pDialog = new ProgressDialog(Calcular_Tarifa.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Calculando la tarifa");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.cancel();//ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                int minuto1=Integer.parseInt(minuto);
                int distancia= Integer.parseInt(metros);
                int km=distancia/1000;
                int m=distancia%1000;
                if(distancia<1000)
                {
                    tv_monto_distancia.setText("Mt. "+ metros);
                }else{
                    tv_monto_distancia.setText("Km. "+ km+"."+m);
                }


                tv_monto_tiempo.setText("Hrs. "+ formatearMinutosAHoraMinuto(minuto1));
                tv_tarifa_normal.setText("Bs. "+ normal);
                tv_tarifa_de_lujo.setText("Bs. "+ de_lujo);
                tv_tarifa_con_aire.setText("Bs. "+ con_aire);
                tv_tarifa_maletero.setText("Bs. "+ maletero);
                tv_tarifa_con_pedido.setText("Bs. "+ pedido);
                tv_tarifa_con_reserva.setText("Bs. "+ reserva);
                tv_tarifa_moto.setText("Bs. "+moto);
                tv_tarifa_moto_pedido.setText("Bs. "+ moto_pedido);

            } else if(s.equals("2")) {
                mensaje_error(suceso.getMensaje());
            }
            else
            {
                mensaje_error("Falla en tu conexión a Internet.");
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


    public String formatearMinutosAHoraMinuto(int minutos) {
        String formato = "%02d:%02d";
        long horasReales = TimeUnit.MINUTES.toHours(minutos);
        long minutosReales = TimeUnit.MINUTES.toMinutes(minutos) - TimeUnit.HOURS.toMinutes(TimeUnit.MINUTES.toHours(minutos));
        return String.format(formato, horasReales, minutosReales);
    }

    // comenzar el servicio con el motista....
    public class Servicio_taxi_ruta extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

            //Obtener el camino mas corto. para llegar mas rapido ..
            if (params[1] == "4") {
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



                        rutas= new JSONObject(result.toString());//Creo un JSONObject a partir del

                        devuelve="7";
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
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Log.e("respuesta del servidor=", "" + s);
            if(s.equals("7"))
            {
                dibujar_ruta(rutas);
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

    public void mensaje_error(String mensaje)
    {




        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage(mensaje);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                finish();
            }
        });

        dialogo1.show();




    }

    public void dibujar_ruta(JSONObject jObject){

        String tiempo="";
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        LatLng punto=new LatLng(0,0);
        PolylineOptions polylineOptions = new PolylineOptions();

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            double lat=((LatLng)list.get(l)).latitude;
                            double lon=((LatLng)list.get(l)).longitude;
                            punto = new LatLng(lat, lon);
                            polylineOptions.add(punto);

                        }
                    }

                    tiempo=(String)((JSONObject)((JSONObject)jLegs.get(j)).get("duration")).get("text");
                }
            }




            mMap.addPolyline(polylineOptions.width(8).color(Color.BLACK));

        } catch (JSONException e) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            dibujar_ruta(rutas);
            e.printStackTrace();
        }catch (Exception e){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            dibujar_ruta(rutas);
        }

    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


}
