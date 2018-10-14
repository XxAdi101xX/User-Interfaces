import java.awt.Color;
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

	// get the line width
	public int getLineWidth() {
		return lineWidth;
	}

	// set the line width
	public void setLineWidth(int newWidth) {
		lineWidth = newWidth;
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

	public ArrayList<Shape> getShapeList() {
		return shapes;
	}

	public int getShapeListSize() {
		return shapes.size();
	}

	// add a shape to the shape list
	public void addShape (Shape shape) {
		shapes.add(shape);
	}

	// get shape by index
	public Shape getShapeByIndex(int index) {
		return shapes.get(index);
	}

	// remove shape by index
	public void removeShapeByIndex(int index) {
		shapes.remove(index);
	}
	
	// notify the IView observer
	private void notifyObservers() {
			for (IView view : this.views) {
				System.out.println("Model: notify View");
				view.updateView();
			}
	}
}
