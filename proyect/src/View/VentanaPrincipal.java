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

    public VentanaPrincipal() {
        super("Sonivibe - Login");
        usuarioLogueado = false;
        inicializarLogin();

    }

    public void addController(ControladorPrincipal controlador) {
        this.controlador = controlador;
    }

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

        // Menú Datos
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

        // Menú Reproducir
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

        // Menú Usuario
        JMenu menuUsuario = new JMenu("Usuario");
        menuUsuario.setMnemonic(KeyEvent.VK_U);

        JMenuItem itemCrearUsuario = new JMenuItem("Crear Usuario", KeyEvent.VK_N);
        itemCrearUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearUsuario();
            }
        });
        menuUsuario.add(itemCrearUsuario);

        JMenuItem itemLoginUsuario = new JMenuItem("Login", KeyEvent.VK_L);

        itemCrearUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUsuario();
            }
        });
        menuUsuario.add(itemLoginUsuario);

        // Menú Playlist
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

    private void mostrarEstadisticas() {

        JInternalFrame estadisticasFrame = new JInternalFrame("Estadísticas", true, true, true, true);
        estadisticasFrame.setSize(400, 300);
        estadisticasFrame.setLocation(150, 50);
        estadisticasFrame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        JPanel panelEstadisticas = new JPanel(new GridLayout(3, 1, 10, 10));
        panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int maxInterpreteIndex = 0;
        for (int i = 1; i < controlador.getReproduccionesPorInterprete().length; i++) {
            if (controlador.getReproduccionesPorInterprete()[i] > controlador.getReproduccionesPorInterprete()[maxInterpreteIndex]) {
                maxInterpreteIndex = i;
            }
        }
        String interpreteMasReproducido = controlador.getInterpretes()[maxInterpreteIndex].getNombre();
        JLabel lblInterprete = new JLabel("Intérprete más reproducido: " + interpreteMasReproducido + " (" + controlador.getReproduccionesPorInterprete()[maxInterpreteIndex] + " reproducciones)");
        panelEstadisticas.add(lblInterprete);

        // Álbum más reproducido
        int maxAlbumIndex = 0;
        for (int i = 1; i < controlador.getReproduccionesPorAlbum().length; i++) {
            if (controlador.getReproduccionesPorAlbum()[i] > controlador.getReproduccionesPorAlbum()[maxAlbumIndex]) {
                maxAlbumIndex = i;
            }
        }
        String albumMasReproducido = controlador.getAlbumes()[maxAlbumIndex];
        JLabel lblAlbum = new JLabel("Álbum más reproducido: " + albumMasReproducido + " (" + controlador.getReproduccionesPorAlbum()[maxAlbumIndex] + " reproducciones)");
        panelEstadisticas.add(lblAlbum);

        // Top 3 canciones más reproducidas
        Integer[] indicesCanciones = new Integer[controlador.getCanciones().length];
        for (int i = 0; i < indicesCanciones.length; i++) {
            indicesCanciones[i] = i;
        }
        Arrays.sort(indicesCanciones, (i1, i2) -> Integer.compare(controlador.getReproduccionesPorCancion()[2], controlador.getReproduccionesPorCancion()[1]));

        JPanel panelTop3 = new JPanel(new GridLayout(3, 1, 0, 5));
        for (int i = 0; i < Math.min(3, indicesCanciones.length); i++) {
            int index = indicesCanciones[i];
            JLabel lblCancion = new JLabel((i + 1) + ". " + controlador.getCanciones()[index].getNombre() + " (" + controlador.getReproduccionesPorCancion()[index] + " reproducciones)");
            panelTop3.add(lblCancion);
        }
        panelEstadisticas.add(panelTop3);

        estadisticasFrame.add(panelEstadisticas);
        desktopPane.add(estadisticasFrame);
        estadisticasFrame.setVisible(true);

    }

    private void crearUsuario() {
        JTextField dniField = new JTextField(10);
        JTextField nombreField = new JTextField(20);
        JTextField contrasenaField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("DNI:"));
        panel.add(dniField);
        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Contraseña:"));
        panel.add(contrasenaField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Crear Nuevo Usuario",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String dni = dniField.getText().trim();
            String nombre = nombreField.getText().trim();
            String contraseña = contrasenaField.getText().trim();

            if (dni.isEmpty() || nombre.isEmpty() || contraseña.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean exito = controlador.crearUsuario(dni, nombre, contraseña);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loginUsuario() {
        JTextField dniField = new JTextField(10);
        JTextField contraseñaField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("DNI:"));
        panel.add(dniField);
        panel.add(new JLabel("Contraseña:"));
        panel.add(contraseñaField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Login de Usuario",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String dni = dniField.getText().trim();
            String contraseña = contraseñaField.getText().trim();

            if (dni.isEmpty() || contraseña.isEmpty()) {
                JOptionPane.showMessageDialog(this, "DNI y Contraseña son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (controlador.validarUsuario(dni, contraseña)) {
                usuarioLogueado = true;
                JOptionPane.showMessageDialog(this, "Login exitoso.", "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "DNI o Contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


}