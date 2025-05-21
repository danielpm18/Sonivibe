import Control.ControladorPrincipal;
import Control.GestorDatos;
import View.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Ejecutamos la aplicación en el hilo de eventos de Swing para garantizar seguridad en la interfaz gráfica
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Iniciando la aplicación Sonivibe...");

                    // Creamos el modelo de datos que gestionará la información
                    GestorDatos modelo = new GestorDatos();

                    // Creamos el controlador principal, que coordina la lógica entre modelo y vista
                    ControladorPrincipal controlador = new ControladorPrincipal(modelo);

                    // Creamos la ventana principal de la aplicación (la interfaz gráfica)
                    VentanaPrincipal ventana = new VentanaPrincipal();

                    // Asociamos el controlador con la ventana para manejar eventos del usuario
                    ventana.addController(controlador);

                } catch (Exception e) {
                    // Si ocurre algún error durante la inicialización, lo mostramos por consola
                    System.err.println("Ocurrió un error al iniciar la aplicación: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
