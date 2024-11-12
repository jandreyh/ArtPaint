import javax.swing.JFrame;
import java.awt.*;

public class MainFrame extends JFrame {
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

