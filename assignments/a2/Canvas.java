// HelloMVC: a simple MVC example
// the model is just a counter 
// inspired by code by Joseph Mack, http://www.austintek.com/mvc/

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;

public class Canvas extends JPanel {

	// the model that this view is showing
	private Model model;
	//private JLabel label = new JLabel();

	Canvas(Model model) {
		// create UI
		setBackground(Color.WHITE);
		//this.setPreferredSize(new Dimension(1800, this.getHeight()));
		
		// s1et the model
		this.model = model;
		
		// anonymous class acts as model listener
		this.model.addView(new IView() {
			public void updateView() {
				System.out.println("Canvas: updateView");
			}
		});
		// this.setLayout(new GridBagLayout());
		setLayout(new BorderLayout());
		// this.setLayout(new BorderLayout());
		PaintShapes surface = new PaintShapes();
		this.add(surface, BorderLayout.CENTER);
			
		// setup the event to go to the "controller"
		// (this anonymous class is essentially the controller)		
		// addMouseListener(new MouseAdapter() {
		// 		public void mouseClicked(MouseEvent e) {
		// 			System.out.println("clickeddd" + e.getX() + " " + e.getY());
		// 			PaintShape line = new Line(e.getX(), e.getY());
		// 			add(line, new GridBagConstraints());
		// 			// add(new JButton("  Two  "));
		// 			revalidate();
		// 		}
		// });
	}

	class PaintShapes extends JComponent {
		Shape currentShape;
		ArrayList<Shape> shapes = new ArrayList<Shape>();

		PaintShapes() {
			currentShape = null;

			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (Canvas.this.model.isDrawingTool()) {
						Shape newShape = new Shape(
							Canvas.this.model.getTool(),
							Canvas.this.model.getColour(), 
							Canvas.this.model.getLineWidth(), 
							e.getX(), e.getY(), 
							e.getX(), e.getY()
						);
						shapes.add(newShape);
						currentShape = newShape;
					} else {
						for (int i = shapes.size() - 1; i >= 0; i--) {
							if (shapes.get(i).hasIntersected(new Point(e.getX(), e.getY()))) {
								// remove shape from arraylist temporarily
								currentShape = shapes.get(i);
								shapes.remove(i);
								
								// handle behaviour based on selected utility tool
								switch (Canvas.this.model.getTool()) {
									case CURSOR:
										System.out.println("yoooooooooooooooooo");

										shapes.add(currentShape); // add shape back to front of array for increased priority
										break;
									case ERASER:
										currentShape = null; // delete shape
										break;
									case FILL:
										shapes.add(currentShape); // add shape back to front of array for increased priority
										break;
									default:
										System.out.println("ERROR: hasIntersected has failed spectacularily for some reason");
										break;
								}
								break; // find our top most shape and stop looking
							}
						}
					}
					repaint();
				}
		
				public void mouseReleased(MouseEvent e) {
					if (Canvas.this.model.isDrawingTool()) {
						currentShape = null;
					}
				  	repaint();
				}
			});

			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					if (Canvas.this.model.isDrawingTool()) {
						currentShape.setEndPoints(e.getX(), e.getY());
						repaint();
					}
				}
			});
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		
			Graphics2D g2d = (Graphics2D) g.create();
			// System.out.println(getWidth()/4 + " " + getHeight()/2 + " " + (int) (getWidth() * (0.75)) + " " + getHeight()/2);

			for (Shape s : shapes) {
				Point startPoint = s.getStartPoints();
				Point endPoint = s.getEndPoints();

				switch(s.getShape()) {
					case LINE: {
						if (currentShape == s && Canvas.this.model.getTool() == Tool.CURSOR) {
							// draw outline to show that this is selected
							g2d.setStroke(new BasicStroke(s.getLineWidth() + 5));
							g2d.setColor(Color.BLACK);
							g2d.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
						}
						g2d.setStroke(new BasicStroke(s.getLineWidth()));
						g2d.setColor(s.getBorderColour());
						g2d.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
						break;
					}
					case RECTANGLE: {
						g2d.setStroke(new BasicStroke(s.getLineWidth()));
						g2d.setColor(s.getBorderColour());
						g2d.drawRect(startPoint.x, startPoint.y, endPoint.x - startPoint.x, endPoint.y - startPoint.y);
						break;
					}
					case CIRCLE: {
						g2d.setStroke(new BasicStroke(s.getLineWidth()));
						g2d.setColor(s.getBorderColour());
						g2d.drawOval(startPoint.x, startPoint.y, endPoint.x - startPoint.x, endPoint.y - startPoint.y);
						break;
					}
				}
			}

			g2d.dispose();
		}
	}
}
