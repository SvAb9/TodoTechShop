package todotech.vista;

import todotech.modelo.Cliente;
import todotech.servicio.ClienteServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Formulario CRUD de Cliente en Swing.
 * MOD-08 — Gestionar Clientes.
 * Entregable CRUD oficial de la materia.
 */
public class FormCliente extends JPanel {

    // Servicio
    private final ClienteServicio servicio = new ClienteServicio();

    // Campos del formulario
    private JTextField txtIdentificacion;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtBuscar;

    // Tabla
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // Estado interno
    private Cliente clienteSeleccionado;

    public FormCliente() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));
        construirUI();
        cargarTabla();
    }

    private void construirUI() {
        // ─── TÍTULO ───────────────────────────────────────────────────
        JLabel titulo = new JLabel("Gestión de Clientes");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(31, 78, 121));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // ─── PANEL PRINCIPAL: FORMULARIO + TABLA ─────────────────────
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
            "Datos del Cliente", 0, 0,
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

        // Identificación
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(etiqueta("Identificación *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtIdentificacion = new JTextField(15);
        panel.add(txtIdentificacion, gbc);

        // Nombre
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(etiqueta("Nombre *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtNombre = new JTextField(15);
        panel.add(txtNombre, gbc);

        // Teléfono
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(etiqueta("Teléfono *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtTelefono = new JTextField(15);
        panel.add(txtTelefono, gbc);

        // Correo
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 5;
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

    JButton btnGuardar    = boton("Guardar",    null);
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
            "Clientes Registrados", 0, 0,
            new Font("Arial", Font.BOLD, 13), new Color(31, 78, 121)));

        String[] columnas = { "ID", "Identificación", "Nombre", "Teléfono", "Correo" };
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

        // Al hacer click en fila, llena el formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarEnFormulario();
        });

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    // ─── ACCIONES CRUD ────────────────────────────────────────────────

    private void guardar() {
        try {
            Cliente c = leerFormulario();
            servicio.registrar(c);
            mostrarExito("Cliente registrado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void actualizar() {
        if (clienteSeleccionado == null) {
            mostrarError("Seleccione un cliente de la tabla primero.");
            return;
        }
        try {
            clienteSeleccionado.setNombre(txtNombre.getText().trim());
            clienteSeleccionado.setTelefono(txtTelefono.getText().trim());
            clienteSeleccionado.setCorreo(txtCorreo.getText().trim());
            servicio.actualizar(clienteSeleccionado);
            mostrarExito("Cliente actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminar() {
        if (clienteSeleccionado == null) {
            mostrarError("Seleccione un cliente de la tabla primero.");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
            "¿Desea eliminar al cliente " + clienteSeleccionado.getNombre() + "?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        try {
            servicio.desactivar(clienteSeleccionado.getId());
            mostrarExito("Cliente eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void buscar() {
        String texto = txtBuscar.getText().trim();
        try {
            List<Cliente> lista = texto.isEmpty()
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
            mostrarError("Error al cargar clientes: " + ex.getMessage());
        }
    }

    private void poblarTabla(List<Cliente> lista) {
        modeloTabla.setRowCount(0);
        for (Cliente c : lista) {
            modeloTabla.addRow(new Object[]{
                c.getId(), c.getIdentificacion(), c.getNombre(),
                c.getTelefono(), c.getCorreo()
            });
        }
    }

    private void cargarEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        try {
            int id = (int) modeloTabla.getValueAt(fila, 0);
            String identificacion = (String) modeloTabla.getValueAt(fila, 1);
            clienteSeleccionado = servicio.buscarPorIdentificacion(identificacion);
            txtIdentificacion.setText(clienteSeleccionado.getIdentificacion());
            txtIdentificacion.setEditable(false); // RN-01: no se puede cambiar
            txtNombre.setText(clienteSeleccionado.getNombre());
            txtTelefono.setText(clienteSeleccionado.getTelefono());
            txtCorreo.setText(clienteSeleccionado.getCorreo());
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private Cliente leerFormulario() {
        return new Cliente(
            txtIdentificacion.getText().trim(),
            txtNombre.getText().trim(),
            txtTelefono.getText().trim(),
            txtCorreo.getText().trim()
        );
    }

    private void limpiar() {
        txtIdentificacion.setText("");
        txtIdentificacion.setEditable(true);
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtBuscar.setText("");
        tabla.clearSelection();
        clienteSeleccionado = null;
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
