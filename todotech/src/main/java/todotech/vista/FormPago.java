package todotech.vista;

import todotech.estrategia.*;
import todotech.modelo.OrdenVenta;
import todotech.servicio.OrdenServicio;

import javax.swing.*;
import java.awt.*;

/**
 * Formulario de Procesamiento de Pago.
 * MOD-03 — Procesar Métodos de Pago.
 * Usa el patrón Strategy: selecciona la estrategia según el medio de pago.
 */
public class FormPago extends JPanel {

    private final OrdenServicio ordenServicio = new OrdenServicio();

    // Buscar orden
    private JTextField txtNumeroOrden;
    private JLabel     lblDatosOrden;
    private OrdenVenta ordenActual;

    // Selección de pago
    private JRadioButton rbEfectivo;
    private JRadioButton rbTarjeta;
    private JRadioButton rbCheque;

    // Paneles por método
    private JPanel panelEfectivo;
    private JPanel panelTarjeta;
    private JPanel panelCheque;
    private JPanel panelMetodos;

    // Campos efectivo
    private JTextField txtMontoBrindado;

    // Campos tarjeta
    private JTextField txtNumeroTarjeta;

    // Campos cheque
    private JTextField txtNumeroCheque;

    public FormPago() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));
        construirUI();
    }

    private void construirUI() {
        JLabel titulo = new JLabel("Procesar Pago");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(31, 78, 121));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);
        add(panelCentral(), BorderLayout.CENTER);
        add(panelBotonPagar(), BorderLayout.SOUTH);
    }

    private JPanel panelCentral() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(31, 78, 121)),
            "Datos del Pago", 0, 0,
            new Font("Arial", Font.BOLD, 13), new Color(31, 78, 121)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Buscar orden
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Número de orden:"), gbc);
        gbc.gridx = 1;
        txtNumeroOrden = new JTextField(15);
        panel.add(txtNumeroOrden, gbc);
        gbc.gridx = 2;
        JButton btnBuscar = boton("Buscar", new Color(46, 117, 182));
        btnBuscar.addActionListener(e -> buscarOrden());
        panel.add(btnBuscar, gbc);

        // Datos de la orden
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        lblDatosOrden = new JLabel("No hay orden seleccionada");
        lblDatosOrden.setFont(new Font("Arial", Font.BOLD, 14));
        lblDatosOrden.setForeground(new Color(31, 78, 121));
        panel.add(lblDatosOrden, gbc);

        // Separador
        gbc.gridy = 2;
        panel.add(new JSeparator(), gbc);

        // Selección de método de pago
        gbc.gridy = 3; gbc.gridwidth = 1; gbc.gridx = 0;
        panel.add(new JLabel("Medio de pago:"), gbc);

        rbEfectivo = new JRadioButton("Efectivo");
        rbTarjeta  = new JRadioButton("Tarjeta / Redcompra");
        rbCheque   = new JRadioButton("Cheque");
        rbEfectivo.setSelected(true);

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbEfectivo);
        grupo.add(rbTarjeta);
        grupo.add(rbCheque);

        rbEfectivo.addActionListener(e -> mostrarPanel("efectivo"));
        rbTarjeta.addActionListener(e  -> mostrarPanel("tarjeta"));
        rbCheque.addActionListener(e   -> mostrarPanel("cheque"));

        JPanel pRadios = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pRadios.setBackground(Color.WHITE);
        pRadios.add(rbEfectivo);
        pRadios.add(rbTarjeta);
        pRadios.add(rbCheque);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(pRadios, gbc);

        // Panel dinámico según método
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 3;
        panelMetodos = new JPanel(new CardLayout());
        panelMetodos.setBackground(Color.WHITE);
        panelMetodos.add(construirPanelEfectivo(), "efectivo");
        panelMetodos.add(construirPanelTarjeta(),  "tarjeta");
        panelMetodos.add(construirPanelCheque(),   "cheque");
        panel.add(panelMetodos, gbc);

        return panel;
    }

    private JPanel construirPanelEfectivo() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(Color.WHITE);
        p.add(new JLabel("Monto entregado por el cliente: $"));
        txtMontoBrindado = new JTextField(12);
        p.add(txtMontoBrindado);
        return p;
    }

    private JPanel construirPanelTarjeta() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(Color.WHITE);
        p.add(new JLabel("Número de tarjeta:"));
        txtNumeroTarjeta = new JTextField(16);
        p.add(txtNumeroTarjeta);
        return p;
    }

    private JPanel construirPanelCheque() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(Color.WHITE);
        p.add(new JLabel("Número de cheque:"));
        txtNumeroCheque = new JTextField(12);
        p.add(txtNumeroCheque);
        return p;
    }

    private JPanel panelBotonPagar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setBackground(new Color(31, 78, 121));
        JButton btnPagar = boton("Procesar Pago", new Color(55, 183, 100));
        btnPagar.setFont(new Font("Arial", Font.BOLD, 15));
        btnPagar.addActionListener(e -> procesarPago());
        p.add(btnPagar);
        return p;
    }

    // ─── ACCIONES ─────────────────────────────────────────────────────

    private void buscarOrden() {
        try {
            ordenActual = ordenServicio.buscarPorNumero(txtNumeroOrden.getText().trim());
            lblDatosOrden.setText(
                ordenActual.getNumeroOrden() + "  |  " +
                ordenActual.getCliente().getNombre() + "  |  " +
                String.format("Total: $%.2f", ordenActual.getTotal())
            );
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
            ordenActual = null;
            lblDatosOrden.setText("No hay orden seleccionada");
        }
    }

    private void procesarPago() {
        if (ordenActual == null) { mostrarError("Busque una orden primero."); return; }

        try {
            // ─── PATRÓN STRATEGY ───────────────────────────────────────
            // Selecciona la estrategia según el radio seleccionado
            MetodoPago estrategia;

            if (rbEfectivo.isSelected()) {
                double monto = Double.parseDouble(txtMontoBrindado.getText().trim());
                estrategia = new PagoEfectivo(monto);

            } else if (rbTarjeta.isSelected()) {
                String numero = txtNumeroTarjeta.getText().trim();
                if (numero.isEmpty()) throw new Exception("Ingrese el número de tarjeta.");
                estrategia = new PagoTarjeta(numero);

            } else {
                String numeroCheque = txtNumeroCheque.getText().trim();
                if (numeroCheque.isEmpty()) throw new Exception("Ingrese el número de cheque.");
                estrategia = new PagoCheque(numeroCheque);
            }

            // OrdenServicio delega el pago a la estrategia sin saber cuál es
            ordenServicio.procesarPago(ordenActual, estrategia);

            // Mostrar cambio si fue efectivo
            String extra = "";
            if (estrategia instanceof PagoEfectivo) {
                extra = String.format("\nCambio: $%.2f", ((PagoEfectivo) estrategia).getCambio());
            }

            mostrarExito("Pago aprobado. Orden " + ordenActual.getNumeroOrden() +
                         " marcada como PAGADA." + extra);
            limpiar();

        } catch (NumberFormatException e) {
            mostrarError("Ingrese un monto válido.");
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void mostrarPanel(String nombre) {
        CardLayout cl = (CardLayout) panelMetodos.getLayout();
        cl.show(panelMetodos, nombre);
    }

    private void limpiar() {
        ordenActual = null;
        txtNumeroOrden.setText("");
        lblDatosOrden.setText("No hay orden seleccionada");
        txtMontoBrindado.setText("");
        txtNumeroTarjeta.setText("");
        txtNumeroCheque.setText("");
        rbEfectivo.setSelected(true);
        mostrarPanel("efectivo");
    }

    private JButton boton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private void mostrarExito(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Pago Exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
