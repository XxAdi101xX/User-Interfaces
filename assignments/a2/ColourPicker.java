import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.event.*;

class ColourPicker extends JPanel {
    private JColorChooser colourChooser = new JColorChooser();
    private JButton button;
    private Model model;

    public ColourPicker(Model model) {
        this.model = model;
        // setBorder(new LineBorder(Color.BLACK, 1));
        button = new JButton("Colour Picker");
        button.setBorder(new LineBorder(Color.BLACK, 1));
        button.setPreferredSize(new Dimension(200,60));

        this.setLayout(new GridBagLayout());
		this.add(button, new GridBagConstraints());
        // Show colour picker
        button.addMouseListener(new MouseAdapter() {
            public void mouseReleased (MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Color chosenColour = colourChooser.showDialog(new JPanel(), "Colour Picker", Color.ORANGE);
                    if (chosenColour != null) {
                        model.updateColour(chosenColour, true);
                        setAsSelected(chosenColour);
                    }
                }
            }
        });

        this.model.addView(new IView() {
			public void updateView() {
                System.out.println("ColourPicker: updateView");
                if (!model.isUsingCustomColour()) {
                    removeAsSelected();
                }
				// button.setText(Integer.toString(model.getCounterValue()));
			}
		});
    }

    public void setAsSelected(Color colour) {
        setBorder(new LineBorder(colour, 5));

    }
    
    public void removeAsSelected() {
        System.out.println("resettt");
        setBorder(new LineBorder(Color.BLACK, 1));
    }
}