package com.elisoft.radiomovilclasico.menu_otra_direccion;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.AndroidPermissions;
import com.elisoft.radiomovilclasico.Buscador.PlacesAutoCompleteAdapter;
import com.elisoft.radiomovilclasico.Buscador.RecyclerItemClickListener;
import com.elisoft.radiomovilclasico.Constants;
import com.elisoft.radiomovilclasico.GeocodeAddressIntentService;
import com.elisoft.radiomovilclasico.NewGPSTracker;
import com.elisoft.radiomovilclasico.Pedido_usuario;
import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.radiomovilclasico.Suceso;
import com.elisoft.radiomovilclasico.agregar_direccion.Agregar_direccion_nuevo;
import com.elisoft.radiomovilclasico.perfil.CDireccion;
import com.elisoft.radiomovilclasico.perfil.Item_direccion;
import com.elisoft.radiomovilclasico.perfil.Perfil_pasajero;
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
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import java.util.List;

public class Buscar_direccion_inicio extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener,TextView.OnEditorActionListener {

    boolean sw_ver_taxi_cerca = false;
    Servicio_ver_movil hilo_m;
    private JSONArray puntos_taxi;
    JSONObject rutas=null;

    ListView lv_direccion;
    ArrayList<CDireccion> direccion;

    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PETICION_CONFIG_UBICACION = 201;
    private Context mContext;
    private GoogleApiClient apiClient;

    private LocationRequest locRequest;

    LatLng myPosition;
    String s_direccion="";
    double latitude_incio=0,longitude_inicio=0;
    Suceso suceso;



    private AddressResultReceiver mResultReceiver;
    int fetchType = Constants.USE_ADDRESS_LOCATION;
    private LatLng addressLatLng;
    private NewGPSTracker gpsTracker;


    int limpiar_mapa=0;


    private GoogleMap mMap;


    int sw_acercar_a_mi_ubicacion;

    double latitud_buscador=0,longitud_buscador=0;

    boolean sw_obteniendo_direccion;

    LocationManager manager = null;


    AlertDialog alert = null;



LinearLayout ll_agregar_en_otro_momento;
TextView tv_direccion_casa,tv_direccion_trabajo;
ImageView ib_agregar_casa,ib_agregar_trabajo;

    SharedPreferences mis_datos;
    //  inicio de modificacion de pedir taxi
    protected GoogleApiClient mGoogleApiClient;

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-24,-68), new LatLng(-8,-58));





    @Override
    protected void onRestart() {
        cargar_direcciones();
        super.onRestart();
    }
//fin de buscador de ubicacion...


    private EditText mAutocompleteView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    ImageView delete;


    LinearLayout bt_fijar_ubicacion;

    SharedPreferences casa=null;
    SharedPreferences trabajo=null;

    TextView tv_casa,tv_trabajo;
    LinearLayout ll_casa,ll_trabajo;
    AlertDialog alert2 = null;

    @Override
    protected void onStart() {
        boolean sw=estaConectado();

        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_buscar_direccion_inicio);
        bt_fijar_ubicacion=(LinearLayout)findViewById(R.id.bt_fijar_ubicacion);
        ll_agregar_en_otro_momento=(LinearLayout)findViewById(R.id.ll_agregar_en_otro_momento);
        tv_direccion_casa=(TextView)findViewById(R.id.tv_direccion_casa);
        tv_direccion_trabajo=(TextView)findViewById(R.id.tv_direccion_trabajo);
        lv_direccion=(ListView)findViewById(R.id.lv_direccion);

        ll_casa=(LinearLayout)findViewById(R.id.ll_casa);
        ll_trabajo=(LinearLayout)findViewById(R.id.ll_trabajo);

        tv_casa=(TextView) findViewById(R.id.tv_casa);
        tv_trabajo=(TextView) findViewById(R.id.tv_trabajo);


        ib_agregar_casa=(ImageView)findViewById(R.id.ib_agregar_casa);
        ib_agregar_trabajo=(ImageView)findViewById(R.id.ib_agregar_trabajo);

         casa=getSharedPreferences(getString(R.string.direccion_casa),MODE_PRIVATE);

         trabajo=getSharedPreferences(getString(R.string.direccion_trabajo),MODE_PRIVATE);




