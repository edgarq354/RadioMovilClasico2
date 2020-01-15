package com.elisoft.radiomovilclasico.guia_turistica;

/**
 * Created by ELIO on 23/12/2017.
 */

public class CCategoria {
    private int id;
    private String nombre;

   public CCategoria(){
        setId(0);
        setNombre("");
    }

    public  CCategoria( int id, String nombre){
       this.setId(id);
       this.setNombre(nombre);
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
}
