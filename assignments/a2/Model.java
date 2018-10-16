import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class Model {
	// application specific data
	private Tool tool;
	private Color colour;
	private Boolean isCustomColour = false;
	private int lineWidth;
	private Shape currentShape;
	private ArrayList<Shape> shapes = new ArrayList<Shape>(); // all the shapes
	private ArrayList<IView> views = new ArrayList<IView>(); // all the views of this model
	private int currentCanvasWidth = 0;
	private int currentCanvasHeight = 0;
	private int fixedCanvasWidth = 0;
	private int fixedCanvasHeight = 0;

	// set the view observer
	public void addView(IView view) {
		views.add(view);
		view.updateView(); // update the view to current state of the model
	}

	// get the current tool
	public Tool getTool() {
		return tool;
	}

	// set the current tool
	public void setTool(Tool newTool) {
		tool = newTool;
		notifyObservers();
	}

	// check if the current tool is an circle, line or rectangle
	public Boolean isDrawingTool() {
		return tool == Tool.LINE || tool == Tool.RECTANGLE || tool == Tool.CIRCLE;
	}

	// get the colour
	public Color getColour() {
		return colour;
	}
	
	// set the current colour
	public void setColour(Color newColour, Boolean usingCustomColour) {
		colour = newColour;
		isCustomColour = usingCustomColour;
		notifyObservers();
	}
	

	// return whether a custom colour is being used
	public Boolean isUsingCustomColour() {
		return isCustomColour;
	}

	public void setUsingCustomColour(Boolean custom) {
		isCustomColour = custom;
		// notifyObservers();
	}

	// get the line width
	public int getLineWidth() {
		return lineWidth;
	}

	// set the line width
	public void setLineWidth(int newWidth) {
		lineWidth = newWidth;
		System.out.println("itgdgdf" + newWidth);
		notifyObservers();
	}

	// get the current shape
	public Shape getCurrentShape() {
		return currentShape;
	}

	// set the current shape
	public void setCurrentShape(Shape shape) {
		currentShape = shape;
		notifyObservers();
	}

	// get the array list of shapes
	public ArrayList<Shape> getShapeList() {
		return shapes;
	}

	// get the size of the shapes arraylist
	public int getShapeListSize() {
		return shapes.size();
	}

	// add a shape to the shape list
	public void addShape (Shape shape) {
		shapes.add(shape);
		// notifyObservers();
	}

	// get shape by index
	public Shape getShapeByIndex(int index) {
		return shapes.get(index);
	}

	// remove shape by index
	public void removeShapeByIndex(int index) {
		shapes.remove(index);
	}

	// clear the shapes arraylist
	public void clearShapes() {
		shapes.clear();
		currentShape = null;
		notifyObservers();
	}

	public void setCurrentCanvasSize(int width, int height) {
		currentCanvasWidth = width;
		currentCanvasHeight = height;
		notifyObservers();
	}

	public Point getCurrentCanvasSize() {
		return new Point(currentCanvasWidth, currentCanvasHeight);
	}

	public void setFixedCanvasSize(int width, int height) {
		fixedCanvasWidth = width;
		fixedCanvasHeight = height;
	}

	public Point getFixedCanvasSize() {
		return new Point(fixedCanvasWidth, fixedCanvasHeight);
	}
	
	// notify the IView observer
	private void notifyObservers() {
		for (IView view : this.views) {
			view.updateView();
		}
	}
}
