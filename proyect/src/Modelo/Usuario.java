package Modelo;

public class Usuario {
    private String dni;
    private String nombre;
    private String contrasena;

    public Usuario(String dni, String nombre, String contrasena) {
        this.dni = dni;
        this.nombre = nombre;
        this.contrasena = contrasena;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContrasena() {
        return contrasena;
    }

    public boolean validarContrasena(String contrasena) {
        return this.contrasena.equals(contrasena);
    }
}