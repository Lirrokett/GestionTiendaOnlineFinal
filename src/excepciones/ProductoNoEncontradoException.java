package excepciones;

public class ProductoNoEncontradoException extends TiendaException {

    private static final long serialVersionUID = 1L;

    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
