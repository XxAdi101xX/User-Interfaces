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

	public void resetCanvas() {
		sprites.clear();
		addHumanFigure();
	}

	/*
	 * Create a human like intereactive ragdoll onto the canvas
	 */
	public void addHumanFigure() {
		// create the body parts
		Sprite body = new RectangleSprite(SpriteType.BODY, 80, 160);
		Sprite head = new EllipseSprite(SpriteType.HEAD, 50, 50);

		Sprite rightUpperArm = new EllipseSprite(SpriteType.UPPERARM, 80, 20);
		Sprite rightLowerArm = new EllipseSprite(SpriteType.LOWERARM, 60, 20);
		Sprite rightHand = new EllipseSprite(SpriteType.HAND, 20, 20);

		Sprite leftUpperArm = new EllipseSprite(SpriteType.UPPERARM, 80, 20);
		Sprite leftLowerArm = new EllipseSprite(SpriteType.LOWERARM, 60, 20);
		Sprite leftHand = new EllipseSprite(SpriteType.HAND, 20, 20);

		Sprite rightUpperLeg = new EllipseSprite(SpriteType.UPPERLEG, 20, 80);
		Sprite rightLowerLeg = new EllipseSprite(SpriteType.LOWERLEG, 20, 60);
		Sprite rightFoot = new EllipseSprite(SpriteType.FOOT, 50, 20);

		Sprite leftUpperLeg = new EllipseSprite(SpriteType.UPPERLEG, 20, 80);
		Sprite leftLowerLeg = new EllipseSprite(SpriteType.LOWERLEG, 20, 60);
		Sprite leftFoot = new EllipseSprite(SpriteType.FOOT, 50, 20);

		// define the initial transformations
		body.transform(AffineTransform.getTranslateInstance(480, 220));
		head.transform(AffineTransform.getTranslateInstance(38, 3));
		head.transform(AffineTransform.getRotateInstance(Math.toRadians(230)));

		rightUpperArm.transform(AffineTransform.getTranslateInstance(93, -5));
		rightUpperArm.transform(AffineTransform.getRotateInstance(Math.toRadians(65)));
		rightLowerArm.transform(AffineTransform.getTranslateInstance(80, 0));
		rightLowerArm.transform(AffineTransform.getRotateInstance(Math.toRadians(20)));
		rightHand.transform(AffineTransform.getTranslateInstance(55, 10));
		rightHand.transform(AffineTransform.getRotateInstance(Math.toRadians(-40)));

		leftUpperArm.transform(AffineTransform.getTranslateInstance(5, 4));
		leftUpperArm.transform(AffineTransform.getRotateInstance(Math.toRadians(115)));
		leftLowerArm.transform(AffineTransform.getTranslateInstance(72, 0));
		leftLowerArm.transform(AffineTransform.getRotateInstance(Math.toRadians(340)));
		leftHand.transform(AffineTransform.getTranslateInstance(57, 10));
		leftHand.transform(AffineTransform.getRotateInstance(Math.toRadians(-45)));

		rightUpperLeg.transform(AffineTransform.getTranslateInstance(50, 160));
		rightLowerLeg.transform(AffineTransform.getTranslateInstance(0, 77));
		rightFoot.transform(AffineTransform.getTranslateInstance(5, 55));

		leftUpperLeg.transform(AffineTransform.getTranslateInstance(10, 160));
		leftLowerLeg.transform(AffineTransform.getTranslateInstance(0, 77));
		leftFoot.transform(AffineTransform.getTranslateInstance(13, 75));
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

	/*
	 * Create a giraffe figure onto the canvas
	 */
	public void addDogFigure() {
		// create the body parts
		Sprite body = new RectangleSprite(SpriteType.BODY, 220, 80);
		Sprite tail = new RectangleSprite(SpriteType.PIVOT, 90, 10);
		Sprite neck = new RectangleSprite(SpriteType.NECK, 20, 70);
		Sprite head = new EllipseSprite(SpriteType.HEAD, 70, 40);

		Sprite rightUpperLeg = new EllipseSprite(SpriteType.UPPERLEG, 20, 60);
		Sprite rightLowerLeg = new EllipseSprite(SpriteType.LOWERLEG, 20, 40);
		Sprite rightFoot = new EllipseSprite(SpriteType.FOOT, 50, 20);

		Sprite leftUpperLeg = new EllipseSprite(SpriteType.UPPERLEG, 20, 80);
		Sprite leftLowerLeg = new EllipseSprite(SpriteType.LOWERLEG, 20, 60);
		Sprite leftFoot = new EllipseSprite(SpriteType.FOOT, 50, 20);

		// define the initial transformations
		body.transform(AffineTransform.getTranslateInstance(420, 270));
		body.transform(AffineTransform.getRotateInstance(Math.toRadians(20)));

		tail.transform(AffineTransform.getTranslateInstance(220, 45));
		tail.transform(AffineTransform.getRotateInstance(Math.toRadians(-40)));

		neck.transform(AffineTransform.getTranslateInstance(10, 0));
		neck.transform(AffineTransform.getRotateInstance(Math.toRadians(140)));

		head.transform(AffineTransform.getTranslateInstance(10, 60));
		head.transform(AffineTransform.getRotateInstance(Math.toRadians(30)));

		leftUpperLeg.transform(AffineTransform.getTranslateInstance(10, 80));
		leftUpperLeg.transform(AffineTransform.getRotateInstance(Math.toRadians(-20)));
		leftLowerLeg.transform(AffineTransform.getTranslateInstance(0, leftUpperLeg.getDimensions().getY() - 3));
		leftFoot.transform(AffineTransform.getTranslateInstance(13, 75));
		leftFoot.transform(AffineTransform.getRotateInstance(Math.toRadians(175)));

		rightUpperLeg.transform(AffineTransform.getTranslateInstance(170, 85));
		rightUpperLeg.transform(AffineTransform.getRotateInstance(Math.toRadians(-20)));
		rightLowerLeg.transform(AffineTransform.getTranslateInstance(0, 60));
		rightFoot.transform(AffineTransform.getTranslateInstance(13, 55));
		rightFoot.transform(AffineTransform.getRotateInstance(Math.toRadians(175)));

		// build dog with body as base
		body.addChild(tail);

		body.addChild(neck);
		neck.addChild(head);

		body.addChild(rightUpperLeg);
		rightUpperLeg.addChild(rightLowerLeg);
		rightLowerLeg.addChild(rightFoot);

		body.addChild(leftUpperLeg);
		leftUpperLeg.addChild(leftLowerLeg);
		leftLowerLeg.addChild(leftFoot);
		
		// add root sprite
		addSprite(body);
	}

	/*
	 * Create a tree figure onto the canvas
	 */
	public void addScarecrowFigure() {
		// create the body parts
		Sprite body = new EllipseSprite(SpriteType.BODY, 60, 340);
		Sprite base = new RectangleSprite(SpriteType.BASE, 220, 80);
		Sprite head = new EllipseSprite(SpriteType.HEAD, 50, 60);
		Sprite leftEye = new EllipseSprite(SpriteType.EYE, 10, 10);
		Sprite rightEye = new EllipseSprite(SpriteType.EYE, 10, 10);
		Sprite leftBranch = new RectangleSprite(SpriteType.LOWERARM, 200, 10);
		Sprite leftStick1 = new RectangleSprite(SpriteType.PIVOT, 70, 10);
		Sprite leftStick2 = new RectangleSprite(SpriteType.PIVOT, 70, 10);
		Sprite rightBranch = new RectangleSprite(SpriteType.LOWERARM, 200, 10);
		Sprite rightStick1 = new RectangleSprite(SpriteType.PIVOT, 70, 10);
		Sprite rightStick2 = new RectangleSprite(SpriteType.PIVOT, 70, 10);

		// define the initial transformations
		body.transform(AffineTransform.getTranslateInstance(430, 170));
		base.transform(AffineTransform.getTranslateInstance(-80, body.getDimensions().getY()));
		head.transform(AffineTransform.getTranslateInstance(30, 5));
		head.transform(AffineTransform.getRotateInstance(Math.toRadians(230)));
		leftEye.transform(AffineTransform.getTranslateInstance(30, 20));
		rightEye.transform(AffineTransform.getTranslateInstance(15, 25));

		leftBranch.transform(AffineTransform.getTranslateInstance(0, 140));
		leftBranch.transform(AffineTransform.getRotateInstance(Math.toRadians(190)));
		
		leftStick1.transform(AffineTransform.getTranslateInstance(140, 10));
		leftStick1.transform(AffineTransform.getRotateInstance(Math.toRadians(65)));
		leftStick2.transform(AffineTransform.getTranslateInstance(140, -6));
		leftStick2.transform(AffineTransform.getRotateInstance(Math.toRadians(-65)));

		rightBranch.transform(AffineTransform.getTranslateInstance(60, 140));
		rightBranch.transform(AffineTransform.getRotateInstance(Math.toRadians(-30)));

		rightStick1.transform(AffineTransform.getTranslateInstance(140, 10));
		rightStick1.transform(AffineTransform.getRotateInstance(Math.toRadians(65)));
		rightStick2.transform(AffineTransform.getTranslateInstance(140, -6));
		rightStick2.transform(AffineTransform.getRotateInstance(Math.toRadians(-65)));


		// build dog with body as base
		body.addChild(head);
		head.addChild(leftEye);
		head.addChild(rightEye);
		body.addChild(base);
		body.addChild(leftBranch);
		leftBranch.addChild(leftStick1);
		leftBranch.addChild(leftStick2);
		body.addChild(rightBranch);
		rightBranch.addChild(rightStick1);
		rightBranch.addChild(rightStick2);
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

