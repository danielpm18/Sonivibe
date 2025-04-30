import Control.ControladorPrincipal;
import Control.GestorDatos;

public class Main {
    public static void main(String[] args) {
        GestorDatos modelo = new GestorDatos();
        ControladorPrincipal controlador = new ControladorPrincipal(modelo);
        controlador.iniciar();
    }
}