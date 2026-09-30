/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author songo
 */
public class Pedido implements Serializable{
    
    private int id;
    private LocalDate fechaPedido;
    private LocalDate fechaFin;
    private boolean entregado;
    private ArrayList<Integer> productos;
    private int clienteId;

    public Pedido() {
    }

    public Pedido(int id, LocalDate fechaPedido, LocalDate fechaFin, boolean entregado, ArrayList<Integer> productoId, int clienteId) {
        this.id = id;
        this.fechaPedido = fechaPedido;
        this.fechaFin = fechaFin;
        this.entregado = entregado;
        this.productos = productoId;
        this.clienteId = clienteId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDate fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isEntregado() {
        return entregado;
    }

    public void setEntregado(boolean entregado) {
        this.entregado = entregado;
    }

    public ArrayList<Integer> getProductoId() {
        return productos;
    }

    public void setProductoId(ArrayList<Integer> productoId) {
        this.productos = productoId;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    @Override
    public String toString() {
        return "Pedido{" + "id=" + id + ", fechaPedido=" + fechaPedido + ", fechaFin=" + fechaFin + ", entregado=" + entregado + ", productos=" + productos + ", clienteId=" + clienteId + '}';
    }
    
}
