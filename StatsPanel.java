import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class StatsPanel extends JPanel implements ActionListener {
    private Color BACKGROUND = Color.decode("#ffffff");
    private Color TEXT_DARK = Color.decode("#1a1a1b");
    private Color BORDER_COLOR = Color.decode("#d3d6da");
    private Color ACCENT = Color.decode("#6aaa64");

    private JButton mainMenuButton;
    private JLabel titleLabel, giveUps, correctGuess, incorrectGuess;

    private CardLayout cLayout;
    private JPanel cardPanel;

    private int noOfGiveUps, noOfCorrectGuess, noOfIncorrectGuess;

    public StatsPanel(CardLayout cLayout, JPanel cardPanel) {
        BoxLayout boxLayout1 = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout1);
        setPreferredSize(new Dimension(400, 440));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("STATISTICS");
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(TEXT_DARK);

        mainMenuButton = new JButton("MAIN MENU");
        mainMenuButton.setAlignmentX(CENTER_ALIGNMENT);
        buttonDesign(mainMenuButton, "#6aaa64");
        mainMenuButton.addActionListener(this);

        giveUps = new JLabel();
        correctGuess = new JLabel();
        incorrectGuess = new JLabel();
        setStatLabels();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(500, 50));
        buttonPanel.add(mainMenuButton);

        add(Box.createVerticalStrut(60));
        add(titleLabel);
        add(Box.createVerticalStrut(50));
        add(giveUps);
        add(Box.createVerticalStrut(16));
        add(correctGuess);
        add(Box.createVerticalStrut(16));
        add(incorrectGuess);
        add(Box.createVerticalStrut(40));
        add(buttonPanel);

        setcLayout(cLayout);
        setCardPanel(cardPanel);
    }

    public void buttonDesign(JButton button, String color) {
        Color base = Color.decode(color);
        Color hover = base.darker();

        button.setPreferredSize(new Dimension(500, 46));
        button.setMaximumSize(new Dimension(500, 46));
        button.setBackground(base);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(base);
            }
        });
    }

    public void labelDesign(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(TEXT_DARK);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(Color.decode("#f7f7f8"));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        label.setMaximumSize(new Dimension(340, 55));
    }

    public void setStatLabels() {
        File fileReader = new File("Stats\\Stats.txt");

        try {
            Scanner scan = new Scanner(fileReader);

            String content = scan.nextLine();
            String token[] = content.split(";");

            noOfGiveUps = Integer.parseInt(token[0]);
            noOfCorrectGuess = Integer.parseInt(token[1]);
            noOfIncorrectGuess = Integer.parseInt(token[2]);

            giveUps.setText("No. of Give Ups: " + noOfGiveUps);
            correctGuess.setText("No. of Correct Guesses: " + noOfCorrectGuess);
            incorrectGuess.setText("No. of Incorrect Guesses: " + noOfIncorrectGuess);

            labelDesign(giveUps);
            labelDesign(correctGuess);
            labelDesign(incorrectGuess);

            scan.close();
        } catch (FileNotFoundException e) {}
    }

    public void updateStats(boolean isGiveUp, boolean isCorrectGuess, boolean isIncorrectGuess) {
        try {
            FileWriter fileWriter = new FileWriter("Stats\\Stats.txt");

            if(isGiveUp) noOfGiveUps++;
            if(isCorrectGuess) noOfCorrectGuess++;
            if(isIncorrectGuess) noOfIncorrectGuess++;

            giveUps.setText("No. of Give Ups: " + noOfGiveUps);
            correctGuess.setText("No. of Correct Guesses: " + noOfCorrectGuess);
            incorrectGuess.setText("No. of Incorrect Guesses: " + noOfIncorrectGuess);

            fileWriter.write(Integer.toString(noOfGiveUps) + ";" + Integer.toString(noOfCorrectGuess) + ";" + Integer.toString(noOfIncorrectGuess));

            fileWriter.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == mainMenuButton) cLayout.show(cardPanel, "Main Menu");
    }

    public CardLayout getcLayout() {
        return cLayout;
    }

    public void setcLayout(CardLayout cLayout) {
        this.cLayout = cLayout;
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    public void setCardPanel(JPanel cardPanel) {
        this.cardPanel = cardPanel;
    }
}