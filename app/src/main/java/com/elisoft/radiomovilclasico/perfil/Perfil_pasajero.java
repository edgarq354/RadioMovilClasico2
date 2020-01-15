package com.elisoft.radiomovilclasico.perfil;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.Constants;
import com.elisoft.radiomovilclasico.Inicio;
import com.elisoft.radiomovilclasico.Menu_usuario;
import com.elisoft.radiomovilclasico.Pedido_usuario;
import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.Servicio_descargar_imagen_perfil;
import com.elisoft.radiomovilclasico.Servicio_pedido;
import com.elisoft.radiomovilclasico.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.radiomovilclasico.Suceso;
import com.elisoft.radiomovilclasico.corporativo.Empresa;
import com.elisoft.radiomovilclasico.preregistro.Foto_conductor;
import com.elisoft.radiomovilclasico.registro_inicio_sesion.Animacion;
import com.facebook.login.LoginManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.support.v4.content.FileProvider.getUriForFile;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class Perfil_pasajero extends AppCompatActivity implements View.OnClickListener {
    EditText nombre,apellido,celular,email;
    ImageButton bt_actualizar_dato;
    LinearLayout bt_editar_password;
    boolean sw=false;
    boolean click;
    ImageView perfil;

    TextView tv_direccion_guardada,tv_billetera,tv_corporativo,tv_cerrar_sesion;

    Suceso suceso;
    ProgressDialog pDialog;



    private String mPath;
    private Uri path;

    Intent CropIntent;





    private   String CARPETA_RAIZ="";
    private   String RUTA_IMAGEN="";

    final int COD_SELECCIONA=10;
    final int COD_FOTO=20;

    String path2;



    String id_usuario="";

    AlertDialog alert2 = null;

    private static final String[] SMS_PERMISSIONS = { android.Manifest.permission.INTERNET,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.ACCESS_NETWORK_STATE };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Menu_usuario.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_pasajero);
        nombre=(EditText)findViewById(R.id.nombre);
        apellido=(EditText)findViewById(R.id.apellido);
        celular=(EditText)findViewById(R.id.celular);
        email=(EditText)findViewById(R.id.email);

        tv_direccion_guardada=(TextView)findViewById(R.id.tv_direccion_guardada);
        tv_billetera=(TextView)findViewById(R.id.tv_billetera);

        perfil=(ImageView)findViewById(R.id.perfil);
        bt_actualizar_dato=(ImageButton) findViewById(R.id.bt_actualizar_dato);
        bt_editar_password=(LinearLayout)findViewById(R.id.bt_editar_password);
        tv_corporativo=(TextView)findViewById(R.id.tv_corporativo);
        tv_cerrar_sesion=(TextView)findViewById(R.id.tv_cerrar_sesion);

        cargar_datos();
        click=false;

        bt_actualizar_dato.setOnClickListener(this);
        tv_direccion_guardada.setOnClickListener(this);
        tv_billetera.setOnClickListener(this);
        tv_cerrar_sesion.setOnClickListener(this);
        tv_corporativo.setOnClickListener(this);


        imagen_en_vista(perfil);

        perfil.setOnClickListener(this);
        bt_editar_password.setOnClickListener(this);
        celular.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        if(validaPermisos()){
            perfil.setEnabled(true);
        }else{
            perfil.setEnabled(false);
        }

        CARPETA_RAIZ=getString(R.string.app_name)+"/";
        RUTA_IMAGEN=CARPETA_RAIZ+"misFotos";
        SharedPreferences perfil2=getSharedPreferences("perfil",MODE_PRIVATE);
        id_usuario=perfil2.getString("id_usuario","");
    }



    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }



    public  void direcciones_guardadas()
    {
        startActivity(new Intent(this,Mis_direcciones.class));
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tv_cerrar_sesion:
                actualizar();
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle(getString(R.string.app_name));
                dialogo1.setMessage("¿Cerrar Sesion?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        //cargamos los datos
                        SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                        try {
                            int id_pedido = Integer.parseInt(prefe.getString("id_pedido", ""));
                            if (id_pedido == 0) {
                                cerrar_sesion();
                            } else if (id_pedido != 0 ) {
                                mensaje_cerrar_sesion("No se puede Cerrar Sesion porque tiene un pedido en camino.",id_pedido);

                            }
                        } catch (Exception e) {
                            cerrar_sesion();
                        }


                    }
                });
                dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                    }
                });
                dialogo1.show();
                break;
            case R.id.tv_corporativo:
                SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
                try {
                    int id_usuario = Integer.parseInt(prefe.getString("id_usuario", ""));
                    Servicio_corporativo hilo = new  Servicio_corporativo();
                    hilo.execute(getString(R.string.servidor) + "frmCorporativo.php?opcion=verificar_administrador_empresa", "1", String.valueOf(id_usuario));// parametro que recibe el doinbackground
                } catch (Exception e) {

                }
                break;
            case R.id.tv_direccion_guardada:
                direcciones_guardadas();
                break;
            case R.id.tv_billetera:
                startActivity(new Intent(this,Billetera.class));
                break;
            case R.id.editar:
                click=!click;
                fb_reconfiguracion(click);
                if(click==false )
                {
                    if(nombre.getText().toString().length()>=3 && apellido.getText().toString().length()>=3 && email.getText().toString().length()>=8) {
                        validacion();
                    }
                    else
                    {
                        mensaje("Por favor Ingrese los datos correctamente.");
                    }
                }
                break;
            case  R.id.bt_actualizar_dato:
                if(nombre.getText().toString().trim().length()>=3 && apellido.getText().toString().trim().length()>=3 ) {
                    validacion();
                }
                else
                {
                    mensaje("Por favor Ingrese los datos correctamente.");
                }
                break;
            case  R.id.bt_editar_password:
                startActivity(new Intent(this,Modificar_contrasenia.class));
                break;
            case R.id.perfil:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_camara();
                }
                else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_almacenamiento();
                } else {
                    cargarImagen();

                }
                break;
            case  R.id.celular:
                verificar_permiso_sms();
                break;
        }



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //ubicacion de la imagen

            switch (requestCode){

                case 1:


                    try {

                        if(savebitmap()==true)
                        {
                            subir_imagen_servidor("imagen_1");
                        }else{
                            mensaje("Vuelve a tomar la foto.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this,"Error al subir la imagen. ->"+e.toString(),Toast.LENGTH_LONG).show();
                    }
                    break;

                case COD_SELECCIONA:
                    Uri uri=data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    guardar_en_memoria(bitmap);

                    CropImage(uri);

                    // perfil.setImageURI(miPath);
                    break;

                case COD_FOTO:
                    MediaScannerConnection.scanFile(this, new String[]{path2}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Ruta de almacenamiento","Path: "+path2);
                                    CropImage(uri);
                                }
                            });


                    //Convertir MPath a Bitmap
                    // File newFile2 = new File(path2);
                    //  Uri uri2 = Uri.fromFile(newFile2);
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mPath = savedInstanceState.getString("file_path");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                perfil.setEnabled(true);
            }
        }else if(requestCode == 101){
            cargar_datos();
        }else{
            showExplanation();
        }
    }






    private void actualizar() {
        SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
        try {
            int id_usuario = Integer.parseInt(prefe.getString("id_usuario", ""));
            Servicio_cerrar hilo = new Servicio_cerrar();
            hilo.execute(getString(R.string.servidor) + "frmPedido.php?opcion=get_pedido_por_id_usuario", "6", String.valueOf(id_usuario));// parametro que recibe el doinbackground

        } catch (Exception e) {

        }


    }
    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        try {
            builder.show();
        }catch (Exception e){
            Log.e("Permiso Explanation","sin permiso en configuracion");
        }
    }

    public Bitmap imagen_cuadrado(Bitmap originalBitmap)
    {
        if (originalBitmap.getWidth() > originalBitmap.getHeight()){
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        }else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }
        return originalBitmap;
    }
    public void cargar_datos()
    {
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        nombre.setText(perfil.getString("nombre",""));
        apellido.setText(perfil.getString("apellido",""));
        celular.setText(perfil.getString("celular",""));
        email.setText(perfil.getString("email",""));
    }

    public void fb_reconfiguracion(boolean direccion)
    {
        click=direccion;
        //  editar.setImageResource(R.drawable.ic_menu_manage);

        this.nombre.setEnabled(direccion);
        this.apellido.setEnabled(direccion);
        this.celular.setEnabled(direccion);
        this.email.setEnabled(direccion);

        if(direccion)
        {
            //   editar.setImageResource(R.drawable.ic_menu_send);

        }


    }


    public void validacion()
    {

        try {
            int numero = Integer.parseInt(celular.getText().toString());


            if (celular.getText().toString().trim().length() >= 8 && numero >= 60000000 && numero <= 79999999) {

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle("Vamos a verificar el número de telefono");
                dialogo1.setMessage(""+celular.getText()+" \n¿Es Correcto este número o quieres modificarlo?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        //cargamos los datos.
                        Servicio servicio=new Servicio();
                        SharedPreferences perfil =getSharedPreferences("perfil",MODE_PRIVATE);
                        servicio.execute(getString(R.string.servidor)+"frmUsuario.php?opcion=actualizar_dato","1",perfil.getString("id_usuario",""),nombre.getText().toString(),apellido.getText().toString(),celular.getText().toString(),email.getText().toString());

                    }
                });
                dialogo1.setNegativeButton("EDITAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                    }
                });
                dialogo1.show();


            } else {

                mensaje_error("Número Invalido.\n" +
                        "Por favor ingrese un número valido.");
            }
        }

        catch (Exception e)
        {
            mensaje_error("Por favor complete los campos.");
        }
    }
    // comenzar el servicio con el motista....
    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";



//Enviamos un correo m¡para confirmar la modificacion de su cuenta..
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
                    jsonParam.put("id", params[2]);
                    jsonParam.put("nombre", params[3]);
                    jsonParam.put("apellido", params[4]);
                    jsonParam.put("celular", params[5]);
                    jsonParam.put("email", params[6]);


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


            if (params[1] == "2") {
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
                    jsonParam.put("imagen", params[3]);
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
            pDialog = new ProgressDialog(Perfil_pasajero.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Autenticando ....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();//ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);
            if(s.equals("3")||s.equals("1"))
            {
                mensaje(suceso.getMensaje());
            }
            else if(s.equals("2"))
            {
                mensaje(suceso.getMensaje());
                finish();
            }
            else if(s.equals("5")){
                SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
                SharedPreferences.Editor editor=perfil.edit();
                editor.putString("nombre",nombre.getText().toString());
                editor.putString("apellido",apellido.getText().toString());
                editor.putString("celular",celular.getText().toString());
                editor.putString("email",email.getText().toString());
                editor.commit();
                finish();
            }else if(s.equals("4"))
            {
                mensaje_error(suceso.getMensaje());
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



    // comenzar el servicio para la conexion con la base de datos.....
    public class Servicio_cerrar extends AsyncTask<String,Integer,String> {


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
             if(s.equals("9"))
            {
                Intent intent = new Intent(Perfil_pasajero.this, Servicio_pedido.class);
                intent.setAction(Constants.ACTION_RUN_ISERVICE);
                startService(intent);

                SharedPreferences spedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);

                Intent pedido=new Intent(Perfil_pasajero.this,Pedido_usuario.class);
                pedido.putExtra("latitud", Double.parseDouble(spedido.getString("latitud","0")));
                pedido.putExtra("longitud", Double.parseDouble(spedido.getString("longitud","0")));
                pedido.putExtra("id_pedido",spedido.getString("id_pedido","0"));
                startActivity( pedido);
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
            pDialog = new ProgressDialog(Perfil_pasajero.this);
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



    public void imagen_en_vista(ImageView imagen)
    { Drawable dw;
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        String mPath = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name)+"/Imagen"
                + File.separator + perfil.getString("id_usuario","")+"_perfil.jpg";


        File newFile = new File(mPath);
        Bitmap bitmap =  BitmapFactory.decodeFile(newFile.getAbsolutePath());
        //Convertir Bitmap a Drawable.
        dw = new BitmapDrawable(getResources(), bitmap);
        //se edita la imagen para ponerlo en circulo.

        if( bitmap==null)
        { dw = getResources().getDrawable(R.drawable.ic_perfil_negro);}

        imagen_circulo(dw,imagen);
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

    public static Bitmap ReducirImagen_b(Bitmap BitmapOrg, int w, int h) {

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;
        // calculamos el escalado de la imagen destino
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // para poder manipular la imagen
        // debemos crear una matriz
        Matrix matrix = new Matrix();
        // Cambiar el tamaño del mapa de bits
        matrix.postScale(scaleWidth, scaleHeight);
        // volvemos a crear la imagen con los nuevos valores
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);

        return resizedBitmap;
    }

    /*Ahora que se tiene la imagen cargado en mapa de bits.
Vamos a convertir este mapa de bits a cadena de base64
este método es para convertir este mapa de bits a la cadena de base64*/
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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

    public void mensaje_cerrar_sesion(String mensaje, final int id_pedido)
    {
        try {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención");
            dialogo1.setMessage(mensaje);
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    Intent pedido=new Intent(getApplication(),Pedido_usuario.class);
                    pedido.putExtra("id_pedido",String.valueOf(id_pedido));
                    startActivity(pedido);
                }
            });

            dialogo1.show();
        }catch (Exception e){

        }
    }



    public void mensaje(String mensaje)
    {
        Toast toast = Toast.makeText(this,mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
    public void mensaje_error(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }

    private void CropImage(Uri uri) {

        Uri uri2=Uri.fromFile(new File(path2));
        try{


            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri,"image/*");

            CropIntent.putExtra("crop","true");
            CropIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri2);
            CropIntent.putExtra ("outputX", 5000);
            CropIntent.putExtra ("outputY", 5000);
            CropIntent.putExtra ("aspectX", 4);
            CropIntent.putExtra ("aspectY", 4);
            CropIntent.putExtra ("scale", true);
            CropIntent.putExtra ("scaleUpIfNeeded", true);


            startActivityForResult(CropIntent,1);
        }
        catch (ActivityNotFoundException ex)
        {

        }

    }


    public void verificar_permiso_sms()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de SMS para realizar la autenficación");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Perfil_pasajero.this,
                            SMS_PERMISSIONS,
                            101);

                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();

                }
            });
            dialogo1.show();
        } else {
            ActivityCompat.requestPermissions(Perfil_pasajero.this,
                    SMS_PERMISSIONS,
                    101);
        }
    }


    public void verificar_permiso_camara()
    {
        final String[] CAMERA_PERMISSIONS = { android.Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
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
                    ActivityCompat.requestPermissions(Perfil_pasajero.this,
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
            ActivityCompat.requestPermissions(Perfil_pasajero.this,
                    CAMERA_PERMISSIONS,
                    1);
        }
    }

    public void verificar_permiso_almacenamiento()
    {
        final String[] CAMERA_PERMISSIONS = { android.Manifest.permission.INTERNET,
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
                    ActivityCompat.requestPermissions(Perfil_pasajero.this,
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
            ActivityCompat.requestPermissions(Perfil_pasajero.this,
                    CAMERA_PERMISSIONS,
                    1);
        }
    }



    public boolean savebitmap() {

        boolean result = false;
        // nombre = fecha + valoresGenerales.isTipoArchivoImagenExtencionPng;

        String filename = path2;

        OutputStream outStream = null;

        File file = new File(filename );


        int m_inSampleSize = 0;
        int m_compress = 80;

        // 100 dejarlo original
        //  0  comprimir al maximo .. no se recomeinda.


        try {

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inPurgeable = true;
            bmOptions.inSampleSize = m_inSampleSize;
            //Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            Bitmap bitmap = BitmapFactory.decodeFile(filename, bmOptions);
            // make a new bitmap from your file
            //Bitmap bitmap = BitmapFactory.decodeFile(file.getName());

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, m_compress, outStream);
            outStream.flush();
            outStream.close();
            result = true;



        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        Log.e("file", "" + file);
        return result;

    }

    private void subir_imagen_servidor(String opcion_subir_imagen ) {



        switch (opcion_subir_imagen)
        {

            case "imagen_1":
                serverUpdate(getString(R.string.servidor) + "frmUsuario.php?opcion=insertar_imagen_perfil",opcion_subir_imagen);
                break;

        }
    }

    class ServerUpdate extends AsyncTask<String,String,String> {

        ProgressDialog pDialog;
        String resultado="",tipo="";
        @Override
        protected String doInBackground(String... arg0) {
            resultado=uploadFoto(arg0[0],arg0[1] );
            tipo=arg0[1];


            if(suceso.getSuceso().equals("1"))
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {

                        // TODO Auto-generated method stub
                        Toast.makeText(Perfil_pasajero.this, suceso.getMensaje(),Toast.LENGTH_LONG).show();
                    }
                });
            else
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        AlertDialog.Builder builder = new AlertDialog.Builder(Perfil_pasajero.this);
                        builder.setTitle("Importante");
                        builder.setMessage(suceso.getMensaje());
                        builder.setPositiveButton("OK", null);
                        builder.create();
                        builder.show();
                    }
                });
            return null;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Perfil_pasajero.this);
            pDialog.setMessage("Subiendo la imagen, espere..." );
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();


            if(resultado.equals("2"))
            {
                File newFile = new File(path2);
                Bitmap bitmap =  BitmapFactory.decodeFile(newFile.getAbsolutePath());
                //Convertir Bitmap a Drawable.
               Drawable dw = new BitmapDrawable(getResources(), bitmap);
                //se edita la imagen para ponerlo en circulo.

                if( bitmap==null)
                { dw = getResources().getDrawable(R.drawable.ic_perfil_negro);}

                imagen_circulo(dw,perfil);
                guardar_en_memoria_BITMAP(bitmap);



            }else if(resultado.equals("500"))
            {
                mensaje(suceso.getMensaje());
            }else
            {
                mensaje("Falla en tu conexion a internet.");
            }
        }

    }



    private void serverUpdate(String url,String tipo){
        File file = new File(path2);
        if (file.exists()){
            new ServerUpdate().execute(url,tipo);
        }
        else
        {
            Log.e("Imagen","No se pudo localizar la imagen");
        }
    }


    ///PRUEBA DE INSERTAR IMAGEN

    private String uploadFoto(String url,String tipo){
        String devuelve="";
        File file = new File(path2);

        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpPost httppost = new HttpPost(url);

        MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        ContentBody foto = new FileBody(file);
        mpEntity.addPart("imagen", foto);


        try {
            mpEntity.addPart("id_usuario", new StringBody(id_usuario));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httppost.setEntity(mpEntity);


        String resultado;
        HttpResponse response;
        try {
            response=httpclient.execute(httppost);
            HttpEntity entity =response.getEntity();

            InputStream inputStream= entity.getContent();
            resultado=convertStreamToString(inputStream);

            JSONObject respuestaJSON = new JSONObject(resultado.toString());//Creo un JSONObject a partir del
            suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

            if (suceso.getSuceso().equals("1")) {

                if(tipo=="imagen_1"){

                    devuelve="2";
                }else{
                    devuelve = "500";
                }
            }else
            {
                devuelve = "500";
            }

            httpclient.getConnectionManager().shutdown();


            return devuelve;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return devuelve;
    }



    public String convertStreamToString(InputStream is) throws IOException{
        if(is!=null)
        {
            StringBuilder sb= new StringBuilder();
            String line;
            try{
                BufferedReader reader=new BufferedReader(new InputStreamReader(is,"UTF-8"));
                while ((line=reader.readLine())!=null){
                    sb.append(line).append("\n");
                }

            }finally {
                is.close();
            }
            return  sb.toString();
        }else {
            return "";
        }
    }


    private void guardar_en_memoria_BITMAP(Bitmap bitmapImage)
    {

        if(bitmapImage!=null) {

            try {
                File file=null;
                FileOutputStream fos = null;
                String APP_DIRECTORY = getString(R.string.app_name)+"/";//nombre de directorio
                String MEDIA_DIRECTORY = APP_DIRECTORY + "Imagen";//nombre de la carpeta
                file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
                File mypath = new File(file, id_usuario+ "_perfil.jpg");//nombre del archivo imagen

                boolean isDirectoryCreated = file.exists();//pregunto si esxiste el directorio creado
                if (!isDirectoryCreated)
                    isDirectoryCreated = file.mkdirs();

                if (isDirectoryCreated) {
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
    private void guardar_en_memoria(Bitmap bitmapImage)
    {
        File file=null;
        FileOutputStream fos = null;

        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        try {
            String APP_DIRECTORY = getString(R.string.app_name)+"/";//nombre de directorio
            String MEDIA_DIRECTORY = APP_DIRECTORY + "Imagen";//nombre de la carpeta
            file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);

            boolean isDirectoryCreated = file.exists();//pregunto si esxiste el directorio creado
            if(!isDirectoryCreated)
                isDirectoryCreated = file.mkdirs();

            if(isDirectoryCreated) {
                fos = new FileOutputStream(new File(path2));
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    private void tomarFotografia() {
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }

        if(isCreada==true){
            nombreImagen=(System.currentTimeMillis()/1000)+".jpg";
        }


        path2=Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombreImagen;

        File imagen=new File(path2);

        Intent intent=null;
        intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ////
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            String authorities=getApplicationContext().getPackageName()+".provider";
            Uri imageUri= FileProvider.getUriForFile(this,authorities,imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }
        startActivityForResult(intent,COD_FOTO);

        ////
    }

    public void abrir_galeria()
    {
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }

        if(isCreada==true){
            nombreImagen=(System.currentTimeMillis()/1000)+".jpg";
        }


        path2=Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombreImagen;

        File imagen=new File(path2);




        Intent pictureActionIntent = null;
        pictureActionIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pictureActionIntent.setType("image/*");
        startActivityForResult(
                pictureActionIntent,
                COD_SELECCIONA);
        //startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA);
    }



    public void  cargarImagen()
    {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.seleccionar_opcion, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);


        final LinearLayout ll_camara=(LinearLayout) promptView.findViewById(R.id.ll_camara);
        final LinearLayout ll_galeria=(LinearLayout) promptView.findViewById(R.id.ll_galeria);


        ll_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFotografia();
                alert2.cancel();
            }
        });

        ll_galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrir_galeria();
                alert2.cancel();
            }
        });

        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x=0;
        lp.y=0;


        alert2.getWindow().setAttributes(lp);

        alert2.getWindow().getAttributes().gravity= Gravity.BOTTOM;
        alert2.getWindow().getAttributes().horizontalMargin=0.01F;
        alert2.getWindow().getAttributes().verticalMargin=0.01F;
        alert2.show();


    }



}
