
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		Model model = new Model();	

		// add scene graph to the canvas
		Canvas canvas = new Canvas(model);
		model.addSprite(Main.makeSprite());

		MenuBar menuBar = new MenuBar(model);

		// create a frame to hold it
		JFrame frame = new JFrame("Interactive Ragdoll");
		frame.getContentPane().add(canvas);
		frame.getContentPane().setLayout(new GridLayout(1, 1));
		frame.setJMenuBar(menuBar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setVisible(true);
		frame.setResizable(false);
	}
	
	/* Make sample scene graph for testing purposes. */
	private static Sprite makeSprite() {
		// create four different parts
		Sprite firstSprite = new RectangleSprite(70, 50);
		Sprite secondSprite = new RectangleSprite(50, 40);
		Sprite thirdSprite = new RectangleSprite(70, 30);
		Sprite fourthSprite = new RectangleSprite(10, 10);

		// define them based on relative, successive transformations
		firstSprite.transform(AffineTransform.getTranslateInstance(50, 100));
		secondSprite.transform(AffineTransform.getTranslateInstance(80, 5));
		thirdSprite.transform(AffineTransform.getTranslateInstance(50, 5));
		fourthSprite.transform(AffineTransform.getTranslateInstance(70, 30));
		fourthSprite.transform(AffineTransform.getScaleInstance(4, 3));

		// build scene graph
		firstSprite.addChild(secondSprite);
		secondSprite.addChild(thirdSprite);
		thirdSprite.addChild(fourthSprite);
		
		// return root of the tree
		return firstSprite;
	}

}
