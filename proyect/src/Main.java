import Control.ControladorPrincipal;
import Control.GestorDatos;
import View.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Ejecutar la inicialización en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Inicializando aplicación Sonivibe...");
                GestorDatos modelo = new GestorDatos();
                ControladorPrincipal controlador = new ControladorPrincipal(modelo);
                VentanaPrincipal ventana = new VentanaPrincipal();
                ventana.addController(controlador);

            } catch (Exception e) {
                System.err.println("Error al inicializar la aplicación: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}