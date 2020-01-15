package com.elisoft.radiomovilclasico.perfil;

public class CDireccion {
    private int id;
    private String nombre;
    private String direccion;
    private double latitud;
    private double longitud;

    public CDireccion() {
        setId(0);
        setNombre("");
        setDireccion("");
        setLatitud(0);
        setLongitud(0);
    }

    public CDireccion(int id, String nombre, String direccion, double latitud, double longitud) {
        this.setId(id);
        this.setNombre(nombre);
        this.setDireccion(direccion);
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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
