package com.elisoft.radiomovilclasico.recorrido_compartido;

/**
 * Created by ELIO on 12/06/2017.
 */

public class CCompartido {
    private int id;
    private String nombre;
    private String direccion;
    private String fecha_inicio;
    private int estado;
    private int id_pedido;
    private int estado_carrera;
    private int id_conductor;
    private int id_carrera;

    private String placa;
    private String nombre_pasajero;
    private String apellido_pasajero;
    private String conductor;
    private String celular_conductor;
    private String celular_pasajero;
    private String marca;
    private String color;
    private String razon_social;
    private String id_empresa;
    private String url;


    public CCompartido()
    {
        setId(0);
        setEstado(0);
        setNombre("");
        setDireccion("");
        setId_pedido(0);
        setId_carrera(0);
        setEstado_carrera(0);
        setId_conductor(0);
        setFecha_inicio("");

        setNombre_pasajero("");
        setApellido_pasajero("");
        setConductor("");
        setCelular_conductor("");
        setCelular_pasajero("");
        setMarca("");
        setColor("");
        setRazon_social("");
        setPlaca("");
        setId_empresa("");
        setUrl("");
    }

    public CCompartido(int id, String nombre, String direccion, int estado)
    {
        setId(id);
        setNombre(nombre);
        setDireccion(direccion);
        this.setEstado(estado);
        setId_pedido(0);
        setId_carrera(0);
        setEstado_carrera(0);
        setId_conductor(0);
        setFecha_inicio("");

        setNombre_pasajero("");
        setApellido_pasajero("");
        setConductor("");
        setCelular_conductor("");
        setCelular_pasajero("");
        setMarca("");
        setColor("");
        setRazon_social("");
        setPlaca("");
        setId_empresa("");
        setUrl("");


    }
    public CCompartido(int id_usuario, int id_pedido, int id_carrera, int estado_pedido, int id_conductor, int estado_carrera, String fecha_inicio, String nombre_pasajero
, String apellido_pasajero
, String conductor
, String celular_conductor
, String celular_pasajero
, String marca
, String color
, String razon_social, String placa, String id_empresa, String url)
    {
        setId(id_usuario);
        this.setEstado(estado_pedido);
        this.setEstado_carrera(estado_carrera);
        this.setId_pedido(id_pedido);
        this.setId_carrera(id_carrera);
        this.setId_conductor(id_conductor);
        this.setFecha_inicio(fecha_inicio);

        setNombre_pasajero(nombre_pasajero);
        setApellido_pasajero(apellido_pasajero);
        setConductor(conductor);
        setCelular_conductor(celular_conductor);
        setCelular_pasajero(celular_pasajero);
        setMarca(marca);
        setColor(color);
        setRazon_social(razon_social);
        setPlaca(placa);
        setId_empresa(id_empresa);
        setUrl(url);
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

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }

    public int getEstado_carrera() {
        return estado_carrera;
    }

    public void setEstado_carrera(int estado_carrera) {
        this.estado_carrera = estado_carrera;
    }

    public int getId_conductor() {
        return id_conductor;
    }

    public void setId_conductor(int id_conductor) {
        this.id_conductor = id_conductor;
    }

    public int getId_carrera() {
        return id_carrera;
    }

    public void setId_carrera(int id_carrera) {
        this.id_carrera = id_carrera;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public String getNombre_pasajero() {
        return nombre_pasajero;
    }

    public void setNombre_pasajero(String nombre_pasajero) {
        this.nombre_pasajero = nombre_pasajero;
    }

    public String getApellido_pasajero() {
        return apellido_pasajero;
    }

    public void setApellido_pasajero(String apellido_pasajero) {
        this.apellido_pasajero = apellido_pasajero;
    }

    public String getConductor() {
        return conductor;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public String getCelular_conductor() {
        return celular_conductor;
    }

    public void setCelular_conductor(String celular_conductor) {
        this.celular_conductor = celular_conductor;
    }

    public String getCelular_pasajero() {
        return celular_pasajero;
    }

    public void setCelular_pasajero(String celular_pasajero) {
        this.celular_pasajero = celular_pasajero;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(String id_empresa) {
        this.id_empresa = id_empresa;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
