package excepciones;

public class AccesoBaseDatosException extends TiendaException {

    private static final long serialVersionUID = 1L;

    public AccesoBaseDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
