package com.elisoft.radiomovilclasico.corporativo;

import android.graphics.drawable.Drawable;

/**
 * Created by ELIO on 09/09/2016.
 */
public class CContacto {

    private String nombre;
    private String id;
    private String numero;
    private Drawable imagen;

    public CContacto() {
        super();
    }

    public CContacto(String id, String nombre, String numero, Drawable imagen) {
        super();
        this.nombre = nombre;
        this.numero = numero;
        this.imagen = imagen;
        this.id =id;
    }
    public String getNombre()
    {
        return this.nombre;
    }
    public String getid()
    {
        return this.id;
    }
    public String getNumero()
    {
        return this.numero;
    }
    public Drawable getImagen()
    {
        return this.imagen;
    }




}
