import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.*;	

public class Main {
	public static void main(String[] args){	
		JFrame frame = new JFrame("HelloMVC3b");
		
		// create Model and initialize it
		Model model = new Model();
		
		// create the views
		ToolPalette toolPalette = new ToolPalette(model);
		ColourPalette colourPalette = new ColourPalette(model);
		LineWidthPalette lineWidthPalette = new LineWidthPalette(model);
		Canvas canvas = new Canvas(model);
		// Menu DO I NEED THE FINAL KEYWORD
		MenuBar menuBar = new MenuBar(model);

		JPanel palettePanel = new JPanel(new GridLayout(3, 1));
		// palettePanel.setMaximumSize(new Dimension(300, palettePanel.getHeight()));
		//palettePanel.setPreferredSize(new Dimension(300, palettePanel.getHeight()));
		palettePanel.add(toolPalette);
		palettePanel.add(colourPalette);
		palettePanel.add(lineWidthPalette);
		
		// create a layout panel to hold the two views
		JPanel windowPanel = new JPanel(new BorderLayout());
		frame.getContentPane().add(windowPanel);

		// add views (each view is a JPanel)
		windowPanel.add(palettePanel, BorderLayout.WEST);
		windowPanel.add(canvas, BorderLayout.EAST);

		frame.setJMenuBar(menuBar);
		
		// create the window
		frame.setPreferredSize(new Dimension(1280,800));
		frame.setMinimumSize(new Dimension(640,480));
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
