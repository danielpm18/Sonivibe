package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class VentanaPrincipal extends JFrame {
    private JLabel etiquetaUsuario;
    private JButton botonVerCanciones;
    private JButton botonVerPlaylists;
    private JButton botonVerEstadisticas;
    private JButton botonCerrarSesion;

    public VentanaPrincipal(ActionListener controlador) {
        setTitle("Sonivibe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setSize(300, 200);
        setLocationRelativeTo(null);

        etiquetaUsuario = new JLabel("Usuario: Invitado");
        add(etiquetaUsuario);

        botonVerCanciones = new JButton("Ver Todas las Canciones");
        botonVerCanciones.setActionCommand("VER_CANCIONES");
        botonVerCanciones.addActionListener(controlador);
        add(botonVerCanciones);

        botonVerPlaylists = new JButton("Ver Mis Playlists");
        botonVerPlaylists.setActionCommand("VER_PLAYLISTS");
        botonVerPlaylists.addActionListener(controlador);
        add(botonVerPlaylists);

        botonVerEstadisticas = new JButton("Ver Estadísticas");
        botonVerEstadisticas.setActionCommand("VER_ESTADISTICAS");
        botonVerEstadisticas.addActionListener(controlador);
        add(botonVerEstadisticas);

        botonCerrarSesion = new JButton("Cerrar Sesión");
        botonCerrarSesion.setActionCommand("CERRAR_SESION");
        botonCerrarSesion.addActionListener(controlador);
        add(botonCerrarSesion);
    }

    public void actualizarInfoUsuario(String nombreUsuario) {
        etiquetaUsuario.setText("Usuario: " + nombreUsuario);
    }
}