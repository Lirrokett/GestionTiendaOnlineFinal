package excepciones;

public class TiendaException extends Exception {

    private static final long serialVersionUID = 1L;

    public TiendaException(String mensaje) {
        super(mensaje);
    }

    public TiendaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
