package view;

import controller.Generation;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainMenuWindow extends JFrame
{
    //private String filePath = "Aucun fichier sélectionné";
    private String filePath = "C:\\Users\\mathy\\OneDrive\\Bureau\\Robert_Schuman\\2\\A32\\a31-chessgame\\ChessGame\\src\\model";
    private String savePath;

    public MainMenuWindow(Generation generation)
    {
        // On ajoute un titre à notre fenêtre
        super("UML Generator");
        this.savePath = System.getProperty("user.dir") + "\\uml";
        setSize(800,500);

        // Panels
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        setContentPane(panel);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());

        JPanel confirmPanel = new JPanel();
        confirmPanel.setLayout(new GridBagLayout());

        JPanel bottomPanel = new JPanel();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;

        //Labels
        JLabel title = new JLabel("UML Generator", SwingConstants.CENTER);

        JLabel subtitle = new JLabel("Bienvenue sur le générateur d'UML par Mathys PIERREZ !", SwingConstants.CENTER);

        JLabel filePathLabel = new JLabel("Le fichier ou répertoire suivant sera converti :", SwingConstants.CENTER);
        JLabel savePathLabel = new JLabel("Le fichier UML sera sauvegardé ici :", SwingConstants.CENTER);

        JLabel version = new JLabel("Version 1.0.0", SwingConstants.CENTER);

        // Text Areas
        JTextArea fileTextArea = new JTextArea(filePath, 1, 30);
        fileTextArea.setLineWrap(false);
        fileTextArea.setEditable(false);
        JScrollPane fileScrollPane = new JScrollPane(fileTextArea);

        JTextArea saveTextArea = new JTextArea(savePath, 1, 30);
        saveTextArea.setLineWrap(false);
        saveTextArea.setEditable(false);
        JScrollPane saveScrollPane = new JScrollPane(saveTextArea);

        // File Chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));

        // Buttons
        JButton confirmButton = new JButton("Confirmer");
        confirmButton.addActionListener( actionEvent -> {
            try {
                generation.generate(new File(filePath), savePath);
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        });
        JButton searchButton = new JButton("Chercher");
        searchButton.addActionListener(actionEvent -> {
            fileChooser.setDialogTitle("Sélectionnez le fichier à convertir");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                this.filePath = fileChooser.getSelectedFile().toString();
                fileTextArea.setText(filePath);
                SwingUtilities.updateComponentTreeUI(centerPanel);
            }
        });

        JButton saveButton = new JButton("Définir");
        saveButton.addActionListener(actionEvent -> {
            fileChooser.setDialogTitle("Sélectionnez l'emplacement de sauvegarde");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                this.savePath = fileChooser.getSelectedFile().toString();
                saveTextArea.setText(savePath);
                SwingUtilities.updateComponentTreeUI(centerPanel);
            }
        });

        // Set styles
        title.setFont(new Font("Sans-Serif", Font.BOLD, 22));
        topPanel.add(title, constraints);
        constraints.gridy++;
        topPanel.add(subtitle, constraints);
        constraints.gridy++;

        constraints.insets = new Insets(0, 50, 0, 0);
        centerPanel.add(filePathLabel, constraints);
        constraints.gridy++;
        centerPanel.add(fileScrollPane, constraints);
        constraints.insets = new Insets(0, 10, 0, 0);
        constraints.gridx++;
        centerPanel.add(searchButton, constraints);
        constraints.gridx--;
        constraints.gridy++;
        constraints.insets = new Insets(10, 50, 0, 0);

        centerPanel.add(savePathLabel, constraints);
        constraints.gridy++;
        centerPanel.add(saveScrollPane, constraints);
        constraints.gridx++;
        constraints.insets = new Insets(0, 10, 0, 0);
        centerPanel.add(saveButton, constraints);
        constraints.gridx--;
        constraints.gridy++;
        constraints.insets = new Insets(10, 0, 0, 0);
        confirmPanel.add(confirmButton, constraints);
        constraints.gridy++;
        bottomPanel.add(version);

        add(topPanel);
        add(centerPanel);
        add(confirmPanel);
        add(bottomPanel);

        // On rend la fenêtre visible
        setResizable(false);
        setVisible(true);

    }

}
