package com.elisoft.radiomovilclasico.compartir;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.elisoft.radiomovilclasico.R;
import com.elisoft.radiomovilclasico.SqLite.AdminSQLiteOpenHelper;

import java.util.ArrayList;

public class Compartir_desde_pedido extends AppCompatActivity implements View.OnClickListener {
    CheckBox cb_todo;
    Button bt_cancelar;
    Button bt_compartir;
    ImageButton ib_buscar;
    ListView lv_lista;
    ArrayList<CUsuario> historial=new ArrayList<CUsuario>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartir_desde_pedido);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        cb_todo= (CheckBox) findViewById(R.id.cb_todo);
        bt_cancelar= (Button) findViewById(R.id.bt_cancelar);
        bt_compartir= (Button) findViewById(R.id.bt_compartir);
        ib_buscar= (ImageButton) findViewById(R.id.ib_buscar);
        lv_lista= (ListView) findViewById(R.id.lv_lista);

        bt_compartir.setOnClickListener(this);

        cargar_usuarios(historial);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ib_buscar:
                startActivity(new Intent(getApplicationContext(), Buscar_usuario.class));
                break;
            case R.id.bt_compartir:
                break;
            case R.id.bt_cancelar:
                break;
            case R.id. cb_todo:
                break;
        }
    }

    public void cargar_usuarios(ArrayList<CUsuario> historial) {
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
        UsuarioAdapter adaptador = new UsuarioAdapter(this,historial);
        lv_lista.setAdapter(adaptador);
    }
}