ll_agregar_en_otro_momento.setOnClickListener(this);
ll_casa.setOnClickListener(this);
ll_trabajo.setOnClickListener(this);
ib_agregar_casa.setOnClickListener(this);
ib_agregar_trabajo.setOnClickListener(this);
bt_fijar_ubicacion.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        sw_acercar_a_mi_ubicacion = 0;



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.



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
            latitude_incio=  bundle.getDouble("latitud_inicio",0);
            longitude_inicio= bundle.getDouble("longitud_inicio",0);

            if(latitude_incio!=0 && longitude_inicio!=0){
                sw_acercar_a_mi_ubicacion=2;
            }
        }catch (Exception e)
        {
            sw_acercar_a_mi_ubicacion=0;
        }

        sw_obteniendo_direccion=false;
        hilo_m = new Servicio_ver_movil();

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





        buildGoogleApiClient();


        mAutoCompleteAdapter =  new PlacesAutoCompleteAdapter(this, R.layout.searchview_adapter,
                mGoogleApiClient, BOUNDS_INDIA, null);

        mLinearLayoutManager=new LinearLayoutManager(this);


        //fin de implementacion de buscador de ubicaicon.



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);






        buildGoogleApiClient();
        mAutocompleteView = (EditText)findViewById(R.id.autocomplete_places);

        delete=(ImageView)findViewById(R.id.cross);

        mAutoCompleteAdapter =  new PlacesAutoCompleteAdapter(this, R.layout.searchview_adapter,
                mGoogleApiClient, BOUNDS_INDIA, null);

        mRecyclerView=(RecyclerView)findViewById(R.id.recyclerView);
    
        mLinearLayoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAutoCompleteAdapter);
        delete.setOnClickListener(this);

        mAutocompleteView.setOnEditorActionListener(this);


        mAutocompleteView.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAutoCompleteAdapter.getFilter().filter(s.toString());

                }else if(!mGoogleApiClient.isConnected()){
                    Toast.makeText(getApplicationContext(), Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
                    Log.e(Constants.PlacesTag, Constants.API_NOT_CONNECTED);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });




        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                        final String placeId = String.valueOf(item.placeId);
                        Log.i("TAG", "Autocomplete item selected: " + item.description);
                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */

                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                .getPlaceById(mGoogleApiClient, placeId);
                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if(places.getCount()==1){
                                    //Do the things here on Click.....
                                    //   Toast.makeText(getApplicationContext(),String.valueOf(places.get(0).getLatLng()),Toast.LENGTH_SHORT).show();
                                    try{
                                        latitud_buscador=places.get(0).getLatLng().latitude;
                                        longitud_buscador=places.get(0).getLatLng().longitude;
                                        // tv_ubicacion.setText(item.description);
                                        sw_acercar_a_mi_ubicacion=2;

                                        mAutocompleteView.setText("");
                                        mAutoCompleteAdapter =  new PlacesAutoCompleteAdapter(Buscar_direccion_inicio.this, R.layout.searchview_adapter,
                                                mGoogleApiClient, BOUNDS_INDIA, null);
                                        mRecyclerView.setAdapter(mAutoCompleteAdapter);

                                        String direccion =obtener_direccion( latitud_buscador, longitud_buscador);
                                        sw_obteniendo_direccion=false;
                                        try {



                                            SharedPreferences ultimo = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                                            if (ultimo.getString("id_pedido", "").equals("") == true || ultimo.getString("id_pedido", "0").equals("0") == true ) {

                                                if(latitud_buscador==0 && longitud_buscador==0){
                                                    Toast.makeText(getApplicationContext(),"Marque su ubicación final ", Toast.LENGTH_LONG).show();
                                                }else if(latitude_incio==0 && longitude_inicio==0)
                                                {
                                                    Toast.makeText(getApplicationContext(),"Marque su ubicación inicial ", Toast.LENGTH_LONG).show();
                                                }else {
                                                    Intent tarifa=new Intent(getApplicationContext(),Calcular_tarifa_confirmar.class);
                                                    double latitud=latitude_incio;
                                                    double longitud=longitude_inicio;
                                                    double latitud_fin=latitud_buscador;
                                                    double longitud_fin=longitud_buscador;
                                                    tarifa.putExtra("latitud_inicio",latitud);
                                                    tarifa.putExtra("longitud_inicio",longitud);
                                                    tarifa.putExtra("latitud_fin",latitud_fin);
                                                    tarifa.putExtra("longitud_fin",longitud_fin);
                                                    startActivity(tarifa);
                                                }



                                            } else {
                                                finish();
                                            }







                                        }catch (Exception e)
                                        {
                                            Log.e("addres:",e.toString());
                                        }


                                    }catch (Exception e)
                                    {
                                        sw_acercar_a_mi_ubicacion=0;
                                    }

                                }else {
                                    Toast.makeText(getApplicationContext(), Constants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        Log.i("TAG", "Clicked: " + item.description);
                        Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId);
                    }
                })
        );

        //fin de implementacion de buscador de ubicaicon.



        cargar_direcciones();
        cargar_direccion_en_la_lista();

        lv_direccion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    CDireccion hi=new CDireccion();
                    hi=direccion.get(i);

                    Intent tarifa=new Intent(getApplicationContext(),Calcular_tarifa_confirmar.class);
                    double latitud=latitude_incio;
                    double longitud=longitude_inicio;
                    double latitud_fin=hi.getLatitud();
                    double longitud_fin=hi.getLongitud();
                    tarifa.putExtra("latitud_inicio",latitud);
                    tarifa.putExtra("longitud_inicio",longitud);
                    tarifa.putExtra("latitud_fin",latitud_fin);
                    tarifa.putExtra("longitud_fin",longitud_fin);
                    startActivity(tarifa);
                }catch (Exception e)
                {
                    Log.e("carrera",e.toString());
                }

            }
        });


        mis_datos=getSharedPreferences(getString(R.string.mis_datos),MODE_PRIVATE);

    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            startActivity(new Intent(this,Otra_direccion.class));
            finish();
            return true;
        }

        return false;
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
        //envia los parametros al Activity principal MENU_P... con los parametros de ltitud y longitud
        /*
        Toast.makeText(this,"latitud "+latitud+" longitud "+longitud,Toast.LENGTH_SHORT).show();
        Intent menu=new Intent(this,Datos_de_pedido.class);
        menu.putExtra("latitud",latitud);
        menu.putExtra("longitud",longitud);
        startActivity(menu);
        */
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
        ver_moviles();
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

















    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Controles UI
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
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
            LatLng lng=new LatLng(0,0);


        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
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
                            status.startResolutionForResult(Buscar_direccion_inicio.this, PETICION_CONFIG_UBICACION);
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
        if (ActivityCompat.checkSelfPermission(Buscar_direccion_inicio.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest, Buscar_direccion_inicio.this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.

        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
        Log.v("Google API Callback","Connection Failed");
        Log.v("Error Code", String.valueOf(result.getErrorCode()));
        Toast.makeText(this, Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
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

                } catch (Exception e) {
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
        Log.e(getString(R.string.app_name), "Starting Service");
        startService(intent);
    }

    @Override
    public void onClick(View view) {
        SharedPreferences ultimo = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        double lat_direccion=0;
        double lon_direccion=0;
        switch (view.getId())
        {
            case R.id.bt_fijar_ubicacion:
                startActivity(new Intent(this,Otra_direccion.class));
                finish();
                break;
            case R.id.ll_agregar_en_otro_momento:

                if (ultimo.getString("id_pedido", "").equals("") == true || ultimo.getString("id_pedido", "0").equals("0") == true ) {

                    if(latitude_incio==0 && longitude_inicio==0)
                    {
                        Toast.makeText(getApplicationContext(),"Marque su ubicación inicial ", Toast.LENGTH_LONG).show();
                    }else {

                        escribir_referencia(1,0,"solicitando taxi");

                    }
                } else {
                    finish();
                }
                break;
            case R.id.ll_casa:

               lat_direccion=Double.parseDouble(casa.getString("latitud","0"));
               lon_direccion=Double.parseDouble(casa.getString("longitud","0"));

                if (ultimo.getString("id_pedido", "").equals("") == true || ultimo.getString("id_pedido", "0").equals("0") == true ) {

                    if(latitude_incio==0 || longitude_inicio==0 || lat_direccion==0 || lon_direccion==0)
                    {
                        Toast.makeText(getApplicationContext(),"Agregar una nueva dirección de casa.", Toast.LENGTH_LONG).show();
                        abrir_direccion_nuevo(1);
                    }else {
                        Intent tarifa=new Intent(getApplicationContext(),Calcular_tarifa_confirmar.class);
                        double latitud=latitude_incio;
                        double longitud=longitude_inicio;
                        double latitud_fin=lat_direccion;
                        double longitud_fin=lon_direccion;
                        tarifa.putExtra("latitud_inicio",latitud);
                        tarifa.putExtra("longitud_inicio",longitud);
                        tarifa.putExtra("latitud_fin",latitud_fin);
                        tarifa.putExtra("longitud_fin",longitud_fin);
                        startActivity(tarifa);
                    }
                }

                break;


            case R.id.ll_trabajo:
                 lat_direccion=Double.parseDouble(trabajo.getString("latitud","0"));
                 lon_direccion=Double.parseDouble(trabajo.getString("longitud","0"));

                if (ultimo.getString("id_pedido", "").equals("") == true || ultimo.getString("id_pedido", "0").equals("0") == true ) {

                    if(latitude_incio==0 || longitude_inicio==0 || lat_direccion==0 ||  lon_direccion==0)
                    {
                        Toast.makeText(getApplicationContext(),"Agregar una nueva dirección de trabajo.", Toast.LENGTH_LONG).show();
                        abrir_direccion_nuevo(2);
                    }else {
                        Intent tarifa=new Intent(getApplicationContext(),Calcular_tarifa_confirmar.class);
                        double latitud=latitude_incio;
                        double longitud=longitude_inicio;
                        double latitud_fin=lat_direccion;
                        double longitud_fin=lon_direccion;
                        tarifa.putExtra("latitud_inicio",latitud);
                        tarifa.putExtra("longitud_inicio",longitud);
                        tarifa.putExtra("latitud_fin",latitud_fin);
                        tarifa.putExtra("longitud_fin",longitud_fin);
                        startActivity(tarifa);
                    }
                }

                break;

            case R.id.ib_agregar_casa:

                abrir_direccion_nuevo(1);
                break;
            case R.id.ib_agregar_trabajo:

                abrir_direccion_nuevo(2);
                break;
        }

    }

    private void abrir_direccion_nuevo(int i) {
        Intent casa=new Intent(this, Agregar_direccion_nuevo.class);
        casa.putExtra("direccion",i);
        startActivity(casa);
    }


        public void pedir_taxi(String numero, String referencia, int clase_vehiculo, int tipo_pedido_empresa){
            ///verifica si el GPS esta activo.
            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                AlertNoGps();
            }
            else {
                if (existe_celular() == true) {


                        Intent datos_pedido = new Intent(this, Pedido_usuario.class);

                    double latitud=latitude_incio;
                    double longitud=longitude_inicio;

                        datos_pedido.putExtra("latitud",latitud);
                        datos_pedido.putExtra("longitud",longitud);
                        datos_pedido.putExtra("latitud_final", latitud);
                        datos_pedido.putExtra("longitud_final",longitud);
                        datos_pedido.putExtra("referencia", referencia);
                        datos_pedido.putExtra("numero",numero);
                        datos_pedido.putExtra("clase_vehiculo",clase_vehiculo);
                        datos_pedido.putExtra("direccion",s_direccion);
                        datos_pedido.putExtra("direccion_final",s_direccion);
                        datos_pedido.putExtra("tipo_pedido_empresa",tipo_pedido_empresa);
                        startActivity(datos_pedido);

                } else {
                    actualizar_perfil();
                }
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
        if (perfil.getString("celular", "").equals("") == true) {
            sw = false;
        }
        return sw;
    }


    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
                    final String[] direccion = {""};
                    try {
                        if(sw_obteniendo_direccion==false) {
                            new Thread(new Runnable() {
                                public void run() {
                                    sw_obteniendo_direccion=true;
                                    direccion[0] =obtener_direccion( addressLatLng.latitude, addressLatLng.longitude);

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            sw_obteniendo_direccion=false;
                                            try {
                                                //   tv_direccion.setText(String.valueOf(direccion[0]));
                                                s_direccion= String.valueOf(direccion[0]);
                                            }catch (Exception e)
                                            {
                                                Log.e("addres:",e.toString());
                                            }

                                        }
                                    });
                                }
                            }).start();
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
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            Log.v("Google API","Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }
    //fin de buscador de direcciones..














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

    public void cargar_direcciones()
    {
        double lat_direccion=Double.parseDouble(casa.getString("latitud","0"));
        double lon_direccion=Double.parseDouble(casa.getString("longitud","0"));

        if(lat_direccion!=0 &&lon_direccion!=0)
        {
            tv_direccion_casa.setText(casa.getString("direccion","Agregar dirección de casa"));
            tv_casa.setVisibility(View.VISIBLE);
        }else
        {
            tv_casa.setVisibility(View.INVISIBLE);
        }

        lat_direccion=Double.parseDouble(trabajo.getString("latitud","0"));
        lon_direccion=Double.parseDouble(trabajo.getString("longitud","0"));

        if(lat_direccion!=0 &&lon_direccion!=0)
        {
            tv_direccion_trabajo.setText(trabajo.getString("direccion","Agregar dirección de trabajo"));
            tv_trabajo.setVisibility(View.VISIBLE);
        }else
        {
            tv_trabajo.setVisibility(View.INVISIBLE);
        }

    }


    public void  escribir_referencia(final int clase_vehiculo,final int tipo_pedido_empresa,String tipo_pedido_texto)
    {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.escribir_referencia, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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
                pedir_taxi("0", et_referencia.getText().toString().trim(),clase_vehiculo,tipo_pedido_empresa);
                alert2.cancel();

            }
        });
        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();
    }


    public void cargar_direccion_en_la_lista() {
        direccion = new ArrayList<CDireccion>();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select id,latitud,longitud,nombre,detalle from direccion   ORDER BY id DESC ", null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)



            do {
                int id= Integer.parseInt(fila.getString(0));
                double latitud= Double.parseDouble(fila.getString(1));
                double longitud= Double.parseDouble(fila.getString(2));

                String nombre= String.valueOf(fila.getString(3));
                String detalle= String.valueOf(fila.getString(4));
                CDireccion hi = new CDireccion(id,nombre,detalle,latitud,longitud);

                direccion.add(hi);
            } while (fila.moveToNext());

        } else
            Toast.makeText(this, "No hay registrados",
                    Toast.LENGTH_SHORT).show();

        bd.close();
        actualizar_lista();
    }

    public void actualizar_lista() {

        Item_direccion adaptador = new Item_direccion(this, this,direccion);
        lv_direccion.setAdapter(adaptador);



    }






}
