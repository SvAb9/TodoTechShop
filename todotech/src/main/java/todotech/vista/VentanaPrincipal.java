package todotech.vista;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del sistema TodoTech Shop.
 * Contiene el menú de navegación con pestañas por módulo.
 */
public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        configurarVentana();
        construirUI();
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

        JLabel usuario = new JLabel("Usuario: Admin  ");
        usuario.setFont(new Font("Arial", Font.PLAIN, 13));
        usuario.setForeground(new Color(200, 220, 240));
        header.add(usuario, BorderLayout.EAST);

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
