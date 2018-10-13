import java.awt.Color;
import java.util.ArrayList;

public class Model {
	// application specific data
	private Color colour;
	private Boolean isCustomColour = false;
	private int lineWidth;

	// all views of this model
	private ArrayList<IView> views = new ArrayList<IView>();

	// set the view observer
	public void addView(IView view) {
		views.add(view);
		view.updateView(); // update the view to current state of the model
	}
	
	// set the colour
	public void setColour(Color newColour, Boolean usingCustomColour) {
		colour = newColour;
		isCustomColour = usingCustomColour;
		notifyObservers();
	}
	
	// get the colour
	public Color getColour() {
		return colour;
	}

	// return whether a custom colour is being used
	public Boolean isUsingCustomColour() {
		return isCustomColour;
	}

	// set the line width
	public void setLineWidth(int newWidth) {
		lineWidth = newWidth;
		notifyObservers();
	}

	// get the line width
	public int getLineWidth() {
		return lineWidth;
	}
	
	// notify the IView observer
	private void notifyObservers() {
			for (IView view : this.views) {
				System.out.println("Model: notify View");
				view.updateView();
			}
	}
}
