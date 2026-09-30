package excepciones;

public class AccesoFicheroException extends TiendaException {

    private static final long serialVersionUID = 1L;

    public AccesoFicheroException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
