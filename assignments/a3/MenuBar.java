import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.awt.List;
import java.awt.Point;
import java.awt.event.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MenuBar extends JMenuBar {
	private Model model;
	private JFileChooser fileChooser = new JFileChooser();
	private FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("jPanel Text File", "txt");
	private JMenu file = new JMenu("File");
	private JMenuItem resetCanvas;
	private JMenuItem loadCanvas;
	private JMenuItem saveCanvas;
	private JMenuItem quit;
	private JMenu addFigure;
	private JMenuItem humanFigure;
	private JMenuItem dogFigure;
	private JMenuItem scarecrowFigure;

	public MenuBar(Model model) {
		this.model = model;
		fileChooser.setFileFilter(fileFilter);
		
		file = new JMenu("File");
		addFigure = new JMenu("Add");
		add(file);
		add(addFigure);
		
		// create menu items
		resetCanvas = new JMenuItem("Reset");
		quit = new JMenuItem("Quit");
		loadCanvas = new JMenuItem("Load");
		saveCanvas = new JMenuItem("Save");


		// set shortcuts for convenience
		resetCanvas.setAccelerator(KeyStroke.getKeyStroke (KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		quit.setAccelerator(KeyStroke.getKeyStroke (KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		loadCanvas.setAccelerator(KeyStroke.getKeyStroke (KeyEvent.VK_L, ActionEvent.CTRL_MASK));		
		saveCanvas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

		// add to file menu
		file.add(resetCanvas);
		file.addSeparator();
		file.add(quit);
		// file.add(loadCanvas);
		// file.add(saveCanvas);
		
		// Menu Items for AddFigure
		humanFigure = new JMenuItem("Human");
		dogFigure = new JMenuItem("Dog");
		scarecrowFigure = new JMenuItem("Crazy Scarecrow");
		
		addFigure.add(humanFigure);
		addFigure.add(dogFigure);
		addFigure.add(scarecrowFigure);
		
		resetCanvas.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				loadNewCanvas();
			}
		});

		quit.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				System.exit(0);
			}
		});
		
		// loadCanvas.addActionListener(new ActionListener() {
		// 	public void actionPerformed (ActionEvent e) {
		// 		loadCanvasFromFile();
		// 	}
		// });

		// saveCanvas.addActionListener(new ActionListener() {
		// 	public void actionPerformed(ActionEvent e) {
		// 		saveCanvasToFile();
		// 	}
		// });

		humanFigure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.addHumanFigure();
			}
		});

		dogFigure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.addDogFigure();
			}
		});

		scarecrowFigure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.addScarecrowFigure();
			}
		});
	}

	private void loadNewCanvas() {
		Object[] options = {"Yes", "No", "Cancel"};
		int response = JOptionPane.showOptionDialog(
							new JPanel(),
							"Would you like to save before resetting your canvas?",
							"JSketch",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE,
							null,
							options,
							options[2]
						);
		
		if (response == 0) {
			// save
			// saveCanvasToFile(); TODO
		} else if (response == 1) {
			// reset
			model.resetCanvas();
		}
	}

	// private void loadCanvasFromFile() {
	// 	try {
	// 		int response = fileChooser.showOpenDialog(getParent());
	// 		if (response == JFileChooser.APPROVE_OPTION) {
	// 			model.clearShapes();
	// 			Scanner scanner = new Scanner(new File(fileChooser.getSelectedFile().getName()));
	// 			while(scanner.hasNextInt()) {
	// 				Tool tool = Tool.getToolFromInt(scanner.nextInt());
	// 				Color borderColour = new Color(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
	// 				Color backgroundColour = new Color(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
	// 				Boolean isFilled = scanner.nextInt() == 1 ? true : false;
	// 				int lineWidth = scanner.nextInt();
	// 				Point startPoint = new Point(scanner.nextInt(),scanner.nextInt());
	// 				Point endPoint = new Point(scanner.nextInt(), scanner.nextInt());
					
	// 				Shape shape = new Shape(tool, borderColour, backgroundColour, isFilled, lineWidth, startPoint.x, startPoint.y, endPoint.x, endPoint.y);
	// 				this.model.addShape(shape);
	// 			}
	// 			scanner.close();
	// 		}
	// 	} catch (Exception exception) {
	// 		System.out.println ("Exception Occured when Loading. Invalid file!");
	// 	}
	// }
	
	// private void saveCanvasToFile() {
	// 	try {
	// 		int response = fileChooser.showSaveDialog(getParent());

	// 		if (response == JFileChooser.APPROVE_OPTION) {
	// 			PrintWriter out = new PrintWriter(new FileWriter(fileChooser.getSelectedFile().getName()));

	// 			for (Shape s : this.model.getShapeList()) {
	// 				String shapeInfo = "" + s.getShape().ordinal() + " " + 
	// 									s.getBorderColour().getRed() + " " + 
	// 									s.getBorderColour().getGreen() + " " +
	// 									s.getBorderColour().getBlue() + " ";
	// 				if (s.getBackgroundColour() != null) {
	// 					shapeInfo += "" + s.getBackgroundColour().getRed() + " " +
	// 									s.getBackgroundColour().getGreen() + " " +
	// 				   					s.getBackgroundColour().getBlue() + " ";
	// 				} else {
	// 					shapeInfo += "0 0 0 ";
	// 				}
	// 				int isFilled = s.getFilled() ? 1 : 0;
	// 				shapeInfo += "" + isFilled + " " +
	// 							s.getLineWidth() + " " +
	// 							s.getStartPoints().x + " " +
	// 							s.getStartPoints().y + " " +
	// 							s.getEndPoints().x + " " +
	// 							s.getEndPoints().y;
	// 				out.println(shapeInfo);
	// 			}
	// 			out.close();
	// 		}
	// 	} catch (Exception exception) {
	// 		System.out.println ("Exception Occured when Saving. Invalid file!");
	// 	}
	// }
}