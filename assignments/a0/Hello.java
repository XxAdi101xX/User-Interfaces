import javax.swing.*;
import java.awt.Font;

public class Hello extends JFrame {

    public Hello(String version){
        JLabel l = new JLabel("Hello Java " + version);
        l.setFont(new Font("Serif", Font.PLAIN, 24));
        add(l);
        setSize(300, 125);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
