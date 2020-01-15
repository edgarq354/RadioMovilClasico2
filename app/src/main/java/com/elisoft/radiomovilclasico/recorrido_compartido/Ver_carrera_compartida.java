package com.elisoft.radiomovilclasico.recorrido_compartido;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Ver_carrera_compartida extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,
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

    int id_pedido,id_carrera,id_conductor,estado_pedido,estado_carrera,id_usuario_compartido;
    String fecha_inicio="";

    private String nombre_pasajero="";
    private String apellido_pasajero="";
    private String conductor="";
    private String celular_conductor="";
    private String celular_pasajero="";
    private String marca="";
    private String color="";
    private String razon_social="";
    String placa="";
    String id_empresa="";
    String url="";

    String nombre_url="";

    ImageView im_ruta;


Button bt_perfil_usuario,bt_perfil_conductor;


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_carrera_compartida);
        bt_perfil_usuario=(Button)findViewById(R.id.bt_perfil_usuario);
        bt_perfil_conductor=(Button)findViewById(R.id.bt_perfil_conductor);
        im_ruta=(ImageView)findViewById(R.id.im_ruta);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        try{
            Bundle bundle=getIntent().getExtras();
            id_pedido=bundle.getInt("id_pedido");
            id_carrera=bundle.getInt("id_carrera");
            id_conductor=bundle.getInt("id_conductor");
            id_usuario_compartido=bundle.getInt("id_usuario");
            estado_carrera=bundle.getInt("estado_carrera");
            estado_pedido=bundle.getInt("estado_pedido");
            fecha_inicio=bundle.getString("fecha_inicio");

            nombre_pasajero=bundle.getString("nombre_pasajero");
            apellido_pasajero=bundle.getString("apellido_pasajero");
            conductor=bundle.getString("conductor");
            celular_conductor=bundle.getString("celular_conductor");
            celular_pasajero=bundle.getString("celular_pasajero");
            marca=bundle.getString("marca");
            color=bundle.getString("color");
            razon_social=bundle.getString("razon_social");
            placa=bundle.getString("placa");
            id_empresa=bundle.getString("id_empresa");
            url=bundle.getString("url");
        }catch (Exception e)
        {
            finish();
        }

        bt_perfil_conductor.setOnClickListener(this);
        bt_perfil_usuario.setOnClickListener(this);


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

        if(estado_carrera==2){
            im_ruta.setVisibility(View.VISIBLE);
            String st_mapa=url;

            String st_nombre=""+id_pedido+"_"+id_carrera+".jpg";
            if(carrera_en_vista(im_ruta,st_nombre)==false)
            {
                getImage(st_mapa,st_nombre);
            }

        }else{
            im_ruta.setVisibility(View.INVISIBLE);
        }
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
            case R.id.bt_perfil_usuario:

                Intent intent=new Intent(getApplicationContext(),Perfil_pasajero_compartido.class);
                intent.putExtra("id_usuario",id_usuario_compartido);
                intent.putExtra("nombre",nombre_pasajero);
                intent.putExtra("apellido",apellido_pasajero);
                intent.putExtra("celular",celular_pasajero);
                startActivity(intent);
                break;
            case R.id.bt_perfil_conductor:
                Intent intent2=new Intent(getApplicationContext(),Perfil_conductor_compartido.class);
                intent2.putExtra("id_conductor",id_conductor);
                intent2.putExtra("nombre",conductor);
                intent2.putExtra("celular",celular_conductor);
                intent2.putExtra("marca",marca);
                intent2.putExtra("placa",placa);
                intent2.putExtra("color",color);
                intent2.putExtra("empresa",razon_social);
                intent2.putExtra("id_empresa",id_empresa);
                startActivity(intent2);

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
                            status.startResolutionForResult(Ver_carrera_compartida.this, PETICION_CONFIG_UBICACION);
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
        if (ActivityCompat.checkSelfPermission(Ver_carrera_compartida.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest,Ver_carrera_compartida.this);
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

            LatLng fin = new LatLng(0, 0);
            //fin = ultimo_registro(Integer.parseInt(sid_pedido));
            fin=ultimo_registro();
            ultima_ubicacion=fin;
            mMap.clear();

            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_movil))
                    .position(fin)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .title(""));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(ultima_ubicacion)      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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






    public boolean carrera_en_vista(ImageView imagen, String nombre)
    { boolean sw_carrera=false;

        String mPath = Environment.getExternalStorageDirectory() + File.separator + "Taxi Corp/historial-compartido"
                + File.separator +nombre;


        File newFile = new File(mPath);
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);

        if( bitmap!=null)
        {
            imagen.setImageBitmap(bitmap);
            imagen.setAdjustViewBounds(true);
            imagen.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imagen.setPadding(0, 0, 0, 0);
            sw_carrera=true;
        }

        return sw_carrera;
    }


    private void getImage(String id, String nombre)//
    {this.nombre_url=nombre;
        class GetImage extends AsyncTask<String,Void,Bitmap> {
            ImageView bmImage;
            String nombre;


            public GetImage(ImageView bmImage, String nombre) {
                this.bmImage = bmImage;
                this.nombre=nombre;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                //se edita la imagen para ponerlo en circulo.

                if( bitmap==null)
                { }
                else
                {
                    bmImage.setImageBitmap(bitmap);
                    bmImage.setAdjustViewBounds(true);
                    bmImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    bmImage.setPadding(0, 0, 0, 0);
                    guardar_en_memoria(bitmap,nombre);
                }


            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = strings[0];//hace consulta ala Bd para recurar la imagen

                Bitmap mIcon = null;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
                return mIcon;
            }
        }

        GetImage gi = new GetImage(im_ruta,nombre);
        gi.execute(id);
    }

    private void guardar_en_memoria(Bitmap bitmapImage, String nombre)
    {
        File file=null;
        FileOutputStream fos = null;
        try {
            String APP_DIRECTORY = "Taxi Corp/";//nombre de directorio
            String MEDIA_DIRECTORY = APP_DIRECTORY + "historial-compartido";//nombre de la carpeta
            file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
            File mypath=new File(file,nombre);//nombre del archivo imagen

            boolean isDirectoryCreated = file.exists();//pregunto si esxiste el directorio creado
            if(!isDirectoryCreated)
                isDirectoryCreated = file.mkdirs();

            if(isDirectoryCreated) {
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
