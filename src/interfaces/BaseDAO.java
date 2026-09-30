package interfaces;

import excepciones.AccesoBaseDatosException;
import excepciones.AccesoDatosException;
import excepciones.ProductoDuplicadoException;
import excepciones.ProductoNoEncontradoException;
import modelo.Producto;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Cliente;
import modelo.Pedido;

public interface BaseDAO {

    void registrarProducto(Producto producto)
            throws ProductoDuplicadoException, AccesoBaseDatosException;

    String obtenerRutaFichero(int idProducto)
            throws ProductoNoEncontradoException, AccesoBaseDatosException, SQLException;
    
    boolean editarStock(int idProducto, int stocknuevo)
            throws SQLException;
    
    public ArrayList<Producto> recogerProductos()
            throws SQLException;
    
    public Cliente registrarCliente(Cliente cliente)
            throws AccesoDatosException;
    
     public Cliente buscarCliente(int id)
             throws SQLException, AccesoDatosException;
     
     public Producto buscarProducto(int id)
             throws SQLException, AccesoDatosException;
     
    public List<Producto> consultarProductosDisponibles()
            throws AccesoDatosException;
}
