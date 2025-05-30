package Modelo;

public class Cancion {
    private String id;
    private String nombre;
    private String interpreteId;
    private String album;
    private int reproducciones;
    private int anoPublicacion;

    public Cancion(String id, String nombre, String interpreteId, String album, int reproducciones, int anoPublicacion) {
        this.id = id;
        this.nombre = nombre;
        this.interpreteId = interpreteId;
        this.album = album;
        this.reproducciones = reproducciones;
        this.anoPublicacion = anoPublicacion;
    }

    public int getAnoPublicacion(){
        return anoPublicacion;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getInterpreteId() {
        return interpreteId;
    }

    public String getAlbum() {
        return album;
    }

    public int getReproducciones() {
        return reproducciones;
    }

    public void incrementarReproducciones() {
        reproducciones++;
    }

    @Override
    public String toString() {
        return nombre + " - " + album;
    }

    public String toFileString() {
        return id + ";" + nombre + ";" + interpreteId + ";" + album + ";" + reproducciones;
    }
}