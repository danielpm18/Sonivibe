package Control;

import javazoom.jl.player.Player;
import java.io.File;
import java.io.FileInputStream;

public class ControladorAudio {

    private Player reproductor;
    private boolean isPaused = false;
    private boolean isStopped = false;
    private Thread playerThread;

    // Constructor
    public ControladorAudio() {
        this.reproductor = null;
        this.playerThread = null;
    }

    // Reproducir un archivo MP3
    public synchronized void reproducir(File archivoMP3) {
        if (archivoMP3 == null || !archivoMP3.exists()) {
            System.out.println("Error: El archivo MP3 no existe: " + (archivoMP3 != null ? archivoMP3.getAbsolutePath() : "Archivo nulo"));
            return;
        }

        // Detener y limpiar cualquier reproducción anterior
        detener();
        if (playerThread != null && playerThread.isAlive()) {
            try {
                playerThread.join(); // Espera a que el hilo anterior termine
            } catch (InterruptedException e) {
                System.out.println("Error al esperar el hilo anterior: " + e.getMessage());
            }
        }

        playerThread = new Thread(() -> {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(archivoMP3);
                reproductor = new Player(fis);
                while (reproductor != null && !reproductor.isComplete() && !isStopped) {
                    if (!isPaused) {
                        reproductor.play(1); // Reproduce 1 frame a la vez
                    } else {
                        Thread.sleep(100); // Pausa el hilo si está pausado
                    }
                }
            } catch (Exception e) {
                System.out.println("Error al reproducir " + archivoMP3.getName() + ": " + e.getMessage());
                if (e.getMessage().contains("Exception decoding audio frame")) {
                    System.out.println("El archivo " + archivoMP3.getName() + " podría estar corrupto. Prueba con otro archivo MP3 válido.");
                }
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e) {
                        System.out.println("Error al cerrar FileInputStream: " + e.getMessage());
                    }
                }
                limpiarRecursos();
            }
            try {
                Thread.sleep(500); // Pausa mínima para evitar superposición de hilos
            } catch (InterruptedException e) {
                System.out.println("Error en pausa entre reproducciones: " + e.getMessage());
            }
        });
        playerThread.start();
    }

    // Pausar la reproducción
    public void pausar() {
        if (reproductor != null && !isStopped) {
            isPaused = true;
            System.out.println("Reproducción pausada.");
        }
    }

    // Reanudar la reproducción
    public void reanudar() {
        if (reproductor != null && isPaused && !isStopped) {
            isPaused = false;
            System.out.println("Reproducción reanudada.");
        }
    }

    // Detener la reproducción
    public void detener() {
        if (reproductor != null) {
            isStopped = true;
            try {
                reproductor.close();
            } catch (Exception e) {
                System.out.println("Error al cerrar reproductor: " + e.getMessage());
            }
            limpiarRecursos();
            System.out.println("Reproducción detenida.");
        }
    }

    // Limpiar recursos
    private void limpiarRecursos() {
        reproductor = null;
        isPaused = false;
        isStopped = false;
        if (playerThread != null) {
            try {
                playerThread.join(1000); // Espera hasta 1 segundo para que el hilo termine
            } catch (InterruptedException e) {
                System.out.println("Error al esperar el hilo de reproducción: " + e.getMessage());
            }
            playerThread = null;
        }
    }
}