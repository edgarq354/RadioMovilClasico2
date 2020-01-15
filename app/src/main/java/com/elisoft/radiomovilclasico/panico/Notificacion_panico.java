package com.elisoft.radiomovilclasico.panico;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.Suceso;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Notificacion_panico extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,LocationListener {

    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PETICION_CONFIG_UBICACION = 201;
    private GoogleApiClient apiClient;
    private LocationRequest locRequest;
    private GoogleMap mMap;
    ProgressDialog pDialog;
    String sid_pedido;
    Suceso suceso;
    LatLng ultima_ubicacion = new LatLng(0, 0);
    double latitud = 0, longitud = 0;




    TextView bt_llamar_usuario;
    boolean sw_panico=false;
    boolean sw_verificar_panico=false;


    ArrayList<CUsuario_panico> usuarios_panico=new ArrayList<CUsuario_panico>();



    LinearLayout ll_perfil;
    TextView tv_nombre_pasajero,tv_nombre_conductor,tv_placa,tv_marca,tv_color,tv_celular_conductor,tv_celular_pasajero;
    ImageView im_perfil_conductor,im_perfil_pasajero,im_cerrar;


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_panico);

        tv_nombre_conductor=(TextView)findViewById(R.id.tv_nombre_conductor);
        tv_nombre_pasajero=(TextView)findViewById(R.id.tv_nombre_pasajero);
        tv_marca=(TextView)findViewById(R.id.tv_marca_conductor);
        tv_placa=(TextView)findViewById(R.id.tv_placa_conductor);
        tv_color=(TextView)findViewById(R.id.tv_color_conductor);
        tv_celular_conductor=(TextView)findViewById(R.id.tv_celular_conductor);
        tv_celular_pasajero=(TextView)findViewById(R.id.tv_celular_pasajero);
        im_perfil_conductor=(ImageView) findViewById(R.id.im_perfil_conductor);
        im_perfil_pasajero=(ImageView) findViewById(R.id.im_perfil_pasajero);
        im_cerrar=(ImageView) findViewById(R.id.im_cerrar);
        ll_perfil=(LinearLayout)findViewById(R.id.ll_perfil);

        try {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }catch (Exception e)
        {
        }

        bt_llamar_usuario=(TextView)findViewById(R.id.tv_celular_pasajero);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        im_cerrar.setOnClickListener(this);




// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // localizacion automatica
        //Construcción cliente API Google
        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        enableLocationUpdates();
        //fin de la locatizaocion automatica...

        verificar_panico();
    }

    private void verificar_panico() {
        sw_verificar_panico=false;
        SharedPreferences prefe=getSharedPreferences("perfil", MODE_PRIVATE);
        String id=prefe.getString("id_usuario","");
        Servicio_panico hilo_pedido = new Servicio_panico();
        String ip=getString(R.string.servidor);
        hilo_pedido.execute(ip+"frmCompartir_carrera.php?opcion=lista_usuario_panico_por_id_usuario", "1",id);// parametro que recibe el doinbackground
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitud, longitud))      // Sets the center of the map to Mountain View
                    .zoom(18)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }catch (Exception e)
        {}

        View mapView = (View) getSupportFragmentManager().findFragmentById(R.id.map).getView();
//bicacion del button de Myubicacion de el fragento..
        View btnMyLocation = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        params.setMargins(20, 0, 0, 0);
        btnMyLocation.setLayoutParams(params);
        /*
        try {
            SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
            double lat = Double.parseDouble(prefe.getString("latitud", ""));
            double lon = Double.parseDouble(prefe.getString("longitud", ""));

            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point1))
                    .anchor((float) 0.5, (float) 0.8)
                    .flat(true)
                    .position(new LatLng(lat, lon))
                    .title(prefe.getString("", ""))
                    .snippet(prefe.getString("", "")));
        }catch (Exception e)
        {}
*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tv_celular_pasajero:
                break;
            case R.id.im_cerrar:
                ll_perfil.setVisibility(View.INVISIBLE);
                break;
        }
    }
    public LatLng ultimo_registro() {
        LatLng punto=new LatLng(0,0);
        try
        {
            SharedPreferences punto_pedido=getSharedPreferences("punto taxi",MODE_PRIVATE);
            double lat= Double.parseDouble(punto_pedido.getString("latitud","0"));
            double lon= Double.parseDouble(punto_pedido.getString("longitud","0"));
            punto=new LatLng(lat,lon);
        }catch (Exception e)
        {
        }
        return punto;
    }






    //INICIO DE SERVICIO DE COORDENADAS..

    private void enableLocationUpdates() {

        locRequest = new LocationRequest();
        locRequest.setInterval(2000);
        locRequest.setFastestInterval(1500);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest locSettingsRequest =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locRequest)
                        .build();

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        apiClient, locSettingsRequest);

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        Log.i(LOGTAG, "Configuración correcta");
                        startLocationUpdates();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.i(LOGTAG, "Se requiere actuación del usuario");
                            status.startResolutionForResult(Notificacion_panico.this, PETICION_CONFIG_UBICACION);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(LOGTAG, "Error al intentar solucionar configuración de ubicación");
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(LOGTAG, "No se puede cumplir la configuración de ubicación necesaria");

                        break;
                }
            }
        });
    }



    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(Notificacion_panico.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest,Notificacion_panico.this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.

        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Conectado correctamente a Google Play Services

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services
        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
    }

    private void updateUI(Location loc) {
        if (loc != null) {
if(sw_verificar_panico==true) {
    //obtenemos una nueva ubicacion.
    SharedPreferences prefe = getSharedPreferences("perfil", MODE_PRIVATE);
    String id = prefe.getString("id_usuario", "");
    Servicio_panico hilo_pedido = new Servicio_panico();
    String ip = getString(R.string.servidor);
    hilo_pedido.execute(ip + "frmCompartir_carrera.php?opcion=lista_usuario_panico_por_id_usuario_rec", "2", id);// parametro que recibe el doinbackground
}

        } else {
            // lblLatitud.setText("Latitud: (desconocida)");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Permiso concedido

                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);

                updateUI(lastLocation);

            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.

                Log.e(LOGTAG, "Permiso denegado");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PETICION_CONFIG_UBICACION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(LOGTAG, "El usuario no ha realizado los cambios de configuración necesarios");

                        break;
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i(LOGTAG, "Recibida nueva ubicación!");

        //Mostramos la nueva ubicación recibida
        updateUI(location);
    }
    //FIN DE SERVICIO DE COORDENADAS..


    // comenzar el servicio con el motista....
    public class Servicio_panico extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
//cargar la lista de usuario con panico. con sus datos completos.....
            if (params[1] == "1") {
                devuelve = "500";
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
                    jsonParam.put("ci", params[2]);

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

                        suceso =new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
                            usuarios_panico.clear();
                            JSONArray usu = respuestaJSON.getJSONArray("usuario");
                            int veri=0;
                            for (int i = 0; i < usu.length(); i++) {
                                int id_usuario= Integer.parseInt(usu.getJSONObject(i).getString("id_usuario"));
                                int id_conductor= Integer.parseInt(usu.getJSONObject(i).getString("id_conductor"));
                                int id_pedido= Integer.parseInt(usu.getJSONObject(i).getString("id_pedido"));
                                double latitud= Double.parseDouble(usu.getJSONObject(i).getString("latitud"));
                                double longitud= Double.parseDouble(usu.getJSONObject(i).getString("longitud"));
                                String usuario=usu.getJSONObject(i).getString("usuario");
                                String conductor=usu.getJSONObject(i).getString("conductor");
                                String razon_social=usu.getJSONObject(i).getString("razon_social");
                                String placa=usu.getJSONObject(i).getString("placa");
                                String marca=usu.getJSONObject(i).getString("marca");
                                String color=usu.getJSONObject(i).getString("color");
                                String celular_c=usu.getJSONObject(i).getString("celular_conductor");
                                String celular_pasajero=usu.getJSONObject(i).getString("celular_pasajero");
                                CUsuario_panico usuariop=new CUsuario_panico(id_conductor,id_usuario,conductor,usuario,celular_c,celular_pasajero,placa,marca,color,razon_social,latitud,longitud,id_pedido);
                                usuarios_panico.add(usuariop);

                                veri+=1;
                            }
                            Log.e("Carrera","Finalizo la carga de conductores.");
                            devuelve="1";
                            //verificarmos si tenemos algun contacto en panico para comenzar
                            //el servicio de obtencion de ubicaiones de cada una de ellas.
                            if(veri>0)
                            sw_verificar_panico=true;
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

//cargar la lista de usuario con panico. solo el recorrido REC.....
            if (params[1] == "2") {
                devuelve = "500";
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
                    jsonParam.put("ci", params[2]);

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

                        suceso =new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
                            usuarios_panico.clear();
                            JSONArray usu = respuestaJSON.getJSONArray("usuario");
                            int veri=0;
                            for (int i = 0; i < usu.length(); i++) {
                                int id_pedido= Integer.parseInt(usu.getJSONObject(i).getString("id_pedido"));
                                double latitud= Double.parseDouble(usu.getJSONObject(i).getString("latitud"));
                                double longitud= Double.parseDouble(usu.getJSONObject(i).getString("longitud"));
                                boolean sw=get_posicion_conductor_por_pedido(id_pedido,latitud,longitud);

                                veri+=1;
                            }
                            Log.e("Carrera","Finalizo la carga de conductores.");
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

            pDialog = new ProgressDialog(Notificacion_panico.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Autenticando ....");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss(); //ocultamos proggress dialog
            // Log.e("respuesta del servidor=", "" + s);
            if (s.equals("1")) {
                mostrar_en_mapa_conductores();

            } else if (s.equals("500")) {
                mensaje(suceso.getMensaje());

            } else if (s.equals("2")) {
                sw_panico=false;
                try {
                    mMap.clear();
                } catch (Exception e) {

                }
                mensaje(suceso.getMensaje());

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

    private void mostrar_en_mapa_conductores() {
        mMap.clear();
        for (int i=0;i<usuarios_panico.size();i++)
        {
            CUsuario_panico conductor=usuarios_panico.get(i);

            if(sw_panico==true) {

                double lat = conductor.getLatitud();
                double lon = conductor.getLongitud();
                try {
                    Marker marker = this.mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_persona))
                            .anchor((float) 0.5, (float) 0.8)
                            .flat(true)
                            .position(new LatLng(lat, lon))
                            .title(conductor.getNombre_pasajero())

                    );
                    marker.showInfoWindow();

                } catch (Exception e) {

                }
            }
            else
            {
                sw_panico=true;

                double lat = conductor.getLatitud();
                double lon = conductor.getLongitud();
                try {
                    Marker marker = this.mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_persona))
                            .anchor((float) 0.5, (float) 0.8)
                            .flat(true)
                            .position(new LatLng(lat, lon))
                            .title(conductor.getCelular_pasajero())
                    );
                    marker.showInfoWindow();

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(lat, lon))      // Sets the center of the map to Mountain View
                            .zoom(15)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } catch (Exception e) {

                }
            }
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                ll_perfil.setVisibility(View.VISIBLE);
                int posicion=get_posicion_conductor(marker.getTitle());
                CUsuario_panico usuario_p=usuarios_panico.get(posicion);

                tv_nombre_pasajero.setText(usuario_p.getNombre_pasajero());
                tv_celular_pasajero.setText(usuario_p.getCelular_pasajero());
                tv_nombre_conductor.setText(usuario_p.getNombre_c());
                tv_marca.setText(usuario_p.getMarca_c());
                tv_placa.setText(usuario_p.getPlaca_c());
                tv_color.setText(usuario_p.getColor_c());

                getImage_conductor(String.valueOf(usuario_p.getId_conductor()));
                getImage_pasajero(String.valueOf(usuario_p.getId_pasajero()));



            }
        });
    }


    private void getImage_conductor(String id)//
    {
        class GetImage extends AsyncTask<String,Void,Bitmap> {
            ImageView bmImage;


            public GetImage(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);


                Drawable dw = new BitmapDrawable(getResources(), bitmap);
                //se edita la imagen para ponerlo en circulo.

                if( bitmap==null)
                { dw = getResources().getDrawable(R.mipmap.ic_perfil);}

                imagen_circulo(dw,bmImage);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = getString(R.string.servidor)+"frmTaxi.php?opcion=get_imagen&id_conductor="+strings[0];//hace consulta ala Bd para recurar la imagen
                Drawable d = getResources().getDrawable(R.mipmap.ic_perfil);
                Bitmap mIcon = drawableToBitmap(d);
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
                return mIcon;
            }
        }

        GetImage gi = new GetImage(im_perfil_conductor);
        gi.execute(id);
    }
    private void getImage_pasajero(String id)//
    {
        class GetImage extends AsyncTask<String,Void,Bitmap> {
            ImageView bmImage;


            public GetImage(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);


                Drawable dw = new BitmapDrawable(getResources(), bitmap);
                //se edita la imagen para ponerlo en circulo.

                if( bitmap==null)
                { dw = getResources().getDrawable(R.mipmap.ic_perfil);}

                imagen_circulo(dw,bmImage);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = getString(R.string.servidor)+"frmTaxi.php?opcion=get_imagen_usuario&id_usuario="+strings[0];//hace consulta ala Bd para recurar la imagen
                Drawable d = getResources().getDrawable(R.mipmap.ic_perfil);
                Bitmap mIcon = drawableToBitmap(d);
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
                return mIcon;
            }
        }

        GetImage gi = new GetImage(im_perfil_pasajero);
        gi.execute(id);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void imagen_circulo(Drawable id_imagen, ImageView imagen) {
        Bitmap originalBitmap = ((BitmapDrawable) id_imagen).getBitmap();
        if (originalBitmap.getWidth() > originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        } else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }

//creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

//asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getWidth());
        try {
            imagen.setImageDrawable(roundedDrawable);
        } catch (Exception e) {

        }
    }

    public void mensaje(String mensaje)
    {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Radio Movil Clasico");
            dialogo1.setMessage(mensaje);
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    finish();
                }
            });
            dialogo1.show();
    }

    public int get_posicion_conductor(String pasajero) {
        int i;
        for (i=0;i <=usuarios_panico.size();i++) {
            CUsuario_panico aux =usuarios_panico.get(i);
            if(aux.getNombre_pasajero().equals(pasajero)) return i;
        }
        // si no se encuentra el elemento en el stock:
        return -1;
    }
    public boolean get_posicion_conductor_por_pedido(int id_pedido,double latitud,double longitud) {
        boolean sw=false;
        for (int i=0;i <=usuarios_panico.size();i++) {
            CUsuario_panico aux =usuarios_panico.get(i);
            if(aux.getId_pedido()==id_pedido){
                sw=true;
                aux.setLatitud(latitud);
                aux.setLongitud(longitud);
                usuarios_panico.remove(i);
                usuarios_panico.add(i,aux);
            }
        }
        // si no se encuentra el elemento en el stock:
        return sw;
    }




}
