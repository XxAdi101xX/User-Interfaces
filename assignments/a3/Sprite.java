
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;

/**
 * A building block for creating your own shapes that can be
 * transformed and that can respond to input. This class is
 * provided as an example; you will likely need to modify it
 * to meet the assignment requirements.
 * 
 * Michael Terry & Jeff Avery
 */
public abstract class Sprite {
    
    /**
     * Tracks our current interaction mode after a mouse-down
     */
    protected enum InteractionMode {
        IDLE,
        DRAGGING,
        SCALING,
        ROTATING
    }

    private SpriteType spriteType = null;
    private Sprite parent = null;                               // Pointer to our parent
    private Vector<Sprite> children = new Vector<Sprite>();     // Holds all of our children
    private AffineTransform transform = new AffineTransform();  // Our transformation matrix
    protected Point2D lastPoint = null;                         // Last mouse point
    protected InteractionMode interactionMode = InteractionMode.IDLE;    // current state

    public Sprite() {}
    
    public Sprite(Sprite parent) {
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public void addChild(Sprite s) {
        children.add(s);
        s.setParent(this);
    }
    public Sprite getParent() {
        return parent;
    }
    private void setParent(Sprite s) {
        this.parent = s;
    }

    /**
     * Test whether a point, in world coordinates, is within our sprite.
     */
    public abstract boolean pointInside(Point2D p);

    /**
     * Handles a mouse down event, assuming that the event has already
     * been tested to ensure the mouse point is within our sprite.
     */
    protected void handleMouseDownEvent(MouseEvent e) {
        lastPoint = e.getPoint();
        // if (e.getButton() == MouseEvent.BUTTON1) {
        //     interactionMode = InteractionMode.ROTATING;
        // }
        switch (spriteType) {
            case BODY:
                interactionMode = InteractionMode.DRAGGING;
                break;
            case HEAD:
            case UPPERARM:
            case LOWERARM:
            case HAND:
            case FOOT:
                interactionMode = InteractionMode.ROTATING;
                break;
            // case UPPERLEG:
            //     interactionMode = InteractionMode.ROTATING;
            //     break;
            case UPPERLEG:
            case LOWERLEG:
                // interactionMode = InteractionMode.SCALING;
                // break;
                interactionMode = InteractionMode.ROTATING;
                break;
            default:
                interactionMode = InteractionMode.IDLE;
        }
        // Handle rotation, scaling mode depending on input
    }

    /**
     * Get the angle given between the origin point and the source point with respect to the X axis
     * Pseudocode from https://stackoverflow.com/questions/2198303/java-2d-drag-mouse-to-rotate-image-smoothly
     * @param origin
     * @param source
     * @return angle 
     */
    private double getAngle(Point2D origin, Point2D source) {
        double dy = source.getY() - origin.getY();
        double dx = source.getX() - origin.getX();
        double angle;
        if (dx == 0) { // special case
            angle = dy >= 0 ? Math.PI / 2 : -Math.PI / 2;
        } else {
            angle = Math.atan(dy/dx);
            if (dx < 0) { // hemisphere correction
                angle += Math.PI;
            } 
        }
        // all between 0 and 2PI
        if (angle < 0) { // between -PI/2 and 0
            angle += 2 * Math.PI;
        }

        return angle;
    }

    /**
     * Handle mouse drag event, with the assumption that we have already
     * been "selected" as the sprite to interact with.
     * This is a very simple method that only works because we
     * assume that the coordinate system has not been modified
     * by scales or rotations. You will need to modify this method
     * appropriately so it can handle arbitrary transformations.
     */
    protected void handleMouseDragEvent(MouseEvent e) {
        Point2D newPoint = e.getPoint();
        switch (interactionMode) {
            case IDLE:
                System.out.println("Warning: unhandled mouse drag event");
                break;
            case DRAGGING:
                double x_diff = newPoint.getX() - lastPoint.getX();
                double y_diff = newPoint.getY() - lastPoint.getY();
                transform.translate(x_diff, y_diff);
                break;
            case ROTATING:
                Point2D origin = new Point2D.Double(getFullTransform().getTranslateX(), getFullTransform().getTranslateY());
                double sourceAngle = getAngle(origin, lastPoint);
                double newAngle = getAngle(origin, newPoint);
                transform.rotate(newAngle - sourceAngle);
                break;
            case SCALING:
                ; // Provide scaling code here
                break;
                
        }
        // Save our last point, if it's needed next time around
        lastPoint = e.getPoint();
    }
    
    protected void handleMouseUp(MouseEvent e) {
        interactionMode = InteractionMode.IDLE;
        // Do any other interaction handling necessary here
    }
    
    /**
     * Locates the sprite that was hit by the given event.
     * You *may* need to modify this method, depending on
     * how you modify other parts of the class.
     * 
     * @return The sprite that was hit, or null if no sprite was hit
     */
    public Sprite getSpriteHit(MouseEvent e) {
        for (Sprite sprite : children) {
            Sprite s = sprite.getSpriteHit(e);
            if (s != null) {
                return s;
            }
        }
        if (this.pointInside(e.getPoint())) {
            return this;
        }
        return null;
        // return this;
    }
    
    /*
     * Important note: How transforms are handled here are only an example. You will
     * likely need to modify this code for it to work for your assignment.
     */
    
    /**
     * Returns the full transform to this object from the root
     */
    public AffineTransform getFullTransform() {
        AffineTransform returnTransform = new AffineTransform();
        Sprite curSprite = this;
        while (curSprite != null) {
            returnTransform.preConcatenate(curSprite.getLocalTransform());
            curSprite = curSprite.getParent();
        }
        return returnTransform;
    }

    /**
     * Returns our local transform
     */
    public AffineTransform getLocalTransform() {
        return (AffineTransform)transform.clone();
    }
    
    /**
     * Performs an arbitrary transform on this sprite
     */
    public void transform(AffineTransform t) {
        transform.concatenate(t);
    }

    /**
     * Draws the sprite. This method will call drawSprite after
     * the transform has been set up for this sprite.
     */
    public void draw(Graphics2D g) {
        AffineTransform oldTransform = g.getTransform();

        // Set to our transform
        Graphics2D g2 = (Graphics2D)g;
        AffineTransform currentAT = g2.getTransform();
        currentAT.concatenate(this.getFullTransform());
        g2.setTransform(currentAT);
        
        // Draw the sprite (delegated to sub-classes)
        this.drawSprite(g);
        
        // Restore original transform
        g.setTransform(oldTransform);

        // Draw children
        for (Sprite sprite : children) {
            sprite.draw(g);
        }
    }
    
    /**
     * Set the sprite type
     */
    protected void setSpiritType(SpriteType type) {
        this.spriteType = type;
    }

    /**
     * The method that actually does the sprite drawing. This method
     * is called after the transform has been set up in the draw() method.
     * Sub-classes should override this method to perform the drawing.
     */
    protected abstract void drawSprite(Graphics2D g);
}
