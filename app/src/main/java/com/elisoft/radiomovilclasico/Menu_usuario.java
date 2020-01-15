package com.elisoft.radiomovilclasico;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.Scanear.QR_verificar_vehiculo;
import com.elisoft.radiomovilclasico.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.radiomovilclasico.corporativo.Empresa;
import com.elisoft.radiomovilclasico.guia_turistica.Guia_comercial_catergoria;
import com.elisoft.radiomovilclasico.historial_notificacion.Notificacion;
import com.elisoft.radiomovilclasico.informacion.Informacion;
import com.elisoft.radiomovilclasico.menu_otra_direccion.Buscar_direccion_inicio;
import com.elisoft.radiomovilclasico.notificaciones.SharedPrefManager;
import com.elisoft.radiomovilclasico.perfil.Billetera;
import com.elisoft.radiomovilclasico.perfil.Compartir_amigo;
import com.elisoft.radiomovilclasico.perfil.Mis_direcciones;
import com.elisoft.radiomovilclasico.perfil.Perfil_pasajero;
import com.elisoft.radiomovilclasico.preregistro.Verificar_numero;
import com.elisoft.radiomovilclasico.registro_inicio_sesion.Animacion;
import com.elisoft.radiomovilclasico.reserva.Historial_reserva;
import com.elisoft.radiomovilclasico.tarifario.Tarifa;
import com.elisoft.radiomovilclasico.viajes.Viajes;
import com.elisoft.radiomovilclasico.video_tutorial.Menu_video;
import com.facebook.login.LoginManager;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.support.v7.app.AlertDialog.Builder;
import static android.support.v7.app.AlertDialog.OnClickListener;


public class Menu_usuario extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {
    @Override
    protected void onRestart() {
        super.onRestart();
        ver_moviles();
    }

    @Override
    protected void onStart() {

        ///verifica si el GPS esta activo.
        cantidad_conexion=0;
        if (estaConectado()) {

            actualizar();
            enableLocationUpdates();

        }
        sw_acercar_a_mi_ubicacion = 0;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onStart();

    }


    LocationManager manager = null;
    AlertDialog alert = null;


    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PETICION_CONFIG_UBICACION = 201;
    private Context mContext;
    private GoogleApiClient apiClient;

    private LocationRequest locRequest;

    Builder dialogo1;
    ProgressDialog pDialog, pUbicacion;


    int limpiar_mapa = 0;

    public TextView ciudad;
    private GoogleMap mMap;
    private TextView ubicacion, tv_ubicacion;
    LinearLayout pedi_taxi,pedir_movil_lujo,pedir_movil_maletero,pedir_movil_aire,pedir_movil_pedido, pedir_movil_reserva, buscar_direccion,pedir_moto;
    private int sw_iteraccion;
    FloatingActionButton ver_taxi,fb_llamar,fb_whatsapp;

    int sw_acercar_a_mi_ubicacion;
    boolean sw_ver_taxi_cerca = false;

    private JSONArray puntos_taxi;

    LatLng myPosition;
    Suceso suceso;
    SharedPreferences mis_datos;

    private AddressResultReceiver mResultReceiver;
    int fetchType = Constants.USE_ADDRESS_LOCATION;
    private LatLng addressLatLng;
    private NewGPSTracker gpsTracker;


    boolean sw_obteniendo_direccion;

    AlertDialog alert2 = null;

    int version=0;
    int cantidad_conexion=0;

    CheckBox cb_tipo_pedido_empresa;

    Servicio_ver_movil hilo_m;





    Marker marker_1=null;
    Marker marker_2=null;
    Marker marker_3=null;
    Marker marker_4=null;
    Marker marker_5=null;
    Marker marker_6=null;
    Marker marker_7=null;
    Marker marker_8=null;
    Marker marker_9=null;
    Marker marker_10=null;
    String cond_1="";
    String cond_2="";
    String cond_3="";
    String cond_4="";
    String cond_5="";
    String cond_6="";
    String cond_7="";
    String cond_8="";
    String cond_9="";
    String cond_10="";

    String fecha_1="";
    String fecha_2="";
    String fecha_3="";
    String fecha_4="";
    String fecha_5="";
    String fecha_6="";
    String fecha_7="";
    String fecha_8="";
    String fecha_9="";
    String fecha_10="";

    String fecha_ultimo="";

