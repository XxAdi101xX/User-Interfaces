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

        button = new JButton("Colour Picker");
        button.setPreferredSize(new Dimension(200,60));

        this.setLayout(new GridBagLayout());
		this.add(button, new GridBagConstraints());
        button.addMouseListener(new MouseAdapter() {
            public void mouseReleased (MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // Show colour picker
                    Color chosenColour = colourChooser.showDialog(new JPanel(), "Colour Picker", Color.ORANGE);
                    if (chosenColour != null) {
                        model.setColour(chosenColour, true);
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
			}
		});
    }

    public void setAsSelected(Color colour) {
        setBorder(new LineBorder(colour, 5));

        // button.setForeground(Color.RED);
    }
    
    public void removeAsSelected() {
        System.out.println("resettt");
        setBorder(new LineBorder(Color.BLACK, 5));
    }
}