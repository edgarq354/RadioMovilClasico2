package com.elisoft.radiomovilclasico.menu_otra_direccion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.Pedido_usuario;
import com.elisoft.radiomovilclasico.perfil.Perfil_pasajero;
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

public class Calcular_tarifa_confirmar extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {


    private GoogleMap mMap;
    String[] direccion = {"",""};
    JSONObject rutas=null;
    LatLngBounds AUSTRALIA;
    double latitud_inicio,longitud_inicio,latitud_fin,longitud_fin;
    TextView tv_punto_inicio,tv_punto_fin;
    ProgressDialog pDialog;
    Suceso suceso;

    CheckBox cb_tipo_pedido_empresa;

    TextView tv_monto_distancia,tv_monto_tiempo,tv_tarifa_de_lujo,tv_tarifa_con_aire,tv_tarifa_maletero,tv_tarifa_con_pedido, tv_tarifa_con_reserva,tv_tarifa_moto,tv_tarifa_moto_pedido;
    EditText tv_tarifa_normal;
    TextView tv_billetera;


    int cantidad_solicitud_tarifa=0;
    int cantidad_solicitud_ruta=0;

    double monto_normal=0,monto_billetera=0;


    LocationManager manager = null;
    AlertDialog alert = null;

    LinearLayout pedi_taxi,pedir_movil_lujo,pedir_movil_maletero,pedir_movil_aire,pedir_movil_pedido, pedir_movil_reserva, buscar_direccion,pedir_moto;


    boolean sw_ver_taxi_cerca = false;
    Servicio_ver_movil hilo_m;
    private JSONArray puntos_taxi;


    Marker marker=null;
    Marker marker1=null;

    double lat_1=0,lon_1=0;

    AlertDialog alert2 = null;

    SharedPreferences mis_datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_calcular_tarifa_confirmar);
        tv_punto_inicio=(TextView)findViewById(R.id.tv_punto_inicio);
        tv_punto_fin=(TextView)findViewById(R.id.tv_punto_final);
        tv_monto_distancia=(TextView)findViewById(R.id.tv_monto_distancia);
        tv_monto_tiempo=(TextView)findViewById(R.id.tv_monto_tiempo);
        tv_tarifa_normal=(EditText)findViewById(R.id.tv_tarifa_normal);
        tv_tarifa_de_lujo=(TextView)findViewById(R.id.tv_tarifa_de_lujo);
        tv_tarifa_con_aire=(TextView)findViewById(R.id.tv_tarifa_con_aire);
        tv_tarifa_maletero=(TextView)findViewById(R.id.tv_tarifa_maletero);
        tv_tarifa_con_pedido=(TextView)findViewById(R.id.tv_tarifa_con_pedido);
        tv_tarifa_con_reserva=(TextView)findViewById(R.id.tv_tarifa_con_reserva);
        tv_tarifa_moto=(TextView)findViewById(R.id.tv_tarifa_moto);
        tv_tarifa_moto_pedido=(TextView)findViewById(R.id.tv_tarifa_moto_pedido);
        tv_billetera=(TextView)findViewById(R.id.tv_billetera);

        cb_tipo_pedido_empresa=(CheckBox)findViewById(R.id.cb_tipo_pedido_empresa);





        pedi_taxi = (LinearLayout) findViewById(R.id.pedir_movil);
        pedir_movil_aire = (LinearLayout) findViewById(R.id.pedir_movil_aire);
        pedir_movil_lujo = (LinearLayout) findViewById(R.id.pedir_movil_lujo);
        pedir_movil_maletero = (LinearLayout) findViewById(R.id.pedir_movil_maletero);
        pedir_movil_pedido = (LinearLayout) findViewById(R.id.pedir_movil_pedido);
        pedir_movil_reserva = (LinearLayout) findViewById(R.id.pedir_movil_reserva);
        pedir_moto = (LinearLayout)findViewById(R.id.pedir_moto);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        try{
            Bundle bundle=getIntent().getExtras();
            latitud_inicio=bundle.getDouble("latitud_inicio");
            longitud_inicio=bundle.getDouble("longitud_inicio");
            latitud_fin=bundle.getDouble("latitud_fin");
            longitud_fin=bundle.getDouble("longitud_fin");

            if(latitud_inicio!=latitud_fin && longitud_inicio!=longitud_fin)
            {
                marcar_ruta();
            }
            

            solicitar_tarifa();
        }catch (Exception e){

        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pedi_taxi.setOnClickListener(this);
        pedir_movil_lujo.setOnClickListener(this);
        pedir_movil_aire.setOnClickListener(this);
        pedir_movil_maletero.setOnClickListener(this);
        pedir_movil_pedido.setOnClickListener(this);
        pedir_movil_reserva.setOnClickListener(this);
        pedir_moto.setOnClickListener(this);
        cb_tipo_pedido_empresa.setOnClickListener(this);


        hilo_m = new Servicio_ver_movil();

        actualizar_billetera();
        direccion[0] =obtener_direccion( latitud_inicio,longitud_inicio);
        direccion[1] =obtener_direccion( latitud_fin,longitud_fin);
        tv_punto_inicio.setText(String.valueOf(direccion[0]));
        tv_punto_fin.setText(String.valueOf(direccion[1]));

        mis_datos=getSharedPreferences(getString(R.string.mis_datos),MODE_PRIVATE);

    }

    public void solicitar_tarifa()
    {
        Servicio servicio = new Servicio();
        servicio.execute(getString(R.string.servidor) + "frmCarrera.php?opcion=calcular_tarifa", "1", String.valueOf(latitud_inicio), String.valueOf(longitud_inicio), String.valueOf(latitud_fin), String.valueOf(longitud_fin));// parametro que recibe el doinbackground

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

            marker1= this.mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_punto_inicio_1))
                    .anchor((float) 0.5, (float) 0.8)
                    .flat(true)
                    .position(inicio)
                    .title(direccion[0]));
            marker1.showInfoWindow();


        } catch (Exception e) {
        }

        try {

            marker= this.mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_punto_fin_1))
                    .anchor((float) 0.5, (float) 0.8)
                    .flat(true)
                    .position(fin)
                    .title(direccion[1]));
            marker.showInfoWindow();

        } catch (Exception e) {

        }
        ver_moviles();
        marcar_ruta();





// Set the camera to the greatest possible zoom level that includes the
// bounds

        //agregaranimacion al mover la camara...
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(inicio)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.cb_tipo_pedido_empresa:
                actualizar_billetera();
                if(cb_tipo_pedido_empresa.isChecked()==true)
                {
                    double descuento=0;

                    if(monto_normal>monto_billetera)
                    {
                    descuento=monto_normal-monto_billetera;
                    }
                    tv_tarifa_normal.setText(descuento+"");

                }else
                {
                    tv_tarifa_normal.setText(monto_normal+"");
                }

                break;
            case R.id.pedir_movil:
                int montoTarifa=0;
                //solicita un movil de cualquier caracteristica.

                    if(existe_celular()==true)
                    {

                        try{
                            montoTarifa= Integer.parseInt(tv_tarifa_normal.getText().toString());
                        }catch (Exception e){
                            montoTarifa=0;
                        }

                        try{
                            lat_1=latitud_inicio;
                            lon_1=longitud_inicio;
                        }catch (Exception e){
                            lat_1=0;
                            lon_1=0;
                        }
                        if(montoTarifa>0){
                            if(lat_1!=0&& lon_1!=0){
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                                pedir_taxi("0", "Solicitando un movil",1,0,montoTarifa);
                                //escribir_referencia(1,0,"Solicitando un movil",montoTarifa);
                            }else{
                                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                                dialogo1.setTitle("Atención");
                                dialogo1.setMessage("Por favor geolocalice su ubicación.");
                                dialogo1.setCancelable(false);
                                dialogo1.setPositiveButton("OK", null);
                                dialogo1.show();
                            }
                        }else{
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                            dialogo1.setTitle("Atención");
                            dialogo1.setMessage("Ofrezca un monto de tarifa.");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("OK", null);
                            dialogo1.show();
                        }

                    } else{
                        actualizar_perfil();
                    }

                break;


            case R.id.pedir_movil_aire:

                    if(existe_celular()==true)
                    {


                        try{
                            lat_1=latitud_inicio;
                            lon_1=longitud_inicio;
                        }catch (Exception e){
                            lat_1=0;
                            lon_1=0;
                        }
                        if(lat_1!=0&& lon_1!=0){
                            escribir_referencia(3,0,"Solicitando un movil con aire acondicionado",0);
                        }else{
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                            dialogo1.setTitle("Atención");
                            dialogo1.setMessage("Por favor geolocalice su ubicación.");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("OK", null);
                            dialogo1.show();
                        }


                    } else{
                        actualizar_perfil();
                    }


                break;

            case R.id.pedir_movil_maletero:

                    if(existe_celular()==true)
                    {
                        try{
                            lat_1=latitud_inicio;
                            lon_1=longitud_inicio;
                        }catch (Exception e){
                            lat_1=0;
                            lon_1=0;
                        }
                        if(lat_1!=0&& lon_1!=0){
                            escribir_referencia(4,0,"Solicitando un movil con maletero",0);
                        }else{
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                            dialogo1.setTitle("Atención");
                            dialogo1.setMessage("Por favor geolocalice su ubicación.");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("OK", null);
                            dialogo1.show();
                        }

                    } else {
                        actualizar_perfil();
                    }

                break;
        }
    }


    public void  escribir_referencia(final int clase_vehiculo,final int tipo_pedido_empresa,String tipo_pedido_texto,int montoTarifa)
    {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Calcular_tarifa_confirmar.this);
        View promptView = layoutInflater.inflate(R.layout.escribir_referencia, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Calcular_tarifa_confirmar.this);
        alertDialogBuilder.setView(promptView);

        final Button bt_cancelar= (Button) promptView.findViewById(R.id.bt_cancelar);
        final Button bt_pedir= (Button) promptView.findViewById(R.id.bt_pedir);
        final EditText et_referencia= (EditText) promptView.findViewById(R.id.et_referencia);
        final TextView tv_tipo_pedido=(TextView)promptView.findViewById(R.id.tv_tipo_pedido);
        tv_tipo_pedido.setText(tipo_pedido_texto);

        et_referencia.setText(mis_datos.getString("referencia",""));


        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                alert2.cancel();
            }
        });

        bt_pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editar=mis_datos.edit();
                editar.putString("referencia",et_referencia.getText().toString());
                editar.commit();

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                pedir_taxi("0", et_referencia.getText().toString().trim(),clase_vehiculo,tipo_pedido_empresa,0);
                alert2.cancel();

            }
        });
        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();
    }

    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    public void pedir_taxi(String numero, String referencia, int clase_vehiculo, int tipo_pedido_empresa,int montoTarifa){
        ///verifica si el GPS esta activo.
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertNoGps();
        }
        String billetera="0";
        if(cb_tipo_pedido_empresa.isChecked()==true)
        {
            billetera="1";
        }


            if (existe_celular() == true) {

                if (referencia.length() >= 0) {
                    Intent datos_pedido = new Intent(this, Pedido_usuario.class);
                    datos_pedido.putExtra("latitud", latitud_inicio);
                    datos_pedido.putExtra("longitud", longitud_inicio);
                    datos_pedido.putExtra("latitud_final", latitud_fin);
                    datos_pedido.putExtra("longitud_final", longitud_fin);
                    datos_pedido.putExtra("referencia", referencia);
                    datos_pedido.putExtra("numero",numero);
                    datos_pedido.putExtra("clase_vehiculo",clase_vehiculo);
                    datos_pedido.putExtra("direccion",tv_punto_inicio.getText().toString());
                    datos_pedido.putExtra("direccion_final",tv_punto_fin.getText().toString());
                    datos_pedido.putExtra("tipo_pedido_empresa",tipo_pedido_empresa);
                    datos_pedido.putExtra("estado_billetera",billetera);
                    datos_pedido.putExtra("monto_tarifa",montoTarifa);
                    startActivity(datos_pedido);
                } else {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle("Atención");
                    dialogo1.setMessage("Por favor introduzca una referencia para ayudar al conductor a ubicarlo.");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("OK", null);
                    dialogo1.show();
                }

            } else {
                actualizar_perfil();
            }



    }

    private void actualizar_perfil() {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle(getString(R.string.app_name));
        dialogo1.setMessage("Por favor Ingrese su número del Telefono movil para que podamos identificarte.");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                startActivity(new Intent(getApplicationContext(),Perfil_pasajero.class));
            }
        });

        dialogo1.show();
    }

    public boolean existe_celular() {
        boolean sw = true;
        SharedPreferences perfil = getSharedPreferences("perfil", MODE_PRIVATE);
        if (perfil.getString("celular", "").equals("") == true  || perfil.getString("celular", "").toString().length()<7 ) {
            sw = false;
        }
        return sw;
    }


    public class Servicio extends AsyncTask<String,Integer,String> {
        String metros,minuto,normal,de_lujo,con_aire,maletero,parrilla,pedido,reserva,moto,moto_pedido,viru,expreso,hora;
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
            pDialog = new ProgressDialog(Calcular_tarifa_confirmar.this);
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
                int minuto1= Integer.parseInt(minuto);
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
                
                tv_monto_tiempo.setText("Hrs. "+ formatearMinutosAHoraMinuto(minuto1));
                tv_tarifa_normal.setText(  normal+"");
                tv_tarifa_de_lujo.setText("Bs. "+ de_lujo);
                tv_tarifa_con_aire.setText("Bs. "+ con_aire);
                tv_tarifa_maletero.setText("Bs. "+ maletero);
                tv_tarifa_con_pedido.setText("Bs. "+ pedido);
                tv_tarifa_con_reserva.setText("Bs. "+ reserva);
                tv_tarifa_moto.setText("Bs. "+moto);
                tv_tarifa_moto_pedido.setText("Bs. "+ moto_pedido);

                monto_normal=Double.parseDouble(normal);

            } else if(s.equals("2")) {
                if(cantidad_solicitud_tarifa<3)
                {
                    solicitar_tarifa();
                }else{
                    mensaje_error(suceso.getMensaje());
                }
                cantidad_solicitud_tarifa++;

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

                if(cantidad_solicitud_ruta<3){
                    marcar_ruta();
                }else{
                    mensaje_error("No pudimos conectarnos al servidor.\nVuelve a intentarlo.");
                }
                cantidad_solicitud_ruta++;

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
        Servicio_billetera hilo = new Servicio_billetera();
        hilo.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=get_monto_billetera", "1",sperfil.getString("id_usuario",""));// parametro que recibe el doinbackground

    }

//servicio para verificar la billetera
    public class Servicio_billetera extends AsyncTask<String,Integer,String> {
        String s_monto="";

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";





            if (params[1] == "1") {
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
                            devuelve="1";
                            s_monto=respuestaJSON.getString("monto");
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

            // Log.e("onPostExcute=", "" + s);
            if( s.equals("1"))
            {
               monto_billetera=Double.parseDouble(s_monto);


            }
            else if(s.equals("2"))
            {
            }
            tv_billetera.setText(monto_billetera+" BOB");

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


    private void ver_moviles() {
        SharedPreferences ult=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
        //si no tiene pédidos se le va a mostrar en el mapa....
        if(ult.getString("id_pedido","").equals("")==true) {
            try {

                if(sw_ver_taxi_cerca==false) {
                    sw_ver_taxi_cerca=true;
                    hilo_m=new Servicio_ver_movil();
                    hilo_m.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=get_taxi_en_rango", "1", String.valueOf(latitud_inicio), String.valueOf(longitud_inicio));// parametro que recibe el doinbackground*/
                }
            }catch (Exception e)
            {

                sw_ver_taxi_cerca=false;
                ver_moviles();
            }
        }else
        {
            try {
                int id_pedido= Integer.parseInt(ult.getString("id_pedido",""));
                Servicio hilo_taxi = new  Servicio();
                hilo_taxi.execute(getString(R.string.servidor) + "frmPedido.php?opcion=get_pedido_por_id_pedido", "5", String.valueOf(id_pedido));// parametro que recibe el doinbackground
            }catch (Exception e)
            {
                sw_ver_taxi_cerca=false;
                ver_moviles();
            }
        }
    }




    //servicio para ver los moviles
    public class Servicio_ver_movil extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
// busca taxi dentro de su rango
            if(!isCancelled()) {
                if (params[1] == "1") {
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
                        jsonParam.put("latitud", params[2]);
                        jsonParam.put("longitud", params[3]);
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
                                puntos_taxi = respuestaJSON.getJSONArray("taxi");
                                devuelve = "1";
                            } else {
                                devuelve = "20";
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
            }else{
                devuelve="500";
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
            //ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                //  mMap.clear();
                sw_ver_taxi_cerca=false;
                agregar_en_mapa_ubicaciones_de_taxi();

            }else if(s.equals("500")){

            }
            else
            {
                sw_ver_taxi_cerca=false;
                ver_moviles();

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


    public void agregar_en_mapa_ubicaciones_de_taxi() {
        try {
            for (int i = 0; i < puntos_taxi.length(); i++) {
                int rotacion = Integer.parseInt(puntos_taxi.getJSONObject(i).getString("rotacion"));
                double lat = Double.parseDouble(puntos_taxi.getJSONObject(i).getString("latitud"));
                double lon = Double.parseDouble(puntos_taxi.getJSONObject(i).getString("longitud"));
                String distancia= puntos_taxi.getJSONObject(i).getString("distancia");

                int moto= Integer.parseInt(puntos_taxi.getJSONObject(i).getString("moto"));
                if(moto==0){
                    cargar_puntos_movil(lat, lon,rotacion,distancia);
                }

            }
        } catch (Exception e) {

        }
        //ver_moviles();
    }
    public void cargar_puntos_movil( double lat,double lon,int rotacion,String distancia) {
        try {

            LatLng punto = new LatLng(lat, lon);

            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float) 0.5, (float) 0.8)
                    .flat(true)
                    .rotation(rotacion)
                    .title("Mtrs. " + distancia));

        } catch (Exception e) {

        }

    }



}

