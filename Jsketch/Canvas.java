import javax.swing.*;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
	private Model model;
	private PaintShapes surface;
	private JScrollPane scrollPane;

	Canvas(Model model) {		
		// set the model
		this.model = model;

		surface = new PaintShapes();
		scrollPane = new JScrollPane(surface);

		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);

		this.model.addView(new IView() {
			public void updateView() {
				// System.out.println("Canvas: updateView" + model.getShapeListSize());
				if (model.getCurrentShape() != null && model.getTool() == Tool.CURSOR) {
					model.getCurrentShape().setBorderColour(model.getColour());
					model.getCurrentShape().setLineWidth(model.getLineWidth());
				}

				surface.repaint();

				Point currentCanvasSize = model.getCurrentCanvasSize();
				Point fixedCanvasSize = model.getFixedCanvasSize();
				Point previousCanvasSize = model.getPreviousCanvasSize();

				if (model.getViewFullSize()) {
					scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
					if ((currentCanvasSize.x > fixedCanvasSize.x && currentCanvasSize.y > fixedCanvasSize.y) || 
						model.getNewCanvas()) {
						model.setNewCanvas(false);
						surface.setPreferredSize(new Dimension(currentCanvasSize.x, currentCanvasSize.y));
						model.setFixedCanvasSize(currentCanvasSize.x , currentCanvasSize.y);
					}
				} else {
					model.setNewCanvas(false);
					surface.setPreferredSize(new Dimension(currentCanvasSize.x, currentCanvasSize.y));
					model.setFixedCanvasSize(currentCanvasSize.x , currentCanvasSize.y);
					scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

					for (Shape s : model.getShapeList()) {
						Point startPoint = s.getStartPoints();
						Point endPoint = s.getEndPoints();
	
						double startXRatio = (double)startPoint.x / previousCanvasSize.x;
						double startYRatio = (double)startPoint.y / previousCanvasSize.y;
						double endXRatio = (double)endPoint.x / previousCanvasSize.x;
						double endYRatio = (double)endPoint.y / previousCanvasSize.y;
	
						s.setStartPoints((int)Math.round(currentCanvasSize.x * startXRatio), (int)Math.round(currentCanvasSize.y * startYRatio));
						s.setEndPoints((int)Math.round(currentCanvasSize.x * endXRatio), (int)Math.round(currentCanvasSize.y * endYRatio));
					}			
				}
				model.setPreviousCanvasSize(currentCanvasSize.x, currentCanvasSize.y);
			}
		});
	}

	class PaintShapes extends JComponent {
		Point previousMousePoint;
		PaintShapes() {
			Canvas.this.model.setCurrentShape(null);

			addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						if (Canvas.this.model.getTool() == Tool.CURSOR && Canvas.this.model.getCurrentShape() != null) {
							Canvas.this.model.setCurrentShape(null);
						}
					}
				}
			});

			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					requestFocus();
					if (Canvas.this.model.isDrawingTool()) {
						Shape newShape = new Shape(
							Canvas.this.model.getTool(),
							Canvas.this.model.getColour(), 
							Canvas.this.model.getLineWidth(), 
							e.getX(), e.getY(), 
							e.getX(), e.getY()
						);
						Canvas.this.model.addShape(newShape);
						Canvas.this.model.setCurrentShape(newShape);
					} else {
						for (int i = Canvas.this.model.getShapeListSize() - 1; i >= 0; i--) {
							if (Canvas.this.model.getShapeByIndex(i).hasIntersected(new Point(e.getX(), e.getY()))) {
								Canvas.this.model.setCurrentShape(Canvas.this.model.getShapeByIndex(i));
								Canvas.this.model.removeShapeByIndex(i);
								
								// handle behaviour based on selected utility tool
								switch (Canvas.this.model.getTool()) {
									case CURSOR:
										// add shape back to front of array for increased priority
										previousMousePoint = new Point(e.getX(), e.getY());
										Canvas.this.model.addShape(Canvas.this.model.getCurrentShape());
										break;
									case ERASER:
										// delete shape
										Canvas.this.model.setCurrentShape(null);
										break;
									case FILL:
										Canvas.this.model.getCurrentShape().setFilled();
										Canvas.this.model.getCurrentShape().setBackgroundColour(Canvas.this.model.getColour());
										// add shape back to front of array for increased priority
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
						// reset to indicate that nothing is really selected when drawing a shape
						Canvas.this.model.setCurrentShape(null);
					}
				  	repaint();
				}
			});

			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					if (Canvas.this.model.isDrawingTool() && contains(e.getX(), e.getY())) {
						Canvas.this.model.getCurrentShape().setEndPoints(e.getX(), e.getY());
						repaint();
					} else if (	Canvas.this.model.getTool() == Tool.CURSOR && 
								Canvas.this.model.getCurrentShape() != null &&
								Canvas.this.model.getCurrentShape().hasIntersected(new Point(e.getX(), e.getY()))) {
						Point startPoint = Canvas.this.model.getCurrentShape().getStartPoints();
						Point endPoint = Canvas.this.model.getCurrentShape().getEndPoints();

						// find mouse change 
						int deltaX = previousMousePoint.x - e.getX();
						int deltaY = previousMousePoint.y - e.getY();

						// move points accordingly
						Canvas.this.model.getCurrentShape().setStartPoints(startPoint.x -= deltaX, startPoint.y -= deltaY);
						Canvas.this.model.getCurrentShape().setEndPoints(endPoint.x -= deltaX, endPoint.y -= deltaY);

						// update last mouse position
						previousMousePoint.x = e.getX();
						previousMousePoint.y = e.getY();
						
						repaint();
					}
				}
			});
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		
			Graphics2D g2d = (Graphics2D) g.create();

			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, getWidth(), getHeight());

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
							g2d.setStroke(new BasicStroke(s.getLineWidth() + 7));
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

						if (s.getFilled()) {
							g2d.setStroke(new BasicStroke(s.getLineWidth()));
							g2d.setColor(s.getBackgroundColour());
							g2d.fill(rectangle);
						}
						if (isSelected) {
							// draw outline to show that this is selected
							g2d.setStroke(new BasicStroke(s.getLineWidth() + 7));
							g2d.setColor(Color.BLACK);
							g2d.draw(rectangle);
						}

						g2d.setStroke(new BasicStroke(s.getLineWidth()));
						g2d.setColor(s.getBorderColour());
						g2d.draw(rectangle);
						break;
					}
					case CIRCLE: {
						Ellipse2D.Double circle = s.getEllipsesObject();
						
						if (s.getFilled()) {
							g2d.setStroke(new BasicStroke(s.getLineWidth()));
							g2d.setColor(s.getBackgroundColour());
							g2d.fill(circle);
						}
						if (isSelected) {
							g2d.setStroke(new BasicStroke(s.getLineWidth() + 7));
							g2d.setColor(Color.BLACK);
							g2d.draw(circle);
						}

						g2d.setStroke(new BasicStroke(s.getLineWidth()));
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
