package com.elisoft.radiomovilclasico.chat;

public class CMensaje {
    public boolean left;
    public String mensaje;
    public String fecha;
    public String hora;
    public String titulo;
    int estado=0;
    int id_usuario=0;
    int id_conductor=0;
    int yo=0;

    public CMensaje(boolean left, String message,String titulo,String fecha,String hora,int estado ,int id_usuario,int id_conductor,int yo) {
        super();
        this.left = left;
        this.mensaje = message;
        this.titulo=titulo;
        this.fecha=fecha;
        this.estado=estado;
        this.id_usuario=id_usuario;
        this.id_conductor=id_conductor;
        this.hora=hora;
        this.yo=yo;
    }
}