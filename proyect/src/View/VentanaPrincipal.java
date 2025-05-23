package View;

import Control.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class VentanaPrincipal extends JFrame {
    private ControladorPrincipal controlador;
    private JDesktopPane desktopPane;
    private JMenuBar menuBar;
    private JTextField txtDni;
    private JPasswordField txtContrasena;
    private JButton btnLogin;
    private JLabel lblMensaje;
    private boolean usuarioLogueado;

    //Titulo con el nombre del reproductor
    public VentanaPrincipal() {
        super("Sonivibe - Login");
        usuarioLogueado = false;
        inicializarLogin();
    }
    //Controlador principal
    public void addController(ControladorPrincipal controlador) {
        this.controlador = controlador;
    }
    //Ventana del Login
    private void inicializarLogin() {
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelLogin = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelLogin.add(new JLabel("DNI:"), gbc);

        gbc.gridx = 1;
        txtDni = new JTextField(15);
        panelLogin.add(txtDni, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelLogin.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        txtContrasena = new JPasswordField(15);
        panelLogin.add(txtContrasena, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.addActionListener(e -> intentarLogin());
        panelLogin.add(btnLogin, gbc);

        gbc.gridy = 3;
        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(Color.RED);
        panelLogin.add(lblMensaje, gbc);

        add(panelLogin);
        setVisible(true);
    }

    private void intentarLogin() {
        String dni = txtDni.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();

        if (dni.isEmpty() || contrasena.isEmpty()) {
            lblMensaje.setText("Por favor, complete todos los campos.");
            return;
        }

        if (controlador.validarUsuario(dni, contrasena)) {
            usuarioLogueado = true;
            lblMensaje.setText("Login exitoso. Bienvenido!");
            getContentPane().removeAll();
            setTitle("Sonivibe - Menú Principal");
            setSize(700, 400);
            crearVista();
        } else {
            lblMensaje.setText("DNI o contraseña incorrectos.");
        }
    }

    public void crearVista() {
        desktopPane = new JDesktopPane();
        setContentPane(desktopPane);
        crearMenu();
        revalidate();
        repaint();
        System.out.println("Menú principal mostrado.");

        ImageIcon imagenlogo = new ImageIcon("proyect/src/Data/Imagenes/sonivibe_logo.jpg");
        JLabel logo = new JLabel(imagenlogo);
        logo.setBounds(200, 50, imagenlogo.getIconWidth(), imagenlogo.getIconHeight());
        desktopPane.setBackground(Color.decode("#301934"));

        desktopPane.add(logo);
    }

    private void crearMenu() {
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Menu Datos
        JMenu menuDatos = new JMenu("Datos");
        menuDatos.setMnemonic(KeyEvent.VK_D);

        JMenuItem itemLeer = new JMenuItem("Leer Ficheros", KeyEvent.VK_L);
        itemLeer.addActionListener(controlador);
        itemLeer.setActionCommand(ControladorPrincipal.LEER);
        menuDatos.add(itemLeer);

        JMenuItem itemGuardar = new JMenuItem("Guardar Ficheros", KeyEvent.VK_G);
        itemGuardar.addActionListener(controlador);
        itemGuardar.setActionCommand(ControladorPrincipal.GUARDAR);
        menuDatos.add(itemGuardar);

        JMenuItem itemEstadisticas = new JMenuItem("Mostrar Estadísticas", KeyEvent.VK_E);
        itemEstadisticas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarEstadisticas();
            }
        });
        menuDatos.add(itemEstadisticas);

        // Menu Reproducir
        JMenu menuReproducir = new JMenu("Reproducir");
        menuReproducir.setMnemonic(KeyEvent.VK_R);

        JMenuItem itemCancion = new JMenuItem("Canción", KeyEvent.VK_C);
        itemCancion.addActionListener(controlador);
        itemCancion.setActionCommand(ControladorPrincipal.CANCION);
        menuReproducir.add(itemCancion);

        JMenuItem itemPlaylist = new JMenuItem("Playlist", KeyEvent.VK_P);
        itemPlaylist.addActionListener(controlador);
        itemPlaylist.setActionCommand(ControladorPrincipal.PLAYLIST);
        menuReproducir.add(itemPlaylist);

        JMenuItem itemInterprete = new JMenuItem("Intérprete", KeyEvent.VK_I);
        itemInterprete.addActionListener(controlador);
        itemInterprete.setActionCommand(ControladorPrincipal.INTERPRETE);
        menuReproducir.add(itemInterprete);

        JMenuItem itemAlbum = new JMenuItem("Álbum", KeyEvent.VK_A);
        itemAlbum.addActionListener(controlador);
        itemAlbum.setActionCommand(ControladorPrincipal.ALBUM);
        menuReproducir.add(itemAlbum);

        JMenuItem itemRandom = new JMenuItem("Random", KeyEvent.VK_R);
        itemRandom.addActionListener(controlador);
        itemRandom.setActionCommand(ControladorPrincipal.RANDOM);
        menuReproducir.add(itemRandom);

        // Menu Usuario
        JMenu menuUsuario = new JMenu("Usuario");
        menuUsuario.setMnemonic(KeyEvent.VK_U);

        JMenuItem itemCrearUsuario = new JMenuItem("Crear Usuario", KeyEvent.VK_N);
        itemCrearUsuario.addActionListener(controlador);
        itemCrearUsuario.setActionCommand(ControladorPrincipal.CREAR_USUARIO);
        menuUsuario.add(itemCrearUsuario);

        // Menu Playlist
        JMenu menuPlaylist = new JMenu("Playlist");
        menuPlaylist.setMnemonic(KeyEvent.VK_P);

        JMenuItem itemCrearPlaylist = new JMenuItem("Crear Playlist", KeyEvent.VK_C);
        itemCrearPlaylist.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                controlador.crearPlaylistInterfaz();
            }
        });
        menuPlaylist.add(itemCrearPlaylist);

        menuBar.add(menuDatos);
        menuBar.add(menuReproducir);
        menuBar.add(menuUsuario);
        menuBar.add(menuPlaylist);
    }
    //Mostrar las estadisticas de canciones, interpretes, album, y antes del año 2000
    private void mostrarEstadisticas() {
        JInternalFrame estadisticasFrame = new JInternalFrame("Estadísticas", true, true, true, true);
        estadisticasFrame.setSize(400, 400);
        estadisticasFrame.setLocation(150, 50);
        estadisticasFrame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        JPanel panelEstadisticas = new JPanel(new GridLayout(5, 1, 10, 10));
        panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Interprete más reproducido
        int maxInterpreteIndex = 0;
        for (int i = 1; i < controlador.getReproduccionesPorInterprete().length; i++) {
            if (controlador.getReproduccionesPorInterprete()[i] > controlador.getReproduccionesPorInterprete()[maxInterpreteIndex]) {
                maxInterpreteIndex = i;
            }
        }
        String interpreteMasReproducido = controlador.getInterpretes().length > 0 ? controlador.getInterpretes()[maxInterpreteIndex].getNombre() : "N/A";
        JLabel lblInterprete = new JLabel("Intérprete más reproducido: " + interpreteMasReproducido + " (" + controlador.getReproduccionesPorInterprete()[maxInterpreteIndex] + " reproducciones)");
        panelEstadisticas.add(lblInterprete);

        // Album mas reproducido
        int maxAlbumIndex = 0;
        for (int i = 1; i < controlador.getReproduccionesPorAlbum().length; i++) {
            if (controlador.getReproduccionesPorAlbum()[i] > controlador.getReproduccionesPorAlbum()[maxAlbumIndex]) {
                maxAlbumIndex = i;
            }
        }
        String albumMasReproducido = controlador.getAlbumes().length > 0 ? controlador.getAlbumes()[maxAlbumIndex] : "N/A";
        JLabel lblAlbum = new JLabel("Álbum más reproducido: " + albumMasReproducido + " (" + controlador.getReproduccionesPorAlbum()[maxAlbumIndex] + " reproducciones)");
        panelEstadisticas.add(lblAlbum);

        // Top 3 canciones mas reproducidas
        Integer[] indicesCanciones = new Integer[controlador.getCanciones().length];
        for (int i = 0; i < indicesCanciones.length; i++) {
            indicesCanciones[i] = i;
        }
        Arrays.sort(indicesCanciones, (i1, i2) -> Integer.compare(controlador.getReproduccionesPorCancion()[i2], controlador.getReproduccionesPorCancion()[i1]));

        JPanel panelTop3 = new JPanel(new GridLayout(3, 1, 0, 5));
        panelTop3.add(new JLabel("Top 3 canciones más reproducidas:"));
        for (int i = 0; i < Math.min(3, indicesCanciones.length); i++) {
            int index = indicesCanciones[i];
            JLabel lblCancion = new JLabel((i + 1) + ". " + controlador.getCanciones()[index].getNombre() + " (" + controlador.getReproduccionesPorCancion()[index] + " reproducciones)");
            panelTop3.add(lblCancion);
        }
        panelEstadisticas.add(panelTop3);

        // Top 3 canciones mas reproducidas antes del 2000
        JPanel panel2000 = new JPanel(new GridLayout(4, 1, 0, 5));
        panel2000.add(new JLabel("Top 3 canciones más reproducidas antes del 2000:"));
        int count = 0;
        for (int i = 0; i < indicesCanciones.length && count < 3; i++) {
            int index = indicesCanciones[i];
            if (controlador.getCanciones()[index].getAnoPublicacion() < 2000) {
                JLabel lblCancion = new JLabel((count + 1) + ". " +
                        controlador.getCanciones()[index].getNombre() +
                        " (" + controlador.getReproduccionesPorCancion()[index] + " reproducciones, " +
                        controlador.getCanciones()[index].getAnoPublicacion() + ")");
                panel2000.add(lblCancion);
                count++;
            }
        }
        if (count == 0) {
            JLabel lblNoCanciones = new JLabel("No hay canciones publicadas antes del 2000.");
            panel2000.add(lblNoCanciones);
        }
        panelEstadisticas.add(panel2000);

        estadisticasFrame.add(panelEstadisticas);
        desktopPane.add(estadisticasFrame);
        estadisticasFrame.setVisible(true);
    }


}