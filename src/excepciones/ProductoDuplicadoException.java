package excepciones;

public class ProductoDuplicadoException extends TiendaException {

    private static final long serialVersionUID = 1L;

    public ProductoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
