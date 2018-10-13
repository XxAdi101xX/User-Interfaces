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
		// set the model
		this.model = model;		
		
		// Instantiate the various tool options
		IconButton cursor = new IconButton("icons/cursor2.png");
		IconButton eraser = new IconButton("icons/eraser.png");
		IconButton line = new IconButton("icons/line.png");
		IconButton circle = new IconButton("icons/circle.png");
		IconButton rectangle = new IconButton("icons/rectangle.png");
		IconButton fill = new IconButton("icons/fill.png");
		
		// Add tool options to layout
		this.setLayout (new GridLayout(3, 2));
		this.add(cursor);
		this.add(eraser);
		this.add(line);
		this.add(circle);
		this.add(rectangle);
		this.add(fill);
		
		// Set default tool
		selectedTool = cursor;
		selectedTool.addAsSelected();
		
		this.setBackground(Color.BLACK);
		
		// anonymous class acts as model listener
		this.model.addView(new IView() { // NECESSARY?
			public void updateView() {
				System.out.println("ColourPalette: updateView");
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
						addAsSelected();
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
			setBorder(new LineBorder(Color.BLACK, 1));
		}
	}


}