/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import daos.DaoBaseDeDatos;
import excepciones.AccesoDatosException;
import modelo.Cliente;

/**
 *
 * @author songo
 */
public class ControladorClientes {
    
    DaoBaseDeDatos dao=new DaoBaseDeDatos();
    public Cliente registrarCliente(String nombre, String email,
                                    String telefono, String direccion)
            throws AccesoDatosException {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio.");
        }

        nombre = nombre.trim();
        email = email.trim();
        telefono = telefono == null ? null : telefono.trim();
        direccion = direccion == null ? null : direccion.trim();

        if (nombre.length() > 20) {
            throw new IllegalArgumentException("El nombre no puede superar los 20 caracteres.");
        }
        if (email.length() > 30) {
            throw new IllegalArgumentException("El email no puede superar los 30 caracteres.");
        }
        if (telefono != null && telefono.length() > 20) {
            throw new IllegalArgumentException("El teléfono no puede superar los 20 caracteres.");
        }
        if (direccion != null && direccion.length() > 100) {
            throw new IllegalArgumentException("La dirección no puede superar los 100 caracteres.");
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setEmail(email);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);

        return dao.registrarCliente(cliente);
    }
}
