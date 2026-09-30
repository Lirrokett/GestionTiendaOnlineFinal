/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import excepciones.AccesoBaseDatosException;
import excepciones.AccesoDatosException;
import excepciones.ProductoDuplicadoException;
import excepciones.ProductoNoEncontradoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import modelo.Categoria;
import modelo.Cliente;
import modelo.Pedido;
import modelo.Producto;
import interfaces.BaseDAO;

/**
 *
 * @author songo
 */
public class DaoBaseDeDatos implements BaseDAO {

    DaoFichero dao = new DaoFichero();
    // Los siguientes atributos se utilizan para recoger los valores del fich de
    // configuracion
    private ResourceBundle configFile;
    private String urlBD;
    private String userBD;
    private String passwordBD;

    // Sentencias SQL
    private static final String REDUCIRSTOCK = "UPDATE PRODUCTO SET STOCK = STOCK - ? WHERE ID = ? AND STOCK >= ?";
    private static final String CONSULTARPRODUCTOSDISPONIBLES = "SELECT ID, NOMBRE, PRECIO, CATEGORIA, STOCK, RUTA "
                + "FROM PRODUCTO WHERE STOCK > 0 ORDER BY ID";
    private static final String BUSCARPRODUCTO = "SELECT ID, NOMBRE, PRECIO, CATEGORIA, STOCK, RUTA FROM PRODUCTO WHERE ID = ?";
    private static final String BUSCARCLIENTE = "SELECT ID, NOMBRE, EMAIL, TELEFONO, DIRECCION FROM CLIENTE WHERE ID = ?";
    private static final String INSERTARCLIENTE = "INSERT INTO CLIENTE (NOMBRE, EMAIL, TELEFONO, DIRECCION) VALUES (?, ?, ?, ?)";
    private static final String SQL_RUTA = "SELECT RUTA FROM PRODUCTO WHERE ID = ?";
    private static final String RECOGERPRODUCTOS = "SELECT * FROM producto";
    private static final String sql = "UPDATE PRODUCTO SET stock = ? WHERE ID = ?";
    private static final String SQL_EXISTE_NOMBRE = "SELECT COUNT(*) FROM PRODUCTO WHERE NOMBRE = ?";
    private static final String SQL_INSERTAR
            = "INSERT INTO PRODUCTO (NOMBRE, PRECIO, CATEGORIA, STOCK, RUTA) VALUES (?, ?, ?, ?, ?)";
    // Para la conexi n utilizamos un fichero de configuaraci n, config que
    // guardamos en el paquete control:

    public DaoBaseDeDatos() {
        this.configFile = ResourceBundle.getBundle("configClase");
        this.urlBD = this.configFile.getString("Conn");
        this.userBD = this.configFile.getString("DBUser");
        this.passwordBD = this.configFile.getString("DBPass");
    }

    /**
     *
     * @return
     * @throws AccesoDatosException
     */
    
    public boolean reducirStock(int idProducto, int cantidad) throws AccesoDatosException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        }

        

        try (Connection con = DriverManager.getConnection(urlBD, this.userBD, this.passwordBD); PreparedStatement ps = con.prepareStatement(REDUCIRSTOCK)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidad);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo actualizar el stock del producto.", e);
        }
    }
    
    @Override
    public List<Producto> consultarProductosDisponibles() throws AccesoDatosException {
        List<Producto> productos = new ArrayList<>();

        try (Connection cn = DriverManager.getConnection(urlBD, this.userBD, this.passwordBD);
             PreparedStatement ps = cn.prepareStatement(CONSULTARPRODUCTOSDISPONIBLES);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productos.add(productoDesdeResultSet(rs));
            }

            return productos;
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudieron consultar los productos disponibles.", e);
        }
    }
    
    @Override
    public ArrayList<Producto> recogerProductos() throws SQLException {
        ArrayList<Producto> productos = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlBD, this.userBD, this.passwordBD); PreparedStatement stmt = con.prepareStatement(RECOGERPRODUCTOS)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setId(rs.getInt("ID"));
                    p.setNombre(rs.getString("NOMBRE"));
                    p.setPrecio(rs.getDouble("PRECIO"));
                    p.setCategoria(Categoria.valueOf(rs.getString("CATEGORIA")));
                    p.setStock(rs.getInt("STOCK"));
                    p.setRuta(rs.getString("RUTA"));
                    productos.add(p);
                }
            }

        }

        return productos;
    }

    @Override
    public Cliente registrarCliente(Cliente cliente) throws AccesoDatosException {

        try (Connection cn = DriverManager.getConnection(urlBD, this.userBD, this.passwordBD); PreparedStatement ps = cn.prepareStatement(INSERTARCLIENTE, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getEmail());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getDireccion());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
            }

            return cliente;
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo registrar el cliente.", e);
        }
    }

    @Override
    public boolean editarStock(int idProducto, int stocknuevo) throws SQLException {
        try (Connection con = DriverManager.getConnection(urlBD, this.userBD, this.passwordBD); PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, stocknuevo);
            stmt.setInt(2, idProducto);

            int filasModificadas = stmt.executeUpdate();

            return filasModificadas > 0;
        }
    }

    /**
     *
     * @param producto
     * @throws ProductoDuplicadoException
     * @throws AccesoBaseDatosException
     */
    @Override
    public void registrarProducto(Producto producto)
            throws ProductoDuplicadoException, AccesoBaseDatosException {
        try (Connection con = DriverManager.getConnection(urlBD, this.userBD, this.passwordBD)) {
            try (PreparedStatement sentenciaExiste = con.prepareStatement(SQL_EXISTE_NOMBRE)) {
                sentenciaExiste.setString(1, producto.getNombre());
                try (ResultSet resultado = sentenciaExiste.executeQuery()) {
                    resultado.next();
                    if (resultado.getInt(1) > 0) {
                        throw new ProductoDuplicadoException(
                                "Ya existe un producto registrado con el nombre '" + producto.getNombre() + "'.");
                    }
                }
            }

            try (PreparedStatement sentenciaInsertar
                    = con.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
                sentenciaInsertar.setString(1, producto.getNombre());
                sentenciaInsertar.setDouble(2, producto.getPrecio());
                sentenciaInsertar.setString(3, producto.getCategoria().name());
                sentenciaInsertar.setInt(4, producto.getStock());
                sentenciaInsertar.setString(5, producto.getRuta());
                sentenciaInsertar.executeUpdate();

                try (ResultSet clavesGeneradas = sentenciaInsertar.getGeneratedKeys()) {
                    if (clavesGeneradas.next()) {
                        producto.setId(clavesGeneradas.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new AccesoBaseDatosException(
                    "No se pudo registrar el producto en la base de datos.", e);
        }
    }

    /**
     *
     * @param idProducto
     * @return
     * @throws ProductoNoEncontradoException
     * @throws AccesoBaseDatosException
     * @throws SQLException
     */
    @Override
    public String obtenerRutaFichero(int idProducto)
            throws ProductoNoEncontradoException, AccesoBaseDatosException, SQLException {
        try (Connection con = DriverManager.getConnection(urlBD, this.userBD, this.passwordBD)) {
            try (PreparedStatement sentencia = con.prepareStatement(SQL_RUTA)) {
                sentencia.setInt(1, idProducto);
                try (ResultSet resultado = sentencia.executeQuery()) {
                    if (!resultado.next()) {
                        throw new ProductoNoEncontradoException(
                                "No existe ningun producto con el id " + idProducto + ".");
                    }
                    return resultado.getString("RUTA");
                }
            } catch (SQLException e) {
                throw new AccesoBaseDatosException(
                        "No se pudo consultar la ruta del producto en la base de datos.", e);
            }
        }
    }

    @Override
    public Cliente buscarCliente(int id) throws AccesoDatosException, SQLException {

        try (Connection con = DriverManager.getConnection(urlBD, this.userBD, this.passwordBD)) {
            try (PreparedStatement ps = con.prepareStatement(BUSCARCLIENTE)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }

                    return new Cliente(
                            rs.getInt("ID"),
                            rs.getString("NOMBRE"),
                            rs.getString("EMAIL"),
                            rs.getString("TELEFONO"),
                            rs.getString("DIRECCION")
                    );
                }
            } catch (SQLException e) {
                throw new AccesoDatosException("No se pudo buscar el cliente.", e);
            }
        }
    }

    /**
     *
     * @param id
     * @return
     * @throws AccesoDatosException
     * @throws SQLException
     */
    @Override
    public Producto buscarProducto(int id) throws AccesoDatosException, SQLException {

        try (Connection con = DriverManager.getConnection(urlBD, this.userBD, this.passwordBD)) {
            try (PreparedStatement ps = con.prepareStatement(BUSCARPRODUCTO)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    return productoDesdeResultSet(rs);
                }
            } catch (SQLException e) {
                throw new AccesoDatosException("No se pudo buscar el producto.", e);
            }
        }
    }

    private Producto productoDesdeResultSet(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getInt("ID"));
        producto.setNombre(rs.getString("NOMBRE"));
        producto.setPrecio(rs.getDouble("PRECIO"));

        String categoria = rs.getString("CATEGORIA");
        if (categoria != null) {
            producto.setCategoria(Categoria.valueOf(categoria));
        }

        producto.setStock(rs.getInt("STOCK"));
        producto.setRuta(rs.getString("RUTA"));
        return producto;
    }
}
