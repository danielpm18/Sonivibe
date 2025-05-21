package Control;

import Modelo.Interprete;
import Modelo.Cancion;
import Modelo.Playlist;
import Modelo.Usuario;
import View.VentanaReproduccion;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ControladorPrincipal implements ActionListener {
    public static final String LEER = "LEER";
    public static final String GUARDAR = "GUARDAR";
    public static final String CANCION = "CANCION";
    public static final String PLAYLIST = "PLAYLIST";
    public static final String INTERPRETE = "INTERPRETE";
    public static final String ALBUM = "ALBUM";
    public static final String RANDOM = "RANDOM";
    public static final String PAUSAR = "PAUSAR";
    public static final String REANUDAR = "REANUDAR";
    public static final String DATOS = "DATOS";
    public static final String CREAR_USUARIO = "CREAR_USUARIO";
    public static final String CREAR_PLAYLIST = "CREAR_PLAYLIST";

    private final GestorDatos modelo;
    private boolean usuarioLogueado;
    private String dniUsuarioActual;
    private Usuario usuarioActual;

    private Interprete[] interpretes;
    private String[] albumes;
    private Cancion[] canciones;
    private int[] reproduccionesPorInterprete;
    private int[] reproduccionesPorAlbum;
    private int[] reproduccionesPorCancion;

    private final ControladorAudio controladorAudio;
    private VentanaReproduccion ventanaReproduccion;

    public ControladorPrincipal(GestorDatos modelo) {
        this.modelo = modelo;
        this.usuarioLogueado = false;
        this.dniUsuarioActual = null;
        this.usuarioActual = null;
        this.interpretes = new Interprete[0];
        this.albumes = new String[0];
        this.canciones = new Cancion[0];
        this.reproduccionesPorInterprete = new int[0];
        this.reproduccionesPorAlbum = new int[0];
        this.reproduccionesPorCancion = new int[0];
        this.controladorAudio = new ControladorAudio();
    }

    public ControladorAudio getControladorAudio() {
        return controladorAudio;
    }

    public boolean validarUsuario(String dni, String contrasena) {
        modelo.leerUsuarios();
        for (Usuario usuario : modelo.getUsuarios()) {
            if (usuario.getDni().equals(dni) && usuario.validarContrasena(contrasena)) {
                usuarioActual = usuario;
                usuarioLogueado = true;
                dniUsuarioActual = dni;
                inicializarContadores();
                return true;
            }
        }
        return false;
    }

    private void inicializarContadores() {
        int numInterpretes = modelo.getInterpretes().size();
        int numCanciones = modelo.getCanciones().size();
        int numAlbumes = new ArrayList<String>() {{
            for (Cancion cancion : modelo.getCanciones()) {
                if (!contains(cancion.getAlbum())) {
                    add(cancion.getAlbum());
                }
            }
        }}.size();

        if (numInterpretes > 0) {
            interpretes = new Interprete[numInterpretes];
            reproduccionesPorInterprete = new int[numInterpretes];
            for (int i = 0; i < numInterpretes; i++) {
                interpretes[i] = modelo.getInterpretes().get(i);
                reproduccionesPorInterprete[i] = 0;
            }
        } else {
            interpretes = new Interprete[0];
            reproduccionesPorInterprete = new int[0];
        }

        if (numAlbumes > 0) {
            albumes = new String[numAlbumes];
            reproduccionesPorAlbum = new int[numAlbumes];
            ArrayList<String> albumesUnicos = new ArrayList<>();
            for (Cancion cancion : modelo.getCanciones()) {
                if (!albumesUnicos.contains(cancion.getAlbum())) {
                    albumesUnicos.add(cancion.getAlbum());
                }
            }
            for (int i = 0; i < numAlbumes; i++) {
                albumes[i] = albumesUnicos.get(i);
                reproduccionesPorAlbum[i] = 0;
            }
        } else {
            albumes = new String[0];
            reproduccionesPorAlbum = new int[0];
        }

        if (numCanciones > 0) {
            canciones = new Cancion[numCanciones];
            reproduccionesPorCancion = new int[numCanciones];
            for (int i = 0; i < numCanciones; i++) {
                canciones[i] = modelo.getCanciones().get(i);
                reproduccionesPorCancion[i] = 0;
            }
        } else {
            canciones = new Cancion[0];
            reproduccionesPorCancion = new int[0];
        }

        for (Cancion cancion : modelo.getCanciones()) {
            int interpreteIndex = obtenerIndiceInterprete(cancion.getInterpreteId());
            if (interpreteIndex != -1) {
                reproduccionesPorInterprete[interpreteIndex] += cancion.getReproducciones();
            }
            int albumIndex = obtenerIndiceAlbum(cancion.getAlbum());
            if (albumIndex != -1) {
                reproduccionesPorAlbum[albumIndex] += cancion.getReproducciones();
            }
            int cancionIndex = obtenerIndiceCancion(cancion.getId());
            if (cancionIndex != -1) {
                reproduccionesPorCancion[cancionIndex] += cancion.getReproducciones();
            }
        }
    }

    private void leerDatos() {
        modelo.leerUsuarios();
        modelo.leerInterpretes();
        modelo.leerCanciones();
        modelo.leerPlaylists();
        inicializarContadores();
    }

    private void guardarDatos() {
        modelo.guardarUsuarios();
        modelo.guardarInterpretes();
        modelo.guardarCanciones();
        modelo.guardarPlaylists();
    }

    private void reproducirPorCancion() {
        if (modelo.getCanciones().isEmpty()) {
            return;
        }
        if (ventanaReproduccion == null || !ventanaReproduccion.isVisible()) {
            ventanaReproduccion = new VentanaReproduccion(this, CANCION);
            ventanaReproduccion.mostrarVentana();
        } else {
            ventanaReproduccion.actualizarListaCanciones();
            ventanaReproduccion.toFront();
        }
    }

    private void reproducirPorPlaylist() {
        if (modelo.getPlaylists().isEmpty()) {
            return;
        }
        if (ventanaReproduccion == null || !ventanaReproduccion.isVisible()) {
            ventanaReproduccion = new VentanaReproduccion(this, PLAYLIST);
            ventanaReproduccion.mostrarVentana();
        } else {
            ventanaReproduccion.actualizarListaCanciones();
            ventanaReproduccion.toFront();
        }
    }

    private void reproducirPorInterprete() {
        if (modelo.getInterpretes().isEmpty()) {
            return;
        }
        if (ventanaReproduccion == null || !ventanaReproduccion.isVisible()) {
            ventanaReproduccion = new VentanaReproduccion(this, INTERPRETE);
            ventanaReproduccion.mostrarVentana();
        } else {
            ventanaReproduccion.actualizarListaCanciones();
            ventanaReproduccion.toFront();
        }
    }

    private void reproducirPorAlbum() {
        if (modelo.getCanciones().isEmpty()) {
            return;
        }
        if (ventanaReproduccion == null || !ventanaReproduccion.isVisible()) {
            ventanaReproduccion = new VentanaReproduccion(this, ALBUM);
            ventanaReproduccion.mostrarVentana();
        } else {
            ventanaReproduccion.actualizarListaCanciones();
            ventanaReproduccion.toFront();
        }
    }

    private void reproducirAleatorio() {
        if (modelo.getCanciones().isEmpty()) {
            return;
        }
        Random random = new Random();
        int indiceAleatorio = random.nextInt(modelo.getCanciones().size());
        Cancion cancion = modelo.getCanciones().get(indiceAleatorio);
        cancion.incrementarReproducciones();
        int interpreteIndex = obtenerIndiceInterprete(cancion.getInterpreteId());
        if (interpreteIndex != -1) {
            reproduccionesPorInterprete[interpreteIndex]++;
        }
        int albumIndex = obtenerIndiceAlbum(cancion.getAlbum());
        if (albumIndex != -1) {
            reproduccionesPorAlbum[albumIndex]++;
        }
        int cancionIndex = obtenerIndiceCancion(cancion.getId());
        if (cancionIndex != -1) {
            reproduccionesPorCancion[cancionIndex]++;
        }
        String idNumerico = cancion.getId().replace("CAN", "");
        File archivoAudio = new File("proyect/src/Data/Songs/" + idNumerico + ".wav");
        if (archivoAudio.exists()) {
            controladorAudio.reproducir(archivoAudio);

        }
        modelo.guardarCanciones();
    }

    private int obtenerIndiceInterprete(String idInterprete) {
        for (int i = 0; i < interpretes.length; i++) {
            if (interpretes[i].getId().equals(idInterprete)) {
                return i;
            }
        }
        return -1;
    }

    private int obtenerIndiceAlbum(String album) {
        for (int i = 0; i < albumes.length; i++) {
            if (albumes[i].equals(album)) {
                return i;
            }
        }
        return -1;
    }

    private int obtenerIndiceCancion(String idCancion) {
        for (int i = 0; i < canciones.length; i++) {
            if (canciones[i].getId().equals(idCancion)) {
                return i;
            }
        }
        return -1;
    }

    public GestorDatos getModelo() {
        return modelo;
    }

    // Metodo para crear nuevo usuario
    public boolean crearUsuario(String dni, String nombre, String contrasena) {
        if (dni == null || nombre == null || contrasena == null || dni.trim().isEmpty() || nombre.trim().isEmpty() || contrasena.trim().isEmpty()) {
            return false;
        }
        // Verificar si el DNI ya existe
        modelo.leerUsuarios();
        for (Usuario usuario : modelo.getUsuarios()) {
            if (usuario.getDni().equals(dni)) {
                return false;
            }
        }
        Usuario nuevoUsuario = new Usuario(dni, nombre, contrasena);
        modelo.getUsuarios().add(nuevoUsuario);
        try {
            modelo.guardarUsuarios();
            return true;
        } catch (Exception e) {
            modelo.getUsuarios().remove(nuevoUsuario);
            return false;
        }
    }

    // Metodo para crear una nueva playlist
    public boolean crearPlaylist(String nombre, java.util.List<String> cancionesIds) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        if (cancionesIds == null || cancionesIds.isEmpty()) {
            return false;
        }

        // Verificar si las canciones existen
        modelo.leerCanciones();
        for (String cancionId : cancionesIds) {
            if (!cancionId.startsWith("CAN")) {
                JOptionPane.showMessageDialog(null, "El ID " + cancionId + " no es válido. Debe empezar con 'CAN'.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            boolean existe = false;
            for (Cancion c : modelo.getCanciones()) {
                if (c.getId().equals(cancionId)) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                JOptionPane.showMessageDialog(null, "Canción con ID " + cancionId + " no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        String idPlaylist = "PL" + (modelo.getPlaylists().size() + 1);
        Playlist nuevaPlaylist = new Playlist(idPlaylist, nombre);
        for (String cancionId : cancionesIds) {
            nuevaPlaylist.agregarCancion(cancionId);
        }
        modelo.getPlaylists().add(nuevaPlaylist);
        try {
            modelo.guardarPlaylists();
            return true;
        } catch (Exception e) {
            modelo.getPlaylists().remove(nuevaPlaylist);
            return false;
        }
    }

    // Metodo auxiliar para abrir interfaz de creación de usuario
    public void crearUsuarioInterfaz() {
        JTextField dniField = new JTextField(10);
        JTextField nombreField = new JTextField(20);
        JTextField contrasenaField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("DNI:"));
        panel.add(dniField);
        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Contraseña:"));
        panel.add(contrasenaField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Crear Nuevo Usuario",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String dni = dniField.getText().trim();
            String nombre = nombreField.getText().trim();
            String contrasena = contrasenaField.getText().trim();

            if (crearUsuario(dni, nombre, contrasena)) {
                JOptionPane.showMessageDialog(null, "Usuario creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al crear el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Metodo auxiliar para abrir interfaz de creación de playlist
    public void crearPlaylistInterfaz() {
        String nombre = JOptionPane.showInputDialog(null, "Ingrese el nombre de la nueva Playlist:", "Nueva Playlist", JOptionPane.QUESTION_MESSAGE);
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El nombre de la playlist es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.util.List<String> cancionesIds = new ArrayList<>();
        while (true) {
            String cancionId = JOptionPane.showInputDialog(null, "Ingrese el ID de una canción (o 'fin' para terminar):", "Añadir Canción", JOptionPane.QUESTION_MESSAGE);
            if (cancionId == null) {
                JOptionPane.showMessageDialog(null, "Creación de playlist cancelada.", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            cancionId = cancionId.trim().toUpperCase();
            if (cancionId.equals("FIN")) {
                break;
            }
            cancionesIds.add(cancionId);
        }

        if (cancionesIds.isEmpty()) {
            JOptionPane.showMessageDialog(null, "La playlist debe tener al menos una canción.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (crearPlaylist(nombre, cancionesIds)) {
            JOptionPane.showMessageDialog(null, "Playlist creada exitosamente: " + nombre, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Error al crear la playlist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        switch (comando) {
            case LEER:
                leerDatos();
                break;
            case GUARDAR:
                guardarDatos();
                break;
            case CANCION:
                reproducirPorCancion();
                break;
            case PLAYLIST:
                reproducirPorPlaylist();
                break;
            case INTERPRETE:
                reproducirPorInterprete();
                break;
            case ALBUM:
                reproducirPorAlbum();
                break;
            case RANDOM:
                reproducirAleatorio();
                break;
            case PAUSAR:
                controladorAudio.pausar();
                break;
            case REANUDAR:
                controladorAudio.reanudar();
                break;
            case CREAR_USUARIO:
                crearUsuarioInterfaz();
                break;
            case CREAR_PLAYLIST:
                crearPlaylistInterfaz();
                break;
            default:
        }
    }

    public Interprete[] getInterpretes() {
        return interpretes;
    }

    public String[] getAlbumes() {
        return albumes;
    }

    public Cancion[] getCanciones() {
        return canciones;
    }

    public int[] getReproduccionesPorInterprete() {
        return reproduccionesPorInterprete;
    }

    public int[] getReproduccionesPorAlbum() {
        return reproduccionesPorAlbum;
    }

    public int[] getReproduccionesPorCancion() {
        return reproduccionesPorCancion;
    }

}