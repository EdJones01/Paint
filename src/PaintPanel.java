import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PaintPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, ChangeListener {
    private enum CURSOR_MODE {
        BRUSH,
        SELECTION,
        COLOR
    }

    private BufferedImage backgroundImage;
    private LinkedList<LinkedList<PaintPoint>> painting = new LinkedList<>();
    private LinkedList<LinkedList<LinkedList<PaintPoint>>> previousStates = new LinkedList<>();
    private LinkedList<PaintPoint> currentStroke = new LinkedList<>();
    private Color brushColor = Color.black;
    private int strokeWeight = 3;
    private boolean changesMade = false;

    private CURSOR_MODE cursorMode = CURSOR_MODE.BRUSH;

    private Polygon selection = new Polygon();

    public PaintPanel() {
        setFocusable(true);
        setBackground(Color.white);
        addMouseListener(this);
        addMouseMotionListener(this);

        Tools.addKeyBinding(this, new int[]{KeyEvent.VK_DELETE, KeyEvent.VK_BACK_SPACE}, "delete", (evt) -> {
            if (selection.npoints > 0) {
                deleteSelectedStrokes();
                selection = new Polygon();
                repaint();
            }
        });

        Tools.addKeyBinding(this, KeyEvent.VK_ESCAPE, "deselect", (evt) -> {
            selection = new Polygon();
            repaint();
        });
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundImage != null)
            g2.drawImage(backgroundImage, 0, 0, this);

        for (LinkedList<PaintPoint> stroke : painting)
            drawStroke(g2, stroke);

        drawStroke(g2, currentStroke);

        if (selection.npoints > 0) {
            drawSelectionRectangle(g2);
        }
    }

    private void drawStroke(Graphics2D g2, LinkedList<PaintPoint> stroke) {
        for (int i = 0; i < stroke.size(); i++) {
            PaintPoint p = stroke.get(i);
            g2.setStroke(p.getStroke());
            g2.setColor(p.getColor());
            try {
                PaintPoint p2 = stroke.get(i + 1);
                g2.drawLine(p.getX(), p.getY(), p2.getX(), p2.getY());
            } catch (Exception e) {
            }
        }
    }

    private void drawSelectionRectangle(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 255, 50));
        Rectangle rect = selection.getBounds();
        g2.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    private void changeColorOfSelectedStrokes() {
        LinkedList<Integer> selectedIndices = getSelectedStrokeIndices();
        Color newColor = JColorChooser.showDialog(this, "Select new color", brushColor);

        for (int index : selectedIndices) {
            for (PaintPoint point : painting.get(index)) {
                point.setColor(newColor);
            }
        }
    }

    private void deleteSelectedStrokes() {
        LinkedList<LinkedList<PaintPoint>> newPainting = new LinkedList<>();
        LinkedList<Integer> selectedIndices = getSelectedStrokeIndices();
        previousStates.addFirst(clonePainting());

        for (int i = 0; i < painting.size(); i++) {
            if (!selectedIndices.contains(i))
                newPainting.add(painting.get(i));
        }
        painting = newPainting;
    }

    private void save() {
        BufferedImage bi = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        this.paint(g);
        g.dispose();
        try {
            String path = choosePath("Save");
            if (path != null)

                ImageIO.write(bi, "png", new File(path + ".png"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to save file\n\nERROR:\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    private void load() {
        try {
            String path = choosePath("Load");
            if (path != null)
                reset();
            backgroundImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to load file\n\nERROR:\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    private String choosePath(String type) {
        String path = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setDialogTitle(type);
        int retrival = chooser.showOpenDialog(null);
        if (retrival == JFileChooser.APPROVE_OPTION)
            path = chooser.getSelectedFile().getPath();
        return path;
    }

    private void reset() {
        cursorMode = CURSOR_MODE.BRUSH;
        selection = new Polygon();
        painting.clear();
        currentStroke.clear();
        backgroundImage = null;
        previousStates.clear();
    }

    private LinkedList<LinkedList<PaintPoint>> clonePainting() {
        LinkedList<LinkedList<PaintPoint>> clone = new LinkedList<>();
        for (LinkedList<PaintPoint> stroke : painting) {
            LinkedList<PaintPoint> strokeClone = new LinkedList<>();
            for (PaintPoint point : stroke)
                strokeClone.add(point.clone());
            clone.add(strokeClone);
        }
        return clone;
    }

    private LinkedList<Integer> getSelectedStrokeIndices() {
        LinkedList<Integer> strokeIndices = new LinkedList<>();
        for (int i = 0; i < painting.size(); i++) {
            LinkedList<PaintPoint> stroke = painting.get(i);
            boolean flag = false;
            for (PaintPoint point : stroke) {
                if (Tools.pointWithin(point.getPoint(), selection.getBounds())) {
                    flag = true;
                }
            }
            if (flag)
                strokeIndices.add(i);
        }
        return strokeIndices;
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        selection = new Polygon();
        if (cmd.equals("new")) {
                if (changesMade && !Tools.showYesNoDialog("Changes has been made, are you sure you want to exit?")) {
                    return;
                }
            changesMade = false;
            reset();
        }
        if (cmd.equals("save"))
            save();
        if (cmd.equals("load"))
            load();
        if (cmd.equals("undo")) {
            changesMade = true;
            if (previousStates.size() > 0) {
                painting = previousStates.getFirst();
                previousStates.removeFirst();
            }
        }
        if (cmd.equals("color"))
            brushColor = JColorChooser.showDialog(this, "Select new brush color", brushColor);
        if (cmd.equals("invert")) {
            changesMade = true;
            previousStates.addFirst(clonePainting());
            for (LinkedList<PaintPoint> stroke : painting)
                for (PaintPoint point : stroke) {
                    point.invertColor();
                }
        }
        if (cmd.equals("flipVer")) {
            changesMade = true;
            previousStates.addFirst(clonePainting());
            for (LinkedList<PaintPoint> stroke : painting)
                for (PaintPoint point : stroke) {
                    point.setX(getWidth() - point.getX());
                }
        }
        if (cmd.equals("flipHor")) {
            changesMade = true;
            previousStates.addFirst(clonePainting());
            for (LinkedList<PaintPoint> stroke : painting)
                for (PaintPoint point : stroke) {
                    point.setY(getHeight() - point.getY());
                }
        }
        if (cmd.equals("brush"))
            cursorMode = CURSOR_MODE.BRUSH;
        if (cmd.equals("selection"))
            cursorMode = CURSOR_MODE.SELECTION;
        if (cmd.equals("selectionColor")) {
            if (selection.npoints > 0) {
                changeColorOfSelectedStrokes();
                selection = new Polygon();
                repaint();
            }
        }
        if (cmd.equals("pickColor"))
            cursorMode = CURSOR_MODE.COLOR;
        if (cmd.equals("selectAll")) {
            selection.addPoint(0, 0);
            selection.addPoint(getWidth(), getHeight());
        }

        repaint();
    }

    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (cursorMode == CURSOR_MODE.BRUSH)
                currentStroke.add(new PaintPoint(e.getPoint(), brushColor, new BasicStroke(strokeWeight)));
            if (cursorMode == CURSOR_MODE.SELECTION) {
                setSelectionEnd(e.getX(), e.getY());
            }
        }
        repaint();
    }

    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            changesMade = true;
            if (cursorMode == CURSOR_MODE.BRUSH)
                currentStroke.add(new PaintPoint(e.getPoint(), brushColor, new BasicStroke(strokeWeight)));
            if (cursorMode == CURSOR_MODE.SELECTION) {
                if (selection.npoints == 0)
                    setSelectionStart(e.getX(), e.getY());
                else
                    selection = new Polygon();
            }
            if (cursorMode == CURSOR_MODE.COLOR) {
                BufferedImage bi = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB);
                Graphics g = bi.createGraphics();
                this.paint(g);
                g.dispose();
                brushColor = new Color(bi.getRGB(e.getX(), e.getY()));
                cursorMode = CURSOR_MODE.BRUSH;
            }
        }
        repaint();
    }

    private void setSelectionStart(int x, int y) {
        selection = new Polygon();
        selection.addPoint(x, y);
    }

    private void setSelectionEnd(int x, int y) {
        selection.addPoint(x, y);
    }

    @SuppressWarnings("unchecked")
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            previousStates.addFirst((LinkedList<LinkedList<PaintPoint>>) painting.clone());
            painting.addLast((LinkedList<PaintPoint>) currentStroke.clone());
            currentStroke.clear();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            strokeWeight = source.getValue();
        }
    }

    public boolean isChangesMade() {
        return changesMade;
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }
}