package excepciones;

public class FicheroNoEncontradoException extends TiendaException {

    private static final long serialVersionUID = 1L;

    public FicheroNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public FicheroNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
