import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.awt.List;
import java.awt.Point;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class MenuBar extends JMenuBar {
	private Model model;
	private JFileChooser fileChooser = new JFileChooser();
	private FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("jPanel Text File", "txt");
	private JMenu file = new JMenu("File");
	// private JMenu view = new JMenu("View");

	public MenuBar(Model model) {
		this.model = model;
		fileChooser.setFileFilter(fileFilter);
		
		add(file);

		// Menu Items for File 
		JMenuItem newCanvas = new JMenuItem("New");
		newCanvas.setAccelerator(KeyStroke.getKeyStroke (KeyEvent.VK_N, ActionEvent.CTRL_MASK));

		JMenuItem loadCanvas = new JMenuItem("Load");
		loadCanvas.setAccelerator(KeyStroke.getKeyStroke (KeyEvent.VK_L, ActionEvent.CTRL_MASK));

		JMenuItem saveCanvas = new JMenuItem("Save");
		saveCanvas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

		file.add(newCanvas);
		file.add(loadCanvas);
		file.add(saveCanvas);
		
		// Optional feature yayyy
		// add(view);
		// // Menu Items for View
		// JMenuItem fullSize = new JMenuItem("Full Size");
		// JMenuItem fitToWindow = new JMenuItem("Fit to Window");
		
		// view.add(fullSize);
		// view.add(fitToWindow);
		
		newCanvas.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				loadNewCanvas();
			}
		});
		
		loadCanvas.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				loadCanvasFromFile();
			}
		});

		saveCanvas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveCanvasToFile();
			}
		});
	}

	private void loadNewCanvas() {
		Object[] options = {"Yes", "No", "Cancel"};
		int response = JOptionPane.showOptionDialog(
							new JPanel(),
							"Would you like to save before clearing your canvas?",
							"JSketch",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE,
							null,
							options,
							options[2]
						);
		
		if (response == 0) {
			// save
			saveCanvasToFile();
		} else if (response == 1) {
			// clear
			model.clearShapes();
		}
	}

	private void loadCanvasFromFile() {
		try {
			int response = fileChooser.showOpenDialog(getParent());
			if (response == JFileChooser.APPROVE_OPTION) {
				model.clearShapes();
				Scanner scanner = new Scanner(new File(fileChooser.getSelectedFile().getName()));
				while(scanner.hasNextInt()) {
					Tool tool = Tool.getToolFromInt(scanner.nextInt());
					Color borderColour = new Color(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
					Color backgroundColour = new Color(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
					Boolean isFilled = scanner.nextInt() == 1 ? true : false;
					int lineWidth = scanner.nextInt();
					Point startPoint = new Point(scanner.nextInt(),scanner.nextInt());
					Point endPoint = new Point(scanner.nextInt(), scanner.nextInt());
					
					Shape shape = new Shape(tool, borderColour, backgroundColour, isFilled, lineWidth, startPoint.x, startPoint.y, endPoint.x, endPoint.y);
					this.model.addShape(shape);
				}
				scanner.close();
			}
		} catch (Exception exception) {
			System.out.println ("Exception Occured when Loading!!!!");
			exception.printStackTrace();
		}
	}
	
	private void saveCanvasToFile() {
		try {
			int response = fileChooser.showSaveDialog(getParent());

			if (response == JFileChooser.APPROVE_OPTION) {
				PrintWriter out = new PrintWriter(new FileWriter(fileChooser.getSelectedFile().getName()));

				for (Shape s : this.model.getShapeList()) {
					String shapeInfo = "" + s.getShape().ordinal() + " " + 
										s.getBorderColour().getRed() + " " + 
										s.getBorderColour().getGreen() + " " +
										s.getBorderColour().getBlue() + " ";
					if (s.getBackgroundColour() != null) {
						shapeInfo += "" + s.getBackgroundColour().getRed() + " " +
										s.getBackgroundColour().getGreen() + " " +
					   					s.getBackgroundColour().getBlue() + " ";
					} else {
						shapeInfo += "0 0 0 ";
					}
					int isFilled = s.getFilled() ? 1 : 0;
					shapeInfo += "" + isFilled + " " +
								s.getLineWidth() + " " +
								s.getStartPoints().x + " " +
								s.getStartPoints().y + " " +
								s.getEndPoints().x + " " +
								s.getEndPoints().y;
					out.println(shapeInfo);
				}
				out.close();
			}
		} catch (Exception exception) {
			System.out.println ("Exception Occured when Saving!!!!");
			exception.printStackTrace();
		}
	}
}