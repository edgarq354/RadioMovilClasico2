package com.elisoft.radiomovilclasico.video_tutorial;

/**
 * Created by ELIO on 03/07/2017.
 */

public class Cvideo {
    private int id;
    private int id_empresa;
    private String nombre;
    private String descripcion;
    private String url;
    public Cvideo()
    {
        setId(0);
        setUrl("");
        setDescripcion("");
        setNombre("");
        setId_empresa(0);
    }
    public Cvideo(int id, int id_empresa, String nombre, String descripcion, String url)
    {
        setId(id);
        setId_empresa(id_empresa);
        setNombre(nombre);
        setDescripcion(descripcion);
        setUrl(url);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
