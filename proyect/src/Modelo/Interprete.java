package Modelo;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Collection;

public class Interprete {
    private String idInterprete;
    private String nombre;
    private Map<String, Album> albums;

    public Interprete(String idInterprete, String nombre) {
        if (idInterprete == null || idInterprete.trim().isEmpty() ||
                nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("ID y Nombre del intérprete no pueden estar vacíos.");
        }
        this.idInterprete = idInterprete.trim();
        this.nombre = nombre.trim();
        this.albums = new HashMap<>();
    }

    public void agregarAlbum(Album album) {
        if (album != null && !this.albums.containsKey(album.getNombre())) {
            this.albums.put(album.getNombre(), album);
            // Opcional: asegurar consistencia bidireccional si es necesario
            if (album.getInterprete() != this) {
                // Esto normalmente se manejaría en la lógica de carga o creación del álbum
                System.err.println("Advertencia: Intentando agregar álbum ["+ album.getNombre() +"] con intérprete inconsistente a " + this.nombre);
            }
        }
    }

    // --- Getters ---
    public String getIdInterprete() {
        return idInterprete;
    }

    public String getNombre() {
        return nombre;
    }

    // Retorna la colección de Albumes (values del Map)
    public Collection<Album> getAlbums() {
        return albums.values();
    }

    @Override
    public String toString() {
        // Más útil para JComboBox o JList
        return nombre; // Solo el nombre para mostrar en listas
        // return nombre + " (ID: " + idInterprete + ")"; // Alternativa más detallada
    }

    // hashCode y equals basados en el ID único para usar en Maps/Sets
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interprete that = (Interprete) o;
        return Objects.equals(idInterprete, that.idInterprete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idInterprete);
    }
}
