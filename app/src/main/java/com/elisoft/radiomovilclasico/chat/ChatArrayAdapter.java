package com.elisoft.radiomovilclasico.chat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.radiomovilclasico.Suceso;

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

class ChatArrayAdapter extends ArrayAdapter<CMensaje> {

    private TextView chatText;
    private List<CMensaje> CMensajeList = new ArrayList<CMensaje>();
    private Context context;
    Suceso suceso;
    int posicion=0;

    String id_conductor,id_usuario,titulo,mensaje;
    ImageView im_leido,im_enviado;

    @Override
    public void add(CMensaje object) {
        CMensajeList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.CMensajeList.size();
    }

    public CMensaje getItem(int index) {
        return this.CMensajeList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CMensaje CMensajeObj = getItem(position);
        posicion=position;

        View row = convertView;

        TextView fecha;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (CMensajeObj.left) {
            row = inflater.inflate(R.layout.right, parent, false);
        }else{
            row = inflater.inflate(R.layout.left, parent, false);
        }
        chatText = (TextView) row.findViewById(R.id.msgr);
        fecha = (TextView) row.findViewById(R.id.fecha);
        im_leido = (ImageView) row.findViewById(R.id.im_leido);
        im_enviado = (ImageView) row.findViewById(R.id.im_enviado);
        chatText.setText(CMensajeObj.mensaje);
        fecha.setText(CMensajeObj.hora);
        if(CMensajeObj.estado==0){
            im_enviado.setVisibility(View.INVISIBLE);
            im_leido.setVisibility(View.INVISIBLE);
        } if(CMensajeObj.estado==1)
        {
            im_enviado.setVisibility(View.VISIBLE);
            im_leido.setVisibility(View.INVISIBLE);
        }else  if(CMensajeObj.estado==2){
            im_enviado.setVisibility(View.VISIBLE);
            im_leido.setVisibility(View.VISIBLE);
        }else {
            im_enviado.setVisibility(View.INVISIBLE);
            im_leido.setVisibility(View.INVISIBLE);
        }


        id_conductor=String.valueOf(CMensajeObj.id_conductor);
        id_usuario=String.valueOf(CMensajeObj.id_usuario);
        titulo= CMensajeObj.titulo;
        mensaje= CMensajeObj.mensaje;
        if(CMensajeObj.estado==0)
        {
            Servicio servicio=new Servicio();
            servicio.execute(context.getString(R.string.servidor)+"frmChat.php?opcion=enviar_pasajero","1",String.valueOf(CMensajeObj.id_usuario),String.valueOf(CMensajeObj.id_conductor),CMensajeObj.titulo,CMensajeObj.mensaje);
            CMensajeList.get(posicion).estado=1;
        }
        return row;
    }

    public void vista_estado( int estado)
    {
        if( estado==0){
            im_enviado.setVisibility(View.INVISIBLE);
            im_leido.setVisibility(View.INVISIBLE);
        } else if(estado==1)
    {
        im_enviado.setVisibility(View.VISIBLE);
        im_leido.setVisibility(View.INVISIBLE);
    }else  if( estado==2){
        im_enviado.setVisibility(View.VISIBLE);
        im_leido.setVisibility(View.VISIBLE);
    }else {
        im_enviado.setVisibility(View.INVISIBLE);
        im_leido.setVisibility(View.INVISIBLE);
    }
    }

    public void guardar_mensaje_enviado(String id, String id_conductor,String id_usuario,String titulo,String mensaje,String fecha,String hora,String estado,String yo)
    {
try {
    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context, context.getString(R.string.nombre_sql), null, Integer.parseInt(context.getString(R.string.version_sql)));

    SQLiteDatabase bd = admin.getWritableDatabase();
    ContentValues registro = new ContentValues();
    registro.put("id", id);
    registro.put("id_conductor", id_conductor);
    registro.put("id_usuario", id_usuario);
    registro.put("fecha", fecha);
    registro.put("hora", hora);
    registro.put("mensaje", mensaje);
    registro.put("titulo", titulo);
    registro.put("estado", estado);
    registro.put("yo", yo);
    bd.insert("chat", null, registro);
    bd.close();
}catch (Exception e){
    Log.d("registro Chat",e.toString());
}

    }
    // comenzar el servicio con el motista....
    public class Servicio extends AsyncTask<String,Integer,String> {
        String id,fecha,hora,sid_usuario,sid_conductor,stitulo,smensaje;

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
                    jsonParam.put("id_usuario", params[2]);
                    jsonParam.put("id_conductor", params[3]);
                    jsonParam.put("titulo", params[4]);
                    jsonParam.put("mensaje", params[5]);
                    sid_usuario=params[2];
                    sid_conductor=params[3];
                    stitulo=params[4];
                    smensaje=params[5];


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
                            id=respuestaJSON.getString("id");
                            fecha=respuestaJSON.getString("fecha");
                            hora=respuestaJSON.getString("hora");

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

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("1"))
            {
                Intent serviceIntent = new Intent(context, Servicio_guardar_mensaje_enviado.class);
                serviceIntent.putExtra("id_chat", id);
                serviceIntent.putExtra("id_conductor",sid_conductor);
                serviceIntent.putExtra("id_usuario", sid_usuario);
                serviceIntent.putExtra("titulo", stitulo);
                serviceIntent.putExtra("mensaje",smensaje);
                serviceIntent.putExtra("fecha", fecha);
                serviceIntent.putExtra("hora", hora);
                context.startService(serviceIntent);
                vista_estado(1);
            }
            else
            {


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
}