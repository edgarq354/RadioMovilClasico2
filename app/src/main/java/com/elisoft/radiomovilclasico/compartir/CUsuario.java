package com.elisoft.radiomovilclasico.compartir;

/**
 * Created by ELIO on 06/06/2017.
 */

public class CUsuario {
    int id;
    String nombre;
   String apellido;
    String celular;
    String correo;
    public boolean estado;
    private double latitud;
    private double longitud;
    public CUsuario()
    {
        setId(0);
        setEstado(false);
       nombre=apellido=correo=celular="";
        setLatitud(0);
        setLongitud(0);
    }
    public CUsuario(int id, String nombre, String apellido, String correo, String celular)
    {
        this.setId(id);
        this.setNombre(nombre);
        this.setApellido(apellido);
        this.setCelular(celular);
        this.setCorreo(correo);
        setEstado(false);
        latitud=longitud=0;
    }

    public CUsuario(int id, String nombre, String apellido, double latitud, double longitud)
    {
        this.setId(id);
        this.setNombre(nombre);
        this.setApellido(apellido);
        this.setCelular("");
        this.setCorreo("");
        setEstado(false);
        this.setLatitud(latitud);
        this.setLongitud(longitud);
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
