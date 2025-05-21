package View;

import Control.ControladorAudio;
import Control.ControladorPrincipal;
import Modelo.Cancion;
import Modelo.Interprete;
import Modelo.Playlist;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaReproduccion extends JFrame {
    private final ControladorPrincipal controlador;
    private JList<Object> listaPrincipal;
    private DefaultListModel<Object> modeloLista;
    private JButton btnReproducir, btnPausar, btnReanudar, btnSiguiente, btnAnterior, btnAleatorio;
    private JLabel lblEstado, lblTiempo, lblImagenCancion;
    private JSlider progressBar;
    private Timer updateTimer;
    private final String modo;
    private boolean seleccionandoCategoria;
    private Playlist playlistSeleccionada;
    private Interprete interpreteSeleccionado;
    private String albumSeleccionado;
    private List<Cancion> cancionesAReproducir;
    private int indiceReproduccion;

    public VentanaReproduccion(ControladorPrincipal controlador, String modo) {
        this.controlador = controlador;
        this.modo = modo;
        this.seleccionandoCategoria = modo.equals(ControladorPrincipal.PLAYLIST) ||
                modo.equals(ControladorPrincipal.INTERPRETE) ||
                modo.equals(ControladorPrincipal.ALBUM);
        this.cancionesAReproducir = new ArrayList<>();
        this.indiceReproduccion = 0;
        inicializarComponentes();
        cargarDatosIniciales();
    }

    private void inicializarComponentes() {
        setTitle("Reproducción - Sonivibe");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloLista = new DefaultListModel<>();
        listaPrincipal = new JList<>(modeloLista);
        listaPrincipal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(listaPrincipal);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnReproducir = new JButton("Reproducir");
        btnPausar = new JButton("Pausar");
        btnReanudar = new JButton("Reanudar");
        btnSiguiente = new JButton("Anterior");
        btnAnterior = new JButton("Siguiente");
        btnAleatorio = new JButton("Aleatorio");

        panelBotones.add(btnReproducir);
        panelBotones.add(btnPausar);
        panelBotones.add(btnReanudar);
        panelBotones.add(btnSiguiente);
        panelBotones.add(btnAnterior);
        panelBotones.add(btnAleatorio);

        // Barra de progreso
        progressBar = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        progressBar.setEnabled(false);
        progressBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!progressBar.getValueIsAdjusting() && controlador.getControladorAudio().getClip() != null && !controlador.getControladorAudio().getClip().isRunning()) {
                    long microseconds = (long) progressBar.getValue() * 1000L;
                    controlador.getControladorAudio().getClip().setMicrosecondPosition(microseconds);
                    updateProgressBar();
                }
            }
        });

        // Imagen del album
        lblImagenCancion = new JLabel();
        lblImagenCancion.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagenCancion.setPreferredSize(new Dimension(100, 100));

        // tiempo
        lblTiempo = new JLabel("0:00 / 0:00", SwingConstants.CENTER);

        // Timer para actualizar la barra y el tiempo
        updateTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProgressBar();
            }
        });
        updateTimer.setRepeats(true);

        btnReproducir.setEnabled(true);
        btnPausar.setEnabled(false);
        btnReanudar.setEnabled(false);
        btnSiguiente.setEnabled(false);
        btnAnterior.setEnabled(false);
        btnAleatorio.setEnabled(false);

        lblEstado = new JLabel("Estado: Detenido");
        panel.add(lblEstado, BorderLayout.NORTH);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(lblImagenCancion, BorderLayout.NORTH);
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(lblTiempo, BorderLayout.SOUTH);
        southPanel.add(progressPanel, BorderLayout.CENTER);
        southPanel.add(panelBotones, BorderLayout.SOUTH);
        panel.add(southPanel, BorderLayout.SOUTH);


        btnReproducir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reproducir();
            }
        });

        btnPausar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pausarCancion();
            }
        });

        btnReanudar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reanudarCancion();
            }
        });

        btnSiguiente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reproducirSiguienteManual();
            }
        });

        btnAnterior.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reproducirAnteriorManual();
            }
        });

        btnAleatorio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reproducirAleatorio();
            }
        });


        listaPrincipal.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (seleccionandoCategoria) {
                        btnReproducir.setEnabled(true);
                        btnPausar.setEnabled(false);
                        btnReanudar.setEnabled(false);
                        btnSiguiente.setEnabled(false);
                        btnAnterior.setEnabled(false);
                        btnAleatorio.setEnabled(false);
                    } else {
                        btnReproducir.setEnabled(true);
                        btnPausar.setEnabled(false);
                        btnReanudar.setEnabled(false);
                        btnSiguiente.setEnabled(false);
                        btnAnterior.setEnabled(false);
                        btnAleatorio.setEnabled(false);
                    }
                }
            }
        });

        add(panel);

    }

    private void cargarDatosIniciales() {
        modeloLista.clear();
        switch (modo) {
            case ControladorPrincipal.CANCION:
                seleccionandoCategoria = false;
                for (Cancion cancion : controlador.getModelo().getCanciones()) {
                    modeloLista.addElement(cancion);
                }
                lblEstado.setText("Estado: Seleccione una canción");
                break;
            case ControladorPrincipal.PLAYLIST:
                for (Playlist playlist : controlador.getModelo().getPlaylists()) {
                    modeloLista.addElement(playlist);
                }
                lblEstado.setText("Estado: Seleccione una playlist");
                break;
            case ControladorPrincipal.INTERPRETE:
                for (Interprete interprete : controlador.getModelo().getInterpretes()) {
                    modeloLista.addElement(interprete);
                }
                lblEstado.setText("Estado: Seleccione un intérprete");
                break;
            case ControladorPrincipal.ALBUM:
                List<String> albumes = new ArrayList<>();
                for (Cancion cancion : controlador.getModelo().getCanciones()) {
                    if (!albumes.contains(cancion.getAlbum())) {
                        albumes.add(cancion.getAlbum());
                        modeloLista.addElement(cancion.getAlbum());
                    }
                }
                lblEstado.setText("Estado: Seleccione un álbum");
                break;
        }
        if (modeloLista.isEmpty()) {
            lblEstado.setText("Estado: No hay datos disponibles");
            deshabilitarBotones();
        }
    }

    private void reproducir() {
        Object seleccionado = listaPrincipal.getSelectedValue();
        if (seleccionado == null) {
            lblEstado.setText("Estado: Seleccione una opción primero");
            return;
        }

        cancionesAReproducir.clear();
        indiceReproduccion = 0;

        if (modo.equals(ControladorPrincipal.CANCION)) {
            if (seleccionado instanceof Cancion) {
                cancionesAReproducir.add((Cancion) seleccionado);
                reproducirSiguiente();
            }
        } else if (modo.equals(ControladorPrincipal.PLAYLIST)) {
            playlistSeleccionada = (Playlist) seleccionado;
            List<String> cancionesIds = playlistSeleccionada.getCanciones();
            if (cancionesIds != null && !cancionesIds.isEmpty()) {
                for (String cancionId : cancionesIds) {
                    for (Cancion cancion : controlador.getModelo().getCanciones()) {
                        if (cancion.getId().equals(cancionId)) {
                            cancionesAReproducir.add(cancion);
                        }
                    }
                }
                reproducirSiguiente();
            } else {
                lblEstado.setText("Estado: No hay canciones en esta playlist");
            }
        } else if (modo.equals(ControladorPrincipal.INTERPRETE)) {
            if (seleccionado instanceof Interprete) {
                interpreteSeleccionado = (Interprete) seleccionado;
                String interpreteId = interpreteSeleccionado.getId();
                if (interpreteId == null || interpreteId.isEmpty()) {
                    lblEstado.setText("Estado: ID del intérprete no válido");
                    return;
                }
                for (Cancion cancion : controlador.getModelo().getCanciones()) {
                    if (cancion.getInterpreteId() != null && cancion.getInterpreteId().equals(interpreteId)) {
                        cancionesAReproducir.add(cancion);
                    }
                }
                if (cancionesAReproducir.isEmpty()) {
                }
                reproducirSiguiente();
            } else {
                lblEstado.setText("Estado: Selección inválida para intérprete");
            }
        } else if (modo.equals(ControladorPrincipal.ALBUM)) {
            albumSeleccionado = (String) seleccionado;
            for (Cancion cancion : controlador.getModelo().getCanciones()) {
                if (cancion.getAlbum().equals(albumSeleccionado)) {
                    cancionesAReproducir.add(cancion);
                }
            }
            reproducirSiguiente();
        }

        if (cancionesAReproducir.isEmpty()) {
            lblEstado.setText("Estado: No hay canciones para reproducir");
            deshabilitarBotones();
        } else {
            habilitarBotonesReproduccion();
            progressBar.setEnabled(true);
            updateTimer.start();
        }
    }

    private void reproducirSiguiente() {
        if (indiceReproduccion < cancionesAReproducir.size()) {
            Cancion cancion = cancionesAReproducir.get(indiceReproduccion);
            String idNumerico = cancion.getId().replace("CAN", "");
            File archivoAudio = new File("proyect/src/Data/Songs/" + idNumerico + ".wav");
            if (archivoAudio.exists()) {
                controlador.getControladorAudio().reproducir(archivoAudio);
                lblEstado.setText("Estado: Reproduciendo " + cancion.getNombre());
                cancion.incrementarReproducciones();
                controlador.getModelo().guardarCanciones();
                cargarImagenCancion(cancion.getAlbum());
                controlador.getControladorAudio().getClip().addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        if (indiceReproduccion < cancionesAReproducir.size()) {
                            indiceReproduccion++; // Pasar a la siguiente
                            if (indiceReproduccion < cancionesAReproducir.size()) {
                                reproducirSiguiente();
                            } else {
                                lblEstado.setText("Estado: Reproducción completa");
                                deshabilitarBotonesReproduccion();
                                progressBar.setEnabled(false);
                                updateTimer.stop();
                                lblTiempo.setText("0:00 / 0:00");
                                lblImagenCancion.setIcon(null);
                            }
                        }
                    }
                });
                indiceReproduccion++;
                habilitarBotonesReproduccion();
                updateProgressBar();
            } else {
                lblEstado.setText("Estado: Archivo no encontrado para " + cancion.getNombre());
                indiceReproduccion++;
                reproducirSiguiente();
            }
        }
    }

    private void reproducirSiguienteManual() {
        if (indiceReproduccion < cancionesAReproducir.size() - 1) {
            indiceReproduccion++;
            Cancion cancion = cancionesAReproducir.get(indiceReproduccion);
            String idNumerico = cancion.getId().replace("CAN", "");
            File archivoAudio = new File("proyect/src/Data/Songs/" + idNumerico + ".wav");
            if (archivoAudio.exists()) {
                controlador.getControladorAudio().reproducir(archivoAudio);
                lblEstado.setText("Estado: Reproduciendo " + cancion.getNombre());
                cancion.incrementarReproducciones();
                controlador.getModelo().guardarCanciones();
                cargarImagenCancion(cancion.getAlbum());
                controlador.getControladorAudio().getClip().addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        if (indiceReproduccion < cancionesAReproducir.size() - 1) {
                            indiceReproduccion++;
                            reproducirSiguienteManual();
                        } else {
                            lblEstado.setText("Estado: Reproducción completa");
                            deshabilitarBotonesReproduccion();
                            progressBar.setEnabled(false);
                            updateTimer.stop();
                            lblTiempo.setText("0:00 / 0:00");
                            lblImagenCancion.setIcon(null);
                        }
                    }
                });
            } else {
                lblEstado.setText("Estado: Archivo no encontrado para " + cancion.getNombre());
                reproducirSiguienteManual();
            }
        } else {
            lblEstado.setText("Estado: Fin de la lista");
        }
        habilitarBotonesReproduccion();
        updateProgressBar();
    }

    private void reproducirAnteriorManual() {
        if (indiceReproduccion > 0) {
            indiceReproduccion--;
            Cancion cancion = cancionesAReproducir.get(indiceReproduccion);
            String idNumerico = cancion.getId().replace("CAN", "");
            File archivoAudio = new File("proyect/src/Data/Songs/" + idNumerico + ".wav");
            if (archivoAudio.exists()) {
                controlador.getControladorAudio().reproducir(archivoAudio);
                lblEstado.setText("Estado: Reproduciendo " + cancion.getNombre());
                cancion.incrementarReproducciones();
                controlador.getModelo().guardarCanciones();
                cargarImagenCancion(cancion.getAlbum());
                controlador.getControladorAudio().getClip().addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        if (indiceReproduccion > 0) {
                            indiceReproduccion--;
                            reproducirAnteriorManual();
                        }
                    }
                });
            } else {
                lblEstado.setText("Estado: Archivo no encontrado para " + cancion.getNombre());
                reproducirAnteriorManual();
            }
        } else {
            lblEstado.setText("Estado: Inicio de la lista");
        }
        habilitarBotonesReproduccion();
        updateProgressBar();
    }

    private void reproducirAleatorio() {
        if (!cancionesAReproducir.isEmpty()) {
            Collections.shuffle(cancionesAReproducir);
            indiceReproduccion = 0;
            reproducirSiguiente();
            lblEstado.setText("Estado: Reproducción aleatoria activada");
            habilitarBotonesReproduccion();
        } else {
            lblEstado.setText("Estado: No hay canciones para mezclar");
        }
    }

    private void pausarCancion() {
        controlador.getControladorAudio().pausar();
        lblEstado.setText("Estado: Pausado");
        updateTimer.stop();
        habilitarBotonesReproduccion();
    }

    private void reanudarCancion() {
        controlador.getControladorAudio().reanudar();
        lblEstado.setText("Estado: Reproduciendo");
        updateTimer.start();
        habilitarBotonesReproduccion();
    }

    private void updateProgressBar() {
        Clip clip = controlador.getControladorAudio().getClip();
        if (clip != null && clip.getMicrosecondLength() > 0) {
            long microsecondPosition = clip.getMicrosecondPosition();
            long microsecondLength = clip.getMicrosecondLength();
            int progress = (int) ((microsecondPosition * 100) / microsecondLength);
            progressBar.setValue(progress);

            long secondsPosition = microsecondPosition / 1000000;
            long secondsLength = microsecondLength / 1000000;

            String tiempoActual = formatTime(secondsPosition);
            String tiempoTotal = formatTime(secondsLength);
            lblTiempo.setText(tiempoActual + " / " + tiempoTotal);
        } else if (clip != null && !clip.isRunning()) {
            progressBar.setValue(0);
            lblTiempo.setText("0:00 / 0:00");
        }
    }

    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    private void cargarImagenCancion(String album) {
        String nombreArchivo = album.replaceAll("[^a-zA-Z0-9.-]", "_");
        String imagePath = "proyect/src/Data/Imagenes/" + nombreArchivo + ".jpg";
        File imagenArchivo = new File(imagePath);
        if (imagenArchivo.exists()) {
            ImageIcon imagenOriginal = new ImageIcon(imagePath);
            Image imagenEscalada = imagenOriginal.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon imagenFinal = new ImageIcon(imagenEscalada);
            lblImagenCancion.setIcon(imagenFinal);
        } else {
            lblImagenCancion.setIcon(null);
        }
    }

    private void habilitarBotonesReproduccion() {
        btnPausar.setEnabled(true);
        btnReanudar.setEnabled(true);
        btnSiguiente.setEnabled(indiceReproduccion < cancionesAReproducir.size() - 1);
        btnAnterior.setEnabled(indiceReproduccion > 0);
        btnAleatorio.setEnabled(!cancionesAReproducir.isEmpty());
        btnReproducir.setEnabled(false);
        progressBar.setEnabled(true);
    }

    private void deshabilitarBotonesReproduccion() {
        btnPausar.setEnabled(false);
        btnReanudar.setEnabled(false);
        btnSiguiente.setEnabled(false);
        btnAnterior.setEnabled(false);
        btnAleatorio.setEnabled(false);
        btnReproducir.setEnabled(true);
        progressBar.setEnabled(false);
        updateTimer.stop();
        lblTiempo.setText("0:00 / 0:00");
        lblImagenCancion.setIcon(null);
    }

    private void deshabilitarBotones() {
        btnReproducir.setEnabled(false);
        btnPausar.setEnabled(false);
        btnReanudar.setEnabled(false);
        btnSiguiente.setEnabled(false);
        btnAnterior.setEnabled(false);
        btnAleatorio.setEnabled(false);
        progressBar.setEnabled(false);
        updateTimer.stop();
        lblTiempo.setText("0:00 / 0:00");
        lblImagenCancion.setIcon(null);
    }

    public void mostrarVentana() {
        setVisible(true);
    }

    public void actualizarListaCanciones() {
        cargarDatosIniciales();
        lblEstado.setText("Estado: Lista actualizada");
    }
}