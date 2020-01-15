package com.elisoft.radiomovilclasico;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.Scanear.QR_conductor;
import com.elisoft.radiomovilclasico.Scanear.QR_vehiculo;
import com.elisoft.radiomovilclasico.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.radiomovilclasico.chat.Chat;
import com.elisoft.radiomovilclasico.compartir.Buscar_usuario;
import com.elisoft.radiomovilclasico.compartir.CUsuario;
import com.elisoft.radiomovilclasico.compartir.Usuario_select_adapter;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
import java.security.spec.ECField;
import java.util.ArrayList;

public class Pedido_usuario extends AppCompatActivity implements OnMapReadyCallback , View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private IntentFilter mIntentFilter;
    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PETICION_CONFIG_UBICACION = 201;



    private GoogleApiClient apiClient;

    private LocationRequest locRequest;

    AlertDialog alert2 = null;

    LinearLayout ll_vehiculo;




    Button bt_cancelar;
    private GoogleMap mMap;
    ProgressDialog pDialog;
    String sid_pedido;
    Suceso suceso;
    Marker m_conductor=null;
    int m_conductor_cantida=0;


    LatLng myPosition=new LatLng(0,0);
    LatLng ultima_ubicacion = new LatLng(0, 0);
    boolean sw=true;
    double latitud = 0, longitud = 0, latitud_final=0,longitud_final=0;
    String referencia="",direccion="";
    int numero_casa=0,clase_vehiculo=1,tipo_pedido_empresa=0,clase_vehiculo_en_pedido=1;
    int cant=1;
    int tamanio_help=70;

    boolean sw_verificar_si_tiene_pedido=false,sw_cancelar_pedido=true,sw_cancelar_pedido_durante_el_pedido=false;

       LinearLayout ll_flotante,ll_cancelar;
    TextView tv_mensaje_pedido,tv_nombre,tv_placa,tv_marca,tv_color,tv_titulo,tv_numero_movil;
    ImageView im_rodar_pedido,im_perfil,im_cerrar;
    Button bt_cancelar_pedido,bt_ver_perfil;
    ImageButton bt_contacto_conductor,bt_contactar_empresa;
    RatingBar rb_calificacion_conductor,rb_calificacion_vehiculo;




    LinearLayout ll_perfil;


    boolean sw_destroy=false;

    Servicio_pedir_taxi hilo_pedir_taxi;
    Servicio_pedir_taxi hilo_taxi_obtener_dato;
    Servicio_pedir_cancelar hilo_taxi_cancelar;

    int rotacion=0;


    JSONObject rutas=null;
    String direccion_final="";
    String estado_billetera="0";

    @Override
    public void onBackPressed() {
        ll_perfil.setVisibility(View.INVISIBLE);
        bt_cancelar.setEnabled(true);
        tv_titulo.setText("");

            SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
        SharedPreferences pedido_proceso = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
        int id_pedido=0;

        try{
            id_pedido=Integer.parseInt(pedido.getString("id_pedido","0"));
        }catch (Exception e){
            try{
                id_pedido=Integer.parseInt(pedido_proceso.getString("id_pedido","0"));
            }catch (Exception ee){
                id_pedido=0;
            }
        }
         if(id_pedido==0 ){

             super.onBackPressed();
         }
         else  if( pedido.getString("estado","").equals("2")==true||pedido.getString("estado","").equals("3")==true)
        {
            super.onBackPressed();
        } else
        {
            Toast.makeText(this,"Tiene un pedido en Proceso.", Toast.LENGTH_SHORT).show();
        }

      //

    }

    @Override
    protected void onDestroy() {
        sw_destroy=true;
        Log.e("Destroy","1");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        sw_destroy=false;


        if(estaConectado())
        {
            enableLocationUpdates();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.e("Stop","1");
        super.onStop();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        Log.e("Pause","1");
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_usuario_2);
        bt_cancelar=(Button)findViewById(R.id.bt_cancelar);

        getWindow().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }catch (Exception e)
        {

        }





        //inicio layout flotante....
        ll_flotante=(LinearLayout)findViewById(R.id.ll_flotante);
        ll_cancelar=(LinearLayout)findViewById(R.id.ll_cancelar);
        tv_mensaje_pedido=(TextView)findViewById(R.id.tv_mensaje_pedido);
        im_rodar_pedido=(ImageView)findViewById(R.id.im_rodar_pedido);
        bt_cancelar_pedido=(Button)findViewById(R.id.bt_cancelar_pedido);
        bt_ver_perfil=(Button)findViewById(R.id.bt_ver_perfil);
        tv_nombre=(TextView)findViewById(R.id.tv_nombre);
        tv_marca=(TextView)findViewById(R.id.tv_marca);
        tv_placa=(TextView)findViewById(R.id.tv_placa);
        tv_color=(TextView)findViewById(R.id.tv_color);
        tv_numero_movil=(TextView)findViewById(R.id.tv_numero_movil);
        im_perfil=(ImageView) findViewById(R.id.im_perfil);
        im_cerrar=(ImageView) findViewById(R.id.im_cerrar);
        ll_perfil=(LinearLayout)findViewById(R.id.ll_perfil);
        rb_calificacion_conductor=(RatingBar)findViewById(R.id.rb_conductor);
        rb_calificacion_vehiculo=(RatingBar)findViewById(R.id.rb_vehiculo);
        tv_titulo=(TextView)findViewById(R.id.tv_titulo);
        bt_contacto_conductor=(ImageButton)findViewById(R.id.bt_contacto_conductor);
        bt_contactar_empresa=(ImageButton)findViewById(R.id.bt_contacto_empresa);
        ll_vehiculo=(LinearLayout)findViewById(R.id.ll_vehiculo);
//fin layout flotante
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mIntentFilter = new IntentFilter();

       tv_titulo.setText("");


        hilo_pedir_taxi = new Servicio_pedir_taxi();
        hilo_taxi_obtener_dato = new Servicio_pedir_taxi();
        hilo_taxi_cancelar = new Servicio_pedir_cancelar();

        try {
            Bundle bundle = getIntent().getExtras();
            latitud = bundle.getDouble("latitud", 0);
            longitud = bundle.getDouble("longitud", 0);
            latitud_final = bundle.getDouble("latitud_final", 0);
            longitud_final = bundle.getDouble("longitud_final", 0);
            referencia=bundle.getString("referencia","");
            direccion=bundle.getString("direccion","");
            direccion_final=bundle.getString("direccion_final","");
            sid_pedido= bundle.getString("id_pedido","0");
            clase_vehiculo=bundle.getInt("clase_vehiculo",1);
            tipo_pedido_empresa=bundle.getInt("tipo_pedido_empresa",0);
            estado_billetera=bundle.getString("estado_billetera","0");

            try{
                numero_casa= Integer.parseInt(bundle.getString("numero","0"));
            }catch (Exception e)
            {
                numero_casa=0;
            }
            ultima_ubicacion=new LatLng(latitud,longitud);

             if(sid_pedido.equals("")==false && sid_pedido.equals("0")==false)
            {
                SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);

                try {
                    int id_pedido= Integer.parseInt(prefe.getString("id_pedido",""));
                    Servicio hilo_taxi = new Servicio();
                    hilo_taxi.execute(getString(R.string.servidor) + "frmPedido.php?opcion=get_pedido_por_id_pedido", "5", String.valueOf(id_pedido));// parametro que recibe el doinbackground
                }catch (Exception e) {
                    e.printStackTrace();
                }

                flotante_pedir(false);
            }
            else {
                 if (latitud == 0 || longitud == 0) {
                     mensaje_error_final("Por favor geolocalice su ubicación.");
                 }
                 else {
                     pedir_taxi();
                     flotante_pedir(true);
                 }
            }

        } catch (Exception e) {
            finish();
        }

        bt_cancelar.setOnClickListener(this);
        bt_ver_perfil.setOnClickListener(this);
        im_cerrar.setOnClickListener(this);
        bt_cancelar_pedido.setOnClickListener(this);
        bt_contactar_empresa.setOnClickListener(this);
        bt_contacto_conductor.setOnClickListener(this);

        ll_vehiculo.setOnClickListener(this);


       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        if (pedido.getString("id_pedido", "").equals("") == true) {

        }
        else
        { flotante_pedir(false);
            sid_pedido=pedido.getString("id_pedido", "");
        }











