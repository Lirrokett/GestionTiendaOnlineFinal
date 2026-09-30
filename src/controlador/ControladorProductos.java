/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import daos.DaoBaseDeDatos;
import excepciones.AccesoBaseDatosException;
import excepciones.AccesoDatosException;
import excepciones.ProductoDuplicadoException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Producto;

/**
 *
 * @author songo
 */
public class ControladorProductos {
    DaoBaseDeDatos base=new DaoBaseDeDatos();
    
    public ArrayList<Producto> listarProductos() throws SQLException{
        return base.recogerProductos();
    }
    
    public boolean editarStock(int cod,int stock) throws SQLException{
       return base.editarStock(cod,stock);
        
    }

    public void registrarProducto(Producto producto) throws ProductoDuplicadoException, AccesoBaseDatosException {
        base.registrarProducto(producto);
    }
    
    public List<Producto> consultarProductosDisponibles()
            throws AccesoDatosException {
        return base.consultarProductosDisponibles();
    }
}
