// HelloMVC: a simple MVC example
// the model is just a counter
// inspired by code by Joseph Mack, http://www.austintek.com/mvc/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class ColourPalette extends JPanel  {

	// the view's main user interface
	private JButton button;

	// the model that this view is showing
	private Model model;


	public ColourPalette(Model model) {
		setBackground(Color.CYAN);
		ColourPanel a = new ColourPanel(Color.RED);
		ColourPanel b = new ColourPanel(Color.GREEN);
		ColourPanel c = new ColourPanel(Color.BLUE);
		ColourPanel d = new ColourPanel(Color.ORANGE);
		ColourPanel e = new ColourPanel(Color.BLACK);
		ColourPanel f = new ColourPanel(Color.WHITE);

		this.setLayout (new GridLayout(3, 2));
		this.add(a);
		this.add(b);
		this.add(c);
		this.add(d);
		this.add(e);
		this.add(f);
		// // create the view UI
		// button = new JButton("?");
		// button.setMaximumSize(new Dimension(100, 50));
		// button.setPreferredSize(new Dimension(100, 50));
		// // a GridBagLayout with default constraints centres
		// // the widget in the window
		// this.setMaximumSize(new Dimension(100, 200));
		// this.setLayout(new GridBagLayout());
		// this.add(button, new GridBagConstraints());

		// set the model
		this.model = model;

		// anonymous class acts as model listener
		this.model.addView(new IView() {
			public void updateView() {
				System.out.println("ToolPalette: updateView");
				// button.setText(Integer.toString(model.getCounterValue()));
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


}

class ColourPanel extends JButton {
	public ColourPanel(Color color) {	
		setBackground(color);
	}
}
