package Control;

import Modelo.Usuario;

import java.util.Scanner;

public class ControladorPrincipal {
    private final GestorDatos modelo;
    private final Scanner scanner;
    private boolean usuarioLogueado;
    private String dniUsuarioActual;

    public ControladorPrincipal(GestorDatos modelo) {
        this.modelo = modelo;
        this.scanner = new Scanner(System.in);
        this.usuarioLogueado = false;
        this.dniUsuarioActual = null;
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
            if (usuario.getDni().equals(dni) && usuario.validarContraseña(contrasena)) {
                System.out.println("Login exitoso. Bienvenido, " + usuario.getNombre() + "!");
                return true;
            }
        }
        System.out.println("DNI o contrasena incorrectos.");
        return false;
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
            System.out.println("2. Guardar Datos");
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
                    System.out.println(".");
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
                    System.out.println(".");
                    break;
                case "3":
                    System.out.println(".");
                    break;
                case "4":
                    System.out.println(".");
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
                    System.out.println(".");
                    break;
                case "2":
                    System.out.println(".");
                    break;
                case "3":
                    estadisticasCanciones();
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
        System.out.println(modelo.toString());
        System.out.println("Datos cargados correctamente.");
    }

    private void guardarDatos() {
        System.out.println("Datos guardados correctamente.");
    }

    private void reproducirPorCancion() {

    }

    private void estadisticasCanciones() {

    }

    private void crearPlaylist() {

        }

}