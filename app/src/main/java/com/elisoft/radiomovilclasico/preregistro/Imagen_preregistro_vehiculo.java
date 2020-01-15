package com.elisoft.radiomovilclasico.preregistro;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.Suceso;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import static com.elisoft.radiomovilclasico.perfil.Perfil_pasajero.ReducirImagen_b;

public class Imagen_preregistro_vehiculo extends AppCompatActivity implements  View.OnClickListener {

    ImageView im_imagen_1;
    ImageView im_imagen_2;
    ImageView im_imagen_3;
    ImageView im_imagen_4;
    ImageView im_imagen_soat;
    ImageView im_imagen_ruat;
    ImageView im_imagen_inspeccion_tecnica;


    String v_direccion_imagen_1="";
    String v_direccion_imagen_2= "";
    String v_direccion_imagen_3= "";
    String v_direccion_imagen_4= "";

    String direccion_imagen_ruat="";
    String direccion_imagen_soat="";
    String v_direccion_imagen_inspeccion_tecnica="";



    String direccion_imagen="";
    String direccion_imagen_carnet_1="";
    String direccion_imagen_carnet_2="";
    String direccion_imagen_licencia_1="";
    String direccion_imagen_licencia_2="";


    Suceso suceso;
    ProgressDialog pDialog;

    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    String ci="",placa="";
    String mPath="";
    private Uri path;
    String opcion_subir_imagen="";


    Bitmap bitmap_aux=null;

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_preregistro_vehiculo);
        im_imagen_1=(ImageView)findViewById(R.id.im_imagen_1);
        im_imagen_2=(ImageView)findViewById(R.id.im_imagen_2);
        im_imagen_3=(ImageView)findViewById(R.id.im_imagen_3);
        im_imagen_4=(ImageView)findViewById(R.id.im_imagen_4);

        im_imagen_soat=(ImageView)findViewById(R.id.im_imagen_soat);
        im_imagen_ruat=(ImageView)findViewById(R.id.im_imagen_ruat);
        im_imagen_inspeccion_tecnica=(ImageView)findViewById(R.id.im_imagen_inspeccion_tecnica);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        im_imagen_1.setOnClickListener(this);
        im_imagen_2.setOnClickListener(this);
        im_imagen_3.setOnClickListener(this);
        im_imagen_4.setOnClickListener(this);


        im_imagen_soat.setOnClickListener(this);
        im_imagen_ruat.setOnClickListener(this);
        im_imagen_inspeccion_tecnica.setOnClickListener(this);

        try{
            Bundle bundle=getIntent().getExtras();
            ci=bundle.getString("ci");
            placa=bundle.getString("placa");
            v_direccion_imagen_1=bundle.getString("direccion_imagen_1");
            v_direccion_imagen_2=bundle.getString("direccion_imagen_2");
            v_direccion_imagen_3=bundle.getString("direccion_imagen_3");
            v_direccion_imagen_4=bundle.getString("direccion_imagen_4");

            direccion_imagen_ruat=bundle.getString("direccion_imagen_ruat");
            direccion_imagen_soat=bundle.getString("direccion_imagen_soat");
            v_direccion_imagen_inspeccion_tecnica=bundle.getString("direccion_imagen_inspeccion_tecnica");



            direccion_imagen=bundle.getString("direccion_imagen");
            direccion_imagen_carnet_1=bundle.getString("direccion_imagen_carnet_1");
            direccion_imagen_carnet_2=bundle.getString("direccion_imagen_carnet_2");
            direccion_imagen_licencia_1=bundle.getString("direccion_imagen_licencia_1");
            direccion_imagen_licencia_2=bundle.getString("direccion_imagen_licencia_2");



            getImage(getString(R.string.servidor_web)+"storage/"+v_direccion_imagen_1,"1");
            getImage(getString(R.string.servidor_web)+"storage/"+v_direccion_imagen_2,"2");
            getImage(getString(R.string.servidor_web)+"storage/"+v_direccion_imagen_3,"3");
            getImage(getString(R.string.servidor_web)+"storage/"+v_direccion_imagen_4,"4");



            getImage(getString(R.string.servidor_web)+"storage/"+direccion_imagen_soat,"10");
            getImage(getString(R.string.servidor_web)+"storage/"+direccion_imagen_ruat,"11");
            getImage(getString(R.string.servidor_web)+"storage/"+v_direccion_imagen_inspeccion_tecnica,"12");



            verificar_todas_las_fotos();

        }catch (Exception e)
        {
            finish();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        opcion_subir_imagen="";
        switch (v.getId()){

            case R.id.im_imagen_1:
                opcion_subir_imagen="imagen_1";
                break;
            case R.id.im_imagen_2:
                opcion_subir_imagen="imagen_2";
                break;
            case R.id.im_imagen_3:
                opcion_subir_imagen="imagen_3";
                break;
            case R.id.im_imagen_4:
                opcion_subir_imagen="imagen_4";
                break;
            case R.id.im_imagen_soat:
                opcion_subir_imagen="imagen_soat";
                break;
            case R.id.im_imagen_ruat:
                opcion_subir_imagen="imagen_ruat";
                break;
            case R.id.im_imagen_inspeccion_tecnica:
                opcion_subir_imagen="imagen_inspeccion_tecnica";
                break;
        }



        if(opcion_subir_imagen!="")
        {
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent.createChooser(intent,"Seleccione app de imagen"),SELECT_PICTURE);

/*
            final CharSequence[] options={"Tomar foto","Elegir de galeria","Cancelar"};
            final AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setTitle("Elige una opcion");
            builder.setItems(options, new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(options[i]=="Tomar foto")
                    {
                        openCamara();
                    }else if(options[i]=="Elegir de galeria")
                    {
                        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent.createChooser(intent,"Seleccione app de imagen"),SELECT_PICTURE);

                    }else if (options[i]=="Cancelar")
                    {
                        dialogInterface.dismiss();
                    }
                }
            });
            builder.show();
            */
        }

    }


    private void getImage(String url, final String opcion)//
    {
        class GetImage extends AsyncTask<String,Void,Bitmap> {



            public GetImage() {

            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                if( bitmap!=null)
                {

                    Drawable dd = new BitmapDrawable(getResources(), bitmap);

                    if(opcion.equals("1"))
                    {
                        im_imagen_1.setBackground(dd);
                    }else if(opcion.equals("2"))
                    {
                        im_imagen_2.setBackground(dd);
                    }else if(opcion.equals("3"))
                    {
                        im_imagen_3.setBackground(dd);
                    }else if(opcion.equals("4"))
                    {
                        im_imagen_4.setBackground(dd);
                    }else if(opcion.equals("10"))
                    {
                        im_imagen_soat.setBackground(dd);
                    }else if(opcion.equals("11"))
                    {
                        im_imagen_ruat.setBackground(dd);
                    }else if(opcion.equals("12"))
                    {
                        im_imagen_inspeccion_tecnica.setBackground(dd);
                    }



                }


            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url =strings[0];//hace consulta ala Bd para recurar la imagen

                Bitmap mIcon =null;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
                return mIcon;
            }
        }

        GetImage gi = new GetImage();
        gi.execute(url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //ubicacion de la imagen

            Bitmap img_cargar;

            String uploadImage="";

            switch (requestCode){
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                }
                            });

                    //Convertir MPath a Bitmap
                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);

                    file = new File(mPath);

                    //uploadImage = getStringImage(img_cargar);
                    subir_imagen_servidor(opcion_subir_imagen,uploadImage,bitmap);

                    break;
                case SELECT_PICTURE:
                    path = data.getData();
                    try {//convertir Uri a BitMap
                        Bitmap tempBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(path));

                        guardar_imagen_temp(tempBitmap);
                        img_cargar=tempBitmap;
                        img_cargar=ReducirImagen_b(tempBitmap,1000,1000);
                        //uploadImage = getStringImage(img_cargar);
                        subir_imagen_servidor(opcion_subir_imagen,uploadImage,tempBitmap);

                    }
                    catch (Exception e)
                    {

                    }
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
            }
        }else{
            showExplanation();
        }
    }


    private void guardar_imagen_temp(Bitmap bitmapImage)
    {
        FileOutputStream fos = null;
        try {
            String MEDIA_DIRECTORY = getString(R.string.app_name)+"/Pre registro/";//nombre de directorio
            file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
            File mypath=new File(file,"temp.jpg");//nombre del archivo imagen


            boolean isDirectoryCreated = file.exists();//pregunto si esxiste el directorio creado
            if(!isDirectoryCreated)
                isDirectoryCreated = file.mkdirs();

            if(isDirectoryCreated) {
                fos = new FileOutputStream(mypath);
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            }
            file=mypath;

        } catch (Exception e) {
            e.printStackTrace();
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

        builder.show();
    }

    private void subir_imagen_servidor(String opcion_subir_imagen, String uploadImage, Bitmap bitmap) {


        //Convertir Bitmap a Drawable.
        Drawable dw = new BitmapDrawable(getResources(), bitmap);
        bitmap_aux=bitmap;

        switch (opcion_subir_imagen)
        {

            case "imagen_1":
                serverUpdate(getString(R.string.servidor) + "frmTaxi.php?opcion=insertar_imagen_delante",opcion_subir_imagen,placa);

                // hilo_imagen.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=insertar_imagen_ci_rua", "2",sperfil.getString("placa",""),uploadImage);// parametro que recibe el doinbackground
                break;
            case "imagen_2":
                serverUpdate(getString(R.string.servidor) + "frmTaxi.php?opcion=insertar_imagen_detras",opcion_subir_imagen,placa);

                // hilo_imagen.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=insertar_imagen_doc_propiedad", "3",sperfil.getString("placa",""),uploadImage);// parametro que recibe el doinbackground
                break;
            case "imagen_3":
                serverUpdate(getString(R.string.servidor) + "frmTaxi.php?opcion=insertar_imagen_lateral_derecho",opcion_subir_imagen,placa);

                //  hilo_imagen.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=insertar_imagen_soat", "4",sperfil.getString("placa",""),uploadImage);// parametro que recibe el doinbackground
                break;
            case "imagen_4":
                serverUpdate(getString(R.string.servidor) + "frmTaxi.php?opcion=insertar_imagen_lateral_izquierdo",opcion_subir_imagen,placa);

                //hilo_imagen.execute(getString(R.string.servidor) + "frmTaxi.php?opcion=insertar_imagen_inspeccion_tecnica", "5",sperfil.getString("placa",""),uploadImage);// parametro que recibe el doinbackground
                break;
            case "imagen_soat":
                serverUpdate(getString(R.string.servidor) + "frmTaxi.php?opcion=insertar_imagen_soat",opcion_subir_imagen,placa);
                break;
            case "imagen_ruat":
                serverUpdate(getString(R.string.servidor) + "frmTaxi.php?opcion=insertar_imagen_ruat",opcion_subir_imagen,placa);
            break;
            case "imagen_inspeccion_tecnica":
                serverUpdate(getString(R.string.servidor) + "frmTaxi.php?opcion=insertar_imagen_inspeccion_tecnica",opcion_subir_imagen,placa);
                break;
        }
    }



    class ServerUpdate extends AsyncTask<String,String,String> {

        ProgressDialog pDialog;
        String resultado="",tipo="";
        @Override
        protected String doInBackground(String... arg0) {
            resultado=uploadFoto(arg0[0],arg0[1],arg0[2]);
            tipo=arg0[1];


            if(suceso.getSuceso().equals("1"))
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {

                        // TODO Auto-generated method stub
                        Toast.makeText(Imagen_preregistro_vehiculo.this, suceso.getMensaje(),Toast.LENGTH_LONG).show();
                    }
                });
            else
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        AlertDialog.Builder builder = new AlertDialog.Builder(Imagen_preregistro_vehiculo.this);
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
            pDialog = new ProgressDialog(Imagen_preregistro_vehiculo.this);
            pDialog.setMessage("Subiendo la imagen, espere..." );
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();

          if(resultado.equals("2"))
            {//LICENCIA

                agregar_imagen(bitmap_aux,im_imagen_1);
                v_direccion_imagen_1="direccion de imagen agregada recientemente";
                verificar_todas_las_fotos();
            } else if(resultado.equals("3"))
            {//IDENTIFICACION HAMM

                agregar_imagen(bitmap_aux,im_imagen_2);
                v_direccion_imagen_2="direccion de imagen agregada recientemente";
                verificar_todas_las_fotos();
            } else if(resultado.equals("4"))
            {//IDENTIFICACION HAMM

                agregar_imagen(bitmap_aux,im_imagen_3);
                v_direccion_imagen_3="direccion de imagen agregada recientemente";
                verificar_todas_las_fotos();
            } else if(resultado.equals("5"))
            {//IMAGEN 4

                agregar_imagen(bitmap_aux,im_imagen_4);
                v_direccion_imagen_4="direccion de imagen agregada recientemente";
                verificar_todas_las_fotos();
            } else if(resultado.equals("10"))
            {//SOAT

                agregar_imagen(bitmap_aux,im_imagen_soat);
                direccion_imagen_soat="direccion de imagen agregada recientemente";
                verificar_todas_las_fotos();
            } else if(resultado.equals("11"))
            {//RUAT

                agregar_imagen(bitmap_aux,im_imagen_ruat);
                direccion_imagen_ruat="direccion de imagen agregada recientemente";
                verificar_todas_las_fotos();
            } else if(resultado.equals("12"))
          {//INSPECCION TECNICA

              agregar_imagen(bitmap_aux,im_imagen_inspeccion_tecnica);
              v_direccion_imagen_inspeccion_tecnica="direccion de imagen agregada recientemente";
              verificar_todas_las_fotos();
          }else if(resultado.equals("500"))
            {
                mensaje(suceso.getMensaje());
            }else
            {
                mensaje("Falla en tu conexion a internet.");
            }
        }

    }

    private void serverUpdate(String url,String tipo,String placa){
        if (file.exists()){
            new Imagen_preregistro_vehiculo.ServerUpdate().execute(url,tipo,placa);
        }
        else
        {
            Log.e("Imagen","No se pudo localizar la imagen");
        }
    }


    ///PRUEBA DE INSERTAR IMAGEN

    private String uploadFoto(String url,String tipo,String placa){
        String devuelve="";

        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpPost httppost = new HttpPost(url);

        MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        ContentBody foto = new FileBody(file);
        mpEntity.addPart("imagen", foto);


        try {
            mpEntity.addPart("placa", new StringBody(placa));
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

                if(tipo=="perfil"){

                    devuelve="1";
                }else if(tipo=="imagen_1"){

                    devuelve="2";
                }else if(tipo=="imagen_2"){

                    devuelve="3";
                }else if(tipo=="imagen_3"){

                    devuelve="4";
                }else if(tipo=="imagen_4"){


                    devuelve="5";
                } else if(tipo=="imagen_carnet_1"){
                    devuelve="6";
                }else if(tipo=="imagen_carnet_2"){
                    devuelve="7";
                }else if(tipo=="imagen_licencia_1"){
                    devuelve="8";
                }else if(tipo=="imagen_licencia_2"){
                    devuelve="9";
                }else if(tipo=="imagen_soat"){
                    devuelve="10";
                }else if(tipo=="imagen_ruat"){
                    devuelve="11";
                }else if(tipo=="imagen_inspeccion_tecnica"){
                    devuelve="12";
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

    public void mensaje(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }

    public void verificar_todas_las_fotos()
    {
        if(v_direccion_imagen_1.length()<5)
        {
            mensaje("Agregue su foto del vehiculo");
        }else if(v_direccion_imagen_2.length()<5)
        {
            mensaje("Agregue su foto del vehiculo");
        }else if(v_direccion_imagen_3.length()<5)
        {
            mensaje("Agregue su foto del vehiculo");
        }else if(v_direccion_imagen_4.length()<5)
        {
            mensaje("Agregue su foto del vehiculo");
        }else if(direccion_imagen_soat.length()<5)
        {
            mensaje("Agregue su foto de soat del vehículo");
        }else if(direccion_imagen_ruat.length()<5)
        {
            mensaje("Agregue su foto de ruat del vehículo");
        }else if(v_direccion_imagen_inspeccion_tecnica.length()<5)
        {
            mensaje("Agregue su foto de la inspeccion tecnica de transito del vehículo");
        }else{
            mensaje_continuar("Gracias por completar con el registro del Vehículo");
        }




    }

    public void mensaje_continuar(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("SIGUIENTE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent siguiente = new Intent(Imagen_preregistro_vehiculo.this, Imagenes_preregistro_conductor.class);
                siguiente.putExtra("ci", ci);
                siguiente.putExtra("direccion_imagen", direccion_imagen);
                siguiente.putExtra("direccion_imagen_carnet_1", direccion_imagen_carnet_1);
                siguiente.putExtra("direccion_imagen_carnet_2",direccion_imagen_carnet_2);
                siguiente.putExtra("direccion_imagen_licencia_1", direccion_imagen_licencia_1);
                siguiente.putExtra("direccion_imagen_licencia_2", direccion_imagen_licencia_2);



                startActivity(siguiente);
            }
        });
        builder.create();
        builder.show();
    }

    public void agregar_imagen(Bitmap bitmap,ImageView im_imagen ) {
        bitmap=ReducirImagen_b(bitmap,150,130);
        Drawable d = new BitmapDrawable(getResources(), bitmap);

        im_imagen.setBackground(d);
    }






}
