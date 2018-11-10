import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Model {
	// application specific data
	private ArrayList<Sprite> sprites = new ArrayList<Sprite>(); // All sprites we're managing
	private Sprite interactiveSprite = null; // Sprite with which user is interacting
	private ArrayList<IView> views = new ArrayList<IView>(); // all the views of this model

	// set the view observer
	public void addView(IView view) {
		views.add(view);
		view.updateView(); // update the view to current state of the model
	}

	// set our current sprite model that we are interacting with
	public void setInteractiveSprite(Sprite sprite) {
		interactiveSprite = sprite;
	}

	// get the current interactive sprite
	public Sprite getInteractiveSprite() {
		return interactiveSprite;
	}

	// get our list of sprite models
	public ArrayList<Sprite> getSprites() {
		return sprites;
	}

	public void resetSprites() {
		sprites.clear();
		addRagDoll();
	}

	/**
	 * Create a human like intereactive ragdoll onto the canvas
	 */
	public void addRagDoll() {
		// create the body parts
		Sprite body = new RectangleSprite(SpriteType.BODY, 80, 160);
		Sprite head = new EllipseSprite(SpriteType.HEAD, 50, 50);

		Sprite rightUpperArm = new EllipseSprite(SpriteType.UPPERARM, 80, 20);
		Sprite rightLowerArm = new EllipseSprite(SpriteType.LOWERARM, 60, 20);
		Sprite rightHand = new EllipseSprite(SpriteType.LOWERARM, 20, 20);

		Sprite leftUpperArm = new EllipseSprite(SpriteType.UPPERARM, 80, 20);
		Sprite leftLowerArm = new EllipseSprite(SpriteType.LOWERARM, 60, 20);
		Sprite leftHand = new EllipseSprite(SpriteType.LOWERARM, 20, 20);

		Sprite rightUpperLeg = new EllipseSprite(SpriteType.UPPERLEG, 20, 80);
		Sprite rightLowerLeg = new EllipseSprite(SpriteType.LOWERLEG, 20, 60);
		Sprite rightFoot = new EllipseSprite(SpriteType.FOOT, 50, 20);

		Sprite leftUpperLeg = new EllipseSprite(SpriteType.UPPERLEG, 20, 80);
		Sprite leftLowerLeg = new EllipseSprite(SpriteType.LOWERLEG, 20, 60);
		Sprite leftFoot = new EllipseSprite(SpriteType.FOOT, 50, 20);

		// define the initial transformations
		body.transform(AffineTransform.getTranslateInstance(550, 100));
		head.transform(AffineTransform.getTranslateInstance(15, -60));

		rightUpperArm.transform(AffineTransform.getTranslateInstance(95, -5));
		rightUpperArm.transform(AffineTransform.getRotateInstance(Math.toRadians(65)));
		rightLowerArm.transform(AffineTransform.getTranslateInstance(80, 0));
		rightLowerArm.transform(AffineTransform.getRotateInstance(Math.toRadians(15)));
		rightHand.transform(AffineTransform.getTranslateInstance(62, 0));

		leftUpperArm.transform(AffineTransform.getTranslateInstance(5, 4));
		leftUpperArm.transform(AffineTransform.getRotateInstance(Math.toRadians(115)));
		leftLowerArm.transform(AffineTransform.getTranslateInstance(80, 0));
		leftLowerArm.transform(AffineTransform.getRotateInstance(Math.toRadians(-20)));
		leftHand.transform(AffineTransform.getTranslateInstance(62, 0));

		rightUpperLeg.transform(AffineTransform.getTranslateInstance(50, 160));
		rightLowerLeg.transform(AffineTransform.getTranslateInstance(0, 77));
		rightFoot.transform(AffineTransform.getTranslateInstance(5, 55));

		leftUpperLeg.transform(AffineTransform.getTranslateInstance(10, 160));
		leftLowerLeg.transform(AffineTransform.getTranslateInstance(0, 77));
		leftFoot.transform(AffineTransform.getTranslateInstance(15, 75));
		leftFoot.transform(AffineTransform.getRotateInstance(Math.toRadians(175)));

		// build ragdoll with body as base
		body.addChild(head);
		
		body.addChild(rightUpperArm);
		rightUpperArm.addChild(rightLowerArm);
		rightLowerArm.addChild(rightHand);

		body.addChild(leftUpperArm);
		leftUpperArm.addChild(leftLowerArm);
		leftLowerArm.addChild(leftHand);

		body.addChild(rightUpperLeg);
		rightUpperLeg.addChild(rightLowerLeg);
		rightLowerLeg.addChild(rightFoot);

		body.addChild(leftUpperLeg);
		leftUpperLeg.addChild(leftLowerLeg);
		leftLowerLeg.addChild(leftFoot);
		
		// add root sprite
		addSprite(body);
	}

	// Add a top-level sprite to the canvas	
	private void addSprite(Sprite s) {
		sprites.add(s);
		notifyObservers();
	}
	
	// notify the IView observer
	private void notifyObservers() {
		for (IView view : this.views) {
			view.updateView();
		}
	}
}

