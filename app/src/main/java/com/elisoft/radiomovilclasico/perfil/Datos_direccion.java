package com.elisoft.radiomovilclasico.perfil;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.Menu_usuario;
import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.radiomovilclasico.agregar_direccion.Agregar_direccion_nuevo;

public class Datos_direccion extends AppCompatActivity implements  View.OnClickListener {

    double latitud = 0, longitud = 0;
    String direccion = "",nombre="";
    int id_direccion=0;

    EditText et_nombre;
    TextView et_direccion;

    Button bt_guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_direccion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        et_nombre = (EditText) findViewById(R.id.et_nombre);
        et_direccion = (TextView) findViewById(R.id.et_direccion);
        bt_guardar = (Button) findViewById(R.id.bt_guardar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            Bundle bundle = getIntent().getExtras();
            id_direccion = bundle.getInt("id", 0);
            latitud = bundle.getDouble("latitud", 0);
            longitud = bundle.getDouble("longitud", 0);
            direccion = bundle.getString("direccion", "");
            nombre= bundle.getString("nombre", "");
            et_direccion.setText(direccion);
            et_nombre.setText(nombre);
        } catch (Exception e) {
            finish();
        }

        bt_guardar.setOnClickListener(this);
        et_direccion.setOnClickListener(this);





    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_guardar:
                if(id_direccion==0)
                {
                    guardar_direccion();
                }else{
                    editar_direccion();
                }

                break;
            case R.id.et_direccion:

                if(id_direccion==0){
                    finish();
                }else
                {
                    SharedPreferences sa=getSharedPreferences("buscar_direccion",MODE_PRIVATE);
                    SharedPreferences.Editor edit=sa.edit();

                    edit.putString("latitud",String.valueOf(latitud));
                    edit.putString("longitud",String.valueOf(longitud));
                    edit.putString("nombre",nombre);
                    edit.putString("id",String.valueOf(id_direccion));
                    edit.commit();

                    Intent casa=new Intent(this, Agregar_direccion_nuevo.class);
                    casa.putExtra("direccion",4);
                    startActivity(casa);
                }
                break;
        }
    }

    private void editar_direccion() {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put("nombre", et_nombre.getText().toString());
        newValues.put("detalle", et_direccion.getText().toString());
        newValues.put("latitud", String.valueOf(latitud));
        newValues.put("longitud", String.valueOf(longitud));
        bd.update("direccion", newValues, "id="+id_direccion, null);
        Toast.makeText(this,"Dirección modificada",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, Menu_usuario.class));
    }

    public void guardar_direccion()
    {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("detalle", et_direccion.getText().toString());
        registro.put("nombre", et_nombre.getText().toString());
        registro.put("latitud", String.valueOf(latitud));
        registro.put("longitud", String.valueOf(longitud));
        bd.insert("direccion", null, registro);
        bd.close();

        Toast.makeText(this,"Dirección agregada",Toast.LENGTH_SHORT).show();

        finish();


    }
}
