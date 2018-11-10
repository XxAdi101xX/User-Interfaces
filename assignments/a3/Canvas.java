
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Canvas extends JPanel {
	private Model model;

	public Canvas(Model model) {
		// set model
		this.model = model;

		// Install our event handlers
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				handleMousePress(e);
			}

			public void mouseReleased(MouseEvent e) {
				handleMouseReleased(e);
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				handleMouseDragged(e);
			}
		});

		this.model.addView(new IView() {
			public void updateView() {
				System.out.println("Canvas: updateView");
				repaint();
			}
		});

		makeRagDoll();
	}

	/**
	 * Create a human like intereactive ragdoll onto the canvas
	 */
	private void makeRagDoll() {
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
		this.model.addSprite(body);
	}

	/**
	 * Handle mouse press events
	 */
	private void handleMousePress(java.awt.event.MouseEvent e) {
		for (Sprite sprite : model.getSprites()) {
			model.setInteractiveSprite(sprite.getSpriteHit(e));
			if (model.getInteractiveSprite() != null) {
				model.getInteractiveSprite().handleMouseDownEvent(e);
				break;
			} 
		}
	}

	/**
	 * Handle mouse released events
	 */
	private void handleMouseReleased(MouseEvent e) {
		Sprite currentInteractiveSprite = model.getInteractiveSprite();

		if (currentInteractiveSprite != null) {
			currentInteractiveSprite.handleMouseUp(e);
			repaint();
		}
		model.setInteractiveSprite(null);
	}

	/**
	 * Handle mouse dragged events
	 */
	private void handleMouseDragged(MouseEvent e) {
		Sprite currentInteractiveSprite = model.getInteractiveSprite();

		if (currentInteractiveSprite != null) {
			currentInteractiveSprite.handleMouseDragEvent(e);
			repaint();
		}
	}

	/**
	 * Paint our canvas
	 */
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		AffineTransform save = g2.getTransform();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2.setColor(Color.BLACK);
		for (Sprite sprite : model.getSprites()) {
			sprite.draw(g2);
		}
		g2.setTransform(save);
	}

}
