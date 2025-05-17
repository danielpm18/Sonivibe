package Control;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class ControladorAudio {
    private Clip clip;
    private long clipPosition;
    private File currentFile;
    private boolean isPaused;

    public ControladorAudio() {
        this.clip = null;
        this.clipPosition = 0;
        this.currentFile = null;
        this.isPaused = false;
    }

    public void reproducir(File archivoAudio) {
        try {
            if (clip != null) {
                clip.stop();
                clip.close();
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(archivoAudio);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            clipPosition = 0;
            currentFile = archivoAudio;
            isPaused = false;
            System.out.println("Reproducción iniciada para: " + archivoAudio.getName());
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error al reproducir el archivo: " + e.getMessage());
        }
    }

    public void pausar() {
        if (clip != null && clip.isRunning()) {
            clipPosition = clip.getMicrosecondPosition();
            clip.stop();
            isPaused = true;
            System.out.println("Reproducción pausada en: " + clipPosition + " microsegundos");
        }
    }

    public void reanudar() {
        if (clip != null && isPaused) {
            try {
                if (!clip.isOpen()) {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(currentFile);
                    clip.open(audioInputStream);
                }
                clip.setMicrosecondPosition(clipPosition);
                clip.start();
                isPaused = false;
                System.out.println("Reproducción reanudada desde: " + clipPosition + " microsegundos");
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Error al reanudar la reproducción: " + e.getMessage());
            }
        } else {
            System.out.println("No hay nada que reanudar o la reproducción no está pausada.");
        }
    }

    // Método getter para acceder al Clip
    public Clip getClip() {
        return clip;
    }
}