package com.elisoft.radiomovilclasico.preregistro;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class Foto_conductor extends AppCompatActivity implements View.OnClickListener{


    String direccion_imagen="";
    String ci="";

    Button bt_tomar_foto ;
    ImageView im_foto;

    private   String CARPETA_RAIZ="";
    private   String RUTA_IMAGEN="";
    final int COD_FOTO=20;
    final int COD_SELECCIONA=10;


    String path2;
    Suceso suceso;
    Intent CropIntent;
    AlertDialog alert2 = null;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_conductor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bt_tomar_foto=(Button)findViewById(R.id.bt_tomar_foto);
        im_foto=(ImageView)findViewById(R.id.im_foto);

        bt_tomar_foto.setOnClickListener(this);


        try{
            Bundle bundle=getIntent().getExtras();
            ci=bundle.getString("ci");
            direccion_imagen=bundle.getString("direccion_imagen");

            getImage(getString(R.string.servidor_web)+"public/"+direccion_imagen,"1");

        }catch (Exception e)
        {
            finish();
        }

        CARPETA_RAIZ=getString(R.string.app_name)+"/";
        RUTA_IMAGEN=CARPETA_RAIZ+"misFotos";
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
                        im_foto.setBackground(dd);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_tomar_foto:
                cargarImagen();
                break;

        }

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //ubicacion de la imagen

            switch (requestCode){

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
                                    Log.i("Ruta de almacenamiento","Path: "+path);


                                  CropImage(uri);
                                }
                            });







                    break;

                case 1:



                    //imagen recortada.
                    try {

                        if(savebitmap()==true)
                        {
                            subir_imagen_servidor("imagen_1");
                        }else{
                            mensaje("Vuelve a tomar la foto.");
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(this,"Error al subir la imagen. ->"+e.toString(),Toast.LENGTH_LONG).show();
                    }
                    break;

            }


        }

    }

    private void guardar_en_memoria(Bitmap bitmapImage)
    {
        File file=null;
        FileOutputStream fos = null;

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

        try {
            file = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);

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
                serverUpdate(getString(R.string.servidor) + "frmTaxi.php?opcion=insertar_imagen_perfil_conductor",opcion_subir_imagen,ci);
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
                        Toast.makeText(getApplicationContext(), suceso.getMensaje(),Toast.LENGTH_LONG).show();
                    }
                });
            else
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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
            pDialog = new ProgressDialog(getApplicationContext());
            pDialog.setMessage("Subiendo la imagen, espere..." );
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();

            Bitmap bitmap_aux= BitmapFactory.decodeFile(path2);
            if(resultado.equals("2"))
            {//LICENCIA

                agregar_imagen(bitmap_aux,im_foto);
                direccion_imagen="direccion de imagen agregada recientemente";

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
        File file = new File(path2);
        if (file.exists()){
            new ServerUpdate().execute(url,tipo,placa);
        }
        else
        {
            Log.e("Imagen","No se pudo localizar la imagen");
        }
    }


    ///PRUEBA DE INSERTAR IMAGEN

    private String uploadFoto(String url,String tipo,String placa){
        String devuelve="";
        File file = new File(path2);

        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpPost httppost = new HttpPost(url);

        MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        ContentBody foto = new FileBody(file);
        mpEntity.addPart("imagen", foto);


        try {
            mpEntity.addPart("ci", new StringBody(placa));
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

    public void mensaje(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
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


    public void agregar_imagen(Bitmap bitmap,ImageView im_imagen ) {

        Drawable d = new BitmapDrawable(getResources(), bitmap);

        im_imagen.setBackground(d);
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





}
