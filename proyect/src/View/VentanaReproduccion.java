package View;

import Control.ControladorAudio;
import Control.ControladorPrincipal;
import Modelo.Cancion;
import Modelo.Interprete;
import Modelo.Playlist;

import javax.sound.sampled.LineEvent;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VentanaReproduccion extends JFrame {
    private final ControladorPrincipal controlador;
    private JList<Object> listaPrincipal;
    private DefaultListModel<Object> modeloLista;
    private JButton btnReproducir, btnPausar, btnReanudar;
    private JLabel lblEstado;
    private final String modo; // "CANCION", "PLAYLIST", "INTERPRETE", "ALBUM"
    private boolean seleccionandoCategoria;
    private Playlist playlistSeleccionada;
    private String interpreteSeleccionado;
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
        setSize(400, 300);
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

        panelBotones.add(btnReproducir);
        panelBotones.add(btnPausar);
        panelBotones.add(btnReanudar);
        if (seleccionandoCategoria) {
            btnReproducir.setEnabled(false);
            btnPausar.setEnabled(false);
            btnReanudar.setEnabled(false);
        } else {
            btnReproducir.setEnabled(true);
            btnPausar.setEnabled(true);
            btnReanudar.setEnabled(true);
        }

        lblEstado = new JLabel("Estado: Detenido");
        panel.add(lblEstado, BorderLayout.NORTH);
        panel.add(panelBotones, BorderLayout.SOUTH);

        btnReproducir.addActionListener(e -> reproducir());
        btnPausar.addActionListener(e -> pausarCancion());
        btnReanudar.addActionListener(e -> reanudarCancion());
        listaPrincipal.addListSelectionListener(e -> {
            if (!seleccionandoCategoria && !e.getValueIsAdjusting()) {
                btnReproducir.setEnabled(true);
            } else if (seleccionandoCategoria && !e.getValueIsAdjusting()) {
                btnReproducir.setEnabled(true); // Habilitar en modos de categoría
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
                    System.out.println("Intérprete cargado: " + interprete.getNombre());
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
            interpreteSeleccionado = (String) seleccionado;
            System.out.println("Intérprete seleccionado: " + interpreteSeleccionado);
            for (Cancion cancion : controlador.getModelo().getCanciones()) {
                if (cancion.getInterpreteId().equals(interpreteSeleccionado)) {
                    cancionesAReproducir.add(cancion);
                    System.out.println("Canción añadida: " + cancion.getNombre());
                }
            }
            reproducirSiguiente();
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
                // Añadir LineListener para detectar el fin de la reproducción
                controlador.getControladorAudio().getClip().addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        if (indiceReproduccion < cancionesAReproducir.size()) {
                            indiceReproduccion++; // Pasar a la siguiente
                            reproducirSiguiente();
                        } else {
                            lblEstado.setText("Estado: Reproducción completa");
                        }
                    }
                });
                indiceReproduccion++; // Incrementar el índice después de configurar el listener
            } else {
                System.out.println("Error: Archivo no encontrado - " + archivoAudio.getAbsolutePath());
                lblEstado.setText("Estado: Archivo no encontrado para " + cancion.getNombre());
                indiceReproduccion++; // Pasar a la siguiente
                reproducirSiguiente();
            }
        }
    }

    private void pausarCancion() {
        controlador.getControladorAudio().pausar();
        lblEstado.setText("Estado: Pausado");
    }

    private void reanudarCancion() {
        controlador.getControladorAudio().reanudar();
        lblEstado.setText("Estado: Reproduciendo");
    }

    private void deshabilitarBotones() {
        btnReproducir.setEnabled(false);
        btnPausar.setEnabled(false);
        btnReanudar.setEnabled(false);
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