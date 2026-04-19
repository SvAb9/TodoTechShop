package todotech.vista;

import todotech.modelo.Proveedor;
import todotech.servicio.ProveedorServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Formulario CRUD de Proveedor en Swing.
 * MOD-10 — Gestionar Proveedores.
 * Mismo patrón visual y estructural que FormCliente.
 */
public class FormProveedor extends JPanel {

    private final ProveedorServicio servicio = new ProveedorServicio();

    // Campos del formulario
    private JTextField txtNit;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtBuscar;

    // Tabla
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // Estado interno
    private Proveedor proveedorSeleccionado;

    public FormProveedor() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));
        construirUI();
        cargarTabla();
    }

    private void construirUI() {
        JLabel titulo = new JLabel("Gestión de Proveedores");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(31, 78, 121));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            panelFormulario(), panelTabla());
        split.setDividerLocation(350);
        split.setResizeWeight(0.35);
        add(split, BorderLayout.CENTER);
    }

    private JPanel panelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(31, 78, 121)),
            "Datos del Proveedor", 0, 0,
            new Font("Arial", Font.BOLD, 13), new Color(31, 78, 121)));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Buscar
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        panel.add(new JLabel("Buscar:"), gbc);
        gbc.gridx = 1;
        txtBuscar = new JTextField(15);
        panel.add(txtBuscar, gbc);
        gbc.gridx = 2;
        JButton btnBuscar = boton("Buscar", new Color(46, 117, 182));
        btnBuscar.addActionListener(e -> buscar());
        panel.add(btnBuscar, gbc);

        // Separador
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        panel.add(new JSeparator(), gbc);

        // NIT
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 2;
        panel.add(etiqueta("NIT *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtNit = new JTextField(15);
        panel.add(txtNit, gbc);

        // Nombre
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 3;
        panel.add(etiqueta("Nombre *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtNombre = new JTextField(15);
        panel.add(txtNombre, gbc);

        // Teléfono
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 4;
        panel.add(etiqueta("Teléfono *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtTelefono = new JTextField(15);
        panel.add(txtTelefono, gbc);

        // Correo
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 5;
        panel.add(etiqueta("Correo *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtCorreo = new JTextField(15);
        panel.add(txtCorreo, gbc);

        // Botones CRUD
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3;
        panel.add(panelBotones(), gbc);

        return panel;
    }

    private JPanel panelBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(new Color(231, 232, 254), 2)); // #E7E8FE

        JButton btnGuardar    = boton("Guardar",   null);
        JButton btnActualizar = boton("Actualizar", null);
        JButton btnEliminar   = boton("Eliminar",   null);
        JButton btnLimpiar    = boton("Limpiar",    null);

        btnGuardar.addActionListener(e    -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e   -> eliminar());
        btnLimpiar.addActionListener(e    -> limpiar());

        p.add(btnGuardar);
        p.add(btnActualizar);
        p.add(btnEliminar);
        p.add(btnLimpiar);
        return p;
    }

    private JPanel panelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(31, 78, 121)),
            "Proveedores Registrados", 0, 0,
            new Font("Arial", Font.BOLD, 13), new Color(31, 78, 121)));

        String[] columnas = { "ID", "NIT", "Nombre", "Teléfono", "Correo" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setRowHeight(24);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(new Color(31, 78, 121));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionBackground(new Color(173, 214, 241));
        tabla.getColumnModel().getColumn(0).setMaxWidth(50);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarEnFormulario();
        });

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    // ─── ACCIONES CRUD ────────────────────────────────────────────────

    private void guardar() {
        try {
            Proveedor p = leerFormulario();
            servicio.registrar(p);
            mostrarExito("Proveedor registrado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void actualizar() {
        if (proveedorSeleccionado == null) {
            mostrarError("Seleccione un proveedor de la tabla primero.");
            return;
        }
        try {
            proveedorSeleccionado.setNombre(txtNombre.getText().trim());
            proveedorSeleccionado.setTelefono(txtTelefono.getText().trim());
            proveedorSeleccionado.setCorreo(txtCorreo.getText().trim());
            servicio.actualizar(proveedorSeleccionado);
            mostrarExito("Proveedor actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminar() {
        if (proveedorSeleccionado == null) {
            mostrarError("Seleccione un proveedor de la tabla primero.");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
            "¿Desea eliminar al proveedor " + proveedorSeleccionado.getNombre() + "?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        try {
            servicio.desactivar(proveedorSeleccionado.getId());
            mostrarExito("Proveedor eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void buscar() {
        String texto = txtBuscar.getText().trim();
        try {
            List<Proveedor> lista = texto.isEmpty()
                ? servicio.listarActivos()
                : servicio.buscarPorNombre(texto);
            poblarTabla(lista);
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    // ─── HELPERS ──────────────────────────────────────────────────────

    private void cargarTabla() {
        try {
            poblarTabla(servicio.listarActivos());
        } catch (Exception ex) {
            mostrarError("Error al cargar proveedores: " + ex.getMessage());
        }
    }

    private void poblarTabla(List<Proveedor> lista) {
        modeloTabla.setRowCount(0);
        for (Proveedor p : lista) {
            modeloTabla.addRow(new Object[]{
                p.getId(), p.getNit(), p.getNombre(), p.getTelefono(), p.getCorreo()
            });
        }
    }

    private void cargarEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        try {
            String nit = (String) modeloTabla.getValueAt(fila, 1);
            proveedorSeleccionado = servicio.buscarPorNit(nit);
            txtNit.setText(proveedorSeleccionado.getNit());
            txtNit.setEditable(false); // NIT es inmutable
            txtNombre.setText(proveedorSeleccionado.getNombre());
            txtTelefono.setText(proveedorSeleccionado.getTelefono());
            txtCorreo.setText(proveedorSeleccionado.getCorreo());
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private Proveedor leerFormulario() {
        return new Proveedor(
            txtNombre.getText().trim(),
            txtNit.getText().trim(),
            txtTelefono.getText().trim(),
            txtCorreo.getText().trim()
        );
    }

    private void limpiar() {
        txtNit.setText("");
        txtNit.setEditable(true);
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtBuscar.setText("");
        tabla.clearSelection();
        proveedorSeleccionado = null;
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        return l;
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