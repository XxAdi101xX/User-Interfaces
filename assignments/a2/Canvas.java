// HelloMVC: a simple MVC example
// the model is just a counter 
// inspired by code by Joseph Mack, http://www.austintek.com/mvc/

import javax.swing.*;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

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
		// Shape currentShape;
		// ArrayList<Shape> shapes = new ArrayList<Shape>();

		PaintShapes() {
			// currentShape = null;
			Canvas.this.model.setCurrentShape(null);

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
						// shapes.add(newShape);
						// currentShape = newShape;
						Canvas.this.model.addShape(newShape);
						Canvas.this.model.setCurrentShape(newShape);
					} else {
						for (int i = Canvas.this.model.getShapeListSize() - 1; i >= 0; i--) {
							if (Canvas.this.model.getShapeByIndex(i).hasIntersected(new Point(e.getX(), e.getY()))) {
								// remove shape from arraylist temporarily
								// currentShape = shapes.get(i);
								// shapes.remove(i);
								Canvas.this.model.setCurrentShape(Canvas.this.model.getShapeByIndex(i));
								Canvas.this.model.removeShapeByIndex(i);
								
								// handle behaviour based on selected utility tool
								switch (Canvas.this.model.getTool()) {
									case CURSOR:
										System.out.println("yoooooooooooooooooo");

										// shapes.add(currentShape); // add shape back to front of array for increased priority
										Canvas.this.model.addShape(Canvas.this.model.getCurrentShape());
										break;
									case ERASER:
										// currentShape = null; // delete shape
										Canvas.this.model.setCurrentShape(null);
										break;
									case FILL:
										Canvas.this.model.getCurrentShape().setFilled();
										Canvas.this.model.getCurrentShape().setBackgroundCOlour(Canvas.this.model.getColour());
										// shapes.add(currentShape); // add shape back to front of array for increased priority
										Canvas.this.model.addShape(Canvas.this.model.getCurrentShape());										
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
						// currentShape = null; // reset to indicate that nothing is really selected when drawing a shape
						Canvas.this.model.setCurrentShape(null);
					}
				  	repaint();
				}
			});

			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					if (Canvas.this.model.isDrawingTool()) {
						Canvas.this.model.getCurrentShape().setEndPoints(e.getX(), e.getY());
						repaint();
					}
				}
			});
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		
			Graphics2D g2d = (Graphics2D) g.create();
			// System.out.println(getWidth()/4 + " " + getHeight()/2 + " " + (int) (getWidth() * (0.75)) + " " + getHeight()/2);

			for (Shape s : Canvas.this.model.getShapeList()) {
				Point startPoint = s.getStartPoints();
				Point endPoint = s.getEndPoints();
				// indicatator that a specific shape is "selected" by the cursor
				Boolean isSelected = Canvas.this.model.getCurrentShape() == s && Canvas.this.model.getTool() == Tool.CURSOR; 

				switch(s.getShape()) {
					case LINE: {
						Line2D line = s.getLineObject();
						if (isSelected) {
							// draw outline to show that this is selected
							g2d.setStroke(new BasicStroke(s.getLineWidth() + 5));
							g2d.setColor(Color.BLACK);
							g2d.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
						}
						g2d.setStroke(new BasicStroke(s.getLineWidth()));
						g2d.setColor(s.getBorderColour());
						g2d.draw(line);
						break;
					}
					case RECTANGLE: {
						Rectangle rectangle = s.getRectangleObject();
						
						if (isSelected) {
							// draw outline to show that this is selected
							g2d.setStroke(new BasicStroke(s.getLineWidth() + 5));
							g2d.setColor(Color.BLACK);
							g2d.draw(rectangle);
						}

						g2d.setStroke(new BasicStroke(s.getLineWidth()));
						if (s.getFilled()) {
							g2d.setColor(s.getBackgroundColour());
							g2d.fill(rectangle);
						}
						g2d.setColor(s.getBorderColour());
						g2d.draw(rectangle);
						break;
					}
					case CIRCLE: {
						Ellipse2D.Double circle = s.getEllipsesObject();

						if (isSelected) {
							g2d.setStroke(new BasicStroke(s.getLineWidth() + 5));
							g2d.setColor(Color.BLACK);
							g2d.draw(circle);
						}

						g2d.setStroke(new BasicStroke(s.getLineWidth()));
						if (s.getFilled()) {
							g2d.setColor(s.getBackgroundColour());
							g2d.fill(circle);
						}
						g2d.setColor(s.getBorderColour());
						g2d.draw(circle);
						break;
					}
				}
			}

			g2d.dispose();
		}
	}
}
