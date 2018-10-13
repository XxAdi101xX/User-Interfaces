import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.*;

class ColourPalette extends JPanel  {
	private Model model;
	private ColourPanel selectedColour;

	public ColourPalette(Model model) {
		this.model = model;

		ColourPanel a = new ColourPanel(Color.RED);
		ColourPanel b = new ColourPanel(Color.GREEN);
		ColourPanel c = new ColourPanel(Color.BLUE);
		ColourPanel d = new ColourPanel(Color.ORANGE);
		ColourPanel e = new ColourPanel(Color.PINK);
		ColourPanel f = new ColourPanel(Color.YELLOW);

		this.setLayout (new GridLayout(3, 2));
		this.add(a);
		this.add(b);
		this.add(c);
		this.add(d);
		this.add(e);
		this.add(f);

		selectedColour = a;
		selectedColour.setAsSelected();
		this.model.setColour(selectedColour.getColour(), false);

		// anonymous class acts as model listener
		this.model.addView(new IView() {
			public void updateView() {
				System.out.println("ToolPalette: updateView");
				if (model.isUsingCustomColour()) {
					selectedColour.removeAsSelected();
				}
			}
		});
	}

	class ColourPanel extends JButton {
		private Color colour;

		public ColourPanel(Color colour) {	
			this.colour = colour;
			setBackground(colour);
			setBorder(new LineBorder(Color.BLACK, 1));
			setFocusPainted(false);

			addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) { // left click
						System.out.println("111");
						ColourPalette.this.model.setColour(colour, false);
						setAsSelected();
					} else if (e.getButton() == MouseEvent.BUTTON3){ // right click
						System.out.println("TODO MAYBE TO RECONFIGURE THE PANEL");
					}
				}
			});
		}

		public Color getColour() {
			return colour;
		}

		public void setAsSelected() {
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
