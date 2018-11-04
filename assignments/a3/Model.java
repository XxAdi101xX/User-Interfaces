import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class Model {
	// application specific data
	private ArrayList<Sprite> sprites = new ArrayList<Sprite>(); // All sprites we're managing
	private Sprite interactiveSprite = null; // Sprite with which user is interacting
	private ArrayList<IView> views = new ArrayList<IView>(); // all the views of this model

	// set the view observer
	public void addView(IView view) {
		views.add(view);
		view.updateView(); // update the view to current state of the model
	}

	// set our current sprite model that we are interacting with
	public void setInteractiveSprite(Sprite sprite) {
		interactiveSprite = sprite;
	}

	// get the current interactive sprite
	public Sprite getInteractiveSprite() {
		return interactiveSprite;
	}

	// Add a top-level sprite to the canvas	
	public void addSprite(Sprite s) {
		sprites.add(s);
		notifyObservers();
	}

	// get our list of sprite models
	public ArrayList<Sprite> getSprites() {
		return sprites;
	}
	
	// notify the IView observer
	private void notifyObservers() {
		for (IView view : this.views) {
			view.updateView();
		}
	}
}

