package todotech.vista;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Clase utilitaria de estilos visuales para TodoTech Shop.
 * Centraliza colores, fuentes y componentes estilizados.
 * Aplica flat design moderno con efecto hover.
 */
public class EstiloUI {

    // ─── PALETA DE COLORES ────────────────────────────────────────────
    public static final Color AZUL_PRINCIPAL   = new Color(7, 15, 156);   // #070F9C
    public static final Color AZUL_HOVER       = new Color(20, 35, 190);  // hover más claro
    public static final Color AZUL_PRESIONADO  = new Color(5, 10, 110);   // click más oscuro
    public static final Color TEXTO_BOTON      = new Color(255, 252, 240); // crema/blanco cálido
    public static final Color BORDE_PANEL      = new Color(231, 232, 254); // #E7E8FE
    public static final Color FONDO_APP        = new Color(245, 247, 250);
    public static final Color AZUL_TITULO      = new Color(31, 78, 121);
    public static final Color FONDO_BLANCO     = Color.WHITE;

    // ─── FUENTES ──────────────────────────────────────────────────────
    public static final Font FUENTE_BOTON      = new Font("Arial", Font.BOLD, 12);
    public static final Font FUENTE_TITULO     = new Font("Arial", Font.BOLD, 20);
    public static final Font FUENTE_ETIQUETA   = new Font("Arial", Font.PLAIN, 13);

    /**
     * Crea un botón con estilo flat design moderno.
     * Color de fondo: #070F9C (azul oscuro)
     * Texto: crema/blanco cálido
     * Sin borde visible, con efecto hover.
     *
     * @param texto   texto del botón
     * @return JButton estilizado
     */
    public static JButton boton(String texto) {
        return boton(texto, AZUL_PRINCIPAL);
    }

    /**
     * Crea un botón con color de fondo personalizado pero mismo estilo flat.
     *
     * @param texto texto del botón
     * @param fondo color de fondo base
     * @return JButton estilizado
     */
    public static JButton boton(String texto, Color fondo) {
        JButton btn = new JButton(texto);

        // Estilo base
        btn.setFont(FUENTE_BOTON);
        btn.setBackground(fondo);
        btn.setForeground(TEXTO_BOTON);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        // ─── EFECTO HOVER ─────────────────────────────────────────────
        Color colorHover    = fondo.brighter().brighter();
        Color colorPresion  = fondo.darker();

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(colorHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(fondo);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                btn.setBackground(colorPresion);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setBackground(colorHover);
            }
        });

        return btn;
    }

    /**
     * Aplica borde de panel con color #E7E8FE y título estilizado.
     */
    public static Border bordePanel(String titulo) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDE_PANEL, 2),
            titulo, 0, 0,
            new Font("Arial", Font.BOLD, 13),
            AZUL_TITULO
        );
    }

    /**
     * Crea una etiqueta estándar.
     */
    public static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE_ETIQUETA);
        return l;
    }

    /**
     * Crea una etiqueta en negrita.
     */
    public static JLabel etiquetaBold(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        return l;
    }
}