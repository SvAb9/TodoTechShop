package todotech.vista;

import todotech.modelo.Usuario;
import todotech.servicio.UsuarioServicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Ventana de inicio de sesión de TodoTech Shop.
 * MOD-06 — Gestionar Permisos de Usuario.
 * RF-23: valida credenciales antes de permitir acceso al sistema.
 * Se muestra antes de VentanaPrincipal. Si el login es exitoso,
 * abre VentanaPrincipal y cierra esta ventana.
 */
public class FormLogin extends JFrame {

    private final UsuarioServicio usuarioServicio = new UsuarioServicio();

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JLabel lblMensaje;
    private JButton btnIngresar;

    public FormLogin() {
        configurarVentana();
        construirUI();
    }

    private void configurarVentana() {
        setTitle("TodoTech Shop — Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 480);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void construirUI() {
        // ─── PANEL RAÍZ ───────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        // ─── HEADER ───────────────────────────────────────────────────
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4));
        header.setBackground(new Color(31, 78, 121));
        header.setBorder(new EmptyBorder(28, 30, 24, 30));

        JLabel lblTitulo = new JLabel("TodoTech Shop", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Sistema de Gestión de Ventas", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(180, 210, 240));

        header.add(lblTitulo);
        header.add(lblSubtitulo);
        root.add(header, BorderLayout.NORTH);

        // ─── PANEL CENTRAL — FORMULARIO ───────────────────────────────
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(new Color(245, 247, 250));
        centro.setBorder(new EmptyBorder(30, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.weightx = 1.0;

        // Etiqueta "Iniciar Sesión"
        JLabel lblFormTitulo = new JLabel("Iniciar Sesión");
        lblFormTitulo.setFont(new Font("Arial", Font.BOLD, 17));
        lblFormTitulo.setForeground(new Color(31, 78, 121));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 18, 0);
        centro.add(lblFormTitulo, gbc);

        // Campo usuario
        gbc.insets = new Insets(4, 0, 2, 0);
        gbc.gridy = 1;
        centro.add(etiqueta("Usuario"), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsuario.setPreferredSize(new Dimension(0, 36));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        centro.add(txtUsuario, gbc);

        // Campo contraseña
        gbc.gridy = 3;
        gbc.insets = new Insets(4, 0, 2, 0);
        centro.add(etiqueta("Contraseña"), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 10, 0);
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(0, 36));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        centro.add(txtPassword, gbc);

        // Mensaje de error (inicialmente invisible)
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 8, 0);
        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMensaje.setForeground(new Color(180, 40, 30));
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        centro.add(lblMensaje, gbc);

        // Botón Ingresar
        gbc.gridy = 6;
        gbc.insets = new Insets(4, 0, 0, 0);
        btnIngresar = new JButton("Ingresar");
        btnIngresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnIngresar.setBackground(new Color(31, 78, 121));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        btnIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnIngresar.addActionListener(e -> intentarLogin());
        centro.add(btnIngresar, gbc);

        root.add(centro, BorderLayout.CENTER);

        // ─── FOOTER ───────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(220, 230, 241));
        footer.setPreferredSize(new Dimension(0, 32));
        JLabel lblFooter = new JLabel("Universidad del Quindío — Ingeniería de Software II — 2026");
        lblFooter.setFont(new Font("Arial", Font.PLAIN, 11));
        lblFooter.setForeground(new Color(80, 80, 80));
        footer.add(lblFooter);
        root.add(footer, BorderLayout.SOUTH);

        // ─── TECLA ENTER en cualquier campo dispara el login ──────────
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) intentarLogin();
            }
        };
        txtUsuario.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);
    }

    // ─── LÓGICA DE LOGIN ──────────────────────────────────────────────

    private void intentarLogin() {
        String usuario  = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Ingresa usuario y contraseña.");
            return;
        }

        btnIngresar.setEnabled(false);
        btnIngresar.setText("Verificando...");
        lblMensaje.setText(" ");

        // Usa SwingWorker para no bloquear la UI durante la consulta a BD
        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                return usuarioServicio.login(usuario, password);
            }

            @Override
            protected void done() {
                try {
                    Usuario u = get();
                    abrirSistema(u);
                } catch (Exception ex) {
                    // Extrae el mensaje real (viene envuelto en ExecutionException)
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    mostrarError(msg);
                } finally {
                    btnIngresar.setEnabled(true);
                    btnIngresar.setText("Ingresar");
                    txtPassword.setText("");
                    txtPassword.requestFocus();
                }
            }
        };
        worker.execute();
    }

    /** Abre VentanaPrincipal pasando el usuario autenticado y cierra el login */
    private void abrirSistema(Usuario usuario) {
        VentanaPrincipal ventana = new VentanaPrincipal(usuario);
        ventana.setVisible(true);
        dispose(); // cierra la ventana de login
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
    }

    // ─── HELPERS ──────────────────────────────────────────────────────

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        l.setForeground(new Color(60, 60, 60));
        return l;
    }

    // ─── MAIN — punto de entrada de la aplicación ─────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new FormLogin().setVisible(true);
        });
    }
}