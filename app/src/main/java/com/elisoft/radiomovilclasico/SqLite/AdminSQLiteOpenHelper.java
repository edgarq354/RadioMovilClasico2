package com.elisoft.radiomovilclasico.SqLite;

/**
 * Created by elisoft on 07-11-16.
 */import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String nombre, CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table chat(" +
                "id integer default 0," +
                "titulo text," +
                "mensaje text," +
                "fecha text," +
                "hora text," +
                "id_usuario text," +
                "id_conductor text,"+
                "estado integer default 0," +
                "yo integer default 0," +
                "primary key(id,id_usuario,id_conductor))");

        db.execSQL("create table direccion(" +
                    "id integer  PRIMARY KEY autoincrement not null," +
                    "detalle text," +
                    "latitud decimal(13,7) default 0," +
                    "longitud decimal(13,7) default 0," +
                    "id_empresa integer default 0," +
                    "id_usuario integer default 0,"+
                    "nombre text)");

        db.execSQL("create table pedido_usuario(" +
                "id integer primary key, " +
                "id_conductor integer," +
                "fecha_pedido text," +
                "latitud decimal(13,7)," +
                "longitud decimal(13,7)," +
                "nombre text," +
                "apellido text," +
                "celular text ," +
                "marca text," +
                "placa text," +
                "indicacion text,"+
                "descripcion text,"+
                "estado_pedido integer," +
                "monto_total text," +
                "clase_vehiculo integer," +
                "calificacion_conductor," +
                "calificacion_vehiculo," +
                "monto_billetera text," +
                "estado_billetera integer," +
                "ajuste_porcentaje text," +
                "ganancia text)");
        db.execSQL("create table usuario(" +
                "id integer primary key, " +
                "nombre text," +
                "apellido text," +
                "celular text ," +
                "correo text)");
        db.execSQL("create table usuario_panico(" +
                "id integer primary key, " +
                "nombre text," +
                "apellido text," +
                "celular text ," +
                "correo text)");
        db.execSQL("create table pedido_taxi(" +
                "id integer, " +
                "id_usuario integer," +
                "fecha_pedido text," +
                "latitud decimal(13,7)," +
                "longitud decimal(13,7)," +
                "nombre text," +
                "apellido text," +
                "celular text ," +
                "indicacion text,"+
                "descripcion text,"+
                "estado_pedido integer)");


        //cuarga los puntos de recorrido de los pedidos...
        db.execSQL("create table puntos_pedido(" +
                "id_pedido integer," +
                "latitud decimal(13,7)," +
                "longitud decimal(13,7), " +
                "fecha timestamp default CURRENT_TIMESTAMP," +
                "primary key(id_pedido,latitud,longitud)" +
                ")");
        db.execSQL("create table notificacion(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT not null," +
                "titulo text," +
                "mensaje text," +
                "cliente text," +
                "id_pedido text," +
                "nombre text,"+
                "latitud text," +
                "longitud text," +
                "tipo text," +
                "fecha text," +
                "hora text," +
                "leido integer default 0," +
                "indicacion text)");
        db.execSQL("create table carrera(" +
                "id integer," +
                "latitud_inicio decimal(13,7)," +
                "longitud_inicio decimal(13,7)," +
                "latitud_fin decimal(13,7) default 0," +
                "longitud_fin decimal(13,7) default 0," +
                "distancia decimal(10,3)," +
                "tiempo text," +
                "fecha_inicio text," +
                "fecha_fin text," +
                "id_pedido integer," +
                "id_conductor integer," +
                "monto decimal(10,2)," +
                "ruta text" +
                ")");
//usuarios que forman parte de la empresa
        db.execSQL("create table usuario_empresa(" +
                "id integer not null," +
                "nombre text not null," +
                "telefono text not null)");

//guia turistica CATEGORIA DE LUGAR
        db.execSQL("create table categoria(" +
                "id integer not null," +
                "nombre text not null)");

//guia turistica  GUIA COMERCIA
        db.execSQL("create table lugar(" +
                "id integer not null," +
                "nombre text not null," +
                "direccion text not null," +
                "telefono text not null," +
                "whatsapp text not null," +
                "latitud decimal(13,7) default 0," +
                "longitud decimal(13,7) default 0," +
                "estado integer default 0," +
                "id_categoria integer)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnte, int versionNue) {
        db.execSQL("drop table if exists chat");
        db.execSQL("create table chat(" +
                "id integer default 0," +
                "titulo text," +
                "mensaje text," +
                "fecha text," +
                "hora text," +
                "id_usuario text," +
                "id_conductor text,"+
                "estado integer default 0," +
                "yo integer default 0," +
                "primary key(id,id_usuario,id_conductor))");

        db.execSQL("drop table if exists direccion");
        db.execSQL("create table direccion(" +
                "id integer PRIMARY KEY AUTOINCREMENT not null," +
                "detalle text," +
                "latitud decimal(13,7) default 0," +
                "longitud decimal(13,7) default 0," +
                "id_empresa integer default 0," +
                "id_usuario integer default 0,"+
                "nombre text)");


        db.execSQL("drop table if exists pedido_usuario");
        db.execSQL("create table pedido_usuario(" +
                "id integer primary key, " +
                "id_conductor integer," +
                "fecha_pedido text," +
                "latitud decimal(13,7)," +
                "longitud decimal(13,7)," +
                "nombre text," +
                "apellido text," +
                "celular text ," +
                "marca text," +
                "placa text," +
                "indicacion text,"+
                "descripcion text,"+
                "estado_pedido integer," +
                "monto_total text," +
                "clase_vehiculo integer," +
                "calificacion_conductor," +
                "calificacion_vehiculo," +
                "monto_billetera text , " +
                "estado_billetera integer," +
                "ajuste_porcentaje text," +
                "ganancia text)");

        db.execSQL("drop table if exists pedido_taxi");
        db.execSQL("create table pedido_taxi(" +
                "id integer, " +
                "id_usuario integer," +
                "fecha_pedido text," +
                "latitud decimal(13,7)," +
                "longitud decimal(13,7)," +
                "nombre text," +
                "apellido text," +
                "celular text ," +
                "indicacion text,"+
                "descripcion text,"+
                "estado_pedido integer)");

        //cuarga los puntos de recorrido de los pedidos...
        db.execSQL("drop table if exists puntos_pedido");
        db.execSQL("create table puntos_pedido(" +
                "id_pedido integer," +
                "latitud decimal(13,7)," +
                "longitud decimal(13,7), " +
                "fecha timestamp default CURRENT_TIMESTAMP," +
                "primary key(id_pedido,latitud,longitud)" +
                ")");
        db.execSQL("drop table if exists notificacion");
        db.execSQL("create table notificacion(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT not null," +
                "titulo text," +
                "mensaje text," +
                "cliente text," +
                "id_pedido text," +
                "nombre text,"+
                "latitud text," +
                "longitud text," +
                "tipo text," +
                "fecha text," +
                "hora text," +
                "leido integer default 0," +
                "indicacion  text)");
        db.execSQL("drop table if exists carrera");
        db.execSQL("create table carrera(" +
                "id integer," +
                "latitud_inicio decimal(13,7)," +
                "longitud_inicio decimal(13,7)," +
                "latitud_fin decimal(13,7) default 0," +
                "longitud_fin decimal(13,7) default 0," +
                "distancia decimal(10,3)," +
                "tiempo text," +
                "fecha_inicio text," +
                "fecha_fin text," +
                "id_pedido integer," +
                "id_conductor integer," +
                "monto decimal(10,2)," +
                "ruta text" +
                ")");
        db.execSQL("drop table if exists usuario");
        db.execSQL("create table usuario(" +
                "id integer primary key, " +
                "nombre text," +
                "apellido text," +
                "celular text ," +
                "correo text)");
        db.execSQL("drop table if exists usuario_panico");
        db.execSQL("create table usuario_panico(" +
                "id integer primary key, " +
                "nombre text," +
                "apellido text," +
                "celular text ," +
                "correo text)");
        //usuarios que forman parte de la empresa
        db.execSQL("drop table if exists usuario_empresa");
        db.execSQL("create table usuario_empresa(" +
                "id integer not null," +
                "nombre text not null," +
                "telefono text not null)");

        //guia turistica CATEGORIA DE LUGAR
        db.execSQL("drop table if exists categoria");
        db.execSQL("create table categoria(" +
                "id integer not null," +
                "nombre text not null)");

//guia turistica  GUIA COMERCIA
        db.execSQL("drop table if exists lugar");
        db.execSQL("create table lugar(" +
                "id integer not null," +
                "nombre text not null," +
                "direccion text not null," +
                "telefono text not null," +
                "whatsapp text not null," +
                "latitud decimal(13,7) default 0," +
                "longitud decimal(13,7) default 0," +
                "estado integer default 0," +
                "id_categoria integer)");
    }
}

