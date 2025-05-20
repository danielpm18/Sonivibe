package Control;

import Modelo.Cancion;
import Modelo.Interprete;
import Modelo.Playlist;
import Modelo.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorDatos {
    private List<Usuario> usuarios;
    private List<Interprete> interpretes;
    private List<Cancion> canciones;
    private List<Playlist> playlists;

    public GestorDatos() {
        usuarios = new ArrayList<>();
        interpretes = new ArrayList<>();
        canciones = new ArrayList<>();
        playlists = new ArrayList<>();
    }

    // Leer usuarios desde el txt
    public void leerUsuarios() {
        usuarios.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("proyect/src/Data/usuarios.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length == 3) {
                    usuarios.add(new Usuario(partes[0], partes[1], partes[2]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer usuarios.txt: " + e.getMessage());
        }
    }

    // Leer intérpretes desde eltxt
    public void leerInterpretes() {
        interpretes.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("proyect/src/Data/interpretes.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length == 2) {
                    interpretes.add(new Interprete(partes[0], partes[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer interpretes.txt: " + e.getMessage());
        }
    }

    // Leer canciones desde el txt
    public void leerCanciones() {
        canciones.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("proyect/src/Data/canciones.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length == 5) {
                    Cancion cancion = new Cancion(partes[0], partes[1], partes[3], partes[2], Integer.parseInt(partes[4]));
                    canciones.add(cancion);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer canciones.txt: " + e.getMessage());
        }
    }

    // Leer playlists desde el txt.
    public void leerPlaylists() {
        playlists.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("proyect/src/Data/playlists.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length >= 3) {
                    Playlist playlist = new Playlist(partes[0], partes[1]);
                    for (int i = 2; i < partes.length; i++) {
                        playlist.agregarCancion(partes[i]);
                    }
                    playlists.add(playlist);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer playlists.txt: " + e.getMessage());
        }
    }

    // Guardar usuarios en usuarios.txt con formato DNI;Nombre;contraseña
    public void guardarUsuarios() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("proyect/src/Data/usuarios.txt"))) {
            for (Usuario usuario : usuarios) {
                bw.write(usuario.getDni() + ";" + usuario.getNombre() + ";" + usuario.getContrasena());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios.txt: " + e.getMessage());
        }
    }

    // Guardar intérpretes en txt
    public void guardarInterpretes() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("proyect/src/Data/interpretes.txt"))) {
            for (Interprete interprete : interpretes) {
                bw.write(interprete.getId() + ";" + interprete.getNombre());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar interpretes.txt: " + e.getMessage());
        }
    }

    // Guardar canciones en txt.
    public void guardarCanciones() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("proyect/src/Data/canciones.txt"))) {
            for (Cancion cancion : canciones) {
                bw.write(cancion.getId() + ";" + cancion.getNombre() + ";" + cancion.getAlbum() + ";" + cancion.getInterpreteId() + ";" + cancion.getReproducciones());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar canciones.txt: " + e.getMessage());
        }
    }

    // Guardar playlists en el txt
    public void guardarPlaylists() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("proyect/src/Data/playlists.txt"))) {
            for (Playlist playlist : playlists) {
                StringBuilder linea = new StringBuilder();
                linea.append(playlist.getId()).append(";").append(playlist.getNombre());
                for (String cancionId : playlist.getCanciones()) {
                    linea.append(";").append(cancionId);
                }
                bw.write(linea.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar playlists.txt: " + e.getMessage());
        }
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Interprete> getInterpretes() {
        return interpretes;
    }

    public List<Cancion> getCanciones() {
        return canciones;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }
}