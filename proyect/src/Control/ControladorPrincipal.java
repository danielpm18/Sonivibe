package Control;

import Modelo.Usuario;
import Modelo.Interprete;
import Modelo.Cancion;
import Modelo.Playlist;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class ControladorPrincipal {
    private final GestorDatos modelo;
    private final Scanner scanner;
    private boolean usuarioLogueado;
    private String dniUsuarioActual;

    // Contadores para estadísticas por usuario
    private int[] reproduccionesPorInterprete;
    private int[] reproduccionesPorAlbum;
    private int[] reproduccionesPorCancion;

    // Controlador de audio con JLayer
    private final ControladorAudio controladorAudio;

    public ControladorPrincipal(GestorDatos modelo) {
        this.modelo = modelo;
        this.scanner = new Scanner(System.in);
        this.usuarioLogueado = false;
        this.dniUsuarioActual = null;
        this.reproduccionesPorInterprete = new int[100]; // Tamaño inicial arbitrario, ajustable
        this.reproduccionesPorAlbum = new int[100];
        this.reproduccionesPorCancion = new int[100];
        this.controladorAudio = new ControladorAudio();
    }

    public void iniciar() {
        usuarioLogueado = false;
        while (!usuarioLogueado) {
            System.out.println("=== Sistema de Login ===");
            System.out.print("Ingrese su DNI: ");
            String dni = scanner.nextLine();
            System.out.print("Ingrese su contrasena: ");
            String contrasena = scanner.nextLine();

            if (validarUsuario(dni, contrasena)) {
                usuarioLogueado = true;
                dniUsuarioActual = dni;
                inicializarContadores();
                mostrarMenu();
            } else {
                System.out.print("Desea intentar de nuevo? (s/n): ");
                if (!scanner.nextLine().equalsIgnoreCase("s")) {
                    System.out.println("Saliendo del sistema.");
                    return;
                }
            }
        }
    }

    private boolean validarUsuario(String dni, String contrasena) {
        modelo.leerUsuarios();
        for (Usuario usuario : modelo.getUsuarios()) {
            if (usuario.getDni().equals(dni) && usuario.validarContrasena(contrasena)) {
                System.out.println("Login exitoso. Bienvenido, " + usuario.getNombre() + "!");
                return true;
            }
        }
        System.out.println("DNI o contrasena incorrectos.");
        return false;
    }

    private void inicializarContadores() {
        for (int i = 0; i < reproduccionesPorInterprete.length; i++) {
            reproduccionesPorInterprete[i] = 0;
            reproduccionesPorAlbum[i] = 0;
            reproduccionesPorCancion[i] = 0;
        }
    }

    private void mostrarMenu() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1. Datos");
            System.out.println("2. Usuarios y Playlists");
            System.out.println("3. Reproducir");
            System.out.println("4. Estadisticas");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine();
            switch (opcion) {
                case "1":
                    menuDatos();
                    break;
                case "2":
                    menuUsuariosPlaylists();
                    break;
                case "3":
                    menuReproducir();
                    break;
                case "4":
                    menuEstadisticas();
                    break;
                case "5":
                    salir = true;
                    System.out.println("Saliendo del sistema.");
                    break;
                default:
                    System.out.println("Opcion no valida. Intente de nuevo.");
            }
        }
    }

    private void menuDatos() {
        boolean regresar = false;
        while (!regresar) {
            System.out.println("\n--- Menu Datos ---");
            System.out.println("1. Leer Datos");
            System.out.println("2. Guardar");
            System.out.println("3. Regresar");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine();
            switch (opcion) {
                case "1":
                    leerDatos();
                    break;
                case "2":
                    guardarDatos();
                    break;
                case "3":
                    regresar = true;
                    break;
                default:
                    System.out.println("Opcion no valida. Intente de nuevo.");
            }
        }
    }

    private void guardarDatos() {
        modelo.guardarUsuarios();
        modelo.guardarInterpretes();
        modelo.guardarCanciones();
        modelo.guardarPlaylists();
        System.out.println("Datos guardados correctamente en los archivos.");
    }

    private void menuUsuariosPlaylists() {
        boolean regresar = false;
        while (!regresar) {
            System.out.println("\n--- Menu Usuarios y Playlists ---");
            System.out.println("1. Registrar Usuario");
            System.out.println("2. Crear Playlist");
            System.out.println("3. Regresar");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine();
            switch (opcion) {
                case "1":
                    registrarUsuario();
                    break;
                case "2":
                    crearPlaylist();
                    break;
                case "3":
                    regresar = true;
                    break;
                default:
                    System.out.println("Opcion no valida. Intente de nuevo.");
            }
        }
    }

    private void menuReproducir() {
        boolean regresar = false;
        while (!regresar) {
            System.out.println("\n--- Menu Reproducir ---");
            System.out.println("1. Por Cancion");
            System.out.println("2. Por Interprete");
            System.out.println("3. Por Album");
            System.out.println("4. Por Playlist");
            System.out.println("5. Regresar");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine();
            switch (opcion) {
                case "1":
                    reproducirPorCancion();
                    break;
                case "2":
                    reproducirPorInterprete();
                    break;
                case "3":
                    reproducirPorAlbum();
                    break;
                case "4":
                    reproducirPorPlaylist();
                    break;
                case "5":
                    regresar = true;
                    break;
                default:
                    System.out.println("Opcion no valida. Intente de nuevo.");
            }
        }
    }

    private void menuEstadisticas() {
        boolean regresar = false;
        while (!regresar) {
            System.out.println("\n--- Menu Estadisticas ---");
            System.out.println("1. Estadisticas por Usuario");
            System.out.println("2. Estadisticas por Album");
            System.out.println("3. Estadisticas por Canciones");
            System.out.println("4. Regresar");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine();
            switch (opcion) {
                case "1":
                    estadisticasPorUsuario();
                    break;
                case "2":
                    estadisticasPorAlbum();
                    break;
                case "3":
                    estadisticasPorCanciones();
                    break;
                case "4":
                    regresar = true;
                    break;
                default:
                    System.out.println("Opcion no valida. Intente de nuevo.");
            }
        }
    }

    private void leerDatos() {
        modelo.leerUsuarios();
        modelo.leerInterpretes();
        modelo.leerCanciones();
        modelo.leerPlaylists();
        System.out.println("Datos cargados correctamente.");
    }

    private void reproducirPorCancion() {
        if (modelo.getCanciones().isEmpty()) {
            System.out.println("No hay canciones disponibles. Por favor, cargue los datos primero.");
            return;
        }

        System.out.println("\n--- Lista de Canciones ---");
        for (int i = 0; i < modelo.getCanciones().size(); i++) {
            System.out.println((i + 1) + ". " + modelo.getCanciones().get(i));
        }
        System.out.print("Seleccione una cancion para reproducir (1-" + modelo.getCanciones().size() + "): ");
        try {
            int seleccion = Integer.parseInt(scanner.nextLine()) - 1;
            if (seleccion >= 0 && seleccion < modelo.getCanciones().size()) {
                Cancion cancion = modelo.getCanciones().get(seleccion);
                cancion.incrementarReproducciones();
                // Actualizar contadores por usuario
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
                // Reproducir el archivo MP3
                String idNumerico = cancion.getId().replace("CAN", ""); // Ajusta según el formato del ID
                File archivoMP3 = new File("proyect/src/Data/" + idNumerico + ".mp3");
                if (archivoMP3.exists()) {
                    controladorAudio.reproducir(archivoMP3);
                    System.out.println("Reproduciendo: " + cancion);
                } else {
                    System.out.println("Error: El archivo MP3 para " + cancion.getNombre() + " no se encontró en Data/.");
                }
                modelo.guardarCanciones();
            } else {
                System.out.println("Seleccion no valida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un numero valido.");
        }
    }

    private void reproducirPorInterprete() {
        if (modelo.getInterpretes().isEmpty()) {
            System.out.println("No hay interpretes disponibles. Por favor, cargue los datos primero.");
            return;
        }

        System.out.println("\n--- Lista de Interpretes ---");
        for (int i = 0; i < modelo.getInterpretes().size(); i++) {
            System.out.println((i + 1) + ". " + modelo.getInterpretes().get(i).getNombre());
        }
        System.out.print("Seleccione un interprete para reproducir (1-" + modelo.getInterpretes().size() + "): ");
        try {
            int seleccion = Integer.parseInt(scanner.nextLine()) - 1;
            if (seleccion >= 0 && seleccion < modelo.getInterpretes().size()) {
                Interprete interprete = modelo.getInterpretes().get(seleccion);
                ArrayList<Cancion> cancionesInterprete = new ArrayList<>();
                for (Cancion cancion : modelo.getCanciones()) {
                    if (cancion.getInterpreteId().equals(interprete.getId())) {
                        cancionesInterprete.add(cancion);
                    }
                }

                if (cancionesInterprete.isEmpty()) {
                    System.out.println("No hay canciones disponibles para " + interprete.getNombre() + ".");
                    return;
                }

                System.out.println("Reproduciendo canciones de " + interprete.getNombre() + ":");
                for (Cancion cancion : cancionesInterprete) {
                    cancion.incrementarReproducciones();
                    // Actualizar contadores por usuario
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
                    // Reproducir el archivo MP3
                    String idNumerico = cancion.getId().replace("CAN", "");
                    File archivoMP3 = new File("proyect/src/Data/" + idNumerico + ".mp3");
                    if (archivoMP3.exists()) {
                        controladorAudio.reproducir(archivoMP3);
                        System.out.println("Reproduciendo: " + cancion);
                    } else {
                        System.out.println("Error: El archivo MP3 para " + cancion.getNombre() + " no se encontró.");
                    }
                }
                modelo.guardarCanciones();
            } else {
                System.out.println("Seleccion no valida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un numero valido.");
        }
    }

    private void reproducirPorAlbum() {
        if (modelo.getCanciones().isEmpty()) {
            System.out.println("No hay canciones disponibles. Por favor, cargue los datos primero.");
            return;
        }

        System.out.println("\n--- Lista de Álbumes ---");
        ArrayList<String> albumesUnicos = new ArrayList<>();
        for (Cancion cancion : modelo.getCanciones()) {
            String album = cancion.getAlbum();
            if (!albumesUnicos.contains(album)) {
                albumesUnicos.add(album);
            }
        }
        if (albumesUnicos.isEmpty()) {
            System.out.println("No hay álbumes disponibles.");
            return;
        }
        for (int i = 0; i < albumesUnicos.size(); i++) {
            System.out.println((i + 1) + ". " + albumesUnicos.get(i));
        }
        System.out.print("Seleccione un álbum para reproducir (1-" + albumesUnicos.size() + "): ");
        try {
            int seleccion = Integer.parseInt(scanner.nextLine()) - 1;
            if (seleccion >= 0 && seleccion < albumesUnicos.size()) {
                String albumSeleccionado = albumesUnicos.get(seleccion);
                ArrayList<Cancion> cancionesAlbum = new ArrayList<>();
                for (Cancion cancion : modelo.getCanciones()) {
                    if (cancion.getAlbum().equals(albumSeleccionado)) {
                        cancionesAlbum.add(cancion);
                    }
                }

                if (cancionesAlbum.isEmpty()) {
                    System.out.println("No hay canciones disponibles para el álbum " + albumSeleccionado + ".");
                    return;
                }

                System.out.println("Reproduciendo canciones del álbum " + albumSeleccionado + ":");
                for (Cancion cancion : cancionesAlbum) {
                    cancion.incrementarReproducciones();
                    // Actualizar contadores por usuario
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
                    // Reproducir el archivo MP3
                    String idNumerico = cancion.getId().replace("CAN", "");
                    File archivoMP3 = new File("proyect/src/Data/" + idNumerico + ".mp3");
                    if (archivoMP3.exists()) {
                        controladorAudio.reproducir(archivoMP3);
                        System.out.println("Reproduciendo: " + cancion);
                    } else {
                        System.out.println("Error: El archivo MP3 para " + cancion.getNombre() + " no se encontró.");
                    }
                }
                modelo.guardarCanciones();
            } else {
                System.out.println("Seleccion no valida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un numero valido.");
        }
    }

    private void reproducirPorPlaylist() {
        if (modelo.getPlaylists().isEmpty()) {
            System.out.println("No hay playlists disponibles. Por favor, cargue los datos o cree una playlist.");
            return;
        }

        System.out.println("\n--- Lista de Playlists ---");
        for (int i = 0; i < modelo.getPlaylists().size(); i++) {
            System.out.println((i + 1) + ". " + modelo.getPlaylists().get(i));
        }
        System.out.print("Seleccione una playlist para reproducir (1-" + modelo.getPlaylists().size() + "): ");
        try {
            int seleccion = Integer.parseInt(scanner.nextLine()) - 1;
            if (seleccion >= 0 && seleccion < modelo.getPlaylists().size()) {
                Playlist playlist = modelo.getPlaylists().get(seleccion);
                System.out.println("Reproduciendo playlist: " + playlist.getNombre());
                if (playlist.getCancionesIds().isEmpty()) {
                    System.out.println("La playlist está vacía. No hay canciones para reproducir.");
                    return;
                }
                for (String cancionId : playlist.getCancionesIds()) {
                    Cancion cancion = buscarCancionPorId(cancionId);
                    if (cancion != null) {
                        System.out.println("Canción encontrada: " + cancion); // Depuración
                        cancion.incrementarReproducciones();
                        // Actualizar contadores por usuario
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
                        // Reproducir el archivo MP3
                        String idNumerico = cancion.getId().replace("CAN", "");
                        File archivoMP3 = new File("proyect/src/Data/" + idNumerico + ".mp3");
                        if (archivoMP3.exists()) {
                            controladorAudio.reproducir(archivoMP3);
                            System.out.println("Reproduciendo: " + cancion);
                        } else {
                            System.out.println("Error: El archivo MP3 para " + cancion.getNombre() + " no se encontró en Data/.");
                        }
                    } else {
                        System.out.println("Cancion con ID " + cancionId + " no encontrada en la lista de canciones.");
                    }
                }
                modelo.guardarCanciones();
            } else {
                System.out.println("Seleccion no valida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un numero valido. Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    private Cancion buscarCancionPorId(String id) {
        for (Cancion cancion : modelo.getCanciones()) {
            if (cancion.getId().equals(id)) {
                return cancion;
            }
        }
        return null;
    }

    private int obtenerIndiceInterprete(String idInterprete) {
        for (int i = 0; i < modelo.getInterpretes().size(); i++) {
            if (modelo.getInterpretes().get(i).getId().equals(idInterprete)) {
                return i;
            }
        }
        return -1;
    }

    private int obtenerIndiceAlbum(String album) {
        for (int i = 0; i < modelo.getCanciones().size(); i++) {
            if (modelo.getCanciones().get(i).getAlbum().equals(album)) {
                return i % reproduccionesPorAlbum.length; // Simplificación, podría colisionar
            }
        }
        return -1;
    }

    private int obtenerIndiceCancion(String idCancion) {
        for (int i = 0; i < modelo.getCanciones().size(); i++) {
            if (modelo.getCanciones().get(i).getId().equals(idCancion)) {
                return i % reproduccionesPorCancion.length; // Simplificación, podría colisionar
            }
        }
        return -1;
    }

    private void estadisticasPorUsuario() {
        if (modelo.getInterpretes().isEmpty()) {
            System.out.println("No hay intérpretes disponibles. Por favor, cargue los datos primero.");
            return;
        }

        System.out.println("\n--- Estadísticas por Usuario (" + dniUsuarioActual + ") ---");
        int maxReproduccionesInterprete = -1;
        String interpreteMasEscuchado = "Ninguno";
        for (int i = 0; i < modelo.getInterpretes().size(); i++) {
            if (reproduccionesPorInterprete[i] > maxReproduccionesInterprete) {
                maxReproduccionesInterprete = reproduccionesPorInterprete[i];
                interpreteMasEscuchado = modelo.getInterpretes().get(i).getNombre();
            }
        }
        System.out.println("Intérprete más reproducido: " + interpreteMasEscuchado);
        System.out.println("Nota: Estas estadísticas son específicas del usuario actual y se basan en reproducciones durante esta sesión, no se guardan permanentemente.");
    }

    private void estadisticasPorAlbum() {
        if (modelo.getCanciones().isEmpty()) {
            System.out.println("No hay canciones disponibles. Por favor, cargue los datos primero.");
            return;
        }

        System.out.println("\n--- Estadísticas por Álbum ---");
        int maxReproduccionesAlbum = -1;
        String albumMasReproducido = "Ninguno";
        for (int i = 0; i < modelo.getCanciones().size(); i++) {
            int index = i % reproduccionesPorAlbum.length;
            if (reproduccionesPorAlbum[index] > maxReproduccionesAlbum) {
                maxReproduccionesAlbum = reproduccionesPorAlbum[index];
                albumMasReproducido = modelo.getCanciones().get(i).getAlbum();
            }
        }
        System.out.println("Álbum más reproducido: " + albumMasReproducido);
        System.out.println("Nota: Estas estadísticas son específicas del usuario actual y se basan en reproducciones durante esta sesión, no se guardan permanentemente.");
    }

    private void estadisticasPorCanciones() {
        if (modelo.getCanciones().isEmpty()) {
            System.out.println("No hay canciones disponibles. Por favor, cargue los datos primero.");
            return;
        }

        System.out.println("\n--- Estadísticas por Canciones ---");
        Cancion[] cancionesOrdenadas = new Cancion[Math.min(3, modelo.getCanciones().size())];
        for (int i = 0; i < cancionesOrdenadas.length; i++) {
            cancionesOrdenadas[i] = modelo.getCanciones().get(i);
        }
        for (int i = 0; i < Math.min(3, cancionesOrdenadas.length); i++) {
            for (int j = 0; j < cancionesOrdenadas.length - 1 - i; j++) {
                int indexJ = obtenerIndiceCancion(cancionesOrdenadas[j].getId());
                int indexJ1 = obtenerIndiceCancion(cancionesOrdenadas[j + 1].getId());
                if (indexJ != -1 && indexJ1 != -1 && reproduccionesPorCancion[indexJ] < reproduccionesPorCancion[indexJ1]) {
                    Cancion temp = cancionesOrdenadas[j];
                    cancionesOrdenadas[j] = cancionesOrdenadas[j + 1];
                    cancionesOrdenadas[j + 1] = temp;
                }
            }
        }
        String cancion1 = (cancionesOrdenadas.length > 0 && obtenerIndiceCancion(cancionesOrdenadas[0].getId()) != -1) ?
                cancionesOrdenadas[0].getNombre() + " - " + cancionesOrdenadas[0].getAlbum() + " (" + reproduccionesPorCancion[obtenerIndiceCancion(cancionesOrdenadas[0].getId())] + " reproducciones)" : "Ninguna";
        String cancion2 = (cancionesOrdenadas.length > 1 && obtenerIndiceCancion(cancionesOrdenadas[1].getId()) != -1) ?
                cancionesOrdenadas[1].getNombre() + " - " + cancionesOrdenadas[1].getAlbum() + " (" + reproduccionesPorCancion[obtenerIndiceCancion(cancionesOrdenadas[1].getId())] + " reproducciones)" : "Ninguna";
        String cancion3 = (cancionesOrdenadas.length > 2 && obtenerIndiceCancion(cancionesOrdenadas[2].getId()) != -1) ?
                cancionesOrdenadas[2].getNombre() + " - " + cancionesOrdenadas[2].getAlbum() + " (" + reproduccionesPorCancion[obtenerIndiceCancion(cancionesOrdenadas[2].getId())] + " reproducciones)" : "Ninguna";

        System.out.println("Canciones más reproducidas:");
        System.out.println("  1. " + cancion1);
        System.out.println("  2. " + cancion2);
        System.out.println("  3. " + cancion3);
        System.out.println("Nota: Estas estadísticas son específicas del usuario actual y se basan en reproducciones durante esta sesión, no se guardan permanentemente.");
    }

    private void crearPlaylist() {
        if (modelo.getCanciones().isEmpty()) {
            System.out.println("No hay canciones disponibles. Por favor, cargue los datos primero.");
            return;
        }

        System.out.print("Ingrese el nombre de la playlist: ");
        String nombrePlaylist = scanner.nextLine();
        String idPlaylist = "PL" + (modelo.getPlaylists().size() + 1);
        Playlist playlist = new Playlist(idPlaylist, nombrePlaylist);

        System.out.println("\n--- Lista de Canciones ---");
        for (int i = 0; i < modelo.getCanciones().size(); i++) {
            System.out.println((i + 1) + ". " + modelo.getCanciones().get(i));
        }
        System.out.println("Seleccione canciones para añadir a la playlist (ingrese los números separados por espacios, o 'fin' para terminar):");
        while (true) {
            String entrada = scanner.nextLine();
            if (entrada.equalsIgnoreCase("fin")) {
                break;
            }
            String[] indices = entrada.split(" ");
            for (String indice : indices) {
                try {
                    int seleccion = Integer.parseInt(indice) - 1;
                    if (seleccion >= 0 && seleccion < modelo.getCanciones().size()) {
                        playlist.agregarCancion(modelo.getCanciones().get(seleccion).getId());
                    } else {
                        System.out.println("Índice " + (seleccion + 1) + " no válido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada '" + indice + "' no válida. Ingrese un número.");
                }
            }
        }
        if (playlist.getCancionesIds().isEmpty()) {
            System.out.println("No se seleccionaron canciones. Cancelando creación de playlist.");
            return;
        }
        modelo.getPlaylists().add(playlist);
        modelo.guardarPlaylists();
        System.out.println("Playlist '" + nombrePlaylist + "' creada con éxito.");
    }

    private void registrarUsuario() {
        System.out.println("\n--- Registro de Nuevo Usuario ---");
        System.out.print("Ingrese su DNI: ");
        String dni = scanner.nextLine().trim();
        if (dni.isEmpty()) {
            System.out.println("El DNI no puede estar vacío.");
            return;
        }

        modelo.leerUsuarios();
        for (Usuario usuario : modelo.getUsuarios()) {
            if (usuario.getDni().equals(dni)) {
                System.out.println("El DNI " + dni + " ya está registrado.");
                return;
            }
        }

        System.out.print("Ingrese su nombre: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("El nombre no puede estar vacío.");
            return;
        }

        System.out.print("Ingrese su contraseña: ");
        String contrasena = scanner.nextLine().trim();
        if (contrasena.isEmpty()) {
            System.out.println("La contraseña no puede estar vacía.");
            return;
        }

        Usuario nuevoUsuario = new Usuario(dni, nombre, contrasena);
        modelo.getUsuarios().add(nuevoUsuario);
        modelo.guardarUsuarios();
        System.out.println("Usuario " + nombre + " registrado con éxito con DNI " + dni + ".");

        String[] estadisticas = modelo.calcularEstadisticasGenerales();
        System.out.println("\nEstadísticas generales (basadas en reproducciones totales):");
        System.out.println("Intérprete más reproducido: " + estadisticas[0]);
        System.out.println("Álbum más reproducido: " + estadisticas[1]);
        System.out.println("Canciones más reproducidas:");
        System.out.println("  1. " + estadisticas[2]);
        System.out.println("  2. " + estadisticas[3]);
        System.out.println("  3. " + estadisticas[4]);
        System.out.println("Nota: Estas estadísticas se basan en las reproducciones totales y no reflejan específicamente al usuario recién registrado debido a las limitaciones de los datos actuales.");
    }
}