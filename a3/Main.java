
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		// model for application
		Model model = new Model();

		// components
		Canvas canvas = new Canvas(model);
		MenuBar menuBar = new MenuBar(model);

		// overall frame of application
		JFrame frame = new JFrame("Interactive Ragdoll");

		// initializing frame
		frame.getContentPane().add(canvas);
		frame.getContentPane().setLayout(new GridLayout(1, 1));

		// default window properties
		frame.setJMenuBar(menuBar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1024, 768);
		frame.setVisible(true);
		frame.setResizable(false);
	}
}

