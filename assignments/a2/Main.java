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
		// Menu DO I NEED THE FINAL KEYWORD
		MenuBar menuBar = new MenuBar(model);

		JPanel palettePanel = new JPanel(new GridLayout(3, 1));
		// palettePanel.setMaximumSize(new Dimension(300, palettePanel.getHeight()));
		//palettePanel.setPreferredSize(new Dimension(300, palettePanel.getHeight()));
		palettePanel.add(toolPalette);
		palettePanel.add(colourPalette);
		// palettePanel.add(lineWidthPalette);

		JPanel bottomPalette = new JPanel(new BorderLayout());
		bottomPalette.add(colourPicker, BorderLayout.NORTH);
		bottomPalette.add(lineWidthPalette);

		palettePanel.add(bottomPalette);
		// button.setMaximumSize(new Dimension(200, 200));
		// button.setPreferredSize(new Dimension(200, 200));

		toolPalette.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		// colourPalette.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		
		// create a layout panel to hold the two views
		JPanel windowPanel = new JPanel(new BorderLayout());
		frame.getContentPane().add(windowPanel);

		// add views (each view is a JPanel)
		windowPanel.add(palettePanel, BorderLayout.WEST);
		windowPanel.add(canvas, BorderLayout.EAST);

		frame.setJMenuBar(menuBar);
		
		// create the window
		frame.setPreferredSize(new Dimension(880,800));
		frame.setMinimumSize(new Dimension(640,480));
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
