package view;

import controller.Generation;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainMenuWindow extends JFrame
{
    public MainMenuWindow(Generation generation)
    {
        // On ajoute un titre à notre fenêtre
        super("MuffinShop");
        setSize(500,500);

        // Panels
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;

        //Labels
        JLabel title = new JLabel("UML Generator", SwingConstants.CENTER);
        JLabel subtitle = new JLabel("Bienvenue sur le générateur d'UML par Mathys PIERREZ !", SwingConstants.CENTER);
        JLabel version = new JLabel("Version 1.0.0", SwingConstants.CENTER);

        // Text Areas
        JTextArea textArea = new JTextArea(1,30);
        textArea.setLineWrap(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // File Chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setDialogTitle("Sélectionnez le fichier à convertir");


        // Buttons
        JButton confirmButton = new JButton("Confirmer");
        JButton searchButton = new JButton("Chercher un fichier");
        searchButton.addActionListener( actionEvent -> {
            if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                System.out.println("Selected " + fileChooser.getSelectedFile());
            }
        });

        // Set styles
        title.setFont(new Font("Sans-Serif", Font.BOLD, 22));

        centerPanel.add(title, constraints);
        constraints.gridy++;
        centerPanel.add(subtitle, constraints);
        constraints.gridy++;
        constraints.insets = new Insets(20, 0, 0, 5);
        centerPanel.add(scrollPane, constraints);
        constraints.gridx++;
        centerPanel.add(confirmButton, constraints);
        constraints.gridx--;
        constraints.gridy++;
        centerPanel.add(searchButton, constraints);
        add(centerPanel, BorderLayout.CENTER);
        add(version, BorderLayout.SOUTH);

        // On rend la fenêtre visible
        setResizable(false);
        setVisible(true);

    }

}
