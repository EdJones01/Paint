import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class PaintFrame extends JFrame implements WindowListener {
    private final PaintPanel paintPanel;

    public PaintFrame() {
        setTitle("Paint");
        addWindowListener(this);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        paintPanel = new PaintPanel();
        paintPanel.setPreferredSize(new Dimension(1400, 900));
        setJMenuBar(new PaintMenuBar(paintPanel));
        setContentPane(paintPanel);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (paintPanel.isChangesMade()) {
            if (Tools.showYesNoDialog("Changes has been made, are you sure you want to exit?")) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
