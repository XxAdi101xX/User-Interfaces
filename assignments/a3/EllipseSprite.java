

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
 * 
 * Michael Terry & Jeff Avery
 */
public class EllipseSprite extends Sprite {

    private Ellipse2D.Double ellipse = null;

    /**
     * Creates an ellipse based at the origin with the specified
     * width and height
     */
    public EllipseSprite(SpriteType type, int width, int height) {
        super();
        setSpiritType(type);
        this.initialize(width, height);
    }
    /**
     * Creates an ellipse based at the origin with the specified
     * width, height, and parent
     */
    public EllipseSprite(SpriteType type, int width, int height, Sprite parentSprite) {
        super(parentSprite);
        setSpiritType(type);
        this.initialize(width, height);
    }
    
    private void initialize(int width, int height) {
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
        // Point MT = new Point();
        // inverseTransform.transform((Point)p, MT);
        // return ellipse.contains(MT);
    }

    protected void drawSprite(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(6));
        g2d.setColor(Color.BLACK);
        g2d.draw(ellipse);
    }
    
    public String toString() {
        return "EllipseSprite: " + ellipse;
    }
}
