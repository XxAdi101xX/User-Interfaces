// HelloMVC: a simple MVC example
// the model is just a counter
// inspired by code by Joseph Mack, http://www.austintek.com/mvc/

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;

class ColourPalette extends JPanel  {

	// the view's main user interface
	private JButton button;

	// the model that this view is showing
	private Model model;

	private ColourPanel selectedColour;


	public ColourPalette(Model model) {
		ColourPanel a = new ColourPanel(Color.RED);
		ColourPanel b = new ColourPanel(Color.GREEN);
		ColourPanel c = new ColourPanel(Color.BLUE);
		ColourPanel d = new ColourPanel(Color.ORANGE);
		ColourPanel e = new ColourPanel(Color.BLACK);
		ColourPanel f = new ColourPanel(Color.WHITE);

		this.setLayout (new GridLayout(3, 2));
		this.add(a);
		this.add(b);
		this.add(c);
		this.add(d);
		this.add(e);
		this.add(f);

		selectedColour = a;
		selectedColour.addAsSelected();

		// set the model
		this.model = model;

		// anonymous class acts as model listener
		this.model.addView(new IView() {
			public void updateView() {
				System.out.println("ToolPalette: updateView");
				// button.setText(Integer.toString(model.getCounterValue()));
			}
		});
	}

	class ColourPanel extends JButton {
		public ColourPanel(Color color) {	
			setBackground(color);
			setBorder(new LineBorder(Color.BLACK, 1));
			setFocusPainted(false);

			addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) { // left click
						System.out.println("111");
						addAsSelected();
					} else if (e.getButton() == MouseEvent.BUTTON3){ // right click
						System.out.println("333");
					}
				}
			});
		}

		public void addAsSelected() {
			selectedColour.removeAsSelected();
			setBorder(new LineBorder(Color.GRAY, 5));
			selectedColour = this;

		}
		
		public void removeAsSelected() {
			System.out.println("resettt");
			setBorder(new LineBorder(Color.BLACK, 1));
		}
	}
}
