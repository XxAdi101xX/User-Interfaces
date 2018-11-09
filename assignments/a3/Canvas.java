
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
		// create four different parts
		Sprite body = new RectangleSprite(SpriteType.BODY, 70, 50);
		Sprite upperarm = new EllipseSprite(SpriteType.UPPERARM, 50, 40);
		Sprite lowerarm = new EllipseSprite(SpriteType.LOWERARM, 70, 30);

		// define them based on relative, successive transformations
		body.transform(AffineTransform.getTranslateInstance(50, 100));
		upperarm.transform(AffineTransform.getTranslateInstance(80, 5));
		lowerarm.transform(AffineTransform.getTranslateInstance(50, 5));

		// build scene graph
		body.addChild(upperarm);
		upperarm.addChild(lowerarm);
		
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
