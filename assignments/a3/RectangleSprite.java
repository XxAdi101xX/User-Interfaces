

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;


/**
 * A simple demo of how to create a rectangular sprite.
 * 
 * Michael Terry & Jeff Avery
 */
public class RectangleSprite extends Sprite {

    // private Rectangle2D rect = null;
    private RoundRectangle2D rect = null;
    private double width = 0;
    private double height = 0;

    /**
     * Creates a rectangle based at the origin with the specified
     * width and height
     */
    public RectangleSprite(SpriteType spriteType, double width, double height) {
        super(spriteType);
        initialize(width, height);
    }
    /**
     * Creates a rectangle based at the origin with the specified
     * width, height, and parent
     */
    public RectangleSprite(SpriteType spriteType, double width, double height, Sprite parentSprite) {
        super(spriteType, parentSprite);
        initialize(width, height);
    }

    private void initialize(double width, double height) {
        // rect = new Rectangle2D.Double(0, 0, width, height);
        this.width = width;
        this.height = height;
        rect = new RoundRectangle2D.Double(0, 0, width, height, 15, 15);
    }
    
    /**
     * Test if our rectangle contains the point specified.
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
        return rect.contains(newPoint);
        // Point MT = new Point();
        // inverseTransform.transform((Point)p, MT);
        // return rect.contains(MT);
    }

    protected void drawSprite(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(4));
        g2d.setColor(Color.BLACK);
        g2d.draw(rect);
    }

    protected void updateDimensions(double xInc, double yInc) {
        initialize(width + xInc, height + yInc);
    }

    public Point2D getDimensions() {
        return new Point2D.Double(width, height);
    }
}
