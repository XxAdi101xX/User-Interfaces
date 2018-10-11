// HelloMVC: a simple MVC example
// the model is just a counter
// inspired by code by Joseph Mack, http://www.austintek.com/mvc/

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.tools.Tool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.*;

import java.net.URL;

public class ToolPalette extends JPanel  {
	private Model model;
	private IconButton selectedTool;

	public ToolPalette(Model model) {
		

		this.setLayout (new GridLayout(3, 2));
		this.setBackground(Color.BLACK);
		
		// create the view UI
		IconButton cursor = new IconButton("icons/cursor2.png");
		IconButton eraser = new IconButton("icons/eraser.png");
		IconButton line = new IconButton("icons/line.png");
		IconButton circle = new IconButton("icons/circle.png");
		IconButton rectangle = new IconButton("icons/rectangle.png");
		IconButton fill = new IconButton("icons/fill.png");

		selectedTool = cursor;
		selectedTool.addAsSelected();

		this.add(cursor);
		this.add(eraser);
		this.add(line);
		this.add(circle);
		this.add(rectangle);
		this.add(fill);
		//button.setMaximumSize(new Dimension(200, 200));
		//button.setPreferredSize(new Dimension(200, 200));

		// set the model
		this.model = model;

		// anonymous class acts as model listener
		this.model.addView(new IView() {
			public void updateView() {
				System.out.println("ColourPalette: updateView");
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

	class IconButton extends JButton {
		private ImageIcon icon;
	
		// Taken from docs.oracle.com
		IconButton(String location) {
			URL url = getClass().getResource(location);
			icon = new ImageIcon(url);
			//Image scaledIcon = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			//icon = new ImageIcon(scaledIcon);
			setIcon(icon);
			
			// set button properties
			setBackground(Color.WHITE);			// setMargin(new Insets(15, 15, 15, 15));
			setPreferredSize(new Dimension(100,100));
			setBorder(new LineBorder(Color.BLACK, 1));
			setFocusPainted(false);
	
			addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) { // left click
						System.out.println("111");
						ToolPalette.this.model.getCounterValue();
						addAsSelected();
					} else if (e.getButton() == MouseEvent.BUTTON3){ // right click
						System.out.println("333");
					}
				}
			});
		}

		public void addAsSelected() {
			selectedTool.removeAsSelected();
			setBorder(new LineBorder(Color.GRAY, 5));
			selectedTool = this;

		}
		
		public void removeAsSelected() {
			System.out.println("resettt");
			setBorder(new LineBorder(Color.BLACK, 1));
		}
	}


}