package com.elisoft.radiomovilclasico.panico;

/**
 * Created by ELIO on 03/07/2017.
 */

public class CUsuario_panico {
    private int id_conductor;
    private int id_pasajero;
    private int id_pedido;
    private String nombre_c;
    private String nombre_pasajero;
    private String celular_c;
    private String celular_pasajero;
    private String marca_c;
    private String placa_c;
    private String color_c;
    private String razon_c;
    private double latitud;
    private double longitud;

       public CUsuario_panico()
    {
        setId_conductor(0);
        setId_pasajero(0);
        setId_pedido(0);
        setLatitud(0);
        setLongitud(0);
        setNombre_c("");
        setNombre_pasajero("");
        setCelular_c("");
        setCelular_pasajero("");
        setMarca_c("");
        setPlaca_c("");
        setColor_c("");
        setRazon_c("");
    }

    public CUsuario_panico(int id_conductor, int id_pasajero, String nombre_c, String nombre_pasajero, String celular_c, String celular_pasajero, String placa_c, String marca_c, String color_c, String razon_c, double latitud, double longitud, int id_pedido)
    {
        setId_conductor(id_conductor);
        setId_pasajero(id_pasajero);
        setId_pedido(id_pedido);
        setNombre_c(nombre_c);
        setNombre_pasajero(nombre_pasajero);
        setCelular_c(celular_c);
        setCelular_pasajero(celular_pasajero);
        setMarca_c(marca_c);
        setPlaca_c(placa_c);
        setColor_c(color_c);
        setRazon_c(razon_c);
        setLatitud(latitud);
        setLongitud(longitud);
    }


    public int getId_conductor() {
        return id_conductor;
    }

    public void setId_conductor(int id_conductor) {
        this.id_conductor = id_conductor;
    }

    public int getId_pasajero() {
        return id_pasajero;
    }

    public void setId_pasajero(int id_pasajero) {
        this.id_pasajero = id_pasajero;
    }

    public String getNombre_c() {
        return nombre_c;
    }

    public void setNombre_c(String nombre_c) {
        this.nombre_c = nombre_c;
    }

    public String getNombre_pasajero() {
        return nombre_pasajero;
    }

    public void setNombre_pasajero(String nombre_pasajero) {
        this.nombre_pasajero = nombre_pasajero;
    }

    public String getCelular_c() {
        return celular_c;
    }

    public void setCelular_c(String celular_c) {
        this.celular_c = celular_c;
    }

    public String getCelular_pasajero() {
        return celular_pasajero;
    }

    public void setCelular_pasajero(String celular_pasajero) {
        this.celular_pasajero = celular_pasajero;
    }

    public String getMarca_c() {
        return marca_c;
    }

    public void setMarca_c(String marca_c) {
        this.marca_c = marca_c;
    }

    public String getPlaca_c() {
        return placa_c;
    }

    public void setPlaca_c(String placa_c) {
        this.placa_c = placa_c;
    }

    public String getColor_c() {
        return color_c;
    }

    public void setColor_c(String color_c) {
        this.color_c = color_c;
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

    public String getRazon_c() {
        return razon_c;
    }

    public void setRazon_c(String razon_c) {
        this.razon_c = razon_c;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }
}
