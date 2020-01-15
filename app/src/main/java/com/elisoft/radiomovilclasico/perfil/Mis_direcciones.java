package com.elisoft.radiomovilclasico.perfil;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.Pedido_perfil_taxi;
import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.radiomovilclasico.agregar_direccion.Agregar_direccion_nuevo;
import com.elisoft.radiomovilclasico.carreras.CCarrera;

import java.util.ArrayList;

public class Mis_direcciones extends AppCompatActivity implements View.OnClickListener {

    LinearLayout ll_agregar_direccion;
    ListView lv_direccion;
    ArrayList<CDireccion> direccion;


    SharedPreferences casa=null;
    SharedPreferences trabajo=null;

    TextView tv_casa,tv_trabajo;
    LinearLayout ll_casa,ll_trabajo;
    TextView tv_direccion_casa,tv_direccion_trabajo;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,Perfil_pasajero.class));
    }

    @Override
    protected void onRestart() {
        cargar_direccion_en_la_lista();
        cargar_direcciones();
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_direcciones);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        lv_direccion=(ListView)findViewById(R.id.lv_direccion);
        ll_agregar_direccion=(LinearLayout)findViewById(R.id.ll_agregar_direccion);

        ll_casa=(LinearLayout)findViewById(R.id.ll_casa);
        ll_trabajo=(LinearLayout)findViewById(R.id.ll_trabajo);

        tv_casa=(TextView) findViewById(R.id.tv_casa);
        tv_direccion_casa=(TextView) findViewById(R.id.tv_direccion_casa);
        tv_direccion_trabajo=(TextView) findViewById(R.id.tv_direccion_trabajo);
        tv_trabajo=(TextView) findViewById(R.id.tv_trabajo);


        casa=getSharedPreferences(getString(R.string.direccion_casa),MODE_PRIVATE);

        trabajo=getSharedPreferences(getString(R.string.direccion_trabajo),MODE_PRIVATE);


        ll_casa.setOnClickListener(this);
        ll_trabajo.setOnClickListener(this);
        ll_agregar_direccion.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        cargar_direccion_en_la_lista();
        cargar_direcciones();



        lv_direccion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    CDireccion hi=new CDireccion();
                    hi=direccion.get(i);

                    final CharSequence[] options={"Editar","Eliminar"};
                    final AlertDialog.Builder builder= new AlertDialog.Builder(Mis_direcciones.this);
                    final CDireccion finalHi = hi;
                    builder.setItems(options, new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(options[i]=="Editar")
                            {
                               editar_direccion(finalHi.getId(),finalHi.getNombre(),finalHi.getDireccion(),finalHi.getLatitud(),finalHi.getLongitud());
                            }else if(options[i]=="Eliminar")
                            {
                                 eliminar_direccion(finalHi.getId());
                            }
                        }
                    });
                    builder.show();

                }catch (Exception e)
                {
                    Log.e("carrera",e.toString());
                }

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void eliminar_direccion(int id) {
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                    getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
            SQLiteDatabase db = admin.getWritableDatabase();
            db.execSQL("delete from direccion where id="+id);
            db.close();
        }catch (Exception e)
        {
            Log.e("Vaciar base",""+e);
        }
        cargar_direccion_en_la_lista();
    }

    private void editar_direccion(int id, String nombre, String direccion, double latitud, double longitud) {

        Intent datos= new Intent(this, Datos_direccion.class);
        datos.putExtra("id",id);
        datos.putExtra("latitud",latitud);
        datos.putExtra("longitud",longitud);
        datos.putExtra("direccion",direccion);
        datos.putExtra("nombre",nombre);
        startActivity(datos);
    }

    @Override
    public void onClick(View v) {
        double lat_direccion=0;
        double lon_direccion=0;

        switch (v.getId())
        {
            case R.id.ll_agregar_direccion:
                Intent scasa=new Intent(this, Agregar_direccion_nuevo.class);
                scasa.putExtra("direccion",3);
                startActivity(scasa);
                break;
            case R.id.ll_casa:

                lat_direccion=Double.parseDouble(casa.getString("latitud","0"));
                lon_direccion=Double.parseDouble(casa.getString("longitud","0"));

                if(  lat_direccion==0 || lon_direccion==0)
                    {
                        Toast.makeText(getApplicationContext(),"Agregar una nueva dirección de casa.", Toast.LENGTH_LONG).show();
                        abrir_direccion_nuevo(1,lat_direccion,lon_direccion);
                    }else {
                         abrir_direccion_nuevo(1,lat_direccion,lon_direccion);
                    }


                break;


            case R.id.ll_trabajo:
                lat_direccion=Double.parseDouble(trabajo.getString("latitud","0"));
                lon_direccion=Double.parseDouble(trabajo.getString("longitud","0"));


                    if( lat_direccion==0 ||  lon_direccion==0)
                    {
                        Toast.makeText(getApplicationContext(),"Agregar una nueva dirección de trabajo.", Toast.LENGTH_LONG).show();
                        abrir_direccion_nuevo(2,lat_direccion,lon_direccion);
                    }else {
                        abrir_direccion_nuevo(2,lat_direccion,lon_direccion);
                    }

                break;
        }
    }

    private void abrir_direccion_nuevo(int i,double lat,double lon) {
        Intent casa=new Intent(this, Agregar_direccion_nuevo.class);
        casa.putExtra("direccion",i);
        casa.putExtra("latitud",lat);
        casa.putExtra("longitud",lon);
        startActivity(casa);
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
