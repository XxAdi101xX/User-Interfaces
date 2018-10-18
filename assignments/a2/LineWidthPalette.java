import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.*;

public class LineWidthPalette extends JPanel  {
	private Model model;
	private Line selectedLine;
	private Boolean disabled;
	final private Color defaultColour = Color.DARK_GRAY;
	final private int thinWidth = 5;
	final private int mediumWidth = 8;
	final private int thickWidth = 12;
	final private int thickestWidth = 15;

	public LineWidthPalette(Model model) {
		this.model = model;
		this.disabled = false;

		// Setup the class layout
		this.setLayout(new GridLayout(4, 1));

		Line thinLine = new Line(thinWidth, defaultColour);
		Line mediumLine = new Line(mediumWidth, this.model.getColour());
		Line thickLine = new Line(thickWidth, defaultColour);
		Line thickestLine = new Line(thickestWidth, defaultColour);

		this.add(thinLine);
		this.add(mediumLine);
		this.add(thickLine);
		this.add(thickestLine);

		selectedLine = mediumLine;
		this.model.setLineWidth(mediumLine.getLineWidth());

		this.setBorder(new LineBorder(Color.BLACK, 3));
		this.setBackground(Color.WHITE);

		// anonymous class acts as model listener
		this.model.addView(new IView() {
			public void updateView() {
				// System.out.println("LineWidthPalette: updateView");
				if (model.getTool() == Tool.FILL || model.getTool() == Tool.ERASER) {
					disabled = true;
				} else {
					disabled = false;
				}
				repaint();
				
				if (model.getCurrentShape() != null && 
					model.getTool() == Tool.CURSOR && 
					model.getCurrentShape().getLineWidth() != selectedLine.getLineWidth()) {
					switch (model.getCurrentShape().getLineWidth()) {
						case thinWidth:
							thinLine.setAsSelected();
							model.setLineWidth(thinWidth);
							break;
						case mediumWidth:
							mediumLine.setAsSelected();
							model.setLineWidth(mediumWidth);
							break;
						case thickWidth:
							thickLine.setAsSelected();
							model.setLineWidth(thickWidth);
							break;
						case thickestWidth:
							thickestLine.setAsSelected();
							model.setLineWidth(thickestWidth);
						default:
							System.out.println("error with setting line width of current selected shape");
					}
				}

				selectedLine.updateColour(model.getColour());
			}
		});
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

	class Line extends JComponent {
		final private int width;
		private Color colour;

		Line(int lineWidth, Color lineColour) {
			width = lineWidth;
			colour = lineColour;

			addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						if (LineWidthPalette.this.model.getTool() == Tool.CURSOR && LineWidthPalette.this.model.getCurrentShape() != null) {
							LineWidthPalette.this.model.setCurrentShape(null);
						}
					}
				}
			});

			addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1 && !disabled) { // left click
						if (!isSelected()) {
							LineWidthPalette.this.model.setLineWidth(width);
							setAsSelected();
						}
					}
				}
			});
		}

		private Boolean isSelected() {
			return LineWidthPalette.this.selectedLine == this;
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setColor(colour);
			g2d.setStroke(new BasicStroke(width));
			g2d.drawLine(getWidth()/4, getHeight()/2, (int) (getWidth() * (0.75)), getHeight()/2);
			g2d.dispose();
		}

		public void setAsSelected() {
			selectedLine.removeAsSelected();
			updateColour(LineWidthPalette.this.model.getColour());
			selectedLine = this;
		}

		public void removeAsSelected() {
			updateColour(defaultColour);
		}

		public void updateColour(Color newColour) {
			colour = newColour;
			repaint();
		}

		public int getLineWidth() {
			return width;
		}
	}
}
