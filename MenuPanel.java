import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MenuPanel extends JPanel implements ActionListener {
    private Color BACKGROUND = Color.decode("#ffffff");
    private Color TEXT_MUTED = Color.decode("#565758");

    private JButton playButton, statsButton, exitButton;
    private JLabel subtitle;
    private GuessPanel guessPanel;
    private GameState gameState;

    private CardLayout cLayout;
    private JPanel cardPanel;

    public MenuPanel(CardLayout cLayout, JPanel cardPanel, GuessPanel guessPanel, GameState gameState) {
        BoxLayout boxLayout1 = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout1);
        setPreferredSize(new Dimension(400, 440));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        ImageIcon icon = new ImageIcon("Image\\Wordle Banner.png");
        JLabel banner = new JLabel(icon);
        banner.setAlignmentX(CENTER_ALIGNMENT);

        subtitle = new JLabel("Guess the word before you run out of tries");
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);

        playButton = new JButton("PLAY");
        playButton.setAlignmentX(CENTER_ALIGNMENT);
        playButton.addActionListener(this);
        statsButton = new JButton("STATS");
        statsButton.addActionListener(this);
        statsButton.setAlignmentX(CENTER_ALIGNMENT);
        exitButton = new JButton("EXIT");
        exitButton.addActionListener(this);
        exitButton.setAlignmentX(CENTER_ALIGNMENT);

        JPanel buttonPanel[] = new JPanel[3];
        buttonPanel[0] = new JPanel();
        buttonPanel[0].setOpaque(false);
        buttonPanel[0].setMaximumSize(new Dimension(500, 50));
        buttonPanel[0].add(playButton);
        buttonPanel[1] = new JPanel();
        buttonPanel[1].setOpaque(false);
        buttonPanel[1].setMaximumSize(new Dimension(500, 50));
        buttonPanel[1].add(statsButton);
        buttonPanel[2] = new JPanel();
        buttonPanel[2].setOpaque(false);
        buttonPanel[2].setMaximumSize(new Dimension(500, 50));
        buttonPanel[2].add(exitButton);

        buttonDesign(playButton, "#6aaa64");
        buttonDesign(statsButton, "#c9b458");
        buttonDesign(exitButton, "#787c7e");

        add(Box.createVerticalStrut(110));
        add(banner);
        add(Box.createVerticalStrut(10));
        add(subtitle);
        add(Box.createVerticalStrut(50));
        add(buttonPanel[0]);
        add(Box.createVerticalStrut(16));
        add(buttonPanel[1]);
        add(Box.createVerticalStrut(16));
        add(buttonPanel[2]);

        setcLayout(cLayout);
        setCardPanel(cardPanel);
        setGuessPanel(guessPanel);
        setGameState(gameState);

        if(gameState.isPlaying()) {
            playButton.setText("CONTINUE");
        } else {
            playButton.setText("PLAY");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == playButton) {
            cLayout.show(cardPanel, "Play");
            guessPanel.startPlaying();
            gameState.setPlaying(true);
        } else if(e.getSource() == statsButton) cLayout.show(cardPanel, "Stats");
        else if(e.getSource() == exitButton) System.exit(0);
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

    public void setcLayout(CardLayout cLayout) {
        this.cLayout = cLayout;
    }

    public void setCardPanel(JPanel cardPanel) {
        this.cardPanel = cardPanel;
    }

    public void setGuessPanel(GuessPanel guessPanel) {
        this.guessPanel = guessPanel;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void resetPlayButtonText() {
        playButton.setText("PLAY");
    }
}