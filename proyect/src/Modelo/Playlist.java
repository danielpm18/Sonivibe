package Modelo;

public class Playlist {
    private String id;
    private String nombre;
    private String usuarioDni;
    private String idCancion1;
    private String idCancion2;

    public Playlist(String id, String nombre, String usuarioDni) {
        this.id = id;
        this.nombre = nombre;
        this.usuarioDni = usuarioDni;
        this.idCancion1 = null;
        this.idCancion2 = null;
    }

    public void setCanciones(String idCancion1, String idCancion2) {
        this.idCancion1 = idCancion1;
        this.idCancion2 = idCancion2;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsuarioDni() {
        return usuarioDni;
    }

    public String getIdCancion1() {
        return idCancion1;
    }

    public String getIdCancion2() {
        return idCancion2;
    }

    @Override
    public String toString() {
        return nombre + " (Usuario DNI: " + usuarioDni + ")";
    }
}