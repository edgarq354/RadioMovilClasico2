package com.elisoft.radiomovilclasico.menu_otra_direccion;




// inicio buscar direcciones..

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.AndroidPermissions;
import com.elisoft.radiomovilclasico.Buscador.PlacesAutoCompleteAdapter;
import com.elisoft.radiomovilclasico.Constants;
import com.elisoft.radiomovilclasico.GeocodeAddressIntentService;
import com.elisoft.radiomovilclasico.NewGPSTracker;
import com.elisoft.radiomovilclasico.Pedido_usuario;
import com.elisoft.radiomovilclasico.perfil.Perfil_pasajero;
import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.Reservar_movil;
import com.elisoft.radiomovilclasico.Suceso;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// fin de buscar direcciones..


public class Otra_direccion extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener{


    boolean sw_ver_taxi_cerca = false;
    private JSONArray puntos_taxi;


    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PETICION_CONFIG_UBICACION = 201;
    private Context mContext;
    private GoogleApiClient apiClient;

    private LocationRequest locRequest;

    LatLng myPosition;
    Suceso suceso;

    LinearLayout pedi_taxi,pedir_movil_lujo,pedir_movil_maletero,pedir_movil_aire,pedir_movil_pedido, pedir_movil_reserva, buscar_direccion,pedir_moto;
    CheckBox cb_tipo_pedido_empresa;

    private AddressResultReceiver mResultReceiver;
    int fetchType = Constants.USE_ADDRESS_LOCATION;
    private LatLng addressLatLng;
    private NewGPSTracker gpsTracker;


    int limpiar_mapa=0;

    public TextView tv_ubicacion,tv_nombre;
    private GoogleMap mMap;
    public LocationManager locationManager;
    public LocationListener locationListener;


    int sw_acercar_a_mi_ubicacion=0;

    ListView lista_buscar;
    List<Map<String, String>> data;
    List<Map<String, String>> punto;
    EditText et_buscar;
    double latitud_buscador=0,longitud_buscador=0;

    boolean sw_obteniendo_direccion;

    LocationManager manager = null;

    AlertDialog alert2 = null;
    AlertDialog alert = null;



    //  inicio de modificacion de pedir taxi
    protected GoogleApiClient mGoogleApiClient;

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-24,-68), new LatLng(-8,-58));

    private TextView tv_direccion_fin;

    private LinearLayoutManager mLinearLayoutManager;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    ImageView delete;

    String nombre="",direccion="";
    Servicio_ver_movil hilo_m;
    //fin de buscador de ubicacion...


    @Override
    protected void onStart() {
        if(sw_acercar_a_mi_ubicacion==0){
            sw_acercar_a_mi_ubicacion = 0;
        }
        boolean sw=estaConectado();
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otra_direccion);
        cb_tipo_pedido_empresa= (CheckBox)findViewById(R.id.cb_tipo_pedido_empresa);
        //et_buscar=(EditText)findViewById(R.id.et_buscar);
        pedi_taxi = (LinearLayout) findViewById(R.id.pedir_movil);
        pedir_movil_aire = (LinearLayout) findViewById(R.id.pedir_movil_aire);
        pedir_movil_lujo = (LinearLayout) findViewById(R.id.pedir_movil_lujo);
        pedir_movil_maletero = (LinearLayout) findViewById(R.id.pedir_movil_maletero);
        pedir_movil_pedido = (LinearLayout) findViewById(R.id.pedir_movil_pedido);
        pedir_movil_reserva = (LinearLayout) findViewById(R.id.pedir_movil_reserva);
        pedir_moto = (LinearLayout)findViewById(R.id.pedir_moto);
        tv_ubicacion=(TextView)findViewById(R.id.tv_ubicacion);
        tv_nombre=(TextView)findViewById(R.id.tv_nombre);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // lista_buscar=(ListView)findViewById(R.id.lista_busqueda);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        pedi_taxi.setOnClickListener(this);
        pedir_movil_lujo.setOnClickListener(this);
        pedir_movil_aire.setOnClickListener(this);
        pedir_movil_maletero.setOnClickListener(this);
        pedir_movil_pedido.setOnClickListener(this);
        pedir_movil_reserva.setOnClickListener(this);
        pedir_moto.setOnClickListener(this);


        hilo_m = new  Servicio_ver_movil();
/*
// evento de onclick en la Lista de Busqueda ...
        lista_buscar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> dato;
                //PUNTO. guarda las LATITUDES y LONGITUDES  ..
                try {
                    dato = punto.get(i);
                    lista_buscar.removeAllViews();
                    enviar_parametros(dato.get("latitud"), dato.get("longitud"));
                }catch (Exception e)
                {}
            }
        });

*/

        sw_acercar_a_mi_ubicacion = 0;



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

        addressLatLng=new LatLng(0,0);
        enableLocationUpdates();
        gpsTracker = new NewGPSTracker(getApplicationContext());
        //fin de la locatizaocion automatica...

        try{
            Bundle bundle=getIntent().getExtras();
            latitud_buscador= Double.parseDouble(bundle.getString("latitud","0"));
            longitud_buscador= Double.parseDouble(bundle.getString("longitud","0"));
            tv_ubicacion.setText(bundle.getString("direccion",""));
            nombre=bundle.getString("nombre","");
            tv_nombre.setText(nombre);
            sw_acercar_a_mi_ubicacion=2;
            if(latitud_buscador==0 && longitud_buscador==0)
            {
                sw_acercar_a_mi_ubicacion=0;
            }

        }catch (Exception e)
        {
            sw_acercar_a_mi_ubicacion=0;
        }

        sw_obteniendo_direccion=false;


/*
        et_buscar.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                if(et_buscar.getText().toString().trim().length()>=3) {
                    buscar(et_buscar.getText().toString(), lista_buscar);
                }
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
        */




        //Inicio de implementacion de buscador de ubicacion/...
        buildGoogleApiClient();
        tv_direccion_fin = (TextView)findViewById(R.id.tv_direccion_fin);

        tv_direccion_fin.setOnClickListener(this);

        delete=(ImageView)findViewById(R.id.cross);

        mAutoCompleteAdapter =  new PlacesAutoCompleteAdapter(this, R.layout.searchview_adapter,
                mGoogleApiClient, BOUNDS_INDIA, null);


        mLinearLayoutManager=new LinearLayoutManager(this);

        delete.setOnClickListener(this);




        //fin de implementacion de buscador de ubicaicon.

    }




    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    public void enviar_parametros(String latitud, String longitud)
    {
        try {
            double lat= Double.parseDouble(latitud);
            double lon= Double.parseDouble(longitud);
            //agregaranimacion al mover la camara...
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat,lon))      // Sets the center of the map to Mountain View
                    .zoom(16)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            sw_acercar_a_mi_ubicacion = 1;
        } catch (Exception e) {
            sw_acercar_a_mi_ubicacion = 0;
        }

    }

    public void buscar(String search, ListView lista)
    {
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName("%"+search+"%",5,-10.098670, -70.751953,-22.278459, -55.864717);
            if (addresses != null && !addresses.equals("")) {
                //search(addresses);


                data = new ArrayList<Map<String, String>>();
                punto = new ArrayList<Map<String, String>>();
                for (int i = 0; i < addresses.size(); i++) {
                    String titulo = String.format(
                            "%s",
                            addresses.get(i).getMaxAddressLineIndex() > 0 ? addresses.get(i)
                                    .getAddressLine(i) : "");
                    String sub = String.format(
                            "%s, %s ,%s ", addresses.get(i).getThoroughfare()!=null?addresses.get(i).getThoroughfare():"", addresses.get(i).getLocality(), addresses.get(i).getCountryName());


                    Map<String, String> datum = new HashMap<String, String>(2);
                    datum.put("titulo", titulo);
                    datum.put("detalle", sub);
                    data.add(datum);
                    Map<String, String> puntom = new HashMap<String, String>(2);
                    puntom.put("latitud", String.valueOf(addresses.get(i).getLatitude()));
                    puntom.put("longitud", String.valueOf(addresses.get(i).getLongitude()));
                    punto.add(puntom);

                }

                SimpleAdapter adapter = new SimpleAdapter(this, data,
                        android.R.layout.simple_list_item_2,
                        new String[]{"titulo", "detalle"},
                        new int[]{android.R.id.text1,
                                android.R.id.text2});
                lista.setAdapter(adapter);
            }
        } catch (Exception e) {
            String[] vacio={""};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, vacio);
            lista.setAdapter(adapter);
        }
    }


    ///BBUSCADOR DE DIRECCIONES

    protected void search(List<Address> addresses) {
/*
        Address address = (Address) addresses.get(0);
        double home_long = address.getLongitude();
        double home_lat = address.getLatitude();
        latLng = new LatLng(address.getLatitude(), address.getLongitude());

        String addressText = String.format(
                "%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address
                        .getAddressLine(0) : "", address.getCountryName());

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(latLng);
        markerOptions.title(addressText);

        mMap.clear();
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

       locationTv.setText("Latitude:" + address.getLatitude() + ", Longitude:"
                + address.getLongitude());

*/
    }








    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Controles UI
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            View mapView = (View) getSupportFragmentManager().findFragmentById(R.id.map).getView();
//bicacion del button de Myubicacion de el fragento..
            View btnMyLocation = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            params.setMargins(20, 0, 0, 0);
            btnMyLocation.setLayoutParams(params);

            init();

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }

        }
    }


    private void enableLocationUpdates() {

        locRequest = new LocationRequest();
        locRequest.setInterval(1000);
        locRequest.setFastestInterval(100);
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
                            status.startResolutionForResult(Otra_direccion.this, PETICION_CONFIG_UBICACION);
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
        if (ActivityCompat.checkSelfPermission(Otra_direccion.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest, Otra_direccion.this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.

        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
        Log.v("Google API Callback","Connection Failed");
        Log.v("Error Code", String.valueOf(result.getErrorCode()));
        Toast.makeText(this, com.elisoft.radiomovilclasico.Buscador.Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Conectado correctamente a Google Play Services

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }

        Log.v("Google API Callback", "Connection Done");
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services

        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
        Log.v("Google API Callback", "Connection Suspended");
        Log.v("Code", String.valueOf(i));
    }

    private void updateUI(Location loc) {
        if (loc != null) {

            double lat = loc.getLatitude();
            double lon = loc.getLongitude();
            myPosition = new LatLng(lat, lon);

            // cargamos los puntos de las taxis en nuestro mapa....


            //////////////////get in AsyncTask//////////////////////
            getAddressIntentService(lat, lon);
            ////////////////

            if (sw_acercar_a_mi_ubicacion == 0) {
                //mover la camara del mapa a mi ubicacion.,,
                try {
                    //agregaranimacion al mover la camara...
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(loc.getLatitude(), loc.getLongitude()))      // Sets the center of the map to Mountain View
                            .zoom(16)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    sw_acercar_a_mi_ubicacion = 1;
                    ver_moviles();
                } catch (Exception e) {
                    Log.e("gps_primera",e.toString());
                    sw_acercar_a_mi_ubicacion = 0;
                }

            }
            else if(sw_acercar_a_mi_ubicacion==2)
            {
                //mover la camara del mapa a mi ubicacion.,,
                try {
                    //agregaranimacion al mover la camara...
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitud_buscador, longitud_buscador))      // Sets the center of the map to Mountain View
                            .zoom(16)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    sw_acercar_a_mi_ubicacion = 1;
                    ver_moviles();
                } catch (Exception e) {
                    sw_acercar_a_mi_ubicacion = 0;
                }
            }


            //agregaranimacion al mover la camara...

        } else {

            Log.e("Latitud","(desconocida)");
            Log.e("Longitud","(desconocida)");
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

    public void mensaje_error(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }

    public void mensaje_cerrar_sesion(String mensaje)
    {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Atención");
        dialogo1.setMessage(mensaje);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                startActivity(new Intent(getApplication(),Pedido_usuario.class));
            }
        });

        dialogo1.show();

    }

    private void getAddressIntentService(double lat, double lng) {
        Intent intent = new Intent(this, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
        intent.putExtra(Constants.LOCATION_LATITUDE_DATA_EXTRA,lat);
        intent.putExtra(Constants.LOCATION_LONGITUDE_DATA_EXTRA,lng);
        Log.e("radiomovilclasico", "Starting Service");
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        int tipo_pedido_empresa=0;
        if(cb_tipo_pedido_empresa.isChecked()){
            tipo_pedido_empresa=1;
        }

        switch (v.getId()) {
            case R.id.pedir_movil:
                //solicita un movil de cualquier caracteristica.
                SharedPreferences ultimo = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                if (ultimo.getString("id_pedido", "").equals("") == true || ultimo.getString("id_pedido", "0").equals("0") == true ) {

                    if(myPosition.latitude==0 && myPosition.longitude==0){
                        Toast.makeText(getApplicationContext(),"Marque su ubicación final ", Toast.LENGTH_LONG).show();
                    }else if(addressLatLng.longitude==0 && addressLatLng.longitude==0)
                    {
                        Toast.makeText(getApplicationContext(),"Marque su ubicación inicial ", Toast.LENGTH_LONG).show();
                    }else {
                        Intent tarifa=new Intent(getApplicationContext(),Calcular_tarifa_confirmar.class);
                        //obtener direccion de inicio guardado...
                        SharedPreferences inicio=getSharedPreferences("direccion_inicio",MODE_PRIVATE);
                        String s_latitud_inicio=inicio.getString("latitud_inicio","0");
                        String s_longitud_inicio=inicio.getString("longitud_inicio","0");
                        //fin de  obtener la direccion de inicio.



                        double latitud_fin=addressLatLng.latitude;
                        double longitud_fin=addressLatLng.longitude;
                        //double latitud=myPosition.latitude;
                        //double longitud=myPosition.longitude;
                        double latitud=Double.parseDouble(s_latitud_inicio);
                        double longitud=Double.parseDouble(s_longitud_inicio);

                        tarifa.putExtra("latitud_inicio",latitud);
                        tarifa.putExtra("longitud_inicio",longitud);
                        tarifa.putExtra("latitud_fin",latitud_fin);
                        tarifa.putExtra("longitud_fin",longitud_fin);
                        startActivity(tarifa);
                    }



                } else {
                    finish();
                }
                /*
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_camara();
                }
                else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_almacenamiento();
                } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_llamada();
                }else{
                    if(existe_celular()==true)
                    {
                        double lat_1=0,lon_1=0;
                        try{
                            lat_1=addressLatLng.latitude;
                            lon_1=addressLatLng.longitude;
                        }catch (Exception e){
                            lat_1=0;
                            lon_1=0;
                        }
                        if(lat_1!=0&& lon_1!=0){
                            escribir_referencia(1,tipo_pedido_empresa,"Solicitando un movil");
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
                }
                */

                break;
            case R.id.pedir_movil_lujo:
                //solicita un movil con la mejor caracteristica.
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_camara();
                }
                else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_almacenamiento();
                } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_llamada();
                }else{
                    if(existe_celular()==true)
                    {
                        double lat_1=0,lon_1=0;
                        try{
                            lat_1=addressLatLng.latitude;
                            lon_1=addressLatLng.longitude;
                        }catch (Exception e){
                            lat_1=0;
                            lon_1=0;
                        }
                        if(lat_1!=0 && lon_1!=0){
                            escribir_referencia(2,tipo_pedido_empresa,"Solicitando un movil de lujo");
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
                }

                break;
            case R.id.pedir_movil_aire:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_camara();
                }
                else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_almacenamiento();
                } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_llamada();
                }else{
                    if(existe_celular()==true)
                    {

                        double lat_1=0,lon_1=0;
                        try{
                            lat_1=addressLatLng.latitude;
                            lon_1=addressLatLng.longitude;
                        }catch (Exception e){
                            lat_1=0;
                            lon_1=0;
                        }
                        if(lat_1!=0&& lon_1!=0){
                            escribir_referencia(3,tipo_pedido_empresa,"Solicitando un movil con aire acondicionado");
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
                }

                break;

            case R.id.pedir_movil_maletero:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_camara();
                }
                else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_almacenamiento();
                } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_llamada();
                }else{
                    if(existe_celular()==true)
                    {
                        double lat_1=0,lon_1=0;
                        try{
                            lat_1=addressLatLng.latitude;
                            lon_1=addressLatLng.longitude;
                        }catch (Exception e){
                            lat_1=0;
                            lon_1=0;
                        }
                        if(lat_1!=0&& lon_1!=0){
                            escribir_referencia(4,tipo_pedido_empresa,"Solicitando un movil con maletero");
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
                }

                break;

            case R.id.pedir_movil_pedido:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_camara();
                }
                else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_almacenamiento();
                } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_llamada();
                }else{
                    if(existe_celular()==true)
                    {
                        escribir_referencia_pedido(5,tipo_pedido_empresa);
                    } else{
                        actualizar_perfil();
                    }
                }

                break;

            case R.id.pedir_movil_reserva:
                Intent datos_pedido = new Intent(this, Reservar_movil.class);
                datos_pedido.putExtra("latitud", addressLatLng.latitude);
                datos_pedido.putExtra("longitud", addressLatLng.longitude);
                startActivity(datos_pedido);
                break;
            case R.id.pedir_moto:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_camara();
                }
                else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_almacenamiento();
                } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_llamada();
                }else{
                    if(existe_celular()==true)
                    {
                        escribir_referencia_moto(7,tipo_pedido_empresa );
                    } else{
                        actualizar_perfil();
                    }
                }

                break;

            case R.id.cross:

                break;
            case R.id.ver_movil:
                ver_moviles();
                break;

            case R.id.fb_llamar:


                llamar_radio_movil();

                break;
            case R.id.fb_whatsapp:
                boolean isWhatsapp = appInstalledOrNot("com.whatsapp");
                if (isWhatsapp)
                    AbrirWhatsApp();
                break;
            case R.id.tv_direccion_fin:
                Intent buscar=new Intent(this,Buscar_direccion_inicio.class);
                buscar.putExtra("latitud_inicio",myPosition.latitude);
                buscar.putExtra("longitud_inicio",myPosition.longitude);
                buscar.putExtra("direccion_inicio",tv_ubicacion.getText().toString());
                startActivity(buscar);

                break;

        }

    }

    private void ver_tarifa() {
        Intent tarifa=new Intent(this,Calcular_tarifa_confirmar.class);
        double latitud=myPosition.latitude;
        double longitud=myPosition.longitude;
        double latitud_fin=addressLatLng.latitude;
        double longitud_fin=addressLatLng.longitude;
        tarifa.putExtra("latitud_inicio",latitud);
        tarifa.putExtra("longitud_inicio",longitud);
        tarifa.putExtra("latitud_fin",latitud_fin);
        tarifa.putExtra("longitud_fin",longitud_fin);
        startActivity(tarifa);
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    void AbrirWhatsApp() {

        Uri uri = Uri.parse("smsto: +59176633339");
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        i.putExtra("sms_body", "Un movil por favor");
        startActivity(Intent.createChooser(i, "Radio Movil Clasico"));

      /*  String formattedNumber = "+59176633339";
        try{
            Intent sendIntent =new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT,"hola");
            sendIntent.putExtra("sms_body", "Hola que tal");
            sendIntent.putExtra("jid", formattedNumber +"@s.whatsapp.net");
            sendIntent.setPackage("com.whatsapp");
             startActivity(sendIntent);
        }
        catch(Exception e)
        {
            Toast.makeText(this,"Error/n"+ e.toString(),Toast.LENGTH_SHORT).show();
        }*/
    }


    private void llamar_radio_movil() {
        Intent llamada = new Intent(Intent.ACTION_DIAL);
        llamada.setData(Uri.parse("tel:" +"33474444"));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            verificar_permiso_llamada();
        }else{
            startActivity(llamada);
        }
    }

    public void  escribir_referencia_pedido(final int clase_vehiculo,final int tipo_pedido_empresa)
    {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Otra_direccion.this);
        View promptView = layoutInflater.inflate(R.layout.escribir_pedido_otra_direccion, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Otra_direccion.this);
        alertDialogBuilder.setView(promptView);

        final Button bt_cancelar= (Button) promptView.findViewById(R.id.bt_cancelar);
        final Button bt_pedir= (Button) promptView.findViewById(R.id.bt_pedir);
        final EditText et_referencia= (EditText) promptView.findViewById(R.id.et_referencia);
        final RadioButton rb_mi_en_ubicacion=(RadioButton)promptView.findViewById(R.id.rb_en_mi_ubicacion);
        final RadioButton rb_donde_deseo_ir=(RadioButton)promptView.findViewById(R.id.rb_donde_deseo_ir);



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


                if(rb_mi_en_ubicacion.isChecked()==true){
                    pedir_taxi("0", et_referencia.getText().toString().trim(),clase_vehiculo,tipo_pedido_empresa,true);
                }else{
                    pedir_taxi("0", et_referencia.getText().toString().trim(),clase_vehiculo,tipo_pedido_empresa,false);
                }
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                alert2.cancel();
            }
        });
        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();
    }
    public void  escribir_referencia(final int clase_vehiculo,final int tipo_pedido_empresa,String tipo_pedido_texto)
    {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Otra_direccion.this);
        View promptView = layoutInflater.inflate(R.layout.escribir_referencia_otra_direccion, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Otra_direccion.this);
        alertDialogBuilder.setView(promptView);

        final Button bt_cancelar= (Button) promptView.findViewById(R.id.bt_cancelar);
        final Button bt_pedir= (Button) promptView.findViewById(R.id.bt_pedir);
        final EditText et_referencia= (EditText) promptView.findViewById(R.id.et_referencia);
        final TextView tv_tipo_pedido=(TextView)promptView.findViewById(R.id.tv_tipo_pedido);
        final RadioButton rb_mi_en_ubicacion=(RadioButton)promptView.findViewById(R.id.rb_en_mi_ubicacion);
        final RadioButton rb_donde_deseo_ir=(RadioButton)promptView.findViewById(R.id.rb_donde_deseo_ir);
        tv_tipo_pedido.setText(tipo_pedido_texto);

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
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                if(rb_mi_en_ubicacion.isChecked()==true){
                    pedir_taxi("0", et_referencia.getText().toString().trim(),clase_vehiculo,tipo_pedido_empresa,true);
                }else{
                    pedir_taxi("0", et_referencia.getText().toString().trim(),clase_vehiculo,tipo_pedido_empresa,false);
                }

                alert2.cancel();

            }
        });
        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();
    }

    public void  escribir_referencia_moto(final int clase_vehiculo,final int tipo_pedido_empresa )
    {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Otra_direccion.this);
        View promptView = layoutInflater.inflate(R.layout.escribir_moto_otra_direccion, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Otra_direccion.this);
        alertDialogBuilder.setView(promptView);

        final Button bt_cancelar= (Button) promptView.findViewById(R.id.bt_cancelar);
        final Button bt_pedir= (Button) promptView.findViewById(R.id.bt_pedir);
        final EditText et_referencia= (EditText) promptView.findViewById(R.id.et_referencia);
        final RadioButton rb_mi_en_ubicacion=(RadioButton)promptView.findViewById(R.id.rb_en_mi_ubicacion);
        final RadioButton rb_donde_deseo_ir=(RadioButton)promptView.findViewById(R.id.rb_donde_deseo_ir);
        final RadioButton rb_solo_moto=(RadioButton)promptView.findViewById(R.id.rb_solo_moto);

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
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                int clase_v=clase_vehiculo;
                if(rb_solo_moto.isChecked()==false){
                    clase_v++;
                }
                if(rb_mi_en_ubicacion.isChecked()==true){
                    pedir_taxi("0", et_referencia.getText().toString().trim(),clase_v,tipo_pedido_empresa,true);
                }else{
                    pedir_taxi("0", et_referencia.getText().toString().trim(),clase_v,tipo_pedido_empresa,false);
                }
                alert2.cancel();

            }
        });
        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();
    }



    public boolean existe_celular() {
        boolean sw = true;
        SharedPreferences perfil = getSharedPreferences("perfil", MODE_PRIVATE);
        if (perfil.getString("celular", "").equals("") == true) {
            sw = false;
        }
        return sw;
    }

    private void actualizar_perfil() {
        SharedPreferences perfil = getSharedPreferences("perfil", MODE_PRIVATE);
        if (perfil.getString("celular", "").equals("") == true) {

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle(getString(R.string.app_name));
            dialogo1.setMessage("Por favor Ingrese su número del Telefono movil para que podamos identificarte.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    startActivity(new Intent(getApplicationContext(), Perfil_pasajero.class));
                }
            });

            dialogo1.show();
        }
    }

    private void ver_moviles() {
        SharedPreferences ult=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
        //si no tiene pédidos se le va a mostrar en el mapa....
        if(ult.getString("id_pedido","").equals("")==true) {
            try {

                if(sw_ver_taxi_cerca==false) {
                    sw_ver_taxi_cerca=true;
                    hilo_m=new Servicio_ver_movil();
                    hilo_m.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=get_taxi_en_rango", "1", String.valueOf(addressLatLng.latitude), String.valueOf(addressLatLng.longitude));// parametro que recibe el doinbackground*/
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
    public void pedir_taxi(String numero, String referencia,int clase_vehiculo,int tipo_pedido_empresa,boolean sw_ubicacion){
        ///verifica si el GPS esta activo.
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertNoGps();
        }
        else {
            if (existe_celular() == true) {

                if (referencia.length() >= 3 ) {
                    Intent datos_pedido = new Intent(this, Pedido_usuario.class);

                    if (sw_ubicacion) {
                        //pedir en mi ubicacion
                        datos_pedido.putExtra("latitud", myPosition.latitude);
                        datos_pedido.putExtra("longitud", myPosition.longitude);
                    }else{
                        //en otra direccion
                        datos_pedido.putExtra("latitud", addressLatLng.latitude);
                        datos_pedido.putExtra("longitud", addressLatLng.longitude);
                    }


                    datos_pedido.putExtra("referencia", referencia);
                    datos_pedido.putExtra("numero",numero);
                    datos_pedido.putExtra("clase_vehiculo",clase_vehiculo);
                    datos_pedido.putExtra("tipo_pedido_empresa",tipo_pedido_empresa);
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


                /*
                actualizar();
                SharedPreferences ultimo=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                if(ultimo.getString("id_pedido","").equals("")==true) {
                    try {
                        Intent datos_pedido = new Intent(this, Datos_de_pedido.class);
                        datos_pedido.putExtra("latitud", addressLatLng.latitude);
                        datos_pedido.putExtra("longitud", addressLatLng.longitude);
                        startActivity(datos_pedido);
                    } catch (Exception e) {

                    }
                }else
                {
                    try {
                        int id_pedido=Integer.parseInt(ultimo.getString("id_pedido",""));
                        Servicio hilo_taxi = new Servicio();
                        hilo_taxi.execute(getString(R.string.servidor) + "frmPedido.php?opcion=get_pedido_por_id_pedido", "5",String.valueOf(id_pedido));// parametro que recibe el doinbackground
                    }catch (Exception e)
                    {

                    }
                }*/
    }



    //clase de AddresResulReciever para obtener los datos de Adresss,, latitud y longitud
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("setUpMap",resultData.getString(Constants.RESULT_DATA_KEY));
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("setUpMap",resultData.getString(Constants.RESULT_DATA_KEY));
                    }
                });
            }
        }
    }
    private void init() {

        mResultReceiver = new AddressResultReceiver(null);
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }


        if (Build.VERSION.SDK_INT >= 23) {
            if (!AndroidPermissions.getInstance().checkLocationPermission(this)) {
                AndroidPermissions.getInstance().displayLocationPermissionAlert(this);
            } else {
                setUpMap();
            }
        } else {
            setUpMap();
        }

    }
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    private void setUpMap() {

        if (gpsTracker.checkLocationState()) {
            gpsTracker.startLocationUpdates();
            LatLng latLang = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            //First time if you don't have latitude and longitude user default address
            if (gpsTracker.getLatitude() == 0) {

            }




        }else {
//ir a configuracion de gps
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.GPSAlertDialogTitle);
            alertDialog.setMessage(R.string.GPSAlertDialogMessage);
            alertDialog.setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.show();
            //gpsTracker.showSettingsAlert();
        }

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                if (cameraPosition != null) {
                    //  mMap.clear();
                    Log.e("centerLat", String.valueOf(cameraPosition.target.latitude));
                    Log.e("centerLong", String.valueOf(cameraPosition.target.longitude));
                    //muestra el nombre de la direccion en la que se encuenta la Latirud y longitud. en el TextView...
                    // mostrar_adrres(direccion.latitud, direccion.longitud);

                    //mueve a mi ubicacion

                    addressLatLng = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                    final String[] direccion = {"",""};
                    try {
                        if(sw_obteniendo_direccion==false) {
                            new Thread(new Runnable() {
                                public void run() {
                                    sw_obteniendo_direccion=true;
                                    direccion[0] =obtener_direccion( addressLatLng.latitude, addressLatLng.longitude);
                                    direccion[1] =obtener_direccion( myPosition.latitude, myPosition.longitude);

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            sw_obteniendo_direccion=false;
                                            try {
                                                tv_direccion_fin.setText(String.valueOf(direccion[0]));
                                                tv_ubicacion.setText(String.valueOf(direccion[1]));
                                            }catch (Exception e)
                                            {
                                                Log.e("addres:",e.toString());
                                            }

                                        }
                                    });
                                }
                            }).start();
                        }


                        float mm=mMap.getCameraPosition().zoom;

                        if(addressLatLng.latitude==0||addressLatLng.latitude==0||mm<13)
                        {
                            if(mm<13){
                                CameraPosition cameraPosition1 = new CameraPosition.Builder()
                                        .zoom(Float.parseFloat("15"))
                                        .target(myPosition)      // Sets the center of the map to Mountain View
                                        .bearing(0)                // Sets the orientation of the camera to east
                                        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                                        .build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                            }else{
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                            }
                        }
                    }catch (Exception e)
                    {

                    }
                    //////////////////get in AsyncTask//////////////////////
                    getAddressIntentService(cameraPosition.target.latitude, cameraPosition.target.longitude);

                } else {
                    getAddressIntentService(cameraPosition.target.latitude, cameraPosition.target.longitude);

                }
                //////////////////get in AsyncTask//////////////////////
            }
        });
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
                mMap.clear();
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

    @Override
    protected void onDestroy() {
        hilo_m.cancel(true);
        super.onDestroy();
    }

    // comenzar el servicio para la conexion con la base de datos.....
    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
// busca taxi dentro de su rango
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
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));
                        if (suceso.getSuceso().equals("1")) {
                            puntos_taxi=respuestaJSON.getJSONArray("taxi");
                            devuelve="1";
                        } else  {
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
                mMap.clear();

                agregar_en_mapa_ubicaciones_de_taxi();
                sw_ver_taxi_cerca=false;
            }else if(s.equals("20"))
            {
                sw_ver_taxi_cerca=false;
            }
            else
            {

                mensaje_error("Falla en tu conexión a Internet.");
                sw_ver_taxi_cerca=false;
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
                mensaje_error("Tu Dispositivo no tiene Conexion a Internet.");
                return false;
            }
        }
    }


    //principio de buscar direcciones..
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()){
            Log.v("Google API","Connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {

        if(mGoogleApiClient.isConnected()){
            Log.v("Google API","Dis-Connecting");
            mGoogleApiClient.disconnect();
            hilo_m.cancel(true);
        }
        super.onPause();
    }
    //fin de buscador de direcciones..



    public void agregar_en_mapa_ubicaciones_de_taxi() {
        try {
            for (int i = 0; i < puntos_taxi.length(); i++) {
                int rotacion = Integer.parseInt(puntos_taxi.getJSONObject(i).getString("rotacion"));
                double lat = Double.parseDouble(puntos_taxi.getJSONObject(i).getString("latitud"));
                double lon = Double.parseDouble(puntos_taxi.getJSONObject(i).getString("longitud"));
                String distancia= puntos_taxi.getJSONObject(i).getString("distancia");

                int moto=Integer.parseInt(puntos_taxi.getJSONObject(i).getString("moto"));
                if(moto==0){
                    cargar_puntos_movil(lat, lon,rotacion,distancia);
                }


            }
        } catch (Exception e) {

        }
        ver_moviles();
    }
    public void cargar_puntos_movil( double lat,double lon,int rotacion,String distancia)
    {
        try {

            LatLng punto = new LatLng(lat, lon);

            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(rotacion)
                    .title("Mtrs. "+distancia));

            this.limpiar_mapa=0;
        }
        catch (Exception e)
        {

        }

    }

    public void verificar_permiso_imei()
    {
        final String[] PERMISSIONS = { android.Manifest.permission.INTERNET,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso al ID del Telefono por tema de Seguridad.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Otra_direccion.this,
                            PERMISSIONS,
                            1);

                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();

                }
            });
            dialogo1.show();
        } else {
            ActivityCompat.requestPermissions(Otra_direccion.this,
                    PERMISSIONS,
                    1);
        }
    }


    public void verificar_permiso_sms()
    {
        final String[] SMS_PERMISSIONS = { android.Manifest.permission.INTERNET,
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_SMS)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de SMS para realizar la autenficación");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Otra_direccion.this,
                            SMS_PERMISSIONS,
                            1);

                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();

                }
            });
            dialogo1.show();
        } else {
            ActivityCompat.requestPermissions(Otra_direccion.this,
                    SMS_PERMISSIONS,
                    1);
        }
    }
    public void verificar_permiso_camara()
    {
        final String[] CAMERA_PERMISSIONS = { android.Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a CAMARA.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Otra_direccion.this,
                            CAMERA_PERMISSIONS,
                            1);

                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();

                }
            });
            dialogo1.show();
        } else {
            ActivityCompat.requestPermissions(Otra_direccion.this,
                    CAMERA_PERMISSIONS,
                    1);
        }
    }

    public void verificar_permiso_almacenamiento()
    {
        final String[] PERMISSIONS = { android.Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a ALMACENAMIENTO.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Otra_direccion.this,
                            PERMISSIONS,
                            1);

                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();

                }
            });
            dialogo1.show();
        } else {
            ActivityCompat.requestPermissions(Otra_direccion.this,
                    PERMISSIONS,
                    1);
        }
    }

    public void verificar_permiso_llamada()
    {
        final String[] PERMISSIONS = {Manifest.permission.CALL_PHONE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a LLAMADA.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Otra_direccion.this,
                            PERMISSIONS,
                            1);

                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();

                }
            });
            dialogo1.show();
        } else {
            ActivityCompat.requestPermissions(Otra_direccion.this,
                    PERMISSIONS,
                    1);
        }
    }




}
