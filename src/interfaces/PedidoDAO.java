package interfaces;

import excepciones.AccesoDatosException;
import java.util.List;

import excepciones.AccesoFicheroException;
import excepciones.FicheroNoEncontradoException;
import java.io.File;
import modelo.Pedido;

public interface PedidoDAO {

    List<Pedido> obtenerHistorialProducto(int idProducto)
            throws FicheroNoEncontradoException, AccesoFicheroException;
    
    void crearFichero();
    
    void pedidosPorCliente(int id);
    
    void listarPedidos();
    
    public int siguienteId()
            throws AccesoDatosException;
 
}
