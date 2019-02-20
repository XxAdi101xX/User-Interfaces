
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
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
				repaint();
			}
		});

		this.model.addHumanFigure();
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
