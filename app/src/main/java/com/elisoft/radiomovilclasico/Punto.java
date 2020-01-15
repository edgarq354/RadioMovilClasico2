package com.elisoft.radiomovilclasico;

/**
 * Created by ELIO on 21/09/2016.
 */
public class Punto
{
    public  double latitud,longitud;
    public Punto()
    {
        latitud=longitud=0;
    }

    public void setPunto(double lat,double lon)
    {
        latitud=lat;
        longitud=lon;
    }


}