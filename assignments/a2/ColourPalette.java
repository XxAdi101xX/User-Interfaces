import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.*;

class ColourPalette extends JPanel  {
	private Model model;
	private ColourPanel selectedColour;
	private Color colourA = Color.RED;
	private Color colourB = Color.GREEN;
	private Color colourC = Color.BLUE;
	private Color colourD = Color.ORANGE;
	private Color colourE = Color.PINK;
	private Color colourF = Color.YELLOW;

	public ColourPalette(Model model) {
		this.model = model;

		ColourPanel a = new ColourPanel(colourA);
		ColourPanel b = new ColourPanel(colourB);
		ColourPanel c = new ColourPanel(colourC);
		ColourPanel d = new ColourPanel(colourD);
		ColourPanel e = new ColourPanel(colourE);
		ColourPanel f = new ColourPanel(colourF);

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
				System.out.println("ColourPalette: updateView");
				if (model.isUsingCustomColour()) {
					selectedColour.removeAsSelected();
				} 
				
				if (model.getCurrentShape() != null && 
					model.getTool() == Tool.CURSOR && 
					model.getCurrentShape().getBorderColour() != selectedColour.getColour()) {
					Color shapeColour = model.getCurrentShape().getBorderColour();

					if (shapeColour == colourA) {
						a.setAsSelected();
						model.setColour(colourA, false);
					} else if (shapeColour == colourB) {
						b.setAsSelected();
						model.setColour(colourB, false);
					} else if (shapeColour == colourC) {
						c.setAsSelected();
						model.setColour(colourC, false);
					} else if (shapeColour == colourD) {
						d.setAsSelected();
						model.setColour(colourD, false);
					} else if (shapeColour == colourE) {
						e.setAsSelected();
						model.setColour(colourE, false);
					} else if (shapeColour == colourF) {
						f.setAsSelected();
						model.setColour(colourF, false);
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

		public ColourPanel(Color colour) {	
			this.colour = colour;
			setBackground(colour);
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
			setBorder(new LineBorder(Color.BLACK, 1));
		}
	}
}
