package Control;

import Modelo.Interprete;
import Modelo.Cancion;
import Modelo.Playlist;
import Modelo.Usuario;
import View.VentanaReproduccion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
            System.out.println("Warning: No se encontraron intérpretes para inicializar.");
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
            System.out.println("Warning: No se encontraron álbumes para inicializar.");
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
            System.out.println("Warning: No se encontraron canciones para inicializar.");
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
        System.out.println("Datos cargados correctamente.");
    }

    private void guardarDatos() {
        modelo.guardarUsuarios();
        modelo.guardarInterpretes();
        modelo.guardarCanciones();
        modelo.guardarPlaylists();
        System.out.println("Datos guardados correctamente en los archivos.");
    }

    private void reproducirPorCancion() {
        if (modelo.getCanciones().isEmpty()) {
            System.out.println("No hay canciones disponibles. Por favor, cargue los datos primero.");
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
            System.out.println("No hay playlists disponibles. Por favor, cargue los datos o cree una playlist.");
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
            System.out.println("No hay intérpretes disponibles. Por favor, cargue los datos primero.");
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
            System.out.println("No hay canciones disponibles. Por favor, cargue los datos primero.");
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
            System.out.println("No hay canciones disponibles. Por favor, cargue los datos primero.");
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
        File archivoAudio = new File("proyect/src/Data/" + idNumerico + ".wav");
        if (archivoAudio.exists()) {
            controladorAudio.reproducir(archivoAudio);
            System.out.println("Reproduciendo aleatoriamente: " + cancion);
        } else {
            System.out.println("Error: El archivo de audio para " + cancion.getNombre() + " no se encontró en Data/.");
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
            default:
                System.out.println("Comando no reconocido: " + comando);
        }
    }
}