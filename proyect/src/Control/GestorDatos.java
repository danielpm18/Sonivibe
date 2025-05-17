package Control;

import Modelo.Usuario;
import Modelo.Interprete;
import Modelo.Cancion;
import Modelo.Playlist;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GestorDatos {
    private ArrayList<Usuario> usuarios;
    private ArrayList<Interprete> interpretes;
    private ArrayList<Cancion> canciones;
    private ArrayList<Playlist> playlists;

    public GestorDatos() {
        usuarios = new ArrayList<>();
        interpretes = new ArrayList<>();
        canciones = new ArrayList<>();
        playlists = new ArrayList<>();
    }

    public void leerUsuarios() {
        usuarios.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("proyect/src/Data/usuarios.txt"))) {
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
        try (BufferedReader br = new BufferedReader(new FileReader("proyect/src/Data/interpretes.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length == 2) {
                    interpretes.add(new Interprete(datos[0], datos[1]));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al leer interpretes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void leerCanciones() {
        canciones.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("proyect/src/Data/canciones.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length == 5) {
                    int reproducciones = Integer.parseInt(datos[4]);
                    canciones.add(new Cancion(datos[0], datos[1], datos[2], datos[3], reproducciones));
                } else if (datos.length == 4) {
                    // Compatibilidad con formato antiguo
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
        try (BufferedReader br = new BufferedReader(new FileReader("proyect/src/Data/playlists.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length >= 3) { // Mínimo 3 campos: id, nombre, al menos 1 canción
                    Playlist playlist = new Playlist(datos[0], datos[1]);
                    // Añadir todas las canciones (a partir del tercer campo)
                    for (int i = 2; i < datos.length; i++) {
                        playlist.agregarCancion(datos[i]);
                    }
                    playlists.add(playlist);
                } else {
                    System.err.println("Formato incorrecto en linea de playlists: " + linea);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al leer playlists: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void guardarUsuarios() {
        try (PrintWriter pw = new PrintWriter("proyect/src/Data/usuarios.txt")) {
            for (Usuario usuario : usuarios) {
                pw.println(usuario.getDni() + ";" + usuario.getNombre() + ";" + usuario.getContrasena());
            }
        } catch (Exception e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void guardarInterpretes() {
        try (PrintWriter pw = new PrintWriter("proyect/src/Data/interpretes.txt")) {
            for (Interprete interprete : interpretes) {
                pw.println(interprete.getId() + ";" + interprete.getNombre());
            }
        } catch (Exception e) {
            System.err.println("Error al guardar interpretes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void guardarCanciones() {
        try (PrintWriter pw = new PrintWriter("proyect/src/Data/canciones.txt")) {
            for (Cancion cancion : canciones) {
                pw.println(cancion.toFileString());
            }
        } catch (Exception e) {
            System.err.println("Error al guardar canciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void guardarPlaylists() {
        try (PrintWriter pw = new PrintWriter("proyect/src/Data/playlists.txt")) {
            for (Playlist playlist : playlists) {
                StringBuilder linea = new StringBuilder();
                linea.append(playlist.getId()).append(";").append(playlist.getNombre());
                for (String cancionId : playlist.getCancionesIds()) {
                    linea.append(";").append(cancionId);
                }
                pw.println(linea.toString());
            }
        } catch (Exception e) {
            System.err.println("Error al guardar playlists: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String[] calcularEstadisticasGenerales() {
        if (canciones.isEmpty() || interpretes.isEmpty()) {
            return new String[]{"Ninguno", "Ninguno", "Ninguna", "Ninguna", "Ninguna"};
        }

        // Calcular intérprete más escuchado (Usuario)
        int[] reproduccionesPorInterprete = new int[interpretes.size()];
        for (int i = 0; i < canciones.size(); i++) {
            Cancion cancion = canciones.get(i);
            for (int j = 0; j < interpretes.size(); j++) {
                if (cancion.getInterpreteId().equals(interpretes.get(j).getId())) {
                    reproduccionesPorInterprete[j] += cancion.getReproducciones();
                    break;
                }
            }
        }
        int maxReproduccionesInterprete = -1;
        String interpreteMasEscuchado = "Ninguno";
        for (int i = 0; i < interpretes.size(); i++) {
            if (reproduccionesPorInterprete[i] > maxReproduccionesInterprete) {
                maxReproduccionesInterprete = reproduccionesPorInterprete[i];
                interpreteMasEscuchado = interpretes.get(i).getNombre();
            }
        }

        // Calcular álbum más reproducido (Álbum)
        ArrayList<String> albumesUnicos = new ArrayList<>();
        int[] reproduccionesPorAlbum = new int[canciones.size()];
        int albumesCount = 0;
        for (Cancion cancion : canciones) {
            String album = cancion.getAlbum();
            int index = -1;
            for (int i = 0; i < albumesUnicos.size(); i++) {
                if (albumesUnicos.get(i).equals(album)) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                albumesUnicos.add(album);
                reproduccionesPorAlbum[albumesCount] = cancion.getReproducciones();
                albumesCount++;
            } else {
                reproduccionesPorAlbum[index] += cancion.getReproducciones();
            }
        }
        int maxReproduccionesAlbum = -1;
        String albumMasReproducido = "Ninguno";
        for (int i = 0; i < albumesCount; i++) {
            if (reproduccionesPorAlbum[i] > maxReproduccionesAlbum) {
                maxReproduccionesAlbum = reproduccionesPorAlbum[i];
                albumMasReproducido = albumesUnicos.get(i);
            }
        }

        // Calcular las 3 canciones más reproducidas (Canciones)
        Cancion[] cancionesOrdenadas = new Cancion[canciones.size()];
        for (int i = 0; i < canciones.size(); i++) {
            cancionesOrdenadas[i] = canciones.get(i);
        }
        // Ordenamiento de burbuja para las 3 primeras canciones
        for (int i = 0; i < Math.min(3, cancionesOrdenadas.length); i++) {
            for (int j = 0; j < cancionesOrdenadas.length - 1 - i; j++) {
                if (cancionesOrdenadas[j].getReproducciones() < cancionesOrdenadas[j + 1].getReproducciones()) {
                    Cancion temp = cancionesOrdenadas[j];
                    cancionesOrdenadas[j] = cancionesOrdenadas[j + 1];
                    cancionesOrdenadas[j + 1] = temp;
                }
            }
        }
        String cancion1 = (cancionesOrdenadas.length > 0) ? cancionesOrdenadas[0].getNombre() + " - " + cancionesOrdenadas[0].getAlbum() + " (" + cancionesOrdenadas[0].getReproducciones() + " reproducciones)" : "Ninguna";
        String cancion2 = (cancionesOrdenadas.length > 1) ? cancionesOrdenadas[1].getNombre() + " - " + cancionesOrdenadas[1].getAlbum() + " (" + cancionesOrdenadas[1].getReproducciones() + " reproducciones)" : "Ninguna";
        String cancion3 = (cancionesOrdenadas.length > 2) ? cancionesOrdenadas[2].getNombre() + " - " + cancionesOrdenadas[2].getAlbum() + " (" + cancionesOrdenadas[2].getReproducciones() + " reproducciones)" : "Ninguna";

        return new String[]{interpreteMasEscuchado, albumMasReproducido, cancion1, cancion2, cancion3};
    }

    private Cancion buscarCancionPorId(String id) {
        for (Cancion cancion : canciones) {
            if (cancion.getId().equals(id)) {
                return cancion;
            }
        }
        return null;
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