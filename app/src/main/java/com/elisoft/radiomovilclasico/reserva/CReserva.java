package com.elisoft.radiomovilclasico.reserva;

/**
 * Created by ELIO on 24/12/2017.
 */

public class CReserva {
    private int id;
    private String referencia;
    private String numero;
    private String latitud;
    private String longitud;
    private String fecha;
    private int estado;

    public CReserva()
    {
        setId(0);
        setReferencia("");
        setNumero("0");
        setLatitud("0");
        setLongitud("0");
        setFecha("");
        setEstado(0);
    }
    public  CReserva(int id,
            String referencia,
            String numero,
            String latitud,
            String longitud,
            String fecha,
            int estado)
    {
        this.setId(id);
        this.setReferencia(referencia);
        this.setNumero(numero);
        this.setLatitud(latitud);
        this.setLongitud(longitud);
        setFecha(fecha);
        this.setEstado(estado);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
