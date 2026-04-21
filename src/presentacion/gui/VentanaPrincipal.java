package presentacion.gui;

import dto.FormatoSalida;
import dto.RespuestaPadron;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URI;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import logica.ServicioPadron;
import util.Serializador;

public class VentanaPrincipal extends JFrame {

    private final ServicioPadron servicioPadron;
    private final int puertoHttp;
    private final int puertoTcp;

    private JTextField txtCedula;
    private JComboBox<String> cmbFormato;
    private JTextArea txtResultado;
    private JLabel lblHttp;
    private JLabel lblTcp;

    public VentanaPrincipal(ServicioPadron servicioPadron, int puertoHttp, int puertoTcp) {
        this.servicioPadron = servicioPadron;
        this.puertoHttp = puertoHttp;
        this.puertoTcp = puertoTcp;
        inicializar();
    }

    private void inicializar() {
        setTitle("Sistema de Padrón Electoral - Consulta Dinámica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 560);
        setMinimumSize(new Dimension(760, 500));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelPrincipal = new JPanel(new BorderLayout(12, 12));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        panelPrincipal.setBackground(new Color(245, 247, 250));

        panelPrincipal.add(crearPanelSuperior(), BorderLayout.NORTH);
        panelPrincipal.add(crearPanelCentro(), BorderLayout.CENTER);
        panelPrincipal.add(crearPanelInferior(), BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
        actualizarReferencias();
    }

    private JPanel crearPanelSuperior() {
        JPanel panelSuperior = new JPanel(new BorderLayout(8, 8));
        panelSuperior.setOpaque(false);

        JLabel lblTitulo = new JLabel("La dirección cambia automáticamente según lo que escribas abajo:");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(33, 37, 41));
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelReferencias = new JPanel(new GridLayout(2, 1, 4, 4));
        panelReferencias.setOpaque(false);

        lblHttp = new JLabel();
        lblHttp.setFont(new Font("Monospaced", Font.PLAIN, 14));
        lblHttp.setForeground(new Color(0, 128, 0));
        lblHttp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblHttp.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                abrirNavegador();
            }
        });

        lblTcp = new JLabel();
        lblTcp.setFont(new Font("Monospaced", Font.PLAIN, 14));
        lblTcp.setForeground(new Color(0, 100, 0));

        panelReferencias.add(lblHttp);
        panelReferencias.add(lblTcp);
        panelSuperior.add(panelReferencias, BorderLayout.CENTER);

        return panelSuperior;
    }

    private JPanel crearPanelCentro() {
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.setOpaque(false);

        JPanel panelFormulario = new JPanel(new GridLayout(2, 2, 10, 10));
        panelFormulario.setOpaque(false);
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JLabel lblCedula = new JLabel("Número de Cédula:");
        lblCedula.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panelFormulario.add(lblCedula);

        txtCedula = new JTextField();
        txtCedula.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtCedula.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                actualizarReferencias();
            }
        });
        panelFormulario.add(txtCedula);

        JLabel lblFormato = new JLabel("Formato de salida:");
        lblFormato.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panelFormulario.add(lblFormato);

        cmbFormato = new JComboBox<>(new String[]{"JSON", "XML"});
        cmbFormato.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cmbFormato.addActionListener(e -> actualizarReferencias());
        panelFormulario.add(cmbFormato);

        panelCentro.add(panelFormulario, BorderLayout.NORTH);

        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtResultado.setBackground(new Color(20, 20, 20));
        txtResultado.setForeground(Color.WHITE);
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        txtResultado.setText("Aquí aparecerá la respuesta en JSON o XML.\nProbá con una cédula válida del archivo PADRON_COMPLETO.txt.");

        JScrollPane scroll = new JScrollPane(txtResultado);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        panelCentro.add(scroll, BorderLayout.CENTER);

        return panelCentro;
    }

    private JPanel crearPanelInferior() {
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelInferior.setOpaque(false);

        JButton btnConsultar = new JButton("Consultar Localmente");
        btnConsultar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnConsultar.setPreferredSize(new Dimension(240, 40));
        btnConsultar.addActionListener(e -> consultarLocalmente());
        panelInferior.add(btnConsultar);

        return panelInferior;
    }

    private void actualizarReferencias() {
        String cedula = txtCedula != null ? txtCedula.getText().trim() : "";
        String formato = cmbFormato != null ? cmbFormato.getSelectedItem().toString().toLowerCase() : "json";

        lblHttp.setText("HTTP: http://localhost:" + puertoHttp + "/padron?cedula=" + cedula + "&format=" + formato);
        lblTcp.setText("TCP: localhost:" + puertoTcp + "  (Comando: GET|" + cedula + "|" + formato.toUpperCase() + ")");
    }

    private void consultarLocalmente() {
        try {
            String formatoTexto = cmbFormato.getSelectedItem().toString();
            FormatoSalida formato = FormatoSalida.desde(formatoTexto);
            RespuestaPadron respuesta = servicioPadron.atender(txtCedula.getText());
            txtResultado.setText(Serializador.serializar(respuesta, formato));
            txtResultado.setCaretPosition(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error al consultar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirNavegador() {
        try {
            if (!Desktop.isDesktopSupported()) {
                throw new UnsupportedOperationException("Desktop no soportado en este equipo.");
            }
            String cedula = txtCedula.getText().trim();
            String formato = cmbFormato.getSelectedItem().toString().toLowerCase();
            String url = "http://localhost:" + puertoHttp + "/padron?cedula=" + cedula + "&format=" + formato;
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo abrir el navegador.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void mostrar(ServicioPadron servicioPadron, int puertoHttp, int puertoTcp) {
        SwingUtilities.invokeLater(() -> {
            aplicarLookAndFeel();
            VentanaPrincipal ventana = new VentanaPrincipal(servicioPadron, puertoHttp, puertoTcp);
            ventana.setVisible(true);
        });
    }

    private static void aplicarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // Se continúa con el look and feel por defecto.
        }
    }
}
