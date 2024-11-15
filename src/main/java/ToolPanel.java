import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ToolPanel extends JPanel {
    private JButton btnColor, btnSave, btnClear;
    private JComboBox<String> strokeOptions, modeOptions;
    private JSpinner symmetrySpinner;

    public ToolPanel(DrawingPanel panel) {
        btnColor = new JButton("Color");
        btnColor.addActionListener(e -> chooseColor(panel));

        btnSave = new JButton("Guardar");
        btnSave.addActionListener(e -> saveDrawing(panel));

        btnClear = new JButton("Limpiar");
        btnClear.addActionListener(e -> panel.clear());

        strokeOptions = new JComboBox<>(new String[] { "Delgada", "Medianda", "Gruesa" });
        strokeOptions.addActionListener(e -> updateStroke(panel));

        SpinnerModel model = new SpinnerNumberModel(8, 2, 24, 1);
        symmetrySpinner = new JSpinner(model);
        symmetrySpinner.addChangeListener(e -> panel.setSymmetry((Integer) ((JSpinner) e.getSource()).getValue()));

        modeOptions = new JComboBox<>(new String[] { "Mandala", "Espejo", "Red" });
        modeOptions.addActionListener(e -> panel.setDrawingMode((String) modeOptions.getSelectedItem()));

        add(btnColor);
        add(strokeOptions);
        add(symmetrySpinner);
        add(modeOptions);
        add(btnClear);
        add(btnSave);
    }

    private void chooseColor(DrawingPanel panel) {
        Color color = JColorChooser.showDialog(this, "Elegir un Color", panel.getCurrentColor());
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
