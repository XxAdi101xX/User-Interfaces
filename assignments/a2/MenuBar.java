import javax.swing.*;
// import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.*;

public class MenuBar extends JMenuBar {
	private Model model;
	private JMenu file = new JMenu("File");
	private JMenu view = new JMenu("View");
	// private JFileChooser choose = new JFileChooser();
	// FileNameExtensionFilter filter = new FileNameExtensionFilter("Binary files", "yikwun");

	public MenuBar(Model model) {
		this.model = model;
		// choose.setFileFilter (filter);
		this.add(file);
		this.add(view);

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

		// Menu Items for View
		JMenuItem fullSize = new JMenuItem("Full Size");
		JMenuItem fitToWindow = new JMenuItem("Fit to Window");

		view.add(fullSize);
		view.add(fitToWindow);

		// save.addActionListener(new ActionListener() {
		// 	public void actionPerformed(ActionEvent e) {
		// 		writeFile();
		// 	}
		// });

		// newItem.addActionListener(new ActionListener() {
		// 	public void actionPerformed (ActionEvent e) {
		// 		Object[] options = {"Save", "Don't Save", "Cancel"};
		// 		int val = JOptionPane.showOptionDialog(new JPanel(),
		// 			"Would you like to save your progress?",
		// 			"Doodle",
		// 			JOptionPane.YES_NO_CANCEL_OPTION,
		// 			JOptionPane.QUESTION_MESSAGE,
		// 			null,
		// 			options,
		// 			options[2]);

		// 		// Save
		// 		if (val == 0) {
		// 			if (writeFile() == 1) { model.reset(); }
		// 		}

		// 		// Don't save
		// 		else if (val == 1) {
		// 			model.reset();
		// 		}
		// 	}
		// });

		// load.addActionListener(new ActionListener() {
		// 	public void actionPerformed (ActionEvent e) {
		// 		choose.showOpenDialog(getParent());
		// 		try {
		// 			FileInputStream f = new FileInputStream(choose.getSelectedFile().getAbsolutePath());
		// 			ObjectInputStream out = new ObjectInputStream(f);
		// 			model.loadModel((Model) out.readObject());
		// 		}

		// 		catch (Exception ex) {
		// 			ex.printStackTrace();
		// 		}
		// 	}
		// });
	}

	// 	// Save
	// 	if (val == 0) {
	// 		if (writeFile() == 1) { System.exit(0); }
	// 	}

	// 	// Don't save
	// 	else if (val == 1) {
	// 		System.exit(0);
	// 	}
	// }

	// private int writeFile() {
	// 	try {
	// 		int val = choose.showSaveDialog(getParent());

	// 		if (val == JFileChooser.APPROVE_OPTION)
	// 		{
	// 			FileOutputStream f = new FileOutputStream(choose.getSelectedFile().getAbsolutePath() + "." + filter.getExtensions()[0]);
	// 			ObjectOutputStream out = new ObjectOutputStream(f);
	// 			out.writeObject(model);
	// 			out.close();

	// 			return 1;
	// 		}
	// 	}

	// 	catch (Exception e) {
	// 		System.out.println ("Exception");
	// 	}

	// 	// Cancelled
	// 	return 0;
	// }
}