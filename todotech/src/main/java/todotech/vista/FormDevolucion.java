package todotech.vista;

import todotech.servicio.DevolucionServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Formulario de Gestión de Devoluciones.
 * MOD-09 — Gestionar Devoluciones.
 *
 * Flujo:
 * 1. Vendedor/Cliente solicita devolución ingresando número de orden y motivo.
 * 2. Administrador aprueba o rechaza desde la tabla de pendientes.
 * 3. Al aprobar, el stock se reintegra automáticamente.
 */
public class FormDevolucion extends JPanel {

    private final DevolucionServicio servicio = new DevolucionServicio();

    // Solicitar devolución
    private JTextField txtNumeroOrden;
    private JTextArea  txtMotivo;

    // Tabla de pendientes
    private JTable            tablaPendientes;
    private DefaultTableModel modeloTabla;

    public FormDevolucion() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));
        construirUI();
        cargarPendientes();
    }

    private void construirUI() {
        JLabel titulo = new JLabel("Gestión de Devoluciones");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(31, 78, 121));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            panelSolicitud(), panelPendientes());
        split.setDividerLocation(220);
        add(split, BorderLayout.CENTER);
    }

    // ─── PANEL SUPERIOR: solicitar devolución ────────────────────────
    private JPanel panelSolicitud() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(31, 78, 121)),
            "Solicitar Devolución", 0, 0,
            new Font("Arial", Font.BOLD, 13), new Color(31, 78, 121)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Número de orden
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Número de orden:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtNumeroOrden = new JTextField(20);
        panel.add(txtNumeroOrden, gbc);

        // Motivo
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Motivo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtMotivo = new JTextArea(3, 20);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        txtMotivo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(new JScrollPane(txtMotivo), gbc);

        // Botón solicitar
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JButton btnSolicitar = boton("Enviar Solicitud", new Color(46, 117, 182));
        btnSolicitar.addActionListener(e -> solicitarDevolucion());
        panel.add(btnSolicitar, gbc);

        return panel;
    }

    // ─── PANEL INFERIOR: tabla de pendientes ─────────────────────────
    private JPanel panelPendientes() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(31, 78, 121)),
            "Devoluciones Pendientes (solo Administrador puede aprobar/rechazar)", 0, 0,
            new Font("Arial", Font.BOLD, 13), new Color(31, 78, 121)));

        String[] cols = { "ID Dev.", "Número Orden", "Motivo", "Fecha" };
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaPendientes = new JTable(modeloTabla);
        tablaPendientes.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaPendientes.setRowHeight(24);
        tablaPendientes.getTableHeader().setBackground(new Color(31, 78, 121));
        tablaPendientes.getTableHeader().setForeground(Color.WHITE);
        tablaPendientes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tablaPendientes.getColumnModel().getColumn(0).setMaxWidth(70);

        panel.add(new JScrollPane(tablaPendientes), BorderLayout.CENTER);
        panel.add(panelBotonesAdmin(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel panelBotonesAdmin() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
         p.setBackground(Color.WHITE);
         p.setBorder(BorderFactory.createLineBorder(new Color(231, 232, 254), 2)); // #E7E8FE

        JButton btnRefrescar = boton("Refrescar",null);
        JButton btnAprobar   = boton("Aprobar",   null);
        JButton btnRechazar  = boton("Rechazar", null);

        btnRefrescar.addActionListener(e -> cargarPendientes());
        btnAprobar.addActionListener(e   -> aprobar());
        btnRechazar.addActionListener(e  -> rechazar());

        p.add(btnRefrescar);
        p.add(btnRechazar);
        p.add(btnAprobar);
        return p;
    }

    // ─── ACCIONES ─────────────────────────────────────────────────────

    private void solicitarDevolucion() {
        String numeroOrden = txtNumeroOrden.getText().trim();
        String motivo      = txtMotivo.getText().trim();
        try {
            servicio.solicitarDevolucion(numeroOrden, motivo);
            mostrarExito("Solicitud de devolución registrada para la orden " + numeroOrden + ".");
            txtNumeroOrden.setText("");
            txtMotivo.setText("");
            cargarPendientes();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void aprobar() {
        int fila = tablaPendientes.getSelectedRow();
        if (fila < 0) { mostrarError("Seleccione una devolución de la tabla."); return; }

        int idDev   = (int) modeloTabla.getValueAt(fila, 0);
        // El ID de la orden está guardado en el modelo pero no se muestra en tabla
        // Lo obtenemos de los datos completos recargando
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Aprobar esta devolución? El stock será reintegrado automáticamente.",
            "Confirmar aprobación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // El idOrden lo guardamos como dato oculto en el modelo
            int idOrden = idOrdenDeFila(fila);
            servicio.aprobarDevolucion(idDev, idOrden);
            mostrarExito("Devolución aprobada. Stock reintegrado.");
            cargarPendientes();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void rechazar() {
        int fila = tablaPendientes.getSelectedRow();
        if (fila < 0) { mostrarError("Seleccione una devolución de la tabla."); return; }

        int idDev   = (int) modeloTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Rechazar esta devolución?",
            "Confirmar rechazo", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            servicio.rechazarDevolucion(idDev);
            mostrarExito("Devolución rechazada.");
            cargarPendientes();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    // ─── HELPERS ──────────────────────────────────────────────────────

    private void cargarPendientes() {
        try {
            List<Object[]> lista = servicio.listarPendientes();
            modeloTabla.setRowCount(0);
            for (Object[] fila : lista) {
                // fila: [idDev, idOrden, numeroOrden, motivo, fecha]
                modeloTabla.addRow(new Object[]{
                    fila[0],           // ID Dev.
                    fila[2],           // Número Orden
                    fila[3],           // Motivo
                    fila[4]            // Fecha
                });
            }
            // Guardar idOrden como dato oculto para aprobar
            datosCompletos = lista;
        } catch (Exception ex) {
            mostrarError("Error al cargar devoluciones: " + ex.getMessage());
        }
    }

    // Lista completa con idOrden para usar al aprobar
    private List<Object[]> datosCompletos;

    private int idOrdenDeFila(int fila) {
        if (datosCompletos == null || fila >= datosCompletos.size()) return -1;
        return (int) datosCompletos.get(fila)[1]; // índice 1 = idOrden
    }

  private JButton boton(String texto, Color color) {
    JButton btn = new JButton(texto);
    btn.setFont(new Font("Arial", Font.BOLD, 12));
    btn.setBackground(new Color(44, 86, 122));      // #2C567A — azul grisáceo
    btn.setForeground(Color.WHITE);                 // blanco puro, buen contraste
    btn.setOpaque(true);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

    // Efecto hover
    btn.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override public void mouseEntered(java.awt.event.MouseEvent e) {
            btn.setBackground(new Color(60, 110, 155));  // más claro al pasar el mouse
        }
        @Override public void mouseExited(java.awt.event.MouseEvent e) {
            btn.setBackground(new Color(44, 86, 122));   // vuelve al original
        }
        @Override public void mousePressed(java.awt.event.MouseEvent e) {
            btn.setBackground(new Color(30, 62, 90));    // más oscuro al hacer click
        }
        @Override public void mouseReleased(java.awt.event.MouseEvent e) {
            btn.setBackground(new Color(60, 110, 155));
        }
    });

    return btn;
}

    private void mostrarExito(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
