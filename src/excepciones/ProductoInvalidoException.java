package excepciones;

public class ProductoInvalidoException extends TiendaException {

    private static final long serialVersionUID = 1L;

    public ProductoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
