import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

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
	private ToolPanel selectedToolPanel;	

	public ToolPalette(Model model) {
		// set the model
		this.model = model;		
		
		// Instantiate the various tool options
		ToolPanel cursor = new ToolPanel(Tool.CURSOR, "icons/cursor2.png");
		ToolPanel eraser = new ToolPanel(Tool.ERASER, "icons/eraser.png");
		ToolPanel line = new ToolPanel(Tool.LINE, "icons/line.png");
		ToolPanel circle = new ToolPanel(Tool.CIRCLE, "icons/circle.png");
		ToolPanel rectangle = new ToolPanel(Tool.RECTANGLE, "icons/rectangle.png");
		ToolPanel fill = new ToolPanel(Tool.FILL, "icons/fill.png");
		
		// Add tool options to layout
		this.setLayout (new GridLayout(3, 2));
		this.add(cursor);
		this.add(eraser);
		this.add(line);
		this.add(circle);
		this.add(rectangle);
		this.add(fill);
		
		// Set default tool
		this.model.setTool(Tool.CURSOR);
		selectedToolPanel = cursor;
		selectedToolPanel.addAsSelected();
		
		this.setBackground(Color.BLACK);
		
		// // anonymous class acts as model listener
		// this.model.addView(new IView() { // NECESSARY?
		// 	public void updateView() {
		// 	}
		// });


		// setup the event to go to the "controller"
		// (this anonymous class is essentially the controller)
		// button.addActionListener(new ActionListener() {
		// 	public void actionPerformed(ActionEvent e) {
		// 		model.incrementCounter();
		// 	}
		// });
	}

	class ToolPanel extends JButton {
		private Tool tool;
		private ImageIcon icon;
	
		ToolPanel(Tool toolType, String location) {
			tool = toolType;
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
			ToolPalette.this.model.setTool(tool);
			selectedToolPanel.removeAsSelected();
			selectedToolPanel = this;
			setBorder(new LineBorder(Color.GRAY, 5));

		}
		
		public void removeAsSelected() {
			setBorder(new LineBorder(Color.BLACK, 1));
		}
	}


}