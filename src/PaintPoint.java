import java.awt.*;

public class PaintPoint {
    private Point point;
    private Color color;
    private Stroke stroke;

    public PaintPoint(Point point, Color color, Stroke stroke) {
        this.point = point;
        this.color = color;
        this.stroke = stroke;
    }

    public void invertColor() {
        color = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
    }

    public int getX() {
        return (int) point.getX();
    }

    public int getY() {
        return (int) point.getY();
    }

    public void setX(int x) {
        point.x = x;
    }

    public void setY(int y) {
        point.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public Point getPoint() {
        return point;
    }

    public PaintPoint clone() {
        return new PaintPoint(point, color, stroke);
    }
}