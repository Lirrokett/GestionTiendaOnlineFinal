/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package vista;

import controlador.ControladorClientes;
import controlador.ControladorPedidos;
import controlador.ControladorProductos;
import daos.DaoBaseDeDatos;
import excepciones.*;
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
import main.Utilidades;
import modelo.Categoria;
import modelo.Cliente;
import modelo.Pedido;
import modelo.Producto;

/**
 *
 * @author songo
 */
public class GestionTiendaOnline1 {
    private final ControladorProductos produ;
    private final ControladorPedidos pedi;
    private final ControladorClientes cli;
            public GestionTiendaOnline1() {
        this.pedi = new ControladorPedidos();
        this.produ = new ControladorProductos();
        this.cli = new ControladorClientes();
    }
    public static void main(String[] args) throws SQLException, ProductoDuplicadoException, AccesoBaseDatosException, AccesoDatosException {
        GestionTiendaOnline1 gestion=new GestionTiendaOnline1();
        gestion.crearFichero();
        int opcion,salir=0;
        
        do{
        System.out.println("1-Registrar Producto");
        System.out.println("2-Registrar Cliente");
        System.out.println("3-Consultar productos disponibles");
        System.out.println("4-Editar stock de un producto");
        System.out.println("5-Realizar Pedido");
        System.out.println("6-Consultar pedidos de un cliente");
        System.out.println("7-Ver historial de pedidos en los que se encuentra un producto");
        opcion=Utilidades.leerInt();
            switch (opcion) {
                case 0:
                    System.out.println("Gracias por usar el programa");
                    salir=1;
                    break;
                case 1:
                    gestion.registrarProducto();
                    break;
                case 2:
                    gestion.registrarCliente();
                    break;
                case 3:
                    gestion.consultarProductosDisponibles();
                    break;
                case 4:
                    gestion.editarStock();
                    break;
                case 5:
                    gestion.realizarPedido();
                    break;
                case 6:
                    gestion.consultarPedidosCliente();
                    break;
                case 7:
                    gestion.verHistorialPedidosProducto();
                    break;
                default:
                    System.out.println("Elige una opcion valida");
                    break;
            }
        }while(salir==0);
    }
  
    private void crearFichero(){
    
			pedi.crearFichero();
		
    }
    
    private void registrarProducto() throws SQLException, ProductoDuplicadoException, AccesoBaseDatosException {
        try {
            int contador=0;
            ArrayList<Producto> productos1=produ.listarProductos();
                    for(Producto p : productos1){
                      contador++;
                    }
                    
            String nombre = Utilidades.introducirCadena("Nombre: ");
            double precio = Utilidades.leerDouble("Precio: ");
            Categoria categoria = Categoria.valueOf(Utilidades.introducirCadena("Categoria (CALZADO, TEXTIL, ACCESORIO): ").toUpperCase());
            int stock = Utilidades.leerInt("Stock: ");
            String ruta = Utilidades.introducirCadena("Ruta del fichero de pedidos: ");

            Producto producto = new Producto((contador+1),nombre, precio, categoria, stock, ruta);
            produ.registrarProducto(producto);
            System.out.println("Producto registrado correctamente con id " + producto.getId());
        } catch (NumberFormatException e) {
            System.out.println("Error: el precio y el stock deben ser valores numericos");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: la categoria debe ser CALZADO, TEXTIL o ACCESORIO");
        }
    }
    
    private void registrarCliente()
            throws AccesoDatosException {

        System.out.println("Introduce el nombre del cliente");
        String nombre = Utilidades.introducirCadena("Nombre: ");

        System.out.println("Introduce el email del cliente");
        String email = Utilidades.introducirCadena("Email: ");

        System.out.println("Introduce el telefono del cliente");
        String telefono = Utilidades.introducirCadena("Telefono: ");

        System.out.println("Introduce la direccion del cliente");
        String direccion = Utilidades.introducirCadena("Direccion: ");

        Cliente cliente = cli.registrarCliente(
                nombre, email, telefono, direccion);

        System.out.println("Cliente registrado correctamente.");
        System.out.println("ID asignado: " + cliente.getId());
    }
    
    private void consultarPedidos(){
        pedi.listarPedidos();
    }
    
    private void consultarProductosDisponibles() throws SQLException, AccesoDatosException{
        List<Producto> listaProductos = produ.consultarProductosDisponibles();
                        if (listaProductos.isEmpty()) {
                            System.out.println("No hay productos disponibles.");
                        } else {
                            for (Producto producto : listaProductos) {
                                System.out.println(producto.toString());
                            }
                        }
    }
    
    private void editarStock() throws SQLException{
        int codigo,stock;
                    System.out.println("Introduce el codigo del producto a cambiar el stock");
                    ArrayList<Producto> productos=produ.listarProductos();
                    for(Producto p : productos){
                        System.out.println(p.toString());
                    }
                    codigo=Utilidades.leerInt();
                    System.out.println("Ahora introduce el nuevo stock");
                    stock=Utilidades.leerInt();
                    boolean si=produ.editarStock(codigo, stock);
                    if(si==true){
                        System.out.println("Stock actualizado correctamente");
                    }
                    else{
                        System.out.println("Actualizacion de stock fallida");
                    }
    }
    
    private void consultarPedidosCliente(){
        int clienteid;
                    System.out.println("Introduce el codigo de el usuario de el cual quieras ver los pedidos");
                    clienteid=Utilidades.leerInt();
                    pedi.pedidosPorIdCliente(clienteid);
    }
    
    private void realizarPedido()
            throws AccesoDatosException, SQLException {

        System.out.println("Introduce el codigo del cliente");
        int idCliente = Utilidades.leerInt();

        System.out.println("Introduce el codigo del producto");
        int idProducto = Utilidades.leerInt();

        System.out.println("Introduce la cantidad");
        int cantidad = Utilidades.leerInt();

        Pedido pedido = pedi.realizarPedido(idCliente, idProducto, cantidad);

        System.out.println("Pedido registrado correctamente.");
        System.out.println("ID del pedido: " + pedido.getId());
    }
    
    private void verHistorialPedidosProducto() throws SQLException {
        try {
            Integer id = Integer.valueOf(Utilidades.introducirCadena("Id del producto: "));
            List<Pedido> historial = pedi.verHistorialPedidosProducto(id);
            if (historial.isEmpty()) {
                System.out.println("El producto " + id + " no aparece en ningun pedido");
            } else {
                System.out.println("Historial de pedidos del producto " + id + ":");
                for (Pedido pedido : historial) {
                    String entregado = pedido.isEntregado() ? "Si" : "No";
                    System.out.println("Pedido " + pedido.getId()
                            + " | Cliente: " + pedido.getClienteId()
                            + " | Fecha pedido: " + pedido.getFechaPedido()
                            + " | Fecha fin: " + pedido.getFechaFin()
                            + " | Entregado: " + entregado);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: el id debe ser un valor numerico.");
        } catch (ProductoInvalidoException e) {
            System.out.println("Datos no validos: " + e.getMessage());
        } catch (ProductoNoEncontradoException e) {
            System.out.println("Producto no encontrado: " + e.getMessage());
        } catch (AccesoBaseDatosException e) {
            System.out.println("Error de base de datos: " + describirCausa(e));
        } catch (FicheroNoEncontradoException e) {
            System.out.println("Fichero no encontrado: " + e.getMessage());
        } catch (AccesoFicheroException e) {
            System.out.println("Error de fichero: " + describirCausa(e));
        }
    }
    
    private String describirCausa(TiendaException excepcion) {
        Throwable causa = excepcion.getCause();
        if (causa == null) {
            return excepcion.getMessage();
        }
        return excepcion.getMessage() + " (" + causa.getMessage() + ")";
    }

}
