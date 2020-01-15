package com.elisoft.radiomovilclasico.viajes;

/**
 * Created by elisoft on 06-03-17.
 */



public class CViajes {
    private int id;
    private int id_taxi;
    private int estado_pedido;
    private String fecha_pedido;
    private String nombre;
    private String apellido;
    private String celular;
    private String marca;
    private String placa;
    private String indicacion;
    private String descripcion;
    private String monto_total;
    private double latitud;
    private double longitud;
    private int clase_vehiculo;
    private int calificacion_vehiculo;
    private int calificacion_conductor;

    private String monto_billetera;
    private int estado_billetera;
    private String ajuste_porcentaje;
    private String ganancia;


    public CViajes()
    {

    }
    public CViajes( int id,
                            int id_taxi,
                            int estado_pedido,
                            String fecha_pedido,
                            String nombre,
                            String apellido,
                            String celular,
                            String marca,
                            String placa,
                            String indicacion,
                            String descripcion,
                            double latitud,
                            double longitud,
                            String monto_total,
                            int clase_vehiculo,
                            int calificacion_conductor,
                            int calificacion_vehiculo,
                            String monto_billetera,
                            int estado_billetera,
                            String ajuste_porcentaje,
                            String ganancia
    )
    {

        this.id= id;
        this.id_taxi=id_taxi;
        this.estado_pedido=estado_pedido;
        this.fecha_pedido=fecha_pedido;
        this.nombre=nombre;
        this.apellido=apellido;
        this.celular=celular;
        this.marca=marca;
        this.placa=placa;
        this.indicacion=indicacion;
        this.setDescripcion(descripcion);
        this.latitud=latitud;
        this.longitud=longitud;
        this.monto_total=monto_total;
        this.setClase_vehiculo(clase_vehiculo);
        this.setCalificacion_conductor(calificacion_conductor);
        this.setCalificacion_vehiculo(calificacion_vehiculo);
        this.setMonto_billetera(monto_billetera);
        this.setEstado_billetera(estado_billetera);
        this.setAjuste_porcentaje(ajuste_porcentaje);
        this.setGanancia(ganancia);
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_taxi() {
        return id_taxi;
    }

    public void setId_taxi(int id_taxi) {
        this.id_taxi = id_taxi;
    }

    public int getEstado_pedido() {
        return estado_pedido;
    }

    public void setEstado_pedido(int estado_pedido) {
        this.estado_pedido = estado_pedido;
    }

    public String getFecha_pedido() {
        return fecha_pedido;
    }

    public void setFecha_pedido(String fecha_pedido) {
        this.fecha_pedido = fecha_pedido;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMonto_total() {
        return monto_total;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setMonto_total(String monto_total) {
        this.monto_total = monto_total;
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

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getIndicacion() {
        return indicacion;
    }

    public void setIndicacion(String indicacion) {
        this.indicacion = indicacion;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }



    @Override
    public String toString() {
        return "Cpedido_usuario{" +
                "id='" + id + '\'' +
                ",id_taxi='" + id_taxi + '\'' +
                ", estado_pedido='" + estado_pedido + '\'' +
                ", fecha_pedido='" + fecha_pedido + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", celular='" + celular + '\'' +
                ", marca='" + marca + '\'' +
                ", placa='" + placa + '\'' +
                ", indicacion='" + indicacion + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", latitud='" + latitud + '\'' +
                ", longitud='" + longitud + '\'' +
                ", monto_total='" + monto_total + '\'' +
                '}';
    }

    public int getClase_vehiculo() {
        return clase_vehiculo;
    }

    public void setClase_vehiculo(int clase_vehiculo) {
        this.clase_vehiculo = clase_vehiculo;
    }

    public int getCalificacion_vehiculo() {
        return calificacion_vehiculo;
    }

    public void setCalificacion_vehiculo(int calificacion_vehiculo) {
        this.calificacion_vehiculo = calificacion_vehiculo;
    }

    public int getCalificacion_conductor() {
        return calificacion_conductor;
    }

    public void setCalificacion_conductor(int calificacion_conductor) {
        this.calificacion_conductor = calificacion_conductor;
    }

    public String getMonto_billetera() {
        return monto_billetera;
    }

    public void setMonto_billetera(String monto_billetera) {
        this.monto_billetera = monto_billetera;
    }

    public int getEstado_billetera() {
        return estado_billetera;
    }

    public void setEstado_billetera(int estado_billetera) {
        this.estado_billetera = estado_billetera;
    }

    public String getAjuste_porcentaje() {
        return ajuste_porcentaje;
    }

    public void setAjuste_porcentaje(String ajuste_porcentaje) {
        this.ajuste_porcentaje = ajuste_porcentaje;
    }

    public String getGanancia() {
        return ganancia;
    }

    public void setGanancia(String ganancia) {
        this.ganancia = ganancia;
    }
}
