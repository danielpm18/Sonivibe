import Control.ControladorPrincipal;
import Modelo.GestorDatos;

public class Main {
    public static void main(String[] args) {
        GestorDatos modelo = new GestorDatos();
        ControladorPrincipal controlador = new ControladorPrincipal(modelo);
        controlador.iniciar();
    }
}