import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private CardLayout cLayout;
    private JPanel cardPanel;

    private GuessPanel guessPanel;
    private MenuPanel menuPanel;
    private StatsPanel statsPanel;

    public Main() {
        ImageIcon img = new ImageIcon("Image\\Wordle Icon.png");
        setIconImage(img.getImage());

        setTitle("Wordle");
        setBounds(400, 0, 550, 700);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cLayout = new CardLayout();
        cardPanel = new JPanel(cLayout);

        // -----------------------------------------------
        statsPanel = new StatsPanel(cLayout, cardPanel);
        guessPanel = new GuessPanel(cLayout, cardPanel, statsPanel);
        menuPanel = new MenuPanel(cLayout, cardPanel, guessPanel, guessPanel.getGameState());
        guessPanel.setMenuPanel(menuPanel);

        cardPanel.add(menuPanel, "Main Menu");
        cardPanel.add(guessPanel, "Play");
        cardPanel.add(statsPanel, "Stats");

        add(cardPanel);

        setVisible(true);
    }
    public static void main(String[] args) {
        new Main();
    }
}