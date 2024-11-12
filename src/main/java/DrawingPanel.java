import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;


public class DrawingPanel extends JPanel {
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

        int cellStartX = start.x / cellWidth;
        int cellStartY = start.y / cellHeight;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int offsetX = i * cellWidth - cellStartX * cellWidth;
                int offsetY = j * cellHeight - cellStartY * cellHeight;

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
