
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;

/**
 * Base Sprite class 
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
    private double relativeRotation = 0.0;
    private double maxRotation = 0.0;
    private double yScaleRatio = 1.03;

    public Sprite(SpriteType type) {
        setSpriteType(type);
        setRotationConstraint();
    }
    
    public Sprite(SpriteType type, Sprite parent) {
        if (parent != null) {
            parent.addChild(this);
        }
        setSpriteType(type);
        setRotationConstraint();
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

        switch (spriteType) {
            case BODY:
                interactionMode = InteractionMode.DRAGGING;
                break;
            case HEAD:
            case UPPERARM:
            case LOWERARM:
            case HAND:
            case FOOT:
            case NECK:
            case PIVOT:
            case EYE:
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

    private void handleDraggingEvent(Point2D newPoint) {
        double x_diff = newPoint.getX() - lastPoint.getX();
        double y_diff = newPoint.getY() - lastPoint.getY();
        transform.translate(x_diff, y_diff);
    }

    private void handleRotatingEvent(Point2D newPoint) {
        Point2D origin = new Point2D.Double(getFullTransform().getTranslateX(), getFullTransform().getTranslateY());
        double sourceAngle = getAngle(origin, lastPoint);
        double newAngle = getAngle(origin, newPoint);

        if (Math.abs(Math.toDegrees(newAngle - sourceAngle) + relativeRotation) <= maxRotation) {
            relativeRotation += Math.toDegrees(newAngle - sourceAngle);
            transform.rotate(newAngle - sourceAngle);
        }
    }

    protected void handleScalingEvent(Point2D newPoint) {
        // double y_diff = newPoint.getY() - lastPoint.getY();
        // updateDimensions(0, y_diff);
        if (lastPoint.getY() < newPoint.getY()) {
            transform.scale(1.0, yScaleRatio);
        } else {
            transform.scale(1.0, 1.0/yScaleRatio);
        }
    }

    /**
     * Handle mouse drag event, with the assumption that we have already
     * been "selected" as the sprite to interact with.
     */
    protected void handleMouseDragEvent(MouseEvent e) {
        Point2D newPoint = e.getPoint();

        Point2D origin = new Point2D.Double(getFullTransform().getTranslateX(), getFullTransform().getTranslateY());
        double sourceAngle = getAngle(origin, lastPoint);
        double newAngle = getAngle(origin, newPoint);

        if (Math.abs(Math.toDegrees(newAngle - sourceAngle)) <= 0.4 && (getSpriteType() == SpriteType.UPPERLEG || 
                                                                        getSpriteType() == SpriteType.LOWERLEG)) {
            handleScalingEvent(newPoint);

            // offset scaling for foot
            Sprite foot = children.get(0);
            while (!foot.children.isEmpty()) {
                foot = foot.children.get(0);
            }
            if (lastPoint.getY() < newPoint.getY()) {
                foot.transform.scale(1.0, 1.0 / yScaleRatio);
            } else {
                foot.transform.scale(1.0, yScaleRatio);
            }

            handleRotatingEvent(newPoint);

            // Save our last point and end early after handling the special case
            lastPoint = e.getPoint();
            return;
        }

        switch (interactionMode) {
            case IDLE:
                ; // do nothing
                break;
            case DRAGGING:
                handleDraggingEvent(newPoint);
                break;
            case ROTATING:
                handleRotatingEvent(newPoint);
                break;
            case SCALING:
                handleScalingEvent(newPoint);
                break;   
        }
        // Save our last point before finishing
        lastPoint = e.getPoint();
    }
    
    protected void handleMouseUp(MouseEvent e) {
        interactionMode = InteractionMode.IDLE;
    }
    
    /**
     * Locates the sprite that was hit by the given event.
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
    protected void setSpriteType(SpriteType type) {
        this.spriteType = type;
    }

    /**
     * @return the SpiteType
     */
    private SpriteType getSpriteType() {
        return this.spriteType;
    }

    /**
     * Set the rotation constraints based on the SpriteTpe
     */
    private void setRotationConstraint() {
        relativeRotation = 0.0;
        
        switch(spriteType) {
            case HEAD:
                maxRotation = 50.0;
                break;
            case UPPERARM:
            case EYE:
                maxRotation = 360.0;
                break;
            case LOWERARM:
                maxRotation = 135.0;
                break;
            case HAND:
                maxRotation = 35.0;
                break;
            case UPPERLEG:
            case LOWERLEG:
                maxRotation = 90.0;
                break;
            case FOOT:
                maxRotation = 35.0;
                break;
            case NECK:
                maxRotation = 25.0;
                break;
            case PIVOT:
                maxRotation = 40.0;
                break;
            default:
                maxRotation = 0.0;
        }
    }

    /**
     * The method that actually does the sprite drawing. This method
     * is called after the transform has been set up in the draw() method.
     * Sub-classes should override this method to perform the drawing.
     */
    protected abstract void drawSprite(Graphics2D g);

    /**
     * Update the Dimensions with the x and y increments
     * @param xInc
     * @param yInc
     */
    protected abstract void updateDimensions(double xInc, double yInc);

    /**
     * Get the sprite dimensions
     */
    public abstract Point2D getDimensions();
}