    Handler handle=new Handler();
    int interseccion=0;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_usuario);



        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        sw_obteniendo_direccion = false;

        ubicacion = (TextView) findViewById(R.id.ubicacion);
        tv_ubicacion = (TextView) findViewById(R.id.tv_ubicacion);
        cb_tipo_pedido_empresa= (CheckBox)findViewById(R.id.cb_tipo_pedido_empresa);
        pedi_taxi = (LinearLayout) findViewById(R.id.pedir_movil);
        pedir_movil_aire = (LinearLayout) findViewById(R.id.pedir_movil_aire);
        pedir_movil_lujo = (LinearLayout) findViewById(R.id.pedir_movil_lujo);
        pedir_movil_maletero = (LinearLayout) findViewById(R.id.pedir_movil_maletero);
        pedir_movil_pedido = (LinearLayout) findViewById(R.id.pedir_movil_pedido);
        pedir_movil_reserva = (LinearLayout) findViewById(R.id.pedir_movil_reserva);
        buscar_direccion = (LinearLayout) findViewById(R.id.buscar_direccion);
        pedir_moto = (LinearLayout)findViewById(R.id.pedir_moto);
        ver_taxi = (FloatingActionButton) findViewById(R.id.ver_movil);
        fb_llamar = (FloatingActionButton) findViewById(R.id.fb_llamar);
        fb_whatsapp = (FloatingActionButton) findViewById(R.id.fb_whatsapp);
        hilo_m=new Servicio_ver_movil();

        sw_iteraccion = 0;

        dialogo1 = new AlertDialog.Builder(this);

        //veerificar_ login usuario----
        SharedPreferences perfil = getSharedPreferences("perfil", MODE_PRIVATE);
        if (perfil.getString("login_usuario", "0").equals("0") || perfil.getString("id_usuario", "").equals("") == true) {
            cerrar_sesion();
        } else {
            if(existe_celular()==false)
            { actualizar_perfil();
            }
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Codigo del menu desplegable ...
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                // carga los datos de su perfil. al momento de deslizar el menu. de perfil....
                TextView nombre, celular;
                ImageView perfil;
                nombre = (TextView) drawerView.findViewById(R.id.nombre_completo);
                celular = (TextView) drawerView.findViewById(R.id.celular);
                perfil = (ImageView) drawerView.findViewById(R.id.perfil);
                try {
                    SharedPreferences prefe = getSharedPreferences("perfil", MODE_PRIVATE);
                    nombre.setText(prefe.getString("nombre", "") + " " + prefe.getString("apellido", ""));
                    celular.setText("+591 " + prefe.getString("celular", ""));
                    imagen_en_vista(perfil);

                    Intent intent = new Intent(Menu_usuario.this, Servicio_descargar_imagen_perfil.class);
                    intent.setAction(Constants.ACTION_RUN_ISERVICE);
                    intent.putExtra("id_usuario",Integer.parseInt(prefe.getString("id_usuario", "0")));
                    startService(intent);

                } catch (Exception e) {
                }

            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        pUbicacion = new ProgressDialog(Menu_usuario.this);
        pUbicacion.setTitle(getString(R.string.app_name));
        pUbicacion.setMessage("Espere un momento.\nEstamos calculando su ubicación." + Html.
                fromHtml("<br>Si Tarde demaciado. Porfavor active su <b>GPS</b> y sus <b>Datos de Red</b>."));
        pUbicacion.setIndeterminate(true);
        pUbicacion.setCancelable(false);
        pUbicacion.show();


// localizacion automatica
        //Construcción cliente API Google
        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        addressLatLng = new LatLng(0, 0);
        enableLocationUpdates();
        gpsTracker = new NewGPSTracker(getApplicationContext());
        //fin de la locatizaocion automatica...


        pedi_taxi.setOnClickListener(this);
        pedir_movil_lujo.setOnClickListener(this);
        pedir_movil_aire.setOnClickListener(this);
        pedir_movil_maletero.setOnClickListener(this);
        pedir_movil_pedido.setOnClickListener(this);
        pedir_movil_reserva.setOnClickListener(this);
        pedir_moto.setOnClickListener(this);

        buscar_direccion.setOnClickListener(this);
        ver_taxi.setOnClickListener(this);
        fb_llamar.setOnClickListener(this);
        fb_whatsapp.setOnClickListener(this);

        mContext = getApplicationContext();

        try {
            Bundle bundle=getIntent().getExtras();
            int finalizo_solicitud_pedido=bundle.getInt("finalizo_solicitud_pedido",0);
            if(finalizo_solicitud_pedido==1){
                AlertDialog.Builder builder = new AlertDialog.Builder(Menu_usuario.this);
                builder.setTitle("Ups");
                builder.setCancelable(false);
                builder.setMessage("Parece que ninguno de núestros conductores a logrado aceptar tu solicitud. Profavor vuelve a intentarlo o llamanos a núestra central de Radio Movil.");
                builder.create();
                builder.setPositiveButton("OK",  null);
                builder.show();
            }
        }catch (Exception e){

        }



    }

    private void actualizar_perfil() {



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

    public boolean existe_celular() {
        boolean sw = true;
        SharedPreferences perfil = getSharedPreferences("perfil", MODE_PRIVATE);
        if (perfil.getString("celular", "").equals("") == true  || perfil.getString("celular", "").toString().length()<7 ) {
            sw = false;
        }
        return sw;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // Toast.makeText(this, "cerrar", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_usuario, menu);
        return true;
    }
*/
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            actualizar();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

*/

    private void actualizar() {
        SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
        try {
            int id_usuario = Integer.parseInt(prefe.getString("id_usuario", ""));
            Servicio_actualizar hilo = new Servicio_actualizar();
            hilo.execute(getString(R.string.servidor) + "frmPedido.php?opcion=get_pedido_por_id_usuario", "6", String.valueOf(id_usuario));// parametro que recibe el doinbackground

        } catch (Exception e) {

        }
        sw_ver_taxi_cerca=false;

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.perfil:
                Intent perfil = new Intent(this, Perfil_pasajero.class);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    verificar_permiso_almacenamiento();
                                } else {  startActivity(perfil);   }



                break;
            case R.id.it_mensajes:
                startActivity(new Intent(this, Notificacion.class));
                break;

            case R.id.it_tarifario:
                startActivity(new Intent(this, Tarifa.class));
                break;
            case R.id.historial:
                Intent historial = new Intent(this, Viajes.class);
                  startActivity(historial);
                  break;
            case R.id.it_billetera:
                startActivity(new Intent(this,Billetera.class));
                break;
            case R.id.it_mis_direcciones:
                startActivity(new Intent(this,Mis_direcciones.class));
                break;
            case R.id.reservas:
                startActivity(new Intent(this, Historial_reserva.class));
                break;


            case R.id.informacion:
                startActivity(new Intent(this, Informacion.class));
                break;
            case R.id.it_otras:
                startActivity(new Intent(this,Otras_opciones.class));
                break;





            case R.id.it_compartir:
                startActivity(new Intent(this, Compartir_amigo.class));
                break;


            case R.id.it_whatsapp:
                verificar_todos_los_permisos();
                boolean isWhatsapp = appInstalledOrNot("com.whatsapp");
                if (isWhatsapp)
                    AbrirWhatsApp();
                break;

            case R.id.it_telefono:
                verificar_todos_los_permisos();
                llamar_radio_movil();
                break;

            case R.id.it_registrar_conductor:
                startActivity(new Intent(this, Verificar_numero.class));
                break;


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void enviar(String[] to, String[] cc,
                        String asunto, String mensaje) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_CC, cc);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        emailIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Email "));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Controles UI
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setIndoorEnabled(true);
            View mapView = (View) getSupportFragmentManager().findFragmentById(R.id.map).getView();
