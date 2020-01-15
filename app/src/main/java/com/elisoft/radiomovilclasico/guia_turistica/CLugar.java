package com.elisoft.radiomovilclasico.guia_turistica;

/**
 * Created by ELIO on 23/12/2017.
 */

public class CLugar {
    private int id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String whatsapp;
    private String latitud;
    private String longitud;
    private int estado;
    private int id_categoria;

    public CLugar(){
        setId(0);
        setNombre("");
        setDireccion("");
        setTelefono("");
        setWhatsapp("");
        setLatitud("0");
        setLongitud("0");
        setEstado(0);
        setId_categoria(0);
    }

    public  CLugar(int id,
            String nombre,
            String direccion,
            String telefono,
            String whatsapp,
            String latitud,
            String longitud,
            int estado,
            int id_categoria){
        this.setId(id);
        this.setNombre(nombre);
        this.setDireccion(direccion);
        this.setTelefono(telefono);
        this.setWhatsapp(whatsapp);
        this.setLatitud(latitud);
        this.setLongitud(longitud);
        this.setEstado(estado);
        this.setId_categoria(id_categoria);
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }
}
