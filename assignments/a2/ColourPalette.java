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
		this.setBorder(new LineBorder(Color.BLACK, 3));

		// anonymous class acts as model listener
		this.model.addView(new IView() {
			public void updateView() {
				System.out.println("ColourPalette: updateView");
				if (model.isUsingCustomColour()) {
					selectedColour.removeAsSelected();
				} 
				
				if (model.getCurrentShape() != null && 
					model.getTool() == Tool.CURSOR && 
					model.getCurrentShape().getBorderColour() != selectedColour.getColour()) {
					Color shapeColour = model.getCurrentShape().getBorderColour();

					if (shapeColour == a.getColour()) {
						a.setAsSelected();
						model.setColour(a.getColour(), false);
					} else if (shapeColour == b.getColour()) {
						b.setAsSelected();
						model.setColour(b.getColour(), false);
					} else if (shapeColour == c.getColour()) {
						c.setAsSelected();
						model.setColour(c.getColour(), false);
					} else if (shapeColour == d.getColour()) {
						d.setAsSelected();
						model.setColour(d.getColour(), false);
					} else if (shapeColour == e.getColour()) {
						e.setAsSelected();
						model.setColour(e.getColour(), false);
					} else if (shapeColour == f.getColour()) {
						f.setAsSelected();
						model.setColour(f.getColour(), false);
					} else {
						// TODO deal with custom colour
						// model.setUsingCustomColour(true);
						// ColourPalette.this.model.setColour(shapeColour, true);
					}
				}
			}
		});
	}

	class ColourPanel extends JButton {
		private Color colour;
		private JColorChooser colourChooser = new JColorChooser();

		public ColourPanel(Color panelColour) {	
			this.colour = panelColour;
			setBackground(panelColour);
			setBorder(new LineBorder(Color.BLACK, 1));
			setFocusPainted(false);

			addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						if (ColourPalette.this.model.getTool() == Tool.CURSOR && ColourPalette.this.model.getCurrentShape() != null) {
							ColourPalette.this.model.setCurrentShape(null);
						}
					}
				}
			});

			addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) { // left click
						ColourPalette.this.model.setColour(colour, false);
						setAsSelected();
					} else if (e.getButton() == MouseEvent.BUTTON3){ // right click
						Color chosenColour = colourChooser.showDialog(new JPanel(), "Colour Picker", Color.ORANGE);
						if (chosenColour != null) {
							setColour(chosenColour);
						}
					}
				}
			});
		}

		public void setColour(Color newColour) {
			colour = newColour;
			setBackground(newColour);
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
			setBorder(new LineBorder(Color.BLACK, 1));
		}
	}
}
