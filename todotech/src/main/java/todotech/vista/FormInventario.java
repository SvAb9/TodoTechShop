package todotech.vista;

import todotech.modelo.MovimientoInventario;
import todotech.modelo.Producto;
import todotech.repositorio.ProductoRepositorio;
import todotech.servicio.InventarioServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Formulario de Gestión de Inventario.
 * MOD-07 — Gestionar Inventario.
 * Permite registrar entradas, salidas y ajustes de stock,
 * y consultar el historial de movimientos.
 */
public class FormInventario extends JPanel {

    private final InventarioServicio servicio     = new InventarioServicio();
    private final ProductoRepositorio productoRepo = new ProductoRepositorio();

    // Selección de producto
    private JTextField      txtBuscarProducto;
    private JComboBox<Producto> cmbProductos;
    private JLabel          lblStockActual;

    // Campos de movimiento
    private JRadioButton    rbEntrada;
    private JRadioButton    rbSalida;
    private JRadioButton    rbAjuste;
    private JSpinner        spnCantidad;
    private JTextField      txtMotivo;
    private JTextField      txtUsuario;

    // Tabla de movimientos
    private JTable          tablaMovimientos;
    private DefaultTableModel modeloTabla;

    // Filtro
    private JComboBox<String> cmbFiltroTipo;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public FormInventario() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));
        construirUI();
        cargarProductos();
        cargarMovimientos();
    }

    private void construirUI() {
        JLabel titulo = new JLabel("Gestión de Inventario");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(31, 78, 121));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            panelFormulario(), panelHistorial());
        split.setDividerLocation(380);
        split.setResizeWeight(0.35);
        add(split, BorderLayout.CENTER);
    }

    // ─── PANEL IZQUIERDO: registrar movimiento ────────────────────────
    private JPanel panelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(31, 78, 121)),
            "Registrar Movimiento", 0, 0,
            new Font("Arial", Font.BOLD, 13), new Color(31, 78, 121)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Buscar producto
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        panel.add(etiqueta("Producto:"), gbc);
        gbc.gridx = 1;
        txtBuscarProducto = new JTextField(12);
        panel.add(txtBuscarProducto, gbc);
        gbc.gridx = 2;
        JButton btnBuscar = boton("Buscar", new Color(46, 117, 182));
        btnBuscar.addActionListener(e -> buscarProducto());
        panel.add(btnBuscar, gbc);

        // ComboBox de productos
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        cmbProductos = new JComboBox<>();
        cmbProductos.addActionListener(e -> actualizarStockLabel());
        panel.add(cmbProductos, gbc);

        // Stock actual
        gbc.gridy = 2;
        lblStockActual = new JLabel("Stock actual: —");
        lblStockActual.setFont(new Font("Arial", Font.BOLD, 13));
        lblStockActual.setForeground(new Color(31, 78, 121));
        panel.add(lblStockActual, gbc);

        // Tipo de movimiento
        gbc.gridy = 3; gbc.gridwidth = 1; gbc.gridx = 0;
        panel.add(etiqueta("Tipo:"), gbc);

        rbEntrada = new JRadioButton("Entrada");
        rbSalida  = new JRadioButton("Salida");
        rbAjuste  = new JRadioButton("Ajuste");
        rbEntrada.setSelected(true);
        rbEntrada.setBackground(Color.WHITE);
        rbSalida.setBackground(Color.WHITE);
        rbAjuste.setBackground(Color.WHITE);

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbEntrada);
        grupo.add(rbSalida);
        grupo.add(rbAjuste);

        JPanel pRadios = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pRadios.setBackground(Color.WHITE);
        pRadios.add(rbEntrada);
        pRadios.add(rbSalida);
        pRadios.add(rbAjuste);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(pRadios, gbc);

        // Cantidad
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 4;
        panel.add(etiqueta("Cantidad *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 99999, 1));
        panel.add(spnCantidad, gbc);

        // Motivo
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 5;
        panel.add(etiqueta("Motivo *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtMotivo = new JTextField(15);
        panel.add(txtMotivo, gbc);

        // Usuario
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 6;
        panel.add(etiqueta("Usuario *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtUsuario = new JTextField(15);
        panel.add(txtUsuario, gbc);

        // Botones
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3;
        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        pBotones.setBackground(Color.WHITE);

        JButton btnRegistrar = boton("Registrar", new Color(55, 86, 35));
        JButton btnLimpiar   = boton("Limpiar",   new Color(80, 80, 80));
        btnRegistrar.addActionListener(e -> registrarMovimiento());
        btnLimpiar.addActionListener(e   -> limpiarFormulario());

        pBotones.add(btnRegistrar);
        pBotones.add(btnLimpiar);
        panel.add(pBotones, gbc);

        return panel;
    }

    // ─── PANEL DERECHO: historial de movimientos ──────────────────────
    private JPanel panelHistorial() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(31, 78, 121)),
            "Historial de Movimientos", 0, 0,
            new Font("Arial", Font.BOLD, 13), new Color(31, 78, 121)));

        // Filtro por tipo
        JPanel pFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pFiltro.setBackground(Color.WHITE);
        pFiltro.add(etiqueta("Filtrar por tipo:"));
        cmbFiltroTipo = new JComboBox<>(new String[]{ "TODOS", "ENTRADA", "SALIDA", "AJUSTE" });
        cmbFiltroTipo.addActionListener(e -> cargarMovimientos());
        pFiltro.add(cmbFiltroTipo);
        JButton btnRefrescar = boton("Refrescar", new Color(46, 117, 182));
        btnRefrescar.addActionListener(e -> cargarMovimientos());
        pFiltro.add(btnRefrescar);
        panel.add(pFiltro, BorderLayout.NORTH);

        // Tabla
        String[] columnas = { "Fecha", "Producto", "Tipo", "Cantidad", "Motivo", "Usuario" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaMovimientos = new JTable(modeloTabla);
        tablaMovimientos.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaMovimientos.setRowHeight(24);
        tablaMovimientos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaMovimientos.getTableHeader().setBackground(new Color(31, 78, 121));
        tablaMovimientos.getTableHeader().setForeground(Color.WHITE);
        tablaMovimientos.setSelectionBackground(new Color(173, 214, 241));
        tablaMovimientos.getColumnModel().getColumn(0).setPreferredWidth(120);
        tablaMovimientos.getColumnModel().getColumn(2).setMaxWidth(70);
        tablaMovimientos.getColumnModel().getColumn(3).setMaxWidth(70);

        panel.add(new JScrollPane(tablaMovimientos), BorderLayout.CENTER);
        return panel;
    }

    // ─── ACCIONES ─────────────────────────────────────────────────────

    private void buscarProducto() {
        String texto = txtBuscarProducto.getText().trim();
        try {
            List<Producto> lista = texto.isEmpty()
                ? productoRepo.listarActivos()
                : productoRepo.buscar(texto);
            cmbProductos.removeAllItems();
            if (lista.isEmpty()) { mostrarError("No se encontraron productos."); return; }
            for (Producto p : lista) cmbProductos.addItem(p);
            actualizarStockLabel();
        } catch (Exception ex) {
            mostrarError("Error buscando productos: " + ex.getMessage());
        }
    }

    private void actualizarStockLabel() {
        Producto p = (Producto) cmbProductos.getSelectedItem();
        if (p == null) {
            lblStockActual.setText("Stock actual: —");
        } else {
            lblStockActual.setText("Stock actual: " + p.getStockDisponible() + " unidades");
            lblStockActual.setForeground(p.getStockDisponible() > 0
                ? new Color(55, 86, 35)
                : new Color(180, 40, 30));
        }
    }

    private void registrarMovimiento() {
        Producto p = (Producto) cmbProductos.getSelectedItem();
        if (p == null) { mostrarError("Seleccione un producto."); return; }

        int cantidad = (int) spnCantidad.getValue();
        String motivo  = txtMotivo.getText().trim();
        String usuario = txtUsuario.getText().trim();

        try {
            if (rbEntrada.isSelected()) {
                servicio.registrarEntrada(p, cantidad, motivo, usuario);
            } else if (rbSalida.isSelected()) {
                servicio.registrarSalida(p, cantidad, motivo, usuario);
            } else {
                servicio.registrarAjuste(p, cantidad, motivo, usuario);
            }
            mostrarExito("Movimiento registrado correctamente.");
            limpiarFormulario();
            cargarMovimientos();
            cargarProductos(); // refresca stocks en el combo
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void cargarMovimientos() {
        try {
            String filtro = (String) cmbFiltroTipo.getSelectedItem();
            List<MovimientoInventario> lista;

            if ("TODOS".equals(filtro)) {
                lista = servicio.listarTodos();
            } else {
                lista = servicio.listarPorTipo(filtro);
            }

            modeloTabla.setRowCount(0);
            for (MovimientoInventario m : lista) {
                modeloTabla.addRow(new Object[]{
                    m.getFecha() != null ? m.getFecha().format(FMT) : "—",
                    m.getProducto().getNombre(),
                    m.getTipo(),
                    m.getCantidad(),
                    m.getMotivo(),
                    m.getUsuario()
                });
            }
        } catch (Exception ex) {
            mostrarError("Error cargando movimientos: " + ex.getMessage());
        }
    }

    private void cargarProductos() {
        try {
            List<Producto> lista = productoRepo.listarActivos();
            cmbProductos.removeAllItems();
            for (Producto p : lista) cmbProductos.addItem(p);
            actualizarStockLabel();
        } catch (Exception ex) {
            mostrarError("Error cargando productos: " + ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtBuscarProducto.setText("");
        txtMotivo.setText("");
        txtUsuario.setText("");
        spnCantidad.setValue(1);
        rbEntrada.setSelected(true);
        cargarProductos();
    }

    // ─── HELPERS ──────────────────────────────────────────────────────

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        return l;
    }

    private JButton boton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return btn;
    }

    private void mostrarExito(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}