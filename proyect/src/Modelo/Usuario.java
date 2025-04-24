package Modelo;
import java.util.Objects;

    public class Usuario {
        private final String dni;
        private final String nombre;
        private final String contrasena;

        public Usuario(String dni, String nombre, String contrasena) {
            if (dni == null || dni.trim().isEmpty() ||
                    nombre == null || nombre.trim().isEmpty() ||
                    contrasena == null || contrasena.isEmpty()) {
                throw new IllegalArgumentException("DNI, Nombre y Contraseña del usuario no pueden estar vacíos.");
            }
            this.dni = dni.trim();
            this.nombre = nombre.trim();
            this.contrasena = contrasena;
        }

        public boolean verificarContrasena(String contrasenaIntento) {
            return this.contrasena.equals(contrasenaIntento);
        }

        public String getDni() {
            return dni;
        }

        public String getNombre() {
            return nombre;
        }

        @Override
        public String toString() {
            return "Usuario: " + nombre + " (DNI: " + dni + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Usuario usuario = (Usuario) o;
            return Objects.equals(dni, usuario.dni);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dni);
        }

        protected String getContrasenaParaGuardar() {
            return this.contrasena;
        }
    }

