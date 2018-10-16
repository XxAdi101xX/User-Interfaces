import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.*;

class ColourPalette extends JPanel  {
	private Model model;
	private ColourPanel selectedColour;
	private Boolean disabled;

	public ColourPalette(Model model) {
		this.model = model;
		this.disabled = false;

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
		this.setBackground(Color.BLACK);

		// anonymous class acts as model listener
		this.model.addView(new IView() {
			public void updateView() {
				System.out.println("ColourPalette: updateView");
				if (model.getTool() == Tool.ERASER) {
					disabled = true;
				} else {
					disabled = false;
				}
				a.checkDisabled();
				b.checkDisabled();
				c.checkDisabled();
				d.checkDisabled();
				e.checkDisabled();
				f.checkDisabled();

				if (model.isUsingCustomColour()) {
					selectedColour.removeAsSelected();
				} 
				Boolean equalColour = 	model.getCurrentShape() == null ? true : // we set random value if currentshape is null
										model.getCurrentShape().getBorderColour().getRed() == selectedColour.getColour().getRed() &&
										model.getCurrentShape().getBorderColour().getGreen() == selectedColour.getColour().getGreen() &&
										model.getCurrentShape().getBorderColour().getBlue() == selectedColour.getColour().getBlue();
				if (model.getCurrentShape() != null && // this statement is required 
					model.getTool() == Tool.CURSOR && 
					!equalColour) {
					Color shapeColour = model.getCurrentShape().getBorderColour();

					if (shapeColour.getRed() == a.getColour().getRed() && 
						shapeColour.getGreen() == a.getColour().getGreen() && 
						shapeColour.getBlue() == a.getColour().getBlue()) {
						a.setAsSelected();
						model.setColour(a.getColour(), false);
					} else if (	shapeColour.getRed() == b.getColour().getRed() && 
							   	shapeColour.getGreen() == b.getColour().getGreen() && 
								shapeColour.getBlue() == b.getColour().getBlue()) {
						b.setAsSelected();
						model.setColour(b.getColour(), false);
					} else if (	shapeColour.getRed() == c.getColour().getRed() && 
								shapeColour.getGreen() == c.getColour().getGreen() && 
				 				shapeColour.getBlue() == c.getColour().getBlue()) {
						c.setAsSelected();
						model.setColour(c.getColour(), false);
					} else if (	shapeColour.getRed() == d.getColour().getRed() && 
								shapeColour.getGreen() == d.getColour().getGreen() && 
				 				shapeColour.getBlue() == d.getColour().getBlue()) {
						d.setAsSelected();
						model.setColour(d.getColour(), false);
					} else if (	shapeColour.getRed() == e.getColour().getRed() && 
								shapeColour.getGreen() == e.getColour().getGreen() && 
				 				shapeColour.getBlue() == e.getColour().getBlue()) {
						e.setAsSelected();
						model.setColour(e.getColour(), false);
					} else if (	shapeColour.getRed() == f.getColour().getRed() && 
								shapeColour.getGreen() == f.getColour().getGreen() && 
				 				shapeColour.getBlue() == f.getColour().getBlue()) {
						f.setAsSelected();
						model.setColour(f.getColour(), false);
					} else {
						// TODO deal with custom colour
						// model.setColour(shapeColour, true);]
						model.setUsingCustomColour(true);
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
					if (e.getButton() == MouseEvent.BUTTON1 && !disabled) { // left click
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

		public void checkDisabled() {
			setEnabled(!disabled);
			repaint();
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		
			Graphics2D g2d = (Graphics2D) g.create();
			if (disabled) {
				g2d.setColor(Color.BLACK);
				g2d.setStroke(new BasicStroke(10));
				g2d.drawLine(0, 0, getWidth(), getHeight());
				g2d.drawLine(getWidth(), 0, 0, getHeight());
				g2d.dispose();
			}
	
		}
	}
}
