/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import daos.DaoBaseDeDatos;
import daos.DaoFichero;
import excepciones.AccesoBaseDatosException;
import excepciones.AccesoDatosException;
import excepciones.AccesoFicheroException;
import excepciones.FicheroNoEncontradoException;
import excepciones.ProductoInvalidoException;
import excepciones.ProductoNoEncontradoException;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.Cliente;
import modelo.Pedido;
import modelo.Producto;

/**
 *
 * @author songo
 */
public class ControladorPedidos {
    DaoBaseDeDatos dao1=new DaoBaseDeDatos();
    DaoFichero dao=new DaoFichero();
    
     public void crearFichero(){
         dao.crearFichero();
     }
     
     public void pedidosPorIdCliente(int id){
         dao.pedidosPorCliente(id);
     }

    public void listarPedidos() {
        dao.listarPedidos();
    }

    public List<Pedido> verHistorialPedidosProducto(Integer idProducto)
            throws ProductoInvalidoException, ProductoNoEncontradoException, AccesoBaseDatosException,
            FicheroNoEncontradoException, AccesoFicheroException, SQLException {
        validarId(idProducto);
        String ruta = dao1.obtenerRutaFichero(idProducto);
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new FicheroNoEncontradoException(
                    "El producto " + idProducto + " no tiene ninguna ruta de fichero asociada en la base de datos");
        }
        return dao.obtenerHistorialProducto(idProducto);
    }
    
    private void validarId(Integer id) throws ProductoInvalidoException {
        if (id == null || id <= 0) {
            throw new ProductoInvalidoException("El id del producto debe ser un numero entero positivo");
        }
    }
    
    public Pedido realizarPedido(int idCliente, int idProducto, int cantidad)
            throws AccesoDatosException, SQLException {

        if (idCliente <= 0) {
            throw new IllegalArgumentException("El código del cliente debe ser mayor que 0.");
        }
        if (idProducto <= 0) {
            throw new IllegalArgumentException("El código del producto debe ser mayor que 0.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        }

        Cliente cliente = dao1.buscarCliente(idCliente);
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no existe.");
        }

        Producto producto = dao1.buscarProducto(idProducto);
        if (producto == null) {
            throw new IllegalArgumentException("El producto no existe.");
        }

        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("No hay suficiente stock. Stock disponible: " + producto.getStock());
        }

        Pedido pedido = new Pedido();
        pedido.setId(dao.siguienteId());
        pedido.setFechaPedido(LocalDate.now());
        pedido.setFechaFin(null);
        pedido.setEntregado(false);
        pedido.setClienteId(idCliente);

        ArrayList<Integer> productosPedido = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            productosPedido.add(idProducto);
        }
        pedido.setProductoId(productosPedido);

        if (!dao1.reducirStock(idProducto, cantidad)) {
            throw new IllegalArgumentException("No se pudo actualizar el stock. Comprueba que siga habiendo unidades disponibles.");
        }

        // Pedido se almacena en pedido.dat, tal y como indica el enunciado.
        dao.guardarPedido(pedido);

        return pedido;
    }
}
