import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Clase principal que inicia la aplicación.
 */
public class MandalaDrawingApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame("Mandala Maker");
            frame.setupAndShow();
        });
    }
}

/**
 * Ventana principal del programa, maneja la creación y disposición de los
 * componentes.
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
    private JComboBox<String> strokeOptions, modeOptions;
    private JSpinner symmetrySpinner;

    public ToolPanel(DrawingPanel panel) {
        btnColor = new JButton("Color");
        btnColor.addActionListener(e -> chooseColor(panel));

        btnSave = new JButton("Save");
        btnSave.addActionListener(e -> saveDrawing(panel));

        btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> panel.clear());

        strokeOptions = new JComboBox<>(new String[] { "Thin", "Medium", "Thick" });
        strokeOptions.addActionListener(e -> updateStroke(panel));

        SpinnerModel model = new SpinnerNumberModel(8, 2, 24, 1);
        symmetrySpinner = new JSpinner(model);
        symmetrySpinner.addChangeListener(e -> panel.setSymmetry((Integer) ((JSpinner) e.getSource()).getValue()));

        modeOptions = new JComboBox<>(new String[] { "Mandala", "Mirror", "Grid" });
        modeOptions.addActionListener(e -> panel.setDrawingMode((String) modeOptions.getSelectedItem()));

        add(btnColor);
        add(strokeOptions);
        add(symmetrySpinner);
        add(modeOptions);
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
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(), "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateStroke(DrawingPanel panel) {
        String selected = (String) strokeOptions.getSelectedItem();
        switch (selected) {
            case "Thin":
                panel.setStrokeWidth(1);
                break;
            case "Medium":
                panel.setStrokeWidth(5);
                break;
            case "Thick":
                panel.setStrokeWidth(10);
                break;
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
    private int symmetry = 8; // Número de veces que se duplica el trazo
    private String drawingMode = "Mandala";

    public DrawingPanel() {
        canvas = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(canvas.getWidth(), canvas.getHeight()));
        initDrawing();
    }

    private void initDrawing() {
        MouseAdapter ma = new MouseAdapter() {
            Point lastPoint = null;

            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
                draw(lastPoint, lastPoint);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point newPoint = e.getPoint();
                draw(lastPoint, newPoint);
                lastPoint = newPoint;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastPoint = null;
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    private void draw(Point start, Point end) {
        switch (drawingMode) {
            case "Mandala":
                drawMandala(start, end);
                break;
            case "Mirror":
                drawMirror(start, end);
                break;
            case "Grid":
                drawGrid(start, end);
                break;
        }
    }

    private void drawMandala(Point start, Point end) {
        double centerX = getWidth() / 2.0;
        double centerY = getHeight() / 2.0;
        double angleStep = 2 * Math.PI / symmetry;

        Graphics2D g = canvas.createGraphics();
        g.setColor(currentColor);
        g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        AffineTransform transform = new AffineTransform();
        for (int i = 0; i < symmetry; i++) {
            transform.setToRotation(i * angleStep, centerX, centerY);
            AffineTransform original = g.getTransform();
            g.transform(transform);
            g.drawLine((int) ((start.x - centerX) + centerX), (int) ((start.y - centerY) + centerY),
                    (int) ((end.x - centerX) + centerX), (int) ((end.y - centerY) + centerY));
            g.setTransform(original);
        }

        g.dispose();
        repaint();
    }

    private void drawMirror(Point start, Point end) {
        Graphics2D g = canvas.createGraphics();
        g.setColor(currentColor);
        g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int centerY = getHeight() / 2;

        // Dibujo original
        g.drawLine(start.x, start.y, end.x, end.y);
        // Reflejo vertical
        g.drawLine(start.x, 2 * centerY - start.y, end.x, 2 * centerY - end.y);

        g.dispose();
        repaint();
    }

    private void drawGrid(Point start, Point end) {
        Graphics2D g = canvas.createGraphics();
        g.setColor(currentColor);
        g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int width = getWidth();
        int height = getHeight();
        int cellWidth = width / 3;
        int cellHeight = height / 3;

        // Calcular en qué celda se inició el dibujo
        int cellStartX = start.x / cellWidth;
        int cellStartY = start.y / cellHeight;
        // int cellEndX = end.x / cellWidth;
        // int cellEndY = end.y / cellHeight;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // Calcular el desplazamiento de cada celda
                int offsetX = i * cellWidth - cellStartX * cellWidth;
                int offsetY = j * cellHeight - cellStartY * cellHeight;

                // Dibuja en cada celda
                g.drawLine(start.x + offsetX, start.y + offsetY, end.x + offsetX, end.y + offsetY);
            }
        }

        g.dispose();
        repaint();
    }

    public void setStrokeWidth(int width) {
        this.strokeWidth = width;
    }

    public BufferedImage getCanvasImage() {
        return canvas;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setSymmetry(int symmetry) {
        if (drawingMode.equals("Mandala")) {
            this.symmetry = symmetry;
        }
    }

    public void setDrawingMode(String mode) {
        this.drawingMode = mode;
    }

    public void clear() {
        Graphics2D g = canvas.createGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g.dispose();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null);
    }
}