// localizacion automatica
        //Construcción cliente API Google
        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        enableLocationUpdates();
        //fin de la locatizaocion automatica...


        try{
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }catch (Exception e)
        {}


    }




    private void flotante_pedir(boolean b) {
        Animation giro= AnimationUtils.loadAnimation(this,R.anim.rotar);
        if(b==true)
        {
            sw_cancelar_pedido=false;
            ll_flotante.setVisibility(View.VISIBLE);

            giro.reset();
            im_rodar_pedido.startAnimation(giro);
            ll_perfil.setVisibility(View.INVISIBLE);
            bt_cancelar.setEnabled(false);
            bt_ver_perfil.setVisibility(View.INVISIBLE);
            getSupportActionBar().hide();

        }
        else
        {
            sw_cancelar_pedido=true;
            this.ll_flotante.setVisibility(View.INVISIBLE);
            ll_perfil.setVisibility(View.VISIBLE);
            bt_cancelar.setEnabled(false);
            bt_ver_perfil.setVisibility(View.VISIBLE);
            getSupportActionBar().show();
            tv_titulo.setText("Datos del conductor");

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(sw==true && ultima_ubicacion.latitude!=0 && ultima_ubicacion.longitude!=0)
        {
            //agregaranimacion al mover la camara...
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(ultima_ubicacion)      // Sets the center of the map to Mountain View
                    .zoom(18)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
        else
        { try {

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitud, longitud))      // Sets the center of the map to Mountain View
                        .zoom(18)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }catch (Exception e)
            {}
        }
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
    public void onClick(View v) {

      if(v.getId()==R.id.bt_cancelar)
        {
            try {
                SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                double lat = Double.parseDouble(prefe.getString("latitud", "0"));
                double lon = Double.parseDouble(prefe.getString("longitud", "0"));
                int distancia = getDistancia(ultima_ubicacion.latitude, ultima_ubicacion.longitude, lat, lon);

                if(distancia>500) {

                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle(getString(R.string.app_name));
                    dialogo1.setMessage("¿Desea cancelar el Pedido?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            //cargamos los datos

                            SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                            SharedPreferences usuario = getSharedPreferences("perfil", MODE_PRIVATE);
                            String id_usuario = usuario.getString("id_usuario", "");
                            //dibuja en el mapa las taxi que estan cerca...
                            //hilo_taxi.execute(getString(R.string.servidor)+"frmTaxi.php?opcion=get_taxis_en_rango", "1","64.455","-18.533");// parametro que recibe el doinbackground
                            if(pedido.getString("id_pedido","").equals("")==false && pedido.getString("id_pedido","0").equals("0")==false) {
                                try {
                                    tv_mensaje_pedido.setText("Cancelando su solicitud...");
                                    hilo_taxi_cancelar.cancel(true);
                                    hilo_taxi_cancelar=new Servicio_pedir_cancelar();
                                    hilo_taxi_cancelar.execute(getString(R.string.servidor) + "frmPedido.php?opcion=cancelar_pedido_usuario", "1", id_usuario,pedido.getString("id_pedido",""));// parametro que recibe el doinbackground
                                } catch (Exception e) {

                                }
                            }else
                            {
                                finish();
                            }

                        }
                    });
                    dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {

                        }
                    });
                    dialogo1.show();

                }else
                {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle(getString(R.string.app_name));
                    dialogo1.setMessage("Su Taxi esta a "+distancia+" mt. de distancia. ¿Esta seguro en Cancelar su pedido?");
                    dialogo1.setCancelable(true);
                    dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                          dialogo1.cancel();
                        }
                    });
                    dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            //cargamos los datos
                            Servicio_pedir_taxi hilo_taxi = new Servicio_pedir_taxi();
                            SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                            SharedPreferences usuario = getSharedPreferences("perfil", MODE_PRIVATE);
                            String id_usuario = usuario.getString("id_usuario", "");
                            //dibuja en el mapa las taxi que estan cerca...
                            //hilo_taxi.execute(getString(R.string.servidor)+"frmTaxi.php?opcion=get_taxis_en_rango", "1","64.455","-18.533");// parametro que recibe el doinbackground
                            if(pedido.getString("id_pedido","").equals("")==false && pedido.getString("id_pedido","0").equals("0")==false) {
                                try {
                                    hilo_taxi.execute(getString(R.string.servidor) + "frmPedido.php?opcion=cancelar_pedido_usuario", "1", id_usuario,pedido.getString("id_pedido",""));// parametro que recibe el doinbackground
                                } catch (Exception e) {

                                }
                            }else
                            {
                                finish();
                            }
                        }
                    });

                    dialogo1.show();
                }
            }catch (Exception e)
            {

            }

        }
        else if(v.getId()==R.id.bt_ver_perfil)
          {SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
              getImage(pedido.getString("id_taxi",""));
            ll_perfil.setVisibility(View.VISIBLE);
              bt_cancelar.setEnabled(false);
              bt_ver_perfil.setVisibility(View.VISIBLE);
              tv_titulo.setText("");

          }
        else if(v.getId()==R.id.im_cerrar)
      {
        ll_perfil.setVisibility(View.INVISIBLE);
          bt_cancelar.setEnabled(true);
          tv_titulo.setText("");
      }
      else if(v.getId()==R.id.bt_cancelar_pedido)
      {//cancela el pedido durante la busqueda de un pedido.
          sw_cancelar_pedido_durante_el_pedido=true;

          hilo_taxi_obtener_dato.cancel(true);
          hilo_pedir_taxi.cancel(true);
          hilo_taxi_cancelar.cancel(true);

           hilo_taxi_cancelar = new Servicio_pedir_cancelar();
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
                hilo_pedir_taxi.cancel(true);
                hilo_taxi_obtener_dato.cancel(true);
                tv_mensaje_pedido.setText("Cancelando su solicitud..");
                hilo_taxi_cancelar = new Servicio_pedir_cancelar();
                hilo_taxi_cancelar.execute(getString(R.string.servidor) + "frmPedido.php?opcion=cancelar_pedido_usuario", "1", id,String.valueOf(id_pedido));// parametro que recibe el doinbackground
            } catch (Exception e) {

            }
        }else
        {
            finish();
        }

      }else if(v.getId()==R.id.bt_contacto_conductor){
          llamar_al_conductor();
      }else if(v.getId()==R.id.bt_contacto_empresa)
      {
        llamar_a_taxicorp();
      }
      else if(v.getId()==R.id.ll_vehiculo){
          if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
              verificar_permiso_camara();
          }
          else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
              verificar_permiso_almacenamiento();
          } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
              verificar_permiso_llamada();
          }else{
              Intent empresa=new Intent(this,Datos_vehiculo.class);
              SharedPreferences s_empresa=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
              empresa.putExtra("placa",s_empresa.getString("placa",""));
              startActivity(empresa);
          }

      }
    }

    private void llamar_a_taxicorp() {


                // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Pedido_usuario.this);
        View promptView = layoutInflater.inflate(R.layout.lista_constactar_con_empresa, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Pedido_usuario.this);
        alertDialogBuilder.setView(promptView);

        final Button bt_llamada_empresa= (Button) promptView.findViewById(R.id.bt_llamada_empresa);
        final Button bt_whatsapp_empresa= (Button) promptView.findViewById(R.id.bt_whatsapp_empresa);
        final Button bt_ninguna= (Button) promptView.findViewById(R.id.bt_ninguna);

        bt_llamada_empresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                llamar_radio_movil();


            }
        });

        bt_whatsapp_empresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isWhatsapp = appInstalledOrNot("com.whatsapp");
                if (isWhatsapp)
                    AbrirWhatsApp(getString(R.string.whatsapp));

                alert2.cancel();
            }
        });

/*
        bt_messenger_empresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("https://www.facebook.com/TaxiCorpLaPaz/");
                Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent2);

                alert2.cancel();
            }
        });

*/
        bt_ninguna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert2.cancel();
            }
        });

        // setup a dialog window

        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();

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


    public void AbrirSkype(String number) {
        try {
            Intent sky = new Intent("android.intent.action.VIEW");
            sky.setClassName("com.skype.raider","com.skype.raider.Main");
            sky.setData(Uri.parse("tel:" + number));
            startActivity(sky);
        } catch (ActivityNotFoundException e) {
            Log.e("SKYPE CALL", "Skype failed", e);
        }
    }

    void AbrirWhatsApp(String number) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(i, ""));
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
    public void  llamar_al_conductor()
    {
        verificar_todos_los_permisos();

        SharedPreferences preferencias = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);

        Intent intent = new Intent(Pedido_usuario.this, Servicio_guardar_contacto.class);
        intent.setAction(Constants.ACTION_RUN_ISERVICE);
        intent.putExtra("nombre",preferencias.getString("nombre_taxi", ""));
        intent.putExtra("telefono",preferencias.getString("celular", ""));
        startService(intent);


        LayoutInflater layoutInflater = LayoutInflater.from(Pedido_usuario.this);
        View promptView = layoutInflater.inflate(R.layout.lista_contactar_usuario, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Pedido_usuario.this);
        alertDialogBuilder.setView(promptView);

        final Button bt_voy_asliendo= (Button) promptView.findViewById(R.id.bt_voy_en_camino);
        final Button bt_estamos_esperando= (Button) promptView.findViewById(R.id.bt_estamos_esperando);
        final Button bt_llameme= (Button) promptView.findViewById(R.id.bt_llameme);
        final Button bt_llamada_conductor= (Button) promptView.findViewById(R.id.bt_llamada_conductor);
        final Button bt_mensaje_conductor= (Button) promptView.findViewById(R.id.bt_mensaje_conductor);
        final Button bt_whatsapp_conductor= (Button) promptView.findViewById(R.id.bt_whatsapp_conductor);
        final Button bt_ninguna= (Button) promptView.findViewById(R.id.bt_ninguna);
        final Button bt_qr_vehiculo= (Button) promptView.findViewById(R.id.bt_qr_vehiculo);


        bt_qr_vehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Pedido_usuario.this, QR_vehiculo.class));
                alert2.cancel();
            }
        });
        bt_whatsapp_conductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferencias = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                Intent it_chat=new Intent(getApplicationContext(), Chat.class);
                it_chat.putExtra("id_conductor",preferencias.getString("id_taxi",""));
                it_chat.putExtra("titulo",preferencias.getString("nombre_taxi",""));
                startActivity(it_chat);
                alert2.cancel();
            }
        });
        bt_voy_asliendo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                    SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
                    String detalle=bt_voy_asliendo.getText().toString();

                    Servicio_notificacion hilo = new Servicio_notificacion();
                    hilo.execute(getString(R.string.servidor) + "frmPedido.php?opcion=enviar_notificacion_usuario", "1",pedido.getString("id_pedido",""),perfil.getString("id_usuario",""),detalle);// parametro que recibe el doinbackground
                }catch (Exception e)
                {
                    mensaje("No se pudo enviar la notificación.");
                }
                alert2.cancel();
            }
        });

        bt_estamos_esperando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                    SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
                    String detalle=bt_estamos_esperando.getText().toString();

                    Servicio_notificacion hilo = new Servicio_notificacion();
                    hilo.execute(getString(R.string.servidor) + "frmPedido.php?opcion=enviar_notificacion_usuario", "1",pedido.getString("id_pedido",""),perfil.getString("id_usuario",""),detalle);// parametro que recibe el doinbackground
                }catch (Exception e)
                {
                    mensaje("No se pudo enviar la notificación.");
                }
                alert2.cancel();
            }});


        bt_llameme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                    SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
                    String detalle=bt_llameme.getText().toString();

                    Servicio_notificacion hilo = new Servicio_notificacion();
                    hilo.execute(getString(R.string.servidor) + "frmPedido.php?opcion=enviar_notificacion_usuario", "1",pedido.getString("id_pedido",""),perfil.getString("id_usuario",""),detalle);// parametro que recibe el doinbackground
                }catch (Exception e)
                {
                    mensaje("No se pudo enviar la notificación.");
                }
                alert2.cancel();
            }
        });

        bt_llamada_conductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferencias = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                Intent llamada = new Intent(Intent.ACTION_CALL);
                llamada.setData(Uri.parse("tel:" + preferencias.getString("celular", "")));
                if (ActivityCompat.checkSelfPermission(Pedido_usuario.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                }
                startActivity(llamada);
                alert2.cancel();
            }
        });

        bt_mensaje_conductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        bt_ninguna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert2.cancel();
            }
        });

        // setup a dialog window


        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();


    }


    public void pedir_taxi() {
        //no tiene pedidos
        SharedPreferences pedido2 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = pedido2.edit();
        editor2.putString("abordo", "");
        editor2.commit();
        //no tiene pedidos

        SharedPreferences usuario = getSharedPreferences("perfil", MODE_PRIVATE);
        String id = usuario.getString("id_usuario", "");
        String imei = usuario.getString("imei", "");
        String nombre = usuario.getString("nombre", "");
        nombre = nombre + " " + usuario.getString("apellido", "");
        //dibuja en el mapa las taxi que estan cerca...
        //hilo_taxi.execute(getString(R.string.servidor)+"frmTaxi.php?opcion=get_taxis_en_rango", "1","64.455","-18.533");// parametro que recibe el doinbackground
        try {

            tv_mensaje_pedido.setText("Enviando su solicitud a los Taxistas...");
            hilo_pedir_taxi.cancel(true);
            hilo_pedir_taxi=new Servicio_pedir_taxi();
            hilo_pedir_taxi.execute(getString(R.string.servidor) + "frmPedido.php?opcion=pedir_taxi", "7", id,
                    String.valueOf(latitud),
                    String.valueOf(longitud),
                    nombre,
                    referencia,
                    String.valueOf(numero_casa),
                    imei, String.valueOf(clase_vehiculo),
                    String.valueOf(tipo_pedido_empresa),
                    direccion,
                    String.valueOf(latitud_final),
                    String.valueOf(longitud_final),
                    estado_billetera
                    );// parametro que recibe el doinbackground



        } catch (Exception e) {
            mensaje("Por favor active su GPS para realizar pedidos.");
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

            if(sw_cancelar_pedido_durante_el_pedido==false)
            {
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
                                String snumero_m = dato.getJSONObject(0).getString("numero_movil");
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
                                editar.putString("numero_movil", snumero_m);
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


                                devuelve = "8";
                            } else if (suceso.getSuceso().equals("3")) {
                                devuelve = "500";
                            } else {
                                devuelve = "9";
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

            //enviar pedir taxi..
            if (params[1] == "7") {
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
                        jsonParam.put("id_usuario", params[2]);
                        jsonParam.put("latitud", params[3]);
                        jsonParam.put("longitud", params[4]);
                        jsonParam.put("nombre", params[5]);
                        jsonParam.put("indicacion", params[6]);
                        jsonParam.put("numero_casa", params[7]);
                        jsonParam.put("imei", params[8]);
                        jsonParam.put("clase_vehiculo", params[9]);
                        jsonParam.put("tipo_pedido_empresa", params[10]);
                        jsonParam.put("direccion", params[11]);
                        jsonParam.put("latitud_final", params[12]);
                        jsonParam.put("longitud_final", params[13]);
                        jsonParam.put("direccion_final", direccion_final);
                        jsonParam.put("estado_billetera",  params[14]);
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
                                editor.putString("id_pedido", respuestaJSON.getString("id_pedido"));
                                editor.commit();
                                devuelve = "3";
                            } else

                            {

                                devuelve = "5";
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


            if (s.equals("3")) {
                tv_mensaje_pedido.setText("Esperando la confirmación por el Taxista ...");
                SharedPreferences prefe = getSharedPreferences("pedido_en_proceso", Context.MODE_PRIVATE);

                try {
                    int id_pedido =0;
                    try{
                        id_pedido = Integer.parseInt(prefe.getString("id_pedido", "0"));
                    }catch (Exception e)
                    {
                           id_pedido=0;
                    }
                    if(id_pedido!=0) {
                        Intent msgIntent = new Intent(Pedido_usuario.this, Servicio_pedir_movil.class);
                        msgIntent.putExtra("id_pedido", id_pedido);
                        startService(msgIntent);
                    }
                } catch (Exception e) {
                }
            } else if (s.equals("5") == true) {
               mensaje_error_final(suceso.getMensaje());

            } else if (s.equals("8") == true) {
                //verificar si alguien acepto el pedido.

                Intent intent = new Intent(Pedido_usuario.this, Servicio_pedido.class);
                intent.setAction(Constants.ACTION_RUN_ISERVICE);
                startService(intent);

                SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                tv_nombre.setText(pedido.getString("nombre_taxi",""));
                tv_marca.setText(pedido.getString("marca",""));
                tv_placa.setText(pedido.getString("placa",""));
                tv_color.setText(pedido.getString("color",""));
                tv_numero_movil.setText("Movil Nº:"+pedido.getString("numero_movil","")+" ");

                Intent servicio_contacto = new Intent(Pedido_usuario.this, Servicio_guardar_contacto.class);
                servicio_contacto.setAction(Constants.ACTION_RUN_ISERVICE);
                servicio_contacto.putExtra("nombre",pedido.getString("nombre_taxi", ""));
                servicio_contacto.putExtra("telefono",pedido.getString("celular", ""));
                startService(servicio_contacto);


                try{
                    float conductor= Float.parseFloat(pedido.getString("calificacion_conductor","0"));
                    float vehiculo= Float.parseFloat(pedido.getString("calificacion_vehiculo","0"));
                    rb_calificacion_conductor.setRating(conductor);
                    rb_calificacion_vehiculo.setRating(vehiculo);
                }catch (Exception e)
                {

                }


                getImage(pedido.getString("id_taxi",""));

                flotante_pedir(false);
            } else if (s.equals("9") == true) {
                mensaje_error_final(suceso.getMensaje());

            } else if(s.equals("500")==true)
            {

            }else if(s.equals("6"))
            {
                SharedPreferences pedido2 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                String id_pedido=pedido2.getString("id_pedido","");

                SharedPreferences.Editor editor2 = pedido2.edit();
                editor2.putString("id_pedido", "");
                editor2.putString("estado", "4");
                editor2.commit();
                Intent cancelar_pedido=new Intent(getApplicationContext(),Cancelar_pedido_usuario.class);
                cancelar_pedido.putExtra("id_pedido",id_pedido);
                startActivity(cancelar_pedido);
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
                String id_pedido=pedido2.getString("id_pedido","");

                SharedPreferences.Editor editor2 = pedido2.edit();
                editor2.putString("id_pedido", "");
                editor2.putString("estado", "4");
                editor2.commit();
                Intent cancelar_pedido=new Intent(getApplicationContext(),Cancelar_pedido_usuario.class);
                cancelar_pedido.putExtra("id_pedido",id_pedido);
                startActivity(cancelar_pedido);
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

    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";


            if (params[1] == "1") { //mandar JSON metodo post ENVIAR LA CONFIRMACION DEL LLEGADO DEL MOTISTA.,,,
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
                    jsonParam.put("id_usuario", params[3]);
                    jsonParam.put("usuarios", params[4]);


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
                            devuelve="10";
                        } else  {
                            devuelve = "11";
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
//HACE EL CLICK CON LA CONFIRMACION DE QUE LLEGO EL MOTISTA HASTA DONDE ESTA....
            if (params[1] == "2") { //mandar JSON metodo post ENVIAR LA CONFIRMACION DEL LLEGADO DEL MOTISTA.,,,
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
                            devuelve="3";
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

            if (params[1] == "3") { //ENVIAR PANICO A MIS CONTACTOS
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


                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
                            devuelve="5";
                        } else  {
                            devuelve = "4";
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
                            String snumero_v=dato.getJSONObject(0).getString("numero_movil");
                            String sestado=dato.getJSONObject(0).getString("estado");
                            String sid_pedido=dato.getJSONObject(0).getString("id_pedido");
                            String sid_empresa=dato.getJSONObject(0).getString("id_empresa");
                            String sempresa=dato.getJSONObject(0).getString("empresa");


                            String scalificacion_conductor=dato.getJSONObject(0).getString("calificacion_conductor");
                            String scalificacion_vehiculo=dato.getJSONObject(0).getString("calificacion_vehiculo");

                            SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                            SharedPreferences.Editor editar=pedido.edit();
                            editar.putString("nombre_taxi",snombre);
                            editar.putString("celular",scelular);
                            editar.putString("id_taxi",sid_taxi);
                            editar.putString("marca",smarca);
                            editar.putString("placa",splaca);
                            editar.putString("color",scolor);
                            editar.putString("numero_movil",snumero_v);
                            editar.putString("estado",sestado);
                            editar.putString("latitud",dato.getJSONObject(0).getString("latitud"));
                            editar.putString("longitud",dato.getJSONObject(0).getString("longitud"));
                            editar.putString("id_pedido",sid_pedido);
                            editar.putString("id_empresa",sid_empresa);
                            editar.putString("empresa",sempresa);
                            editar.putString("calificacion_conductor", scalificacion_conductor);
                            editar.putString("calificacion_vehiculo", scalificacion_vehiculo);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog
            pDialog = new ProgressDialog(Pedido_usuario.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Autenticando ....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                pDialog.cancel();//ocultamos proggress dialog
            }catch (Exception e)
            {
                Log.e("pedido usuario",e.toString());
            }
            // Log.e("onPostExcute=", "" + s);
             if(s.equals("3"))
            {   Intent intent = new Intent(getApplicationContext(), Servicio_pedido.class);
                intent.setAction(Constants.ACTION_RUN_ISERVICE);
                stopService(intent);
                eliminar_pedido();
                mensaje(suceso.getMensaje());
            }else  if(s.equals("8"))
             {

                 SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                 tv_nombre.setText(pedido.getString("nombre_taxi",""));
                 tv_marca.setText(pedido.getString("marca",""));
                 tv_placa.setText(pedido.getString("placa",""));
                 tv_color.setText(pedido.getString("color",""));
                 tv_numero_movil.setText("Movil Nº:"+pedido.getString("numero_movil","")+".");
                 getImage(pedido.getString("id_taxi",""));

                 try{
                     float conductor= Float.parseFloat(pedido.getString("calificacion_conductor","0"));
                     float vehiculo= Float.parseFloat(pedido.getString("calificacion_vehiculo","0"));
                     rb_calificacion_conductor.setRating(conductor);
                     rb_calificacion_vehiculo.setRating(vehiculo);
                 }catch (Exception e)
                 {
                 }
             }else if(s.equals("10"))
             {
                 mensaje_error(suceso.getMensaje());
                alert2.cancel();
             }else if(s.equals("11"))
             {
                 mensaje_error(suceso.getMensaje());
             }
            else if(s.equals("2"))
            {
                mensaje(suceso.getMensaje());
                finish();
            }else if(s.equals("4"))
             {
                 Servicio hilo_taxi = new Servicio();
                 SharedPreferences usuario = getSharedPreferences("perfil", MODE_PRIVATE);
                 String id_usuario = usuario.getString("id_usuario", "");

                 SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                 String id_pedido = pedido.getString("id_pedido", "");
                  try {
                     hilo_taxi.execute(getString(R.string.servidor) + "frmCompartir_carrera.php?opcion=enviar_panico", "3", id_usuario,id_pedido);
                 } catch (Exception e) {
                 }

             }else if(s.equals("5"))
             {
                 mensaje(suceso.getMensaje());
             }
            else
            {

                mensaje_error_final("Falla en tu conexión a Internet.");
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

    // comenzar el servicio con el motista....
    public class Servicio_notificacion extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

            //ENVIAR NOTIFICACION AL USUARIO
            if (params[1] == "1") { //mandar JSON metodo post ENVIAR LA CONFIRMACION DEL LLEGADO DEL MOTISTA.,,,
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
                    jsonParam.put("id_usuario", params[3]);
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

                        SystemClock.sleep(950);
                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
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
            pDialog = new ProgressDialog(Pedido_usuario.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Autenticando ....");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.cancel();//ocultamos proggress dialog
            // Log.e("respuesta del servidor=", "" + s);
            if(s.equals("1"))
            {
                //mensaje(suceso.getMensaje());
            }else
            if(s.equals("500"))
            {
                mensaje(suceso.getMensaje());
            }
            else if(s.equals("2"))
            {

                mensaje(suceso.getMensaje());
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


    private void eliminar_pedido() {

            SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
            SharedPreferences.Editor editar=pedido.edit();
            editar.putString("nombre_taxi", "");
            editar.putString("celular", "");
            editar.putString("marca", "");
            editar.putString("placa", "");
            editar.putString("color", "");
            editar.putString("id_pedido", "");
             editar.commit();

        finish();


    }

    public void mensaje(String mensaje)
    {
        Toast toast = Toast.makeText(this,mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

  /*  public void pintar_recorrido_pedido() {
        boolean sw=true;
        boolean sw_punto=false;
        try {
            int id = Integer.parseInt(sid_pedido);
        }catch (Exception e)
        {
            sw=false;
        }
        if(sw==true) {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "taxicorp", null, 1);
            SQLiteDatabase bd = admin.getWritableDatabase();
            Cursor fila = null;
            fila = bd.rawQuery("select * from puntos_pedido where id_pedido=" + sid_pedido + " ORDER BY fecha ASC ", null);

            try {
                //limpiamos el mapa
                // mMap.clear();
                LatLng punto = new LatLng(0, 0);
                PolylineOptions polylineOptions = new PolylineOptions();
                if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
                    do {
                        double lat = Double.parseDouble(fila.getString(1));
                        double lon = Double.parseDouble(fila.getString(2));
                        punto = new LatLng(lat, lon);
                        polylineOptions.add(punto);
                        sw_punto=true;
                    } while (fila.moveToNext());
                }
                if(sw_punto==true) {
                    bd.close();
                    mMap.addPolyline(polylineOptions.width(15).color(-16776961));

                    LatLng inicio = new LatLng(0, 0);
                    inicio = primero_registro(Integer.parseInt(sid_pedido));
                    LatLng fin = new LatLng(0, 0);
                    fin = ultimo_registro(Integer.parseInt(sid_pedido));
                    ultima_ubicacion=fin;
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_inicio))
                            .position(inicio)
                            .anchor((float)0.5,(float)0.8)
                            .flat(true)
                            .rotation(0)
                            .title("I"));

                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_taxi))
                            .position(fin)
                            .anchor((float)0.5,(float)0.8)
                            .flat(true)
                            .rotation(0)
                            .title("F"));


                        try{
                            SharedPreferences prefe=getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                            double lat=Double.parseDouble(prefe.getString("latitud",""));
                            double lon=Double.parseDouble(prefe.getString("longitud",""));

                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point1))
                                    .anchor((float)0.5,(float)0.8)
                                    .flat(true)
                                    .position(new LatLng(lat, lon))
                                    .title(prefe.getString("",""))
                                    .snippet(prefe.getString("","")));

                        }catch (Exception e)
                        {

                        }

                }
            } catch (Exception e) {

            }
        }

    }
    */

    public LatLng ultimo_registro( int id_pedido) {
        LatLng punto=new LatLng(0,0);
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from puntos_pedido where id_pedido="+id_pedido+" ORDER BY fecha DESC limit 1", null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

            double lat= Double.parseDouble(fila.getString(1));
            double lon= Double.parseDouble(fila.getString(2));
            punto=new LatLng(lat,lon);
            // mMap.addPolyline(new PolylineOptions().)

        }
        bd.close();
        return punto;
    }
    public LatLng ultimo_registro() {
        LatLng punto=new LatLng(0,0);
       try
       {
           SharedPreferences punto_pedido=getSharedPreferences("punto taxi",MODE_PRIVATE);
           double lat= Double.parseDouble(punto_pedido.getString("latitud","0"));
           double lon= Double.parseDouble(punto_pedido.getString("longitud","0"));
           rotacion= Integer.parseInt(punto_pedido.getString("rotacion","0"));
           clase_vehiculo_en_pedido=  punto_pedido.getInt("clase_vehiculo",1);
           punto=new LatLng(lat,lon);
       }catch (Exception e)
       {

       }
        return punto;
    }
/*
    public LatLng primero_registro( int id_pedido) {
        LatLng punto=new LatLng(0,0);
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"taxicorp", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from puntos_pedido where id_pedido="+id_pedido+" ORDER BY fecha ASC limit 1 ", null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
            double lat=Double.parseDouble(fila.getString(1));
            double lon=Double.parseDouble(fila.getString(2));
            punto=new LatLng(lat,lon);
        }

        bd.close();
        return punto;
    }

*/

    //INICIO DE SERVICIO DE COORDENADAS..

    private void enableLocationUpdates() {

        locRequest = new LocationRequest();
        locRequest.setInterval(4000);
        locRequest.setFastestInterval(2500);
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
                            status.startResolutionForResult(Pedido_usuario.this, PETICION_CONFIG_UBICACION);
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
        if (ActivityCompat.checkSelfPermission(Pedido_usuario.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest,Pedido_usuario.this);
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

            String abordo="";

            try{
                SharedPreferences prefe=getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                double lat= Double.parseDouble(prefe.getString("latitud",""));
                double lon= Double.parseDouble(prefe.getString("longitud",""));
                abordo=prefe.getString("abordo","");

                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point1))
                        .anchor((float)0.5,(float)0.8)
                        .flat(true)
                        .position(new LatLng(lat, lon))
                        .title(prefe.getString("",""))
                        .snippet(prefe.getString("","")));

            }catch (Exception e)
            {

            }
            if(clase_vehiculo_en_pedido>=7 && clase_vehiculo_en_pedido<=8){
                Marker moto=mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker))
                        .position(fin)
                        .anchor((float)0.5,(float)0.8)
                        .flat(true)
                        .rotation(rotacion)
                        .title("Moto"));
            }else{
                if(m_conductor_cantida==0) {

                    m_conductor = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                            .position(fin)
                            .anchor((float) 0.5, (float) 0.8)
                            .flat(true)
                            .rotation(rotacion)
                    );

                    m_conductor_cantida=1;
                }else{
                    // m_conductor.setPosition(fin);
                    m_conductor.setRotation(rotacion);
                    MarkerAnimation.animateMarkerToGB(m_conductor, fin, new LatLngInterpolator.Spherical());

                }
            }


            float mm=mMap.getCameraPosition().zoom;
            if(mm<13){

                CameraPosition cameraPosition1 = new CameraPosition.Builder()
                        .target(ultima_ubicacion)      // Sets the center of the map to Mountain View
                        .zoom(15)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
            }else{
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ultima_ubicacion));
            }




            if(abordo.equals("0")){
                datos_de_google();
                ll_cancelar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }else if(abordo.equals("1")){
                ll_cancelar.setLayoutParams(new LinearLayout.LayoutParams(0,0));

            }else{

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
                        Log.i(LOGTAG, "El usuario no ha realizado los cambios de configuración necesarios");

                        break;
                }
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int estado_pedido=0;
            int estado_finalizo=0;
            int id_pedido=0;

            if (intent.getAction().equals(Servicio_pedir_movil.ACTION_PROGRESO)) {
                estado_pedido= intent.getIntExtra("estado",0);
                id_pedido= intent.getIntExtra("id_pedido",0);
            } else if (intent.getAction().equals(Servicio_pedir_movil.ACTION_FINAL)) {
                estado_finalizo= intent.getIntExtra("estado",0);
                Intent stopIntent = new Intent(Pedido_usuario.this, Servicio_pedir_movil.class);
                stopService(stopIntent);
            }
            if(estado_pedido==1 || estado_finalizo==1){
                verificar_pedido();
            }
           // tv_titulo.setText("e.p:"+estado_pedido);
           // tv_titulo.setText("e.f:"+estado_finalizo);
        }
    };

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
        builder.setTitle("Ups");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }

    public void mensaje_error_final(String mensaje)
    {   try {
        if(sw_destroy==false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Pedido_usuario.this);
            builder.setTitle("Ups");
            builder.setCancelable(false);
            builder.setMessage(mensaje);
            builder.create();
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    finish();
                }
            });
            builder.show();
        }else
        {
            finish();
        }
    }catch (Exception e)
    {   Log.e("mensaje_error",e.toString());
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
    public void verificar_pedido() {
        SharedPreferences prefe = getSharedPreferences("pedido_en_proceso", Context.MODE_PRIVATE);
        try {
            int id_pedido = Integer.parseInt(prefe.getString("id_pedido", ""));
            hilo_taxi_obtener_dato.cancel(true);
            hilo_taxi_obtener_dato=new Servicio_pedir_taxi();
            hilo_taxi_obtener_dato.execute(getString(R.string.servidor) + "frmPedido.php?opcion=verificar_si_acepto_pedido_sin_notificacion", "2", String.valueOf(id_pedido));// parametro que recibe el doinbackground
            tv_mensaje_pedido.setText("Esperando la confirmación por el Taxista ...");


        } catch (Exception e) {
        }
    }

    private void getImage(String id)//
    {

        /*
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
                    mIcon = drawableToBitmap(d);
                }
                return mIcon;
            }
        }
        */

        String  url=  getString(R.string.servidor_web)+"public/Imagen_Conductor/Perfil-"+id+".png";
        Picasso.with(this).load(url).into(target);
    }


    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            Drawable dw = new BitmapDrawable(getResources(), bitmap);
            //se edita la imagen para ponerlo en circulo.

            if( bitmap==null)
            { dw = getResources().getDrawable(R.drawable.ic_perfil_blanco);}

            imagen_circulo(dw,im_perfil);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
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
}catch (Exception e)
{

}
    }
    public void cargar_usuarios(ArrayList<CUsuario> historial, ListView lista) {
        historial.clear();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select id,nombre,apellido,correo,celular from usuario  ORDER BY nombre ASC ", null);
        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
            do {
                int id= Integer.parseInt(fila.getString(0));
                String nombre= fila.getString(1);
                String apellido= fila.getString(2);
                String correo= fila.getString(3);
                String celular= fila.getString(4);

                historial.add(new CUsuario(id,nombre,apellido,correo,celular));
            } while (fila.moveToNext());

        } else
            Toast.makeText(this, "No hay registrados",
                    Toast.LENGTH_SHORT).show();
        bd.close();
        Usuario_select_adapter adaptador = new Usuario_select_adapter(Pedido_usuario.this,historial);
        lista.setAdapter(adaptador);
    }



    public JSONArray compartir_lista_todo(ArrayList<CUsuario> lista)
    {
        JSONArray array=new JSONArray();
        for (int i=0;i<lista.size();i++)
        {try {
            CUsuario usuario = lista.get(i);
            String id = String.valueOf(usuario.getId());
            JSONObject object = new JSONObject();
            object.put("id_usuario", id);
            array.put(object);
            }catch (Exception e)
            {}
        }
        return  array;
    }
    public JSONArray compartir_lista_seleccionado(ArrayList<CUsuario> lista)
    {
        JSONArray array=new JSONArray();
        for (int i=0;i<lista.size();i++)
        {try {
            CUsuario usuario = lista.get(i);
            String id = String.valueOf(usuario.getId());
            JSONObject object = new JSONObject();

            if(usuario.isEstado()){
            object.put("id_usuario", id);
            array.put(object);
            }
        }catch (Exception e)
        {}
        }
        return  array;
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
                    ActivityCompat.requestPermissions(Pedido_usuario.this,
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
            ActivityCompat.requestPermissions(Pedido_usuario.this,
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
                    ActivityCompat.requestPermissions(Pedido_usuario.this,
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
            ActivityCompat.requestPermissions(Pedido_usuario.this,
                    PERMISSIONS,
                    1);
        }
    }

    public void verificar_permiso_llamada()
    {
        final String[] PERMISSIONS = { android.Manifest.permission.INTERNET,
                Manifest.permission.CALL_PHONE,
                android.Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a LLAMADA.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Pedido_usuario.this,
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
            ActivityCompat.requestPermissions(Pedido_usuario.this,
                    PERMISSIONS,
                    1);
        }
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
    public void dibujar_ruta(JSONObject jObject){

        String tiempo="";
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        boolean sw_punto=false;
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
                    tiempo=(String)((JSONObject)((JSONObject)jLegs.get(j)).get("duration")).get("text");
                }
            }

            try {
                tv_titulo.setText(tiempo);
            } catch (Exception e) {
                tv_titulo.setText("");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

    }

    public void datos_de_google()
    {
        try{
            //buscamos una ruta para el motista     SOLO CO ACCESO A INTERNET
            SharedPreferences punto=getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
            double latitud_fin= Double.parseDouble(punto.getString("latitud","0"));
            double longitud_fin= Double.parseDouble(punto.getString("longitud","0"));

            int distancia =0;
            try {
                distancia = getDistancia(ultima_ubicacion.latitude, ultima_ubicacion.longitude, latitud_fin, longitud_fin);
            }catch (Exception e)
            {
                distancia=0;
            }
            if(distancia>=1000) {

                m_conductor.setTitle("Llegada");
                m_conductor.setSnippet(distancia / 400 +" min.");
                m_conductor.showInfoWindow();
            }else
            {

                m_conductor.setTitle("Llegada");
                m_conductor.setSnippet("1 min.");
                m_conductor.showInfoWindow();
            }
        }catch (Exception e)
        {

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