//bicacion del button de Myubicacion de el fragento..
           View btnMyLocation = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            View btnBrujula = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("5"));

            RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(120,120);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            params.setMargins(20, 0, 0, 0);
            btnMyLocation.setLayoutParams(params);

            params = new RelativeLayout.LayoutParams(120,120);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.setMargins(0, 0, 20, 20);
            btnBrujula.setLayoutParams(params);

            init();
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


    //servicio para ver los moviles
    public class Servicio_ver_movil extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
// busca taxi dentro de su rango
            if(!isCancelled()){
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
                // mMap.clear();
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

    @Override
    protected void onPause() {
        hilo_m.cancel(true);
        super.onPause();
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
                    devuelve="500";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // verificar si tiene un pedido que aun no ha finalizado....
            //obtener datos del pedido en curso.....
            if (params[1] == "5") { //mandar JSON metodo post para login
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

                        SystemClock.sleep(950);
                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
                            JSONArray dato=respuestaJSON.getJSONArray("pedido");
                            String snombre=dato.getJSONObject(0).getString("nombre_taxi");
                            String scelular=dato.getJSONObject(0).getString("celular");
                            String sid_taxi=dato.getJSONObject(0).getString("id_taxi");
                            String smarca=dato.getJSONObject(0).getString("marca");
                            String splaca=dato.getJSONObject(0).getString("placa");
                            String scolor=dato.getJSONObject(0).getString("color");
                            String sid_pedido=dato.getJSONObject(0).getString("id_pedido");
                            SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                            SharedPreferences.Editor editar=pedido.edit();
                            editar.putString("nombre_taxi",snombre);
                            editar.putString("celular",scelular);
                            editar.putString("id_taxi",sid_taxi);
                            editar.putString("marca",smarca);
                            editar.putString("placa",splaca);
                            editar.putString("color",scolor);
                            editar.putString("latitud",dato.getJSONObject(0).getString("latitud"));
                            editar.putString("longitud",dato.getJSONObject(0).getString("longitud"));
                            editar.putString("id_pedido",sid_pedido);
                            editar.commit();

                            devuelve="8";
                        } else  {
                            devuelve = "2";
                        }

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    devuelve="500";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //obtener pedido por id usuario..
            if (params[1] == "6") { //mandar JSON metodo post para login
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
                        SystemClock.sleep(950);

                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
                            JSONArray dato=respuestaJSON.getJSONArray("pedido");
                            String sid_pedido=dato.getJSONObject(0).getString("id");
                            String slatitud=dato.getJSONObject(0).getString("latitud");
                            String slongitud=dato.getJSONObject(0).getString("longitud");
                            SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                            SharedPreferences.Editor editar=pedido.edit();
                            editar.putString("id_pedido",sid_pedido);
                            editar.putString("latitud",slatitud);
                            editar.putString("longitud",slongitud);
                            editar.commit();

                            devuelve="9";
                        } else  {
                            SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                            SharedPreferences.Editor editar=pedido.edit();
                            editar.putString("id_pedido","");
                            editar.commit();

                            devuelve = "10";
                        }

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    if(cantidad_conexion<3){
                        devuelve="506";
                        cantidad_conexion++;
                    }else {
                        devuelve = "500";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //enviar pedir taxi..
            if (params[1] == "7") {
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
                    jsonParam.put("nombre",params[5]);
                    jsonParam.put("indicacion",params[6]);
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
                            devuelve="3";
                        } else {
                            devuelve = "5";
                        }

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    devuelve="500";
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
                //mMap.clear();

                agregar_en_mapa_ubicaciones_de_taxi();
                sw_ver_taxi_cerca=false;
            }
            else
            if(s.equals("3")||s.equals("5")||s.equals("4") )
            {
                Toast.makeText(Menu_usuario.this,suceso.getMensaje(), Toast.LENGTH_SHORT).show();
            }
            else  if(s.equals("2"))
            {
                //Toast.makeText(Menu_usuario.this,suceso.getMensaje(),Toast.LENGTH_SHORT).show();
            }
            else if(s.equals("8"))
            {
                Intent intent = new Intent(Menu_usuario.this, Servicio_pedido.class);
                intent.setAction(Constants.ACTION_RUN_ISERVICE);
                startService(intent);

                SharedPreferences spedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);

                Intent pedido=new Intent(Menu_usuario.this,Pedido_usuario.class);
                pedido.putExtra("latitud", Double.parseDouble(spedido.getString("latitud","0")));
                pedido.putExtra("longitud", Double.parseDouble(spedido.getString("longitud","0")));
                pedido.putExtra("id_pedido",spedido.getString("id_pedido","0"));
                startActivity( pedido);
            }else if(s.equals("9"))
            {
                Intent intent = new Intent(Menu_usuario.this, Servicio_pedido.class);
                intent.setAction(Constants.ACTION_RUN_ISERVICE);
                startService(intent);

                SharedPreferences spedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);

                Intent pedido=new Intent(Menu_usuario.this,Pedido_usuario.class);
                pedido.putExtra("latitud", Double.parseDouble(spedido.getString("latitud","0")));
                pedido.putExtra("longitud", Double.parseDouble(spedido.getString("longitud","0")));
                pedido.putExtra("id_pedido",spedido.getString("id_pedido","0"));
                startActivity( pedido);
            } else if (s.equals("10")) {
                iniciar_verificacion_version();
                //verificar version de la aplicacion.
            }   else if (s.equals("500"))
            {

            }else if(s.equals("506")){
                if(estaConectado()){
                    actualizar();
                }else{
                    finish();
                }
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

    // comenzar el servicio para la conexion con la base de datos.....
    public class Servicio_actualizar extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

            //obtener pedido por id usuario..
            if (params[1] == "6") { //mandar JSON metodo post para login

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
                        SystemClock.sleep(950);

                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
                            JSONArray dato=respuestaJSON.getJSONArray("pedido");
                            String sid_pedido=dato.getJSONObject(0).getString("id");
                            String slatitud=dato.getJSONObject(0).getString("latitud");
                            String slongitud=dato.getJSONObject(0).getString("longitud");
                            SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                            SharedPreferences.Editor editar=pedido.edit();
                            editar.putString("id_pedido",sid_pedido);
                            editar.putString("latitud",slatitud);
                            editar.putString("longitud",slongitud);
                            editar.commit();

                            devuelve="9";
                        } else  {
                            SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                            SharedPreferences.Editor editar=pedido.edit();
                            editar.putString("id_pedido","");
                            editar.commit();

                            devuelve = "10";
                        }

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    if(cantidad_conexion<3){
                        devuelve="506";
                        cantidad_conexion++;
                    }else {
                        devuelve = "500";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog
            try {
                //pb_cargando.setLayoutParams(wrap_content);
            }catch (Exception e)
            {

            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);
            try {
               // pb_cargando.setLayoutParams(cero);
            }catch (Exception e)
            {

            }


            if (s.equals("9")) {
                Intent intent = new Intent(Menu_usuario.this, Servicio_pedido.class);
                intent.setAction(Constants.ACTION_RUN_ISERVICE);
                startService(intent);

                SharedPreferences spedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);

                Intent pedido = new Intent(Menu_usuario.this, Pedido_usuario.class);
                pedido.putExtra("latitud", Double.parseDouble(spedido.getString("latitud", "0")));
                pedido.putExtra("longitud", Double.parseDouble(spedido.getString("longitud", "0")));
                pedido.putExtra("id_pedido", spedido.getString("id_pedido", "0"));
                startActivity(pedido);
            } else if (s.equals("10")) {
                iniciar_verificacion_version();
                //verificar version de la aplicacion.
            }   else if (s.equals("500"))
            {

            }else if(s.equals("506")){
                if(estaConectado()){
                    actualizar();
                }else{
                    finish();
                }
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
    public class Servicio_corporativo extends AsyncTask<String,Integer,String> {
        String id_empresa,nit,razon_social,monto_deuda,direccion;

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
            //enviar pedir taxi..

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

                        SystemClock.sleep(950);
                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));
                        if (suceso.getSuceso().equals("1")) {
                            id_empresa = respuestaJSON.getString("id_empresa");
                            nit = respuestaJSON.getString("nit");
                            razon_social = respuestaJSON.getString("razon_social");
                            direccion = respuestaJSON.getString("direccion");
                            monto_deuda = respuestaJSON.getString("monto_deuda");

                            devuelve = "1";
                        } else {
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
            pDialog = new ProgressDialog(Menu_usuario.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Verificando datos corporativos.");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);
            pDialog.cancel();//ocultamos proggress dialog


            if (s.equals("1")) {
                Intent menu_corporativo=new Intent(getApplicationContext(), Empresa.class);
                menu_corporativo.putExtra("id_empresa",id_empresa);
                menu_corporativo.putExtra("razon_social",razon_social);
                menu_corporativo.putExtra("direccion",direccion);
                menu_corporativo.putExtra("nit",nit);
                menu_corporativo.putExtra("monto_deuda",monto_deuda);
                startActivity(menu_corporativo);
            }
            else  if(s.equals("2"))
            {
                mensaje_error(suceso.getMensaje());
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
                String id= puntos_taxi.getJSONObject(i).getString("ci");
                String fecha= puntos_taxi.getJSONObject(i).getString("fecha");
                fecha_ultimo=fecha;
                int moto=Integer.parseInt(puntos_taxi.getJSONObject(i).getString("moto"));
                if(moto==0) {
                    cargar_puntos_movil(lat, lon, rotacion, distancia, id, fecha);
                }

            }
            for (int i = 0; i < puntos_taxi.length(); i++) {
                int rotacion = Integer.parseInt(puntos_taxi.getJSONObject(i).getString("rotacion"));
                double lat = Double.parseDouble(puntos_taxi.getJSONObject(i).getString("latitud"));
                double lon = Double.parseDouble(puntos_taxi.getJSONObject(i).getString("longitud"));
                String distancia= puntos_taxi.getJSONObject(i).getString("distancia");
                String id= puntos_taxi.getJSONObject(i).getString("ci");
                String fecha= puntos_taxi.getJSONObject(i).getString("fecha");

                int moto=Integer.parseInt(puntos_taxi.getJSONObject(i).getString("moto"));
                if(moto==0){
                    cargar_puntos_movil_segundo(lat, lon,rotacion,distancia,id,fecha);
                }

            }
            ocultar_conductores_no_activos();
        } catch (Exception e) {
            e.printStackTrace();
        }






        interseccion=0;

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (interseccion<6)
                {
                    interseccion=interseccion+1;
                    if(interseccion>=4) {
                        interseccion = 7;
                        handle.post(new Runnable() {
                            @Override
                            public void run() {
                                ver_moviles();
                            }


                        });
                    }
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e)
                    {
                        ver_moviles();
                        e.printStackTrace();
                    }
                }

            }
        }).start();


    }
    public void crear_puntos_conductor()
    {
        try {
            LatLng punto = myPosition;
            marker_1=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_2=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_3=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_4=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_5=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_6=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_7=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_8=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_9=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_10=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void cargar_puntos_movil( double lat,double lon,int rotacion,String distancia,String id,String fecha)
    {


        LatLng ubicacion=new LatLng(lat,lon);

        if(id.equals(cond_1)){
            fecha_1=fecha;
            marker_1.setVisible(true);
            marker_1.setRotation(rotacion);
            MarkerAnimation.animateMarkerToGB(marker_1, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_2)){
            fecha_2=fecha;
            marker_2.setVisible(true);
            marker_2.setRotation(rotacion);
            MarkerAnimation.animateMarkerToGB(marker_2, ubicacion, new LatLngInterpolator.Spherical());
        }
        else  if(id.equals(cond_3)){
            fecha_3=fecha;
            marker_3.setVisible(true);
            marker_3.setRotation(rotacion);
            MarkerAnimation.animateMarkerToGB(marker_3, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_4)){
            fecha_4=fecha;
            marker_4.setVisible(true);
            marker_4.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_4, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_5)){
            fecha_5=fecha;
            marker_5.setVisible(true);
            marker_5.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_5, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_6)){
            fecha_6=fecha;
            marker_6.setVisible(true);
            marker_6.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_6, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_7)){
            fecha_7=fecha;
            marker_7.setVisible(true);
            marker_7.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_7, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_8)){
            fecha_8=fecha;
            marker_8.setVisible(true);
            marker_8.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_8, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_9)){
            fecha_9=fecha;
            marker_9.setVisible(true);
            marker_9.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_9, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_10)){
            fecha_10=fecha;
            marker_10.setVisible(true);
            marker_10.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_10, ubicacion, new LatLngInterpolator.Spherical());
        }

    }

    public void cargar_puntos_movil_segundo( double lat,double lon,int rotacion,String distancia,String id,String fecha)
    {


        LatLng ubicacion=new LatLng(lat,lon);

        if(cond_1.equals(id)&&fecha_1.equals(fecha_ultimo))
        {

        }else if(cond_2.equals(id)&&fecha_2.equals(fecha_ultimo))
        {

        }else if(cond_3.equals(id)&&fecha_3.equals(fecha_ultimo))
        {

        }else if(cond_4.equals(id)&&fecha_4.equals(fecha_ultimo))
        {

        }else if(cond_5.equals(id)&&fecha_5.equals(fecha_ultimo))
        {

        }else if(cond_6.equals(id)&&fecha_6.equals(fecha_ultimo))
        {

        }else if(cond_7.equals(id)&&fecha_7.equals(fecha_ultimo))
        {

        }else if(cond_8.equals(id)&&fecha_8.equals(fecha_ultimo))
        {

        }else if(cond_9.equals(id)&&fecha_9.equals(fecha_ultimo))
        {

        }else if(cond_10.equals(id)&&fecha_10.equals(fecha_ultimo))
        {

        }else if(fecha_1.equals(fecha_ultimo)==false){
            fecha_1=fecha;
            cond_1=id;
            marker_1.setVisible(true);
            marker_1.setRotation(rotacion);
            MarkerAnimation.animateMarkerToGB(marker_1, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_2.equals(fecha_ultimo)==false){
            fecha_2=fecha;
            cond_2=id;
            marker_2.setVisible(true);
            marker_2.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_2, ubicacion, new LatLngInterpolator.Spherical());
        }
        else  if(fecha_3.equals(fecha_ultimo)==false){
            fecha_3=fecha;
            cond_3=id;
            marker_3.setVisible(true);
            marker_3.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_3, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_4.equals(fecha_ultimo)==false){
            fecha_4=fecha;
            cond_4=id;
            marker_4.setVisible(true);
            marker_4.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_4, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_5.equals(fecha_ultimo)==false){
            fecha_5=fecha;
            cond_5=id;
            marker_5.setVisible(true);
            marker_5.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_5, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_6.equals(fecha_ultimo)==false){
            fecha_6=fecha;
            cond_6=id;
            marker_6.setVisible(true);
            marker_6.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_6, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_7.equals(fecha_ultimo)==false){
            fecha_7=fecha;
            cond_7=id;
            marker_7.setVisible(true);
            marker_7.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_7, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_8.equals(fecha_ultimo)==false){
            fecha_8=fecha;
            cond_8=id;
            marker_8.setVisible(true);
            marker_8.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_8, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_9.equals(fecha_ultimo)==false){
            fecha_9=fecha;
            cond_9=id;
            marker_9.setVisible(true);
            marker_9.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_9, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_10.equals(fecha_ultimo)==false){
            fecha_10=fecha;
            cond_10=id;
            marker_10.setVisible(true);
            marker_10.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_10, ubicacion, new LatLngInterpolator.Spherical());
        }

    }

    public void cargar_puntos_moto_segundo( double lat,double lon,int rotacion,String distancia,String id,String fecha)
    {


        LatLng ubicacion=new LatLng(lat,lon);

        if(fecha_1.equals(fecha_ultimo)==false){
            marker_1.setRotation(rotacion);
            fecha_1=fecha;
            cond_1=id;
            MarkerAnimation.animateMarkerToGB(marker_1, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_2.equals(fecha_ultimo)==false){
            marker_2.setRotation(rotacion);
            fecha_2=fecha;
            cond_2=id;
            MarkerAnimation.animateMarkerToGB(marker_2, ubicacion, new LatLngInterpolator.Spherical());
        }
        else  if(fecha_3.equals(fecha_ultimo)==false){
            marker_3.setRotation(rotacion);
            fecha_3=fecha;
            cond_3=id;
            MarkerAnimation.animateMarkerToGB(marker_3, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_4.equals(fecha_ultimo)==false){
            marker_4.setRotation(rotacion);
            fecha_4=fecha;
            cond_4=id;
            MarkerAnimation.animateMarkerToGB(marker_4, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_5.equals(fecha_ultimo)==false){
            marker_5.setRotation(rotacion);
            fecha_5=fecha;
            cond_5=id;
            MarkerAnimation.animateMarkerToGB(marker_5, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_6.equals(fecha_ultimo)==false){
            marker_6.setRotation(rotacion);
            fecha_6=fecha;
            cond_6=id;
            MarkerAnimation.animateMarkerToGB(marker_6, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_7.equals(fecha_ultimo)==false){
            marker_7.setRotation(rotacion);
            fecha_7=fecha;
            cond_7=id;
            MarkerAnimation.animateMarkerToGB(marker_7, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_8.equals(fecha_ultimo)==false){
            marker_8.setRotation(rotacion);
            fecha_8=fecha;
            cond_8=id;
            MarkerAnimation.animateMarkerToGB(marker_8, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_9.equals(fecha_ultimo)==false){
            marker_9.setRotation(rotacion);
            fecha_9=fecha;
            cond_9=id;
            MarkerAnimation.animateMarkerToGB(marker_9, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_10.equals(fecha_ultimo)==false){
            marker_10.setRotation(rotacion);
            fecha_10=fecha;
            cond_10=id;
            MarkerAnimation.animateMarkerToGB(marker_10, ubicacion, new LatLngInterpolator.Spherical());
        }

    }


    public void cargar_puntos_moto( double lat,double lon,int rotacion,String distancia,String id,String fecha)
    {
        try {

            LatLng punto = new LatLng(lat, lon);

            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker))
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

    public void ocultar_conductores_no_activos()
    {
        if(fecha_1.equals(fecha_ultimo)==false){
            marker_1.setVisible(false);
        }
        if(fecha_2.equals(fecha_ultimo)==false){
            marker_2.setVisible(false);
        }
        if(fecha_3.equals(fecha_ultimo)==false){
            marker_3.setVisible(false);
        }
        if(fecha_4.equals(fecha_ultimo)==false){
            marker_4.setVisible(false);
        }
        if(fecha_5.equals(fecha_ultimo)==false){
            marker_5.setVisible(false);
        }
        if(fecha_6.equals(fecha_ultimo)==false){
            marker_6.setVisible(false);
        }
        if(fecha_7.equals(fecha_ultimo)==false){
            marker_7.setVisible(false);
        }
        if(fecha_8.equals(fecha_ultimo)==false){
            marker_8.setVisible(false);
        }
        if(fecha_9.equals(fecha_ultimo)==false){
            marker_9.setVisible(false);
        }
        if(fecha_10.equals(fecha_ultimo)==false){
            marker_10.setVisible(false);
        }

    }


    public void imagen_en_vista(ImageView imagen)
    { Drawable dw;
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        String mPath = Environment.getExternalStorageDirectory() + File.separator + "Radio Movil Clasico/Imagen"
                + File.separator + perfil.getString("id_usuario","")+"_perfil.jpg";

        File newFile = new File(mPath);
        Bitmap bitmap =  BitmapFactory.decodeFile(newFile.getAbsolutePath());
        //Convertir Bitmap a Drawable.
        dw = new BitmapDrawable(getResources(), bitmap);
        //se edita la imagen para ponerlo en circulo.

        if( bitmap==null)
        { dw = getResources().getDrawable(R.drawable.ic_perfil_negro);}

        imagen_circulo(dw,imagen);

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Menu_usuario.this,Perfil_pasajero.class));
            }
        });
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

        imagen.setImageDrawable(roundedDrawable);


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


                break;
            case R.id.pedir_movil_lujo:
                //solicita un movil con la mejor caracteristica.
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


                break;
            case R.id.pedir_movil_aire:
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


                break;

            case R.id.pedir_movil_maletero:
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

                break;

            case R.id.pedir_movil_pedido:
                 if(existe_celular()==true)
                    {
                        escribir_referencia_pedido(5,tipo_pedido_empresa);
                    } else{
                        actualizar_perfil();
                    }


                break;

            case R.id.pedir_movil_reserva:
                Intent datos_pedido = new Intent(this, Reservar_movil.class);
                datos_pedido.putExtra("latitud", addressLatLng.latitude);
                datos_pedido.putExtra("longitud", addressLatLng.longitude);
                startActivity(datos_pedido);
                break;
            case R.id.pedir_moto:
                if(existe_celular()==true)
                    {
                        escribir_referencia_moto(7,tipo_pedido_empresa );
                    } else{
                        actualizar_perfil();
                    }


                break;


            case R.id.ver_movil:
                ver_moviles();


                break;
            case R.id.buscar_direccion:
                if(existe_celular()==true) {

                    double lat_1=0,lon_1=0;
                    try{
                        lat_1=addressLatLng.latitude;
                        lon_1=addressLatLng.longitude;
                    }catch (Exception e){
                        lat_1=0;
                        lon_1=0;
                    }
                    if(lat_1!=0&& lon_1!=0){
                        Intent buscar=new Intent(this,Buscar_direccion_inicio.class);
                        /*
                        buscar.putExtra("latitud_inicio",myPosition.latitude);
                        buscar.putExtra("longitud_inicio",myPosition.longitude);
                        buscar.putExtra("direccion_inicio",tv_ubicacion.getText().toString());
                        */

                        buscar.putExtra("latitud_inicio",addressLatLng.latitude);
                        buscar.putExtra("longitud_inicio",addressLatLng.longitude);
                        buscar.putExtra("direccion_inicio",tv_ubicacion.getText().toString());

                        SharedPreferences inicio=getSharedPreferences("direccion_inicio",MODE_PRIVATE);
                        SharedPreferences.Editor inicio_edit=inicio.edit();
                        inicio_edit.putString("latitud_inicio",String.valueOf(addressLatLng.latitude));
                        inicio_edit.putString("longitud_inicio",String.valueOf(addressLatLng.longitude));
                        inicio_edit.putString("direccion_inicio",tv_ubicacion.getText().toString());
                        inicio_edit.commit();

                        startActivity(buscar);
                    }else{
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                        dialogo1.setTitle("Atención");
                        dialogo1.setMessage("Por favor geolocalice su ubicación.");
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("OK", null);
                        dialogo1.show();
                    }

                }
                else
                {
                    actualizar_perfil();
                }
                break;
            case R.id.fb_llamar:


                llamar_radio_movil();

                break;
            case R.id.fb_whatsapp:
                boolean isWhatsapp = appInstalledOrNot("com.whatsapp");
                if (isWhatsapp)
                    AbrirWhatsApp();
                break;
        }
    }

    private void ver_moviles() {
        SharedPreferences ult=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
        //si no tiene pédidos se le va a mostrar en el mapa....
        int id_pedido=0;
        try{
            id_pedido=Integer.parseInt(ult.getString("id_pedido","0"));
        }catch (Exception e){
            id_pedido=0;
        }

        if(id_pedido==0) {
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

        }
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


    public void mensaje(String mensaje)
    {
        Toast toast = Toast.makeText(this,mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
    public void cerrar_sesion()
    {
        LoginManager.getInstance().logOut();
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        SharedPreferences.Editor editar=perfil.edit();
        editar.putString("id","");
        editar.putString("id_usuario","");
        editar.putString("nombre","");
        editar.putString("apellido","");
        editar.putString("ci","");
        editar.putString("email","");
        editar.putString("direccion","");
        editar.putString("marca","");
        editar.putString("modelo","");
        editar.putString("placa","");
        editar.putString("celular","");
        editar.putString("credito","");
        editar.putString("login_usuario","");
        editar.putString("login_taxi","");
        editar.commit();
        vaciar_toda_la_base_de_datos();
        Intent serv = new Intent(getApplicationContext(), Servicio_pedido.class);
        serv.setAction(Constants.ACTION_RUN_ISERVICE);
        stopService(serv);
        Intent intent=new Intent(getApplicationContext(),Inicio.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        startActivity(new Intent(this,Animacion.class));


SharedPreferences casa=getSharedPreferences(getString(R.string.direccion_casa),MODE_PRIVATE);
        SharedPreferences.Editor e_casa=  casa.edit();
        e_casa.putString("latitud","0");
        e_casa.putString("longitud","0");
        e_casa.putString("direccion","");
        e_casa.commit();
        SharedPreferences trabajo=getSharedPreferences(getString(R.string.direccion_casa),MODE_PRIVATE);
        SharedPreferences.Editor e_trabajo=  trabajo.edit();
        e_trabajo.putString("latitud","0");
        e_trabajo.putString("longitud","0");
        e_trabajo.putString("direccion","");
        e_trabajo.commit();

        LoginManager.getInstance().logOut();
    }


    public void vaciar_toda_la_base_de_datos() {
    try {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
        SQLiteDatabase db = admin.getWritableDatabase();
        db.execSQL("delete from notificacion");
        db.execSQL("delete from direccion");
        db.execSQL("delete from puntos_pedido");
        db.execSQL("delete from pedido");
        db.close();
    }catch (Exception e)
    {
        Log.e("Vaciar base",""+e);
    }
        // Log.e("sqlite ", "vaciar todo");
    }

    //INICIO DE SERVICIO DE COORDENADAS.
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
                            status.startResolutionForResult(Menu_usuario.this, PETICION_CONFIG_UBICACION);
                        } catch (IntentSender.SendIntentException e) {
                            mensaje_error_final("Error al intentar solucionar configuración de ubicación");
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        mensaje_error_final("No se puede cumplir la configuración de ubicación necesaria");

                        break;
                }
            }
        });
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(Menu_usuario.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        apiClient, locRequest, Menu_usuario.this);
            }catch (Exception e)
            {
                finish();
            }
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
            pUbicacion.cancel();//ocultamos proggress dialog
          //  pedi_taxi.setVisibility(View.VISIBLE);
            limpiar_mapa++;
            if(limpiar_mapa>3)
            {
                limpiar_mapa=0;
                // Log.e("Mapa","Limpiar  mapa");
             //   mMap.clear();
            }
            double lat = loc.getLatitude();
            double lon = loc.getLongitude();
            myPosition = new LatLng(lat, lon);
            ubicacion.setText("(" + lat + "," + lon + ")");
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
                            .zoom(Float.parseFloat("16.7"))                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();

                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    sw_acercar_a_mi_ubicacion = 1;
                    mMap.clear();
                    crear_puntos_conductor();
                    ver_moviles();
                } catch (Exception e) {
                    sw_acercar_a_mi_ubicacion = 0;
                }

            }




            //agregaranimacion al mover la camara...

        } else {
            ubicacion.setText("(deconocido)");
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
                finish();
                Log.e(LOGTAG, "Permiso denegado");
            }
        }else if(requestCode == 1000)
        {
            int per=0;
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 ) {
                for (int i=0;i<grantResults.length;i++){
                    if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        per++;
                    }
                }

                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {

                finish();
            }

            if(per<grantResults.length){
                finish();
            }else{
                //tiene todos los permisos...
                Intent intent = new Intent(Menu_usuario.this, Servicio_guardar_contacto_empresa.class);
                intent.setAction(Constants.ACTION_RUN_ISERVICE);
                startService(intent);
            }
            return;

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
                        mensaje_error_final("El usuario no ha realizado los cambios de configuración necesarios");

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

    public void mensaje_error_final(String mensaje)
    {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.app_name));
            builder.setMessage(mensaje);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    finish();
                }
            });
            builder.create();
            builder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
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

           // location

       /*
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
          */
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


                    addressLatLng = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                    //////////////////get in AsyncTask//////////////////////
                    getAddressIntentService(cameraPosition.target.latitude, cameraPosition.target.longitude);
                    //mueve a mi ubicacion
                    final String[] direccion = {""};
                    try {

                        //agregaranimacion al mover la camara...
                        if (sw_acercar_a_mi_ubicacion==0) {
                            try{
                                CameraPosition cameraPosition1 = new CameraPosition.Builder()
                                        .zoom(Float.parseFloat("15"))
                                        .target(myPosition)      // Sets the center of the map to Mountain View
                                        .bearing(0)                // Sets the orientation of the camera to east
                                        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                                        .build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                                sw_acercar_a_mi_ubicacion=1;
                            }catch (Exception e){
                                sw_acercar_a_mi_ubicacion=0;
                            }


                        }else
                        {
                            float mm=mMap.getCameraPosition().zoom;
                            if(mm<13){
                                CameraPosition cameraPosition1 = new CameraPosition.Builder()
                                        .zoom(Float.parseFloat("15"))
                                        .target(myPosition)      // Sets the center of the map to Mountain View
                                        .bearing(0)                // Sets the orientation of the camera to east
                                        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                                        .build();
                               // mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                            }else{
                              //  mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                            }
                        }

                        if(sw_obteniendo_direccion==false) {
                            new Thread(new Runnable() {
                                public void run() {
                                    sw_obteniendo_direccion=true;
                                    direccion[0] =obtener_direccion( addressLatLng.latitude, addressLatLng.longitude);

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            sw_obteniendo_direccion=false;
                                            try {
                                                tv_ubicacion.setText(String.valueOf(direccion[0]));
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
                        Log.e("Adres:",e.toString());
                    }

                } else {
                    getAddressIntentService(cameraPosition.target.latitude, cameraPosition.target.longitude);

                }
                //////////////////get in AsyncTask//////////////////////
            }
        });
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








    void AbrirWhatsApp() {

        Uri uri = Uri.parse("smsto: "+getString(R.string.whatsapp));
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


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
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
        if(conectadoWifi()==true){
            return true;
        }else{
            if(conectadoRedMovil()==true){
                return true;
            }else{
                mensaje_error_final("Tu Dispositivo no tiene Conexion a Internet.");
                return false;
            }
        }
    }



    public void  escribir_referencia(final int clase_vehiculo,final int tipo_pedido_empresa,String tipo_pedido_texto)
    {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Menu_usuario.this);
        View promptView = layoutInflater.inflate(R.layout.escribir_referencia, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Menu_usuario.this);
        alertDialogBuilder.setView(promptView);

        final Button bt_cancelar= (Button) promptView.findViewById(R.id.bt_cancelar);
        final Button bt_pedir= (Button) promptView.findViewById(R.id.bt_pedir);
        final EditText et_referencia= (EditText) promptView.findViewById(R.id.et_referencia);
        final TextView tv_tipo_pedido=(TextView)promptView.findViewById(R.id.tv_tipo_pedido);
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
                pedir_taxi("0", et_referencia.getText().toString().trim(),clase_vehiculo,tipo_pedido_empresa);
                alert2.cancel();

            }
        });
        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();
    }


    public void  escribir_referencia_pedido(final int clase_vehiculo,final int tipo_pedido_empresa)
    {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Menu_usuario.this);
        View promptView = layoutInflater.inflate(R.layout.escribir_pedido, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Menu_usuario.this);
        alertDialogBuilder.setView(promptView);

        final Button bt_cancelar= (Button) promptView.findViewById(R.id.bt_cancelar);
        final Button bt_pedir= (Button) promptView.findViewById(R.id.bt_pedir);
        final EditText et_referencia= (EditText) promptView.findViewById(R.id.et_referencia);



        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert2.cancel();
            }
        });
        bt_pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                alert2.cancel();
            }
        });
        bt_pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pedir_taxi("0", et_referencia.getText().toString().trim(),clase_vehiculo,tipo_pedido_empresa);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                alert2.cancel();
            }
        });
        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();
    }

    public void  escribir_referencia_moto(final int clase_vehiculo,final int tipo_pedido_empresa)
    {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Menu_usuario.this);
        View promptView = layoutInflater.inflate(R.layout.escribir_moto, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Menu_usuario.this);
        alertDialogBuilder.setView(promptView);

        final Button bt_cancelar= (Button) promptView.findViewById(R.id.bt_cancelar);
        final Button bt_pedir= (Button) promptView.findViewById(R.id.bt_pedir);
        final EditText et_referencia= (EditText) promptView.findViewById(R.id.et_referencia);
        final RadioButton rb_solo_moto=(RadioButton)promptView.findViewById(R.id.rb_solo_moto);



        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert2.cancel();
            }
        });
        bt_pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                alert2.cancel();
            }
        });
        bt_pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clase_v=clase_vehiculo;
                if(rb_solo_moto.isChecked()==false){
                    clase_v++;
                }

                pedir_taxi("0", et_referencia.getText().toString().trim(),clase_v,tipo_pedido_empresa);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                alert2.cancel();
            }
        });
        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();
    }
    public void pedir_taxi(String numero, String referencia,int clase_vehiculo,int tipo_pedido_empresa){
        ///verifica si el GPS esta activo.
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertNoGps();
        }
        else {
            if (existe_celular() == true) {

                    if (referencia.length() >= 3) {
                        Intent datos_pedido = new Intent(this, Pedido_usuario.class);
                        datos_pedido.putExtra("latitud", addressLatLng.latitude);
                        datos_pedido.putExtra("longitud", addressLatLng.longitude);
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
                    ActivityCompat.requestPermissions(Menu_usuario.this,
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
            ActivityCompat.requestPermissions(Menu_usuario.this,
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
                    ActivityCompat.requestPermissions(Menu_usuario.this,
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
            ActivityCompat.requestPermissions(Menu_usuario.this,
                   PERMISSIONS,
                    1);
        }
    }


















    public void iniciar_verificacion_version(){
        SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
        try {
            int id_usuario = Integer.parseInt(prefe.getString("id_usuario", ""));
            String token= SharedPrefManager.getInstance(this).getDeviceToken();
            Servicio_version servicio = new Servicio_version();
            servicio.execute(getString(R.string.servidor) + "frm_version.php?opcion=radiomovilclasico_pasajero", "1",String.valueOf(id_usuario),token);// parametro que recibe el doinbackground
        } catch (Exception e) {

        }

    }


    public boolean verificar_version()
    {boolean sw=false;
        // notificacion para verificar la actualizacion nueva
        int actual=getVersionCode(this);
        if(version>actual)
        {
            try {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle("Actualización");
                dialogo1.setMessage("Hay una nueva version.Por favor actualice la aplicación desde Play Store.");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("ACTULIZAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.elisoft.radiomovilclasico");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

                dialogo1.show();
                sw = true;
            }catch (Exception e){
                sw=true;
            }
        }
        return sw;
    }

    public void cuenta_iniciar_en_otro_celular(String mensaje)
    {
            try {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle(getString(R.string.app_name));
                dialogo1.setMessage(mensaje);
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                      cerrar_sesion();
                    }
                });
                dialogo1.show();
            }catch (Exception e){
            }

    }


    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {}
        return 0;
    }

    public class Servicio_version extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
//Registrar usuario.
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
                    jsonParam.put("id_usuario", params[2]);
                    jsonParam.put("token", params[3]);

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

                        version= Integer.valueOf(respuestaJSON.getString("version"));
                        devuelve="1";

                        suceso= new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));
                        if(suceso.getSuceso().equals("2")){
                            devuelve="2";
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

            if (s.equals("1")) {
                verificar_version();
            }else if(s.equals("2"))
            {
                cuenta_iniciar_en_otro_celular(suceso.getMensaje());

            }
            else
            {
                mensaje_error_final("Error: Al conectar con el Servidor.\nVerifique su acceso a Internet.");
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


    public void verificar_todos_los_permisos()
    {
        final String[] SMS_PERMISSIONS1 = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION};


        ActivityCompat.requestPermissions( this,
                SMS_PERMISSIONS1,
                1000);


    }










}
