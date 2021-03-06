

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;


/**
 * Ellipse Sprite Class
 */
public class EllipseSprite extends Sprite {

    private Ellipse2D ellipse = null;
    private double width;
    private double height;

    /**
     * Creates an ellipse based at the origin with the specified
     * width and height
     */
    public EllipseSprite(SpriteType type, double width, double height) {
        super(type);
        this.initialize(width, height);
    }
    /**
     * Creates an ellipse based at the origin with the specified
     * width, height, and parent
     */
    public EllipseSprite(SpriteType type, double width, double height, Sprite parentSprite) {
        super(type, parentSprite);
        this.initialize((double)width, (double)height);
    }
    
    private void initialize(double width, double height) {
        this.width = width;
        this.height = height;
        ellipse = new Ellipse2D.Double(0, 0, width, height);
    }
    
    /**
     * Test if our ellipse contains the point specified.
     */
    public boolean pointInside(Point2D p) {
        AffineTransform fullTransform = this.getFullTransform();
        AffineTransform inverseTransform = null;
        try {
            inverseTransform = fullTransform.createInverse();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        Point2D newPoint = (Point2D)p.clone();
        inverseTransform.transform(newPoint, newPoint);
        return ellipse.contains(newPoint);
    }

    /**
     * Draw the ellipse
     */
    protected void drawSprite(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(4));
        g2d.setColor(Color.BLACK);
        g2d.draw(ellipse);
    }

    /**
     * Update the ellipse dimensions
     */
    protected void updateDimensions(double xInc, double yInc) {
        initialize(width + xInc, height + yInc);
    }

    /**
     * Get the ellipse dimensions
     */
    public Point2D getDimensions() {
        return new Point2D.Double(width, height);
    }
}
