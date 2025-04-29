package Modelo;

public class Usuario {
    private String dni;
    private String nombre;
    private String contraseña;

    public Usuario(String dni, String nombre, String contraseña) {
        this.dni = dni;
        this.nombre = nombre;
        this.contraseña = contraseña;
    }

    public boolean validarContraseña(String input) {
        return input.equals(contraseña) && contraseña.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$");
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }
}