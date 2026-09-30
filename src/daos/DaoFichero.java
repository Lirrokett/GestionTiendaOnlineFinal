/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import excepciones.AccesoBaseDatosException;
import excepciones.AccesoDatosException;
import excepciones.AccesoFicheroException;
import excepciones.FicheroNoEncontradoException;
import excepciones.ProductoNoEncontradoException;
import interfaces.PedidoDAO;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.Pedido;

/**
 *
 * @author songo
 */
public class DaoFichero implements PedidoDAO {

    private final File fichero;

    public DaoFichero() {
        fichero = new File("pedido.dat");
    }

    /**
     *
     */
    @Override
    public void crearFichero() {

        if (!fichero.exists()) {
            File aux = new File("reservas.aux");
            File backup = new File("reservas.backup");
            ArrayList<Integer> productos1 = new ArrayList<>();
            productos1.add(1);
            productos1.add(2);
            productos1.add(3);
            ArrayList<Integer> productos2 = new ArrayList<>();
            productos2.add(2);
            productos2.add(4);
            ArrayList<Integer> productos3 = new ArrayList<>();
            productos3.add(1);
            productos3.add(3);
            productos3.add(5);
            Pedido p1 = new Pedido(1, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5), true, productos1, 1);

            Pedido p2 = new Pedido(2, LocalDate.of(2026, 9, 3), LocalDate.of(2026, 9, 8), true, productos2, 2);

            Pedido p3 = new Pedido(3, LocalDate.of(2026, 9, 7), LocalDate.of(2026, 9, 12), true, productos3, 3);

            Pedido p4 = new Pedido(4, LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 15), true, new ArrayList<>(java.util.Arrays.asList(4, 5)), 4);

            Pedido p5 = new Pedido(5, LocalDate.of(2026, 9, 12), null, false, new ArrayList<>(java.util.Arrays.asList(1, 2, 5)), 5);
            // 1. Escribir el fichero auxiliar
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(aux))) {
                oos.writeObject(p1);
                oos.writeObject(p2);
                oos.writeObject(p3);
                oos.writeObject(p4);
                oos.writeObject(p5);
            } catch (IOException e) {
                System.out.println("Error al guardar pedidos: " + e.getMessage());
                return;
            }

            // 2. Renombrar fichero original a backup
            if (fichero.exists()) {
                if (!fichero.renameTo(backup)) {
                    System.out.println("Error al crear el backup");
                    return;
                }
            }

            // 3. Renombrar auxiliar como definitivo
            if (!aux.renameTo(fichero)) {
                System.out.println("Error al renombrar el fichero auxiliar");
                if (backup.exists()) {
                    backup.renameTo(fichero);
                }
            } else {
                if (backup.exists()) {
                    backup.delete();
                }
            }
        }
    }

    /**
     *
     * @param id
     */
    @Override
    public void pedidosPorCliente(int id) {
        int contador = 0;
        try (ObjectInputStream ois
                = new ObjectInputStream(new FileInputStream(fichero))) {

            while (true) {
                Pedido p = (Pedido) ois.readObject();
                if (p.getClienteId() == id) {
                    System.out.println(p.toString());
                }
            }

        } catch (EOFException e) {
            // fin normal
        } catch (Exception e) {
            System.out.println("Error al leer pedidos");
        }

    }

    @Override
    public void listarPedidos() {
        int contador = 0;
        try (ObjectInputStream ois
                = new ObjectInputStream(new FileInputStream(fichero))) {

            while (true) {
                Pedido p = (Pedido) ois.readObject();
                System.out.println(p.toString());

            }

        } catch (EOFException e) {
            // fin normal
        } catch (Exception e) {
            System.out.println("Error al leer pedidos");
        }

    }

    /**
     *
     * @param idProducto
     * @return
     * @throws FicheroNoEncontradoException
     * @throws AccesoFicheroException
     */
    @Override
    public List<Pedido> obtenerHistorialProducto(int idProducto)
            throws FicheroNoEncontradoException, AccesoFicheroException {

        List<Pedido> historial = new ArrayList<Pedido>();
        if (fichero.length() == 0) {
            return historial;
        }

        try (FileInputStream flujoFichero = new FileInputStream(fichero); BufferedInputStream flujoBuffer = new BufferedInputStream(flujoFichero); ObjectInputStream flujoObjetos = new ObjectInputStream(flujoBuffer)) {
            boolean finFichero = false;
            while (!finFichero) {
                try {
                    Object objeto = flujoObjetos.readObject();
                    if (objeto instanceof Pedido) {
                        Pedido pedido = (Pedido) objeto;
                        if (contieneProducto(pedido, idProducto)) {
                            historial.add(pedido);
                        }
                    }
                } catch (EOFException e) {
                    finFichero = true;
                }
            }
        } catch (FileNotFoundException e) {
            throw new FicheroNoEncontradoException(
                    "No se pudo abrir el fichero de pedidos: " + e);
        } catch (ClassNotFoundException e) {
            throw new AccesoFicheroException(
                    "El fichero de pedidos contiene objetos con un formato no reconocido.", e);
        } catch (IOException e) {
            throw new AccesoFicheroException(
                    "Error de lectura en el fichero de pedidos: " + "pedidos.dat", e);
        }
        return historial;
    }

    private boolean contieneProducto(Pedido pedido, int idProducto) {
        List<Integer> idsProductos = pedido.getProductoId();

        if (idsProductos == null) {
            return false;
        }
        for (Integer id : idsProductos) {
            if (id != null && id == idProducto) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int siguienteId() throws AccesoDatosException {
        int max = 0;

        for (Pedido pedido : leerPedidos()) {
            if (pedido.getId() > max) {
                max = pedido.getId();
            }
        }

        return max + 1;
    }

    public synchronized List<Pedido> leerPedidos() throws AccesoDatosException {
        List<Pedido> pedidos = new ArrayList<>();

        if (!fichero.exists() || fichero.length() == 0) {
            return pedidos;
        }

        try (ObjectInputStream ois
                = new ObjectInputStream(new FileInputStream(fichero))) {

            while (true) {
                pedidos.add((Pedido) ois.readObject());
            }

        } catch (EOFException e) {
            // Fin normal del fichero.
            return pedidos;
        } catch (IOException | ClassNotFoundException e) {
            throw new AccesoDatosException("No se pudo leer pedido.dat.", e);
        }
    }

    public synchronized void guardarPedido(Pedido pedido) throws AccesoDatosException {
        List<Pedido> pedidos = leerPedidos();
        pedidos.add(pedido);

        try (ObjectOutputStream oos
                = new ObjectOutputStream(new FileOutputStream(fichero))) {
            for (Pedido p : pedidos) {
                oos.writeObject(p);
            }
        } catch (IOException e) {
            throw new AccesoDatosException("No se pudo guardar el pedido.", e);
        }
    }
    
    
}
