package Modelo;

import java.util.ArrayList;

public class Playlist {
    private String id;
    private String nombre;
    private ArrayList<String> cancionesIds;

    public Playlist(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.cancionesIds = new ArrayList<>();
    }

    public void agregarCancion(String cancionId) {
        cancionesIds.add(cancionId);
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<String> getCancionesIds() {
        return cancionesIds;
    }

    @Override
    public String toString() {
        return nombre;
    }
}