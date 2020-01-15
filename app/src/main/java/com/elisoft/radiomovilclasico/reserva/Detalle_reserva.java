package com.elisoft.radiomovilclasico.reserva;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.List;

public class Detalle_reserva extends AppCompatActivity  implements OnMapReadyCallback {
    private GoogleMap mMap;
    double latitud,longitud;
    TextView tv_direccion,tv_referencia;
    Button bt_cancelar;
    int id_pedido=0;
    Suceso suceso;
    private ProgressDialog pDialog;

    android.support.v7.app.AlertDialog alert2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_reserva);
        tv_direccion=(TextView)findViewById(R.id.tv_direccion);
        tv_referencia=(TextView)findViewById(R.id.tv_referencia);
        bt_cancelar=(Button)findViewById(R.id.bt_cancelar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try{
            Bundle bundle=getIntent().getExtras();
            latitud=bundle.getDouble("latitud");
            longitud=bundle.getDouble("longitud");
            tv_referencia.setText(bundle.getString("referencia",""));
            id_pedido=bundle.getInt("id_pedido",0);
            tv_direccion.setText(obtener_direccion( latitud, longitud).toString());









        }catch (Exception e){
            finish();
        }

        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelar_reserva(id_pedido);
            }
        });


        try {
            LatLng inicio = new LatLng(latitud, longitud);
            Marker marker1= this.mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_punto_inicio_1))
                    .anchor((float) 0.5, (float) 0.8)
                    .flat(true)
                    .position(inicio) );
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(inicio)      // Sets the center of the map to Mountain View
                    .zoom(16)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } catch (Exception e) {
        }


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


            //  et_direccion.setText(address.getFeatureName()+" | "+address.getSubAdminArea ()+" | "+address.getSubLocality ()+" | "+address.getLocality ()+" | "+address.getSubLocality ()+" | "+address.getPremises ()+" | "+addresses.get(0).getThoroughfare()+" | "+address.getAddressLine(0));
        }
        return s_direccion;
    }

    public void cancelar_reserva(final int id_pedido)
    {
        try {

            // get prompts.xml view
            LayoutInflater layoutInflater = LayoutInflater.from(Detalle_reserva.this);
            View promptView = layoutInflater.inflate(R.layout.escribir_cancelar, null);
            android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(Detalle_reserva.this);
            alertDialogBuilder.setView(promptView);

            final Button bt_cancelar= (Button) promptView.findViewById(R.id.bt_cancelar);
            final Button bt_pedir= (Button) promptView.findViewById(R.id.bt_pedir);
            final EditText et_descripcion= (EditText) promptView.findViewById(R.id.et_descripcion);


            bt_cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert2.cancel();
                }
            });
            bt_pedir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences prefe=getSharedPreferences("perfil", Context.MODE_PRIVATE);
                    String id=prefe.getString("id_usuario","");
                    Servicio_pedir_reserva hilo_taxi_cancelar = new Servicio_pedir_reserva();
                    hilo_taxi_cancelar.execute(getString(R.string.servidor) + "frmPedido.php?opcion=cancelar_pedido_reserva_usuario", "1", id,String.valueOf(id_pedido),et_descripcion.getText().toString());
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    alert2.cancel();
                }
            });
            // create an alert dialog
            alert2 = alertDialogBuilder.create();
            alert2.show();
        }catch (Exception e){

        }


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
        LatLng inicio = new LatLng(latitud, longitud);
        try {

            Marker marker1= this.mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_punto_inicio_1))
                    .anchor((float) 0.5, (float) 0.8)
                    .flat(true)
                    .position(inicio) );

        } catch (Exception e) {
        }







// Set the camera to the greatest possible zoom level that includes the
// bounds

        //agregaranimacion al mover la camara...
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(inicio)      // Sets the center of the map to Mountain View
                .zoom(16)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



    }


    public class Servicio_pedir_reserva extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";


            //verificar si alguien aceptotu pedido..
            // verificar si tiene un pedido que aun no ha finalizado....
            //obtener datos del pedido en curso.....


            //2: CANCELAR EL PEDIDO RESERVA
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
                        jsonParam.put("detalle", params[4]);
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
            try {
                pDialog = new ProgressDialog(Detalle_reserva.this);
                pDialog.setMessage("Descargando la lista de reservas. . .");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(true);
                pDialog.show();
            }catch (Exception e)
            {
                mensaje_error("Por favor actualice la aplicación.");
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.cancel();//ocultamos proggress dialog
            //ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);

            if(s.equals("6"))
            {
            finish();
            }else if(s.equals("7"))
            {
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

    public void mensaje_error_final(String mensaje) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(Detalle_reserva.this);
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
        } catch (Exception e) {
            Log.e("mensaje_error", e.toString());
        }
    }




}
