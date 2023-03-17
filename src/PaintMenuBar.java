import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.event.ChangeListener;

public class PaintMenuBar extends JMenuBar {
    private Font defaultFont = new Font("Product Sans", Font.PLAIN, 14);

    public PaintMenuBar(PaintPanel paintPanel) {
        ActionListener actionListener = (ActionListener) paintPanel;
        ChangeListener changeListener = (ChangeListener) paintPanel;


        setBackground(Color.white);

        JMenu fileMenu = new CustomMenu("File", defaultFont);
        fileMenu.add(new CustomMenuItem("New", actionListener, "new", defaultFont, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK), getIcon("new")));
        fileMenu.add(new CustomMenuItem("Save", actionListener, "save", defaultFont, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), getIcon("save")));
        fileMenu.add(new CustomMenuItem("Load", actionListener, "load", defaultFont, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK), getIcon("load")));
        fileMenu.add(new CustomMenuItem("Undo", actionListener, "undo", defaultFont, KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK), getIcon("undo")));
        fileMenu.add(new CustomMenuItem("Redo", actionListener, "redo", defaultFont, KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK), getIcon("redo")));


        JMenu brushMenu = new CustomMenu("Brush", defaultFont);
        brushMenu.add(new CustomMenuItem("Change color", actionListener, "color", defaultFont));


        brushMenu.add(new CustomMenuItem("Change brush size", defaultFont));

        JSlider brushSizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 3);
        brushSizeSlider.setMajorTickSpacing(1);
        brushSizeSlider.setPaintTicks(true);
        brushSizeSlider.setPaintLabels(true);
        brushSizeSlider.addChangeListener(changeListener);

        brushMenu.add(brushSizeSlider);

        JMenu adjustmentsMenu = new CustomMenu("Adjustments", defaultFont);
        adjustmentsMenu.add(new CustomMenuItem("Invert Colors", actionListener, "invert", defaultFont));
        adjustmentsMenu.add(new CustomMenuItem("Flip Vertically", actionListener, "flipVer", defaultFont));
        adjustmentsMenu.add(new CustomMenuItem("Flip Horizontally", actionListener, "flipHor", defaultFont));

        JMenu cursorMenu = new CustomMenu("Cursor mode", defaultFont);
        cursorMenu.add(new CustomMenuItem("Brush", actionListener, "brush", defaultFont));
        cursorMenu.add(new CustomMenuItem("Selection", actionListener, "selection", defaultFont));
        cursorMenu.add(new CustomMenuItem("Color picker", actionListener, "pickColor", defaultFont));

        JMenu selectionMenu = new CustomMenu("Selection tools", defaultFont);
        selectionMenu.add(new CustomMenuItem("Select all", actionListener, "selectAll", defaultFont, KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK)));
        selectionMenu.add(new CustomMenuItem("Change color", actionListener, "selectionColor", defaultFont));


        add(fileMenu);
        add(brushMenu);
        add(cursorMenu);
        add(adjustmentsMenu);
        add(selectionMenu);

    }

    private Icon getIcon(String filename) {
        try {
            return new ImageIcon(getClass().getResource("/resources/" + filename + ".png"));
        } catch (NullPointerException e) {
            return null;
        }
    }
}

class CustomSeperator extends JMenu {
    public CustomSeperator() {
        super("|");
        setEnabled(false);
    }
}

class CustomMenu extends JMenu {
    public CustomMenu(String title, Font font) {
        super(title);
        setFont(font);
    }
}

class CustomMenuItem extends JMenuItem {
    public CustomMenuItem(String title, ActionListener e, String cmd, Font font) {
        super(title);
        addActionListener(e);
        setActionCommand(cmd);
        setFont(font);
    }

    public CustomMenuItem(String title, Font font) {
        super(title);
        setFont(font);
        setEnabled(false);
    }

    public CustomMenuItem(String title, ActionListener e, String cmd, Font font, KeyStroke accelerator) {
        this(title, e, cmd, font);
        setAccelerator(accelerator);
    }

    public CustomMenuItem(String title, ActionListener e, String cmd, Font font, KeyStroke accelerator, Icon icon) {
        this(title, e, cmd, font, accelerator);
        setIcon(icon);
    }
}