package view;

import javax.swing.*;
import java.awt.*;

public class ConfigWindow extends JFrame
{

    public ConfigWindow(MainMenuWindow mainMenu, String[] names, boolean whitesFirst)
    {
        super("Paramètres");
        setSize( 400, 450 );
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel title = new JLabel("Qui commence la partie ?");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));

        JLabel subtitle = new JLabel("(détermine le joueur qui doit commencer à jouer)");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 9));

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton rdbWhites = (whitesFirst) ? new JRadioButton("Blancs", true) : new JRadioButton("Blancs", false);
        JRadioButton rdbBlacks = (whitesFirst) ? new JRadioButton("Noirs", false) : new JRadioButton("Noirs", true);

        buttonGroup.add(rdbWhites);
        buttonGroup.add(rdbBlacks);

        JLabel lblWhites = new JLabel("Quel est le nom assigné aux blancs ?");
        lblWhites.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        JTextField txtWhites = new JTextField(names[0]);

        JLabel lblBlacks = new JLabel("Quel est le nom assigné aux noirs ?");
        lblBlacks.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        JTextField txtBlacks = new JTextField(names[1]);

        JButton saveButton = new JButton("Sauvegarder");
        saveButton.addActionListener(actionEvent -> {
            mainMenu.setWhitesFirst(rdbWhites.isSelected());
            mainMenu.setWhitesName(txtWhites.getText());
            mainMenu.setBlacksName(txtBlacks.getText());
            this.dispose();
        });

        JButton quitButton = new JButton("Quitter");
        quitButton.addActionListener(actionEvent -> {
            this.dispose();
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(title, constraints);
        constraints.gridy++;
        panel.add(subtitle, constraints);
        constraints.insets = new Insets(10,10,10,10);
        constraints.gridy++;
        panel.add(rdbWhites, constraints);
        constraints.gridy++;
        panel.add(rdbBlacks, constraints);
        constraints.gridy++;
        panel.add(lblWhites, constraints);
        constraints.gridx++;
        panel.add(txtWhites, constraints);
        constraints.gridx--;
        constraints.gridy++;
        panel.add(lblBlacks, constraints);
        constraints.gridx++;
        panel.add(txtBlacks, constraints);
        constraints.gridx--;
        constraints.gridy++;
        panel.add(saveButton, constraints);
        constraints.gridx++;
        panel.add(quitButton, constraints);
        setContentPane(panel);
        setVisible(true);
    }
}
