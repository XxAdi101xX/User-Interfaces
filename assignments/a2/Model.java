import java.awt.Color;
import java.util.ArrayList;

// HelloMVC: a simple MVC example
// the model is just a counter 
// inspired by code by Joseph Mack, http://www.austintek.com/mvc/

public class Model {	
	
	// the data in the model, just a counter
	private int counter = 1;	
	private Color colour;
	private int lineWidth;
	private Boolean isCustomColour = false;
	// all views of this model
	private ArrayList<IView> views = new ArrayList<IView>();

	// set the view observer
	public void addView(IView view) {
		views.add(view);
		// update the view to current state of the model
		view.updateView();
	}
	
	public int getCounterValue() {
		System.out.println("yaaaaaaaaaaay");
		return counter;
	}
	
	public void incrementCounter() {
		if (counter < 5) {
			counter++;
			System.out.println("Model: increment counter to " + counter);
			notifyObservers();
		}
	} 
	
	// my stuff
	public void setColour(Color newColour, Boolean usingCustomColour) {
		colour = newColour;
		isCustomColour = usingCustomColour;
		notifyObservers();
	}
	
	public Color getColour() {
		return colour;
	}

	public Boolean isUsingCustomColour() {
		return isCustomColour;
	}

	public void setLineWidth(int newWidth) {
		lineWidth = newWidth;
		notifyObservers();
	}
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
