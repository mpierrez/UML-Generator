package view;

import controller.MainMenuObserver;
import model.Generation;
import controller.MultipleGeneration;
import controller.SingleGeneration;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.swing.filechooser.FileFilter;


public class MainMenuWindow extends JFrame implements MainMenuObserver
{

    public MainMenuWindow(Generation generation)
    {
        // On ajoute un titre à notre fenêtre
        super("UML Generator");
        generation.setStrategy(new MultipleGeneration(generation.getFile(), generation.getSavePath()));
        setSize(800,500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Panels
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        setContentPane(panel);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());

        JPanel centerPanel2 = new JPanel();
        centerPanel2.setLayout(new GridBagLayout());

        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;

        //Labels
        JLabel title = new JLabel("UML Generator", SwingConstants.CENTER);

        JLabel subtitle = new JLabel("Bienvenue sur le générateur d'UML par Mathys PIERREZ !", SwingConstants.CENTER);

        JLabel filePathLabel = new JLabel("Le fichier ou répertoire suivant sera converti :", SwingConstants.CENTER);
        JLabel savePathLabel = new JLabel("Le fichier UML sera sauvegardé ici :", SwingConstants.CENTER);

        JLabel savingMethod = new JLabel("Comment le résultat doit être généré ?", SwingConstants.CENTER);

        JLabel version = new JLabel("Version 1.0.0", SwingConstants.CENTER);
        JLabel droits = new JLabel("Tous droits réservés.", SwingConstants.CENTER);

        // Text Areas
        JTextArea fileTextArea = new JTextArea(generation.getFile().toString(), 1, 40);
        fileTextArea.setLineWrap(false);
        fileTextArea.setEditable(false);
        JScrollPane fileScrollPane = new JScrollPane(fileTextArea);

        JTextArea saveTextArea = new JTextArea(generation.getSavePath(), 1, 40);
        saveTextArea.setLineWrap(false);
        saveTextArea.setEditable(false);
        JScrollPane saveScrollPane = new JScrollPane(saveTextArea);

        // File Chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));

        // Créer un filtre pour les fichiers .java
        FileFilter javaFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".java");
            }

            @Override
            public String getDescription() {
                return "Fichiers Java (*.java)";
            }
        };

        // Buttons
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton multipleFiles = new JRadioButton("Un fichier pour chaque classe");
        multipleFiles.addActionListener(actionEvent -> generation.setStrategy(new MultipleGeneration(generation.getFile(), generation.getSavePath())));
        JRadioButton oneBigFile = new JRadioButton("Un seul fichier");
        oneBigFile.addActionListener(actionEvent -> generation.setStrategy(new SingleGeneration(generation.getFile(), generation.getSavePath())));
        buttonGroup.add(multipleFiles);
        buttonGroup.add(oneBigFile);
        multipleFiles.setSelected(true);

        JButton confirmButton = new JButton("Confirmer");
        confirmButton.addActionListener( actionEvent -> {
            try {
                generation.startGeneration();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                showMessageBox("Une erreur est survenue, veuillez réessayez plus tard.");
            }
        });
        JButton searchButton = new JButton("Chercher");
        searchButton.addActionListener(actionEvent -> {
            fileChooser.setFileFilter(javaFileFilter);
            fileChooser.setDialogTitle("Sélectionnez le fichier à convertir");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                File file = fileChooser.getSelectedFile();
                fileTextArea.setText(file.toString());
                generation.setFile(file);
                SwingUtilities.updateComponentTreeUI(centerPanel);
            }
        });

        JButton saveButton = new JButton("Définir");
        saveButton.addActionListener(actionEvent -> {
            fileChooser.setDialogTitle("Sélectionnez l'emplacement de sauvegarde");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                String savePath = fileChooser.getSelectedFile().toString();
                saveTextArea.setText(savePath);
                generation.setSavePath(savePath);
                SwingUtilities.updateComponentTreeUI(centerPanel);
            }
        });

        // Set styles
        title.setFont(new Font("Sans-Serif", Font.BOLD, 22));
        topPanel.add(title, constraints);
        constraints.gridy++;
        topPanel.add(subtitle, constraints);
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 20, 0);
        add(topPanel, constraints);

        //
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 10, 0);
        add(filePathLabel, constraints);

        constraints.gridy++;
        centerPanel.add(fileScrollPane, constraints);
        constraints.insets = new Insets(0, 10, 0, 0);
        constraints.gridx++;
        centerPanel.add(searchButton, constraints);
        constraints.gridx--;
        add(centerPanel, constraints);

        //
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 10, 0);
        add(savePathLabel, constraints);

        constraints.gridy++;
        centerPanel2.add(saveScrollPane, constraints);
        constraints.insets = new Insets(0, 10, 0, 0);
        constraints.gridx++;
        centerPanel2.add(saveButton, constraints);
        constraints.gridx--;
        add(centerPanel2, constraints);

        constraints.gridy++;
        constraints.insets = new Insets(20, 0, 0, 0);
        add(savingMethod, constraints);

        parametersPanel.add(multipleFiles, constraints);
        constraints.gridx++;
        parametersPanel.add(oneBigFile, constraints);
        constraints.gridx--;
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 20, 0);
        add(parametersPanel, constraints);

        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 20, 0);
        add(confirmButton, constraints);

        constraints.gridy++;
        add(version, constraints);
        constraints.gridy++;
        add(droits, constraints);

        // On rend la fenêtre visible
        setResizable(false);
        setVisible(true);

    }

    @Override
    public void showMessageBox(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
