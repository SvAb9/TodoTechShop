package todotech.vista;

import javax.swing.*;
import java.awt.*;
import todotech.modelo.Usuario;

/**
 * Ventana principal del sistema TodoTech Shop.
 * Contiene el menú de navegación con pestañas por módulo.
 */
public class VentanaPrincipal extends JFrame {

    private Usuario usuario;
    private JLabel lblUsuario;

    public VentanaPrincipal() {
        configurarVentana();
        construirUI();
    }

    public VentanaPrincipal(Usuario usuario) {
        this();
        setUsuario(usuario);
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        actualizarEtiquetaUsuario();
    }

    private void actualizarEtiquetaUsuario() {
        if (lblUsuario == null) {
            return;
        }
        String texto = "Usuario: Invitado  ";
        if (usuario != null) {
            String nombre = usuario.getNombre();
            if (nombre == null || nombre.isBlank()) {
                nombre = usuario.getUsuario();
            }
            texto = "Usuario: " + (nombre != null ? nombre : "Invitado") + "  ";
        }
        lblUsuario.setText(texto);
    }

    private void configurarVentana() {
        setTitle("TodoTech Shop — Sistema de Gestión de Ventas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
    }

    private void construirUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(31, 78, 121));
        header.setPreferredSize(new Dimension(0, 55));

        JLabel logo = new JLabel("  TodoTech Shop");
        logo.setFont(new Font("Arial", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);
        header.add(logo, BorderLayout.WEST);

        lblUsuario = new JLabel("Usuario: Invitado  ");
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 13));
        lblUsuario.setForeground(new Color(200, 220, 240));
        header.add(lblUsuario, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Pestañas de módulos
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 13));
        tabs.setBackground(new Color(245, 247, 250));

        // Persona A — sus módulos
        tabs.addTab("Clientes",         new FormCliente());
        tabs.addTab("Órdenes de Venta", new JPanel()); // FormOrden — próximo
        tabs.addTab("Catálogo",         new JPanel()); // FormCatalogo — próximo
        tabs.addTab("Pagos",            new JPanel()); // FormPago — próximo
        tabs.addTab("Devoluciones",     new JPanel()); // FormDevolucion — próximo

        // Persona B — sus módulos (placeholders)
        tabs.addTab("Inventario",       new JPanel());
        tabs.addTab("Despacho",         new JPanel());
        tabs.addTab("Histórico",        new JPanel());
        tabs.addTab("Permisos",         new JPanel());
        tabs.addTab("Proveedores",      new JPanel());

        add(tabs, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(220, 230, 241));
        footer.setPreferredSize(new Dimension(0, 28));
        JLabel estado = new JLabel("  Sistema listo.");
        estado.setFont(new Font("Arial", Font.PLAIN, 12));
        estado.setForeground(new Color(60, 60, 60));
        footer.add(estado);
        add(footer, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new VentanaPrincipal().setVisible(true);
        });
    }
}
