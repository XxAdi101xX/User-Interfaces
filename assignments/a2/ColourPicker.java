import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.*;

class ColourPicker extends JPanel {
    private JColorChooser colourChooser = new JColorChooser();
    private JButton button;
    private Model model;

    public ColourPicker(Model model) {
        this.model = model;

        button = new JButton("Colour Picker");
        button.setBackground(new Color(211, 211, 211));
        button.setPreferredSize(new Dimension(getWidth(), 50));

        this.setLayout(new GridLayout(1, 1));
        this.add(button);
        this.setBackground(Color.WHITE);
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
                } else {
                    setAsSelected(model.getCurrentShape().getBorderColour());
                }
			}
		});
    }

    public void setAsSelected(Color colour) {
        setBorder(new LineBorder(colour, 5));
    }
    
    public void removeAsSelected() {
        setBorder(new LineBorder(Color.BLACK, 5));
    }
}