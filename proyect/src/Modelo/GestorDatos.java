package Modelo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class GestorDatos {
    private ArrayList<Usuario> usuarios;
    private ArrayList<Interprete> interpretes;
    private ArrayList<Cancion> canciones;
    private ArrayList<Playlist> playlists;
    private static final String ARCHIVO1 = "proyect/src/Data/usuarios.txt";
    private static final String ARCHIVO2 = "proyect/src/Data/interpretes.txt";
    private static final String ARCHIVO3 = "proyect/src/Data/canciones.txt";
    private static final String ARCHIVO4 = "proyect/src/Data/playlists.txt";

    public GestorDatos() {
        usuarios = new ArrayList<>();
        interpretes = new ArrayList<>();
        canciones = new ArrayList<>();
        playlists = new ArrayList<>();
    }

    public void leerUsuarios() {
        usuarios.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO1))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length == 3) {
                    usuarios.add(new Usuario(datos[0], datos[1], datos[2]));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al leer usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void leerInterpretes() {
        interpretes.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO2))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length == 2) {
                    interpretes.add(new Interprete(datos[0], datos[1]));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al leer intérpretes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void leerCanciones() {
        canciones.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO3))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length == 4) {
                    canciones.add(new Cancion(datos[0], datos[1], datos[2], datos[3]));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al leer canciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void leerPlaylists() {
        playlists.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO4))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length == 4) {
                    Playlist playlist = new Playlist(datos[0], datos[1], datos[2]);
                    playlist.setCanciones(datos[3], datos[4]);
                    playlists.add(playlist);
                } else {
                    System.err.println("Formato incorrecto en línea de playlists: " + linea);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al leer playlists: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    public ArrayList<Interprete> getInterpretes() {
        return interpretes;
    }

    public ArrayList<Cancion> getCanciones() {
        return canciones;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }
}