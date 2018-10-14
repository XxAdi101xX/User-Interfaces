import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.lang.*;

public class Shape {
    private Tool shape;
    private Color borderColour;
    private Color backgroundColour;
    private Boolean isFilled;
    private int lineWidth;
    private Point startPoint;
    private Point endPoint;

    Shape(Tool shape, Color borderColour, int lineWidth, int startingX, int startingY, int endingX, int endingY) {
        this.shape = shape;
        this.borderColour = borderColour;
        this.backgroundColour = null;
        this.isFilled = false;
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

    public Boolean getFilled() {
        return isFilled;
    }

    public void setFilled() {
        isFilled = true;
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
            return Math.abs(remainder) <= lineWidth;
        } else if (shape == Tool.RECTANGLE) {
            return getRectangleObject().contains(intersectionPoint.x, intersectionPoint.y);
        } else if (shape == Tool.CIRCLE) {
            return getEllipsesObject().contains(intersectionPoint.x, intersectionPoint.y);
        }
        return false; // should never return here
    }

    public Rectangle getRectangleObject() {
        Rectangle rectangle= new Rectangle(startPoint);
        rectangle.add(endPoint);
        return rectangle;
    }

    public Ellipse2D.Double getEllipsesObject() {
        Ellipse2D.Double ellipses;
        int width = Math.abs(endPoint.x - startPoint.x); 
        int height = Math.abs(endPoint.y - startPoint.y);

        if (startPoint.y < endPoint.y) {
            if (startPoint.x < endPoint.x) {
                ellipses = new Ellipse2D.Double(startPoint.x, startPoint.y, width, height);
            } else {
                ellipses = new Ellipse2D.Double(startPoint.x - width, startPoint.y, width, height);
            }
        } else {
            if (startPoint.x < endPoint.x) {
                ellipses = new Ellipse2D.Double(startPoint.x, startPoint.y - height, width, height);
            } else {
                ellipses = new Ellipse2D.Double(startPoint.x - width, startPoint.y - height, width, height);
            }
        }

        return ellipses;
    }

    public Line2D.Double getLineObject() {
        Line2D.Double line = new Line2D.Double(startPoint, endPoint);
        return line;
    }
}