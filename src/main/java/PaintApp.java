import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Clase principal que inicia la aplicación.
 */
public class PaintApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame("Mandala Maker");
            frame.setupAndShow();
        });
    }
}

/**
 * Ventana principal del programa, maneja la creación y disposición de los componentes.
 */
class MainFrame extends JFrame {
    private DrawingPanel drawingPanel;
    private ToolPanel toolPanel;

    public MainFrame(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        drawingPanel = new DrawingPanel();
        toolPanel = new ToolPanel(drawingPanel);
        add(toolPanel, BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    public void setupAndShow() {
        setVisible(true);
    }
}

/**
 * Panel de herramientas para interactuar con el área de dibujo.
 */
class ToolPanel extends JPanel {
    private JButton btnColor, btnSave, btnClear;
    private JComboBox<String> strokeOptions;

    public ToolPanel(DrawingPanel panel) {
        btnColor = new JButton("Color");
        btnColor.addActionListener(e -> chooseColor(panel));
        
        btnSave = new JButton("Save");
        btnSave.addActionListener(e -> saveDrawing(panel));
        
        btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> panel.clear());

        strokeOptions = new JComboBox<>(new String[]{"Thin", "Medium", "Thick"});
        strokeOptions.addActionListener(e -> updateStroke(panel));

        add(btnColor);
        add(strokeOptions);
        add(btnClear);
        add(btnSave);
    }

    private void chooseColor(DrawingPanel panel) {
        Color color = JColorChooser.showDialog(this, "Choose a Color", panel.getCurrentColor());
        if (color != null) {
            panel.setCurrentColor(color);
        }
    }

    private void saveDrawing(DrawingPanel panel) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                ImageIO.write(panel.getCanvasImage(), "PNG", file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateStroke(DrawingPanel panel) {
        String selected = (String) strokeOptions.getSelectedItem();
        switch (selected) {
            case "Thin": panel.setStrokeWidth(1); break;
            case "Medium": panel.setStrokeWidth(5); break;
            case "Thick": panel.setStrokeWidth(10); break;
        }
    }
}

/**
 * Panel de dibujo donde se realizan todas las operaciones gráficas.
 */
class DrawingPanel extends JPanel {
    private BufferedImage canvas;
    private Color currentColor = Color.BLACK;
    private int strokeWidth = 5;

    public DrawingPanel() {
        canvas = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(canvas.getWidth(), canvas.getHeight()));
        initDrawing();
    }

    private void initDrawing() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                draw(e.getPoint());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                draw(e.getPoint());
            }
        });
    }

    private void draw(Point point) {
        Graphics2D g = canvas.createGraphics();
        g.setColor(currentColor);
        g.fillOval(point.x - strokeWidth / 2, point.y - strokeWidth / 2, strokeWidth, strokeWidth);
        g.dispose();
        repaint();
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setStrokeWidth(int width) {
        this.strokeWidth = width;
    }

    public void clear() {
        Graphics2D g = canvas.createGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g.dispose();
        repaint();
    }

    public BufferedImage getCanvasImage() {
        return canvas;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null);
    }
}
