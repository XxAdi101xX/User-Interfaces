// NOTE: the following layout of the general architecture was the code was taken from the class examples shown in the HelloMVC repository
// hence, using the transitive property, this was inspired by code from Joseph Mack, http://www.austintek.com/mvc/

import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;	

public class Main {
	public static void main(String[] args){	
		JFrame frame = new JFrame("JSketch");
		
		// create Model and initialize it
		Model model = new Model();
		
		// create the views
		ToolPalette toolPalette = new ToolPalette(model);
		ColourPalette colourPalette = new ColourPalette(model);
		LineWidthPalette lineWidthPalette = new LineWidthPalette(model);
		ColourPicker colourPicker = new ColourPicker(model);
		Canvas canvas = new Canvas(model);
		MenuBar menuBar = new MenuBar(model);

		// create bottom palette with colour picker and line widths
		JPanel bottomPalette = new JPanel(new BorderLayout());
		bottomPalette.add(colourPicker, BorderLayout.NORTH);
		bottomPalette.add(lineWidthPalette);

		// create general palette paneel
		JPanel palettePanel = new JPanel(new GridLayout(3, 1));
		palettePanel.add(toolPalette);
		palettePanel.add(colourPalette);
		palettePanel.add(bottomPalette);

		// create a layout panel to hold the two views
		JPanel windowPanel = new JPanel(new BorderLayout());	
		windowPanel.add(palettePanel, BorderLayout.WEST);
		windowPanel.add(canvas);
		
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				int panelWidth = 180 + windowPanel.getWidth() / 20;
				int scrollPaneWidth = 5;
				model.setCurrentCanvasSize(windowPanel.getWidth() - panelWidth - scrollPaneWidth, windowPanel.getHeight() - scrollPaneWidth);
				palettePanel.setPreferredSize(new Dimension(panelWidth, windowPanel.getHeight()));
			}
		});
		// create the window
		frame.setPreferredSize(new Dimension(1280,800));
		frame.setMinimumSize(new Dimension(640,480));
		frame.getContentPane().add(windowPanel);
		frame.setJMenuBar(menuBar);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
