import javax.swing.*;

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
	private Color defaultColour = Color.BLACK;
	private Line selectedLine;

	public LineWidthPalette(Model model) {
		this.model = model;

		// Setup the class layout
		this.setLayout(new GridLayout(3, 1));

		Line thinLine = new Line(5, defaultColour);
		Line mediumLine = new Line(10, this.model.getColour());
		Line thickLine = new Line(15, defaultColour);

		this.add(thinLine);
		this.add(mediumLine);
		this.add(thickLine);

		selectedLine = mediumLine;
		this.model.setLineWidth(mediumLine.getLineWidth());

		// button = new JButton("?");
		// button.setMaximumSize(new Dimension(200, 200));
		// button.setPreferredSize(new Dimension(200, 200));
		// a GridBagLayout with default constraints centres
		// the widget in the window
		// this.setLayout(new GridBagLayout());
		// this.add(button, new GridBagConstraints());


		// anonymous class acts as model listener
		this.model.addView(new IView() {
			public void updateView() {
				System.out.println("LineWidthPalette: updateView");
				selectedLine.updateColour(model.getColour());
			}
		});


		// setup the event to go to the "controller"
		// (this anonymous class is essentially the controller)
		// button.addActionListener(new ActionListener() {
		// 	public void actionPerformed(ActionEvent e) {
		// 		model.incrementCounter();
		// 	}
		// });
	}

	class Line extends JComponent {
		private int width;
		private Color colour;

		Line(int lineWidth, Color lineColour) {
			width = lineWidth;
			colour = lineColour;

			addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) { // left click
						setAsSelected();
					}
				}
			});
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setColor(colour);
			g2d.setStroke(new BasicStroke(width));
			System.out.println(getWidth() +"    "+getHeight());
			g2d.drawLine(getWidth()/4, getHeight()/2, (int) (getWidth() * (0.75)), getHeight()/2);
			g2d.dispose();
		}

		public void setAsSelected() {
			selectedLine.removeAsSelected();
			selectedLine = this;
			updateColour(LineWidthPalette.this.model.getColour());
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
