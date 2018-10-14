import java.awt.Color;
import java.awt.Point;
import java.lang.*;

public class Shape {
    private Tool shape;
    private Color borderColour;
    private Color backgroundColour;
    private int lineWidth;
    private Point startPoint;
    private Point endPoint;

    Shape(Tool shape, Color borderColour, int lineWidth, int startingX, int startingY, int endingX, int endingY) {
        this.shape = shape;
        this.borderColour = borderColour;
        this.backgroundColour = null;
        this.lineWidth = lineWidth;
        this.startPoint = new Point(startingX, startingY);
        this.endPoint = new Point(endingX, endingY);
    }

    public Tool getShape() {
        return shape;
    }

    public Color getBorderColour() {
        return borderColour;
    }

    public Color getBackgroundColour() {
        return backgroundColour;
    }

    public void setBackgroundCOlour(Color backgroundColour) {
        this.backgroundColour = backgroundColour;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public Point getStartPoints() {
        return startPoint;
    }

    public void setStartPoints(int startingX, int startingY) {
        this.startPoint = new Point(startingX, startingY);
    }

    public Point getEndPoints() {
        return endPoint;
    }

    public void setEndPoints(int endingX, int endingY) {
        this.endPoint = new Point(endingX, endingY);
    }

    public Boolean hasIntersected(Point intersectionPoint) {
        if (shape == Tool.LINE) {
            double m = (double)(endPoint.y - startPoint.y) / (double)(endPoint.x - startPoint.x);
            double b = startPoint.y - (m * startPoint.x);
            double remainder = intersectionPoint.y - (m * intersectionPoint.x + b);
            System.out.println(intersectionPoint.x + " " + intersectionPoint.y);
            System.out.println(startPoint.x + " " + startPoint.y);
            System.out.println(endPoint.x + " " + endPoint.y);
            System.out.println(remainder);
            System.out.println(m + " " + b);

            return Math.abs(remainder) <= lineWidth;
        } else if (shape == Tool.RECTANGLE) {
            return false;
        }
        return false; // shoudln't reach here
    }
}