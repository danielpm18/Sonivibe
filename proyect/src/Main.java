import Control.ControladorPrincipal;
import Control.GestorDatos;
import View.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Iniciando la aplicación Sonivibe...");

                    GestorDatos modelo = new GestorDatos();

                    ControladorPrincipal controlador = new ControladorPrincipal(modelo);

                    VentanaPrincipal ventana = new VentanaPrincipal();

                    ventana.addController(controlador);

                } catch (Exception e) {
                    System.err.println("Ocurrió un error al iniciar la aplicación: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
