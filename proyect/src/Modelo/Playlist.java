package Modelo;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String id;
    private String nombre;
    private List<String> canciones;

    public Playlist(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.canciones = new ArrayList<>();
    }

    public void agregarCancion(String cancionId) {
        canciones.add(cancionId);
    }

    public List<String> getCanciones() {
        return canciones;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}