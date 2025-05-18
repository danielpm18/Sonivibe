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
    private final String modo; // "CANCION", "PLAYLIST", "INTERPRETE", "ALBUM"
    private boolean seleccionandoCategoria;
    private Playlist playlistSeleccionada;
    private Interprete interpreteSeleccionado;
    private String albumSeleccionado;
    private List<Cancion> cancionesAReproducir;
    private int indiceReproduccion; // Índice para controlar el orden de reproducción

    public VentanaReproduccion(ControladorPrincipal controlador, String modo) {
        this.controlador = controlador;
        this.modo = modo;
        this.seleccionandoCategoria = modo.equals(ControladorPrincipal.PLAYLIST) ||
                modo.equals(ControladorPrincipal.INTERPRETE) ||
                modo.equals(ControladorPrincipal.ALBUM);
        this.cancionesAReproducir = new ArrayList<>();
        this.indiceReproduccion = 0; // Inicialización del índice
        inicializarComponentes();
        cargarDatosIniciales();
    }

    private void inicializarComponentes() {
        setTitle("Reproducción - Sonivibe");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500); // Aumentado para incluir la imagen
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
        btnSiguiente = new JButton("Siguiente");
        btnAnterior = new JButton("Anterior");
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
                    long microseconds = (long) progressBar.getValue() * 1000L; // Convertir a microsegundos
                    controlador.getControladorAudio().getClip().setMicrosecondPosition(microseconds);
                    updateProgressBar(); // Actualizar la etiqueta de tiempo después de mover
                }
            }
        });

        // Etiqueta de tiempo
        lblTiempo = new JLabel("0:00 / 0:00", SwingConstants.CENTER);

        // Imagen de la canción
        lblImagenCancion = new JLabel();
        lblImagenCancion.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagenCancion.setPreferredSize(new Dimension(100, 100)); // Tamaño fijo para la imagen

        // Timer para actualizar la barra y el tiempo
        updateTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProgressBar();
            }
        });
        updateTimer.setRepeats(true);

        // Deshabilitar inicialmente todos excepto Reproducir en modo categoría
        btnReproducir.setEnabled(true);
        btnPausar.setEnabled(false);
        btnReanudar.setEnabled(false);
        btnSiguiente.setEnabled(false);
        btnAnterior.setEnabled(false);
        btnAleatorio.setEnabled(false);

        lblEstado = new JLabel("Estado: Detenido");
        panel.add(lblEstado, BorderLayout.NORTH);

        // Panel para la barra de progreso, tiempo e imagen
        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(lblTiempo, BorderLayout.SOUTH);
        southPanel.add(progressPanel, BorderLayout.NORTH);
        southPanel.add(panelBotones, BorderLayout.CENTER);
        southPanel.add(lblImagenCancion, BorderLayout.SOUTH);
        panel.add(southPanel, BorderLayout.SOUTH);

        btnReproducir.addActionListener(e -> reproducir());
        btnPausar.addActionListener(e -> pausarCancion());
        btnReanudar.addActionListener(e -> reanudarCancion());
        btnSiguiente.addActionListener(e -> reproducirSiguienteManual());
        btnAnterior.addActionListener(e -> reproducirAnteriorManual());
        btnAleatorio.addActionListener(e -> reproducirAleatorio());
        listaPrincipal.addListSelectionListener(e -> {
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
        });

        add(panel);
    }

    private void cargarDatosIniciales() {
        modeloLista.clear();
        System.out.println("Cargando datos iniciales para modo: " + modo);
        switch (modo) {
            case ControladorPrincipal.CANCION:
                seleccionandoCategoria = false;
                for (Cancion cancion : controlador.getModelo().getCanciones()) {
                    modeloLista.addElement(cancion);
                    System.out.println("Canción cargada: " + cancion.getNombre());
                }
                lblEstado.setText("Estado: Seleccione una canción");
                break;
            case ControladorPrincipal.PLAYLIST:
                for (Playlist playlist : controlador.getModelo().getPlaylists()) {
                    modeloLista.addElement(playlist);
                    System.out.println("Playlist cargada: " + playlist.getNombre());
                }
                lblEstado.setText("Estado: Seleccione una playlist");
                break;
            case ControladorPrincipal.INTERPRETE:
                for (Interprete interprete : controlador.getModelo().getInterpretes()) {
                    modeloLista.addElement(interprete);
                    System.out.println("Intérprete cargado: " + interprete.getNombre() + " (ID: " + interprete.getId() + ")");
                }
                lblEstado.setText("Estado: Seleccione un intérprete");
                break;
            case ControladorPrincipal.ALBUM:
                List<String> albumes = new ArrayList<>();
                for (Cancion cancion : controlador.getModelo().getCanciones()) {
                    if (!albumes.contains(cancion.getAlbum())) {
                        albumes.add(cancion.getAlbum());
                        modeloLista.addElement(cancion.getAlbum());
                        System.out.println("Álbum cargado: " + cancion.getAlbum());
                    }
                }
                lblEstado.setText("Estado: Seleccione un álbum");
                break;
        }
        if (modeloLista.isEmpty()) {
            lblEstado.setText("Estado: No hay datos disponibles");
            System.out.println("No se encontraron datos para el modo: " + modo);
            deshabilitarBotones();
        }
    }

    private void reproducir() {
        System.out.println("Iniciando reproducción en modo: " + modo);
        Object seleccionado = listaPrincipal.getSelectedValue();
        if (seleccionado == null) {
            lblEstado.setText("Estado: Seleccione una opción primero");
            System.out.println("No se seleccionó ninguna opción.");
            return;
        }

        cancionesAReproducir.clear();
        indiceReproduccion = 0; // Reiniciar índice

        if (modo.equals(ControladorPrincipal.CANCION)) {
            if (seleccionado instanceof Cancion) {
                cancionesAReproducir.add((Cancion) seleccionado);
                reproducirSiguiente();
            }
        } else if (modo.equals(ControladorPrincipal.PLAYLIST)) {
            playlistSeleccionada = (Playlist) seleccionado;
            System.out.println("Playlist seleccionada: " + playlistSeleccionada.getNombre());
            List<String> cancionesIds = playlistSeleccionada.getCanciones();
            if (cancionesIds != null && !cancionesIds.isEmpty()) {
                for (String cancionId : cancionesIds) {
                    for (Cancion cancion : controlador.getModelo().getCanciones()) {
                        if (cancion.getId().equals(cancionId)) {
                            cancionesAReproducir.add(cancion);
                            System.out.println("Canción añadida: " + cancion.getNombre());
                        }
                    }
                }
                reproducirSiguiente();
            } else {
                lblEstado.setText("Estado: No hay canciones en esta playlist");
                System.out.println("No hay canciones en la playlist: " + playlistSeleccionada.getNombre());
            }
        } else if (modo.equals(ControladorPrincipal.INTERPRETE)) {
            if (seleccionado instanceof Interprete) {
                interpreteSeleccionado = (Interprete) seleccionado;
                System.out.println("Intérprete seleccionado: " + interpreteSeleccionado.getNombre() + " (ID: " + interpreteSeleccionado.getId() + ")");
                String interpreteId = interpreteSeleccionado.getId();
                if (interpreteId == null || interpreteId.isEmpty()) {
                    System.out.println("Error: ID del intérprete es nulo o vacío.");
                    lblEstado.setText("Estado: ID del intérprete no válido");
                    return;
                }
                for (Cancion cancion : controlador.getModelo().getCanciones()) {
                    System.out.println("Comprobando canción: " + cancion.getNombre() + " (InterpreteId: " + cancion.getInterpreteId() + ")");
                    if (cancion.getInterpreteId() != null && cancion.getInterpreteId().equals(interpreteId)) {
                        cancionesAReproducir.add(cancion);
                        System.out.println("Canción añadida: " + cancion.getNombre());
                    }
                }
                if (cancionesAReproducir.isEmpty()) {
                    System.out.println("No se encontraron canciones para el intérprete con ID: " + interpreteId);
                }
                reproducirSiguiente();
            } else {
                lblEstado.setText("Estado: Selección inválida para intérprete");
                System.out.println("Objeto seleccionado no es un Interprete.");
            }
        } else if (modo.equals(ControladorPrincipal.ALBUM)) {
            albumSeleccionado = (String) seleccionado;
            System.out.println("Álbum seleccionado: " + albumSeleccionado);
            for (Cancion cancion : controlador.getModelo().getCanciones()) {
                if (cancion.getAlbum().equals(albumSeleccionado)) {
                    cancionesAReproducir.add(cancion);
                    System.out.println("Canción añadida: " + cancion.getNombre());
                }
            }
            reproducirSiguiente();
        }

        if (cancionesAReproducir.isEmpty()) {
            lblEstado.setText("Estado: No hay canciones para reproducir");
            System.out.println("No se encontraron canciones.");
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
            System.out.println("Reproduciendo: " + cancion.getNombre() + " (ID: " + cancion.getId() + ")");
            String idNumerico = cancion.getId().replace("CAN", "");
            File archivoAudio = new File("proyect/src/Data/" + idNumerico + ".wav");
            if (archivoAudio.exists()) {
                System.out.println("Archivo encontrado: " + archivoAudio.getAbsolutePath());
                controlador.getControladorAudio().reproducir(archivoAudio);
                lblEstado.setText("Estado: Reproduciendo " + cancion.getNombre());
                cancion.incrementarReproducciones();
                controlador.getModelo().guardarCanciones();
                cargarImagenCancion(cancion.getAlbum());
                // Añadir LineListener para detectar el fin de la reproducción
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
                indiceReproduccion++; // Incrementar el índice después de configurar el listener
                habilitarBotonesReproduccion();
                updateProgressBar(); // Actualizar la duración inicial
            } else {
                System.out.println("Error: Archivo no encontrado - " + archivoAudio.getAbsolutePath());
                lblEstado.setText("Estado: Archivo no encontrado para " + cancion.getNombre());
                indiceReproduccion++; // Pasar a la siguiente
                reproducirSiguiente();
            }
        }
    }

    private void reproducirSiguienteManual() {
        if (indiceReproduccion < cancionesAReproducir.size() - 1) {
            indiceReproduccion++;
            Cancion cancion = cancionesAReproducir.get(indiceReproduccion);
            String idNumerico = cancion.getId().replace("CAN", "");
            File archivoAudio = new File("proyect/src/Data/" + idNumerico + ".wav");
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
                System.out.println("Error: Archivo no encontrado - " + archivoAudio.getAbsolutePath());
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
            File archivoAudio = new File("proyect/src/Data/" + idNumerico + ".wav");
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
                System.out.println("Error: Archivo no encontrado - " + archivoAudio.getAbsolutePath());
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
            int progress = (int) ((microsecondPosition * 100) / microsecondLength); // Porcentaje
            progressBar.setValue(progress);

            // Calcular tiempo transcurrido y duración total en segundos
            long secondsPosition = microsecondPosition / 1000000;
            long secondsLength = microsecondLength / 1000000;

            // Convertir a formato MM:SS
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
        // Reemplazar caracteres no válidos en el nombre del álbum para usarlo como nombre de archivo
        String nombreArchivo = album.replaceAll("[^a-zA-Z0-9.-]", "_");
        String imagePath = "proyect/src/Data/Imagenes/" + nombreArchivo + ".jpg"; // Ajusta la extensión si usas .png
        File imagenArchivo = new File(imagePath);
        if (imagenArchivo.exists()) {
            ImageIcon imagenOriginal = new ImageIcon(imagePath);
            Image imagenEscalada = imagenOriginal.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon imagenFinal = new ImageIcon(imagenEscalada);
            lblImagenCancion.setIcon(imagenFinal);
            System.out.println("Imagen cargada para el álbum: " + album);
        } else {
            System.out.println("Imagen no encontrada para el álbum: " + album + " en " + imagePath);
            lblImagenCancion.setIcon(null); // Si no hay imagen, limpiar el JLabel
        }
    }

    private void habilitarBotonesReproduccion() {
        btnPausar.setEnabled(true);
        btnReanudar.setEnabled(true);
        btnSiguiente.setEnabled(indiceReproduccion < cancionesAReproducir.size() - 1);
        btnAnterior.setEnabled(indiceReproduccion > 0);
        btnAleatorio.setEnabled(!cancionesAReproducir.isEmpty());
        btnReproducir.setEnabled(false); // Deshabilitar Reproducir una vez iniciado
        progressBar.setEnabled(true);
    }

    private void deshabilitarBotonesReproduccion() {
        btnPausar.setEnabled(false);
        btnReanudar.setEnabled(false);
        btnSiguiente.setEnabled(false);
        btnAnterior.setEnabled(false);
        btnAleatorio.setEnabled(false);
        btnReproducir.setEnabled(true); // Habilitar Reproducir para reiniciar
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
        System.out.println("Lista actualizada para modo: " + modo);
    }
}