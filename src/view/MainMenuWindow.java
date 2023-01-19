package view;

import controller.Game;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class MainMenuWindow extends JFrame
{
    private String[] names = {"Joueur1", "Joueur2"};
    private boolean whitesFirst = true;

    public MainMenuWindow() {
        super( "Menu principal" );
        setSize( 600, 650 );
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel logo = new JLabel();
        logo.setIcon(new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/logo.png"))).getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT)));


        JLabel title = new JLabel("Chess Game");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

        JLabel subtitle = new JLabel("(by Mathys PIERREZ and Yoann GRIM)");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 10));

        // On centre le contenu de label horizontallement
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        // On centre le contenu de label verticalement
        logo.setVerticalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        subtitle.setVerticalAlignment(SwingConstants.CENTER);

        JButton startButton = new JButton("Commencer");
        startButton.addActionListener(actionEvent  -> {
                Game game = new Game(names, whitesFirst);
                game.startGame();
                this.dispose();
        });
        JButton configButton = new JButton("Paramètres");
        configButton.addActionListener(actionEvent -> {
            new ConfigWindow(this, names, whitesFirst);
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
        panel.add(logo, constraints);
        constraints.gridy++;
        panel.add(title, constraints);
        constraints.gridy++;
        panel.add(subtitle, constraints);
        constraints.insets = new Insets(10,10,10,10);
        constraints.gridy++;
        panel.add(startButton, constraints);
        constraints.gridy++;
        panel.add(configButton, constraints);
        constraints.gridy++;
        panel.add(quitButton, constraints);
        setContentPane(panel);
        setVisible(true);
    }

    public void setWhitesName(String whitesName)
    {
        this.names[0] = whitesName;
    }

    public void setBlacksName(String blacksName)
    {
        this.names[1] = blacksName;
    }

    public void setWhitesFirst(boolean b)
    {
        this.whitesFirst = b;
    }
}
