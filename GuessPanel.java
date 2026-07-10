import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GuessPanel extends JPanel implements ActionListener {
    private Color BACKGROUND = Color.decode("#ffffff");
    private Color TEXT_DARK = Color.decode("#1a1a1b");
    private Color GREEN = Color.decode("#6aaa64");
    private Color YELLOW = Color.decode("#c9b458");
    private Color GRAY = Color.decode("#787c7e");
    private Color KEY_DEFAULT = Color.decode("#d3d6da");

    private JTextField guessInput;
    private JButton submitButton, hintButton, giveUpButton, mainMenuButton;
    private JPanel guessOutput, upperKeyboard, middleKeyboard, lowerKeyboard;
    private JLabel[][] label;
    private JLabel[] lettersKeyboard;
    private JLabel timerLabel;
    private GridLayout gridLayout, upperKeyboardGrid, middleKeyboardGrid, lowerKeyboardGrid;
    private Node randomWord = null, guessedWord;
    private KeyboardNode keyboardNode = null;

    private CardLayout cLayout;
    private JPanel cardPanel;
    private GameState gameState;
    private MenuPanel menuPanel;
    private StatsPanel statsPanel;

    private int stringLength, guessAttempts, guessCtr = 0, labelPointer, rightLetters, wordListArraySize = 100, totalWords = 0, noOfLetters;
    private WordList wordList[];
    private ArrayList<ArrayList<Integer>> duplicates = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> listOfDuplicates = new ArrayList<>();
    private ArrayList<String> storeGuesses = new ArrayList<>();
    private Timer timer;

    public GuessPanel(CardLayout cLayout, JPanel cardPanel, StatsPanel statsPanel) {
        stringLength = 5;
        guessAttempts = stringLength + 1;
        guessPanelDesign();

        wordList = new WordList[wordListArraySize];
        int sizeArrangement[] = {10, 5, 9, 6, 8, 7};

        for(int i = 0; i < 6; i++) {
            File file = new File("Word List\\" + sizeArrangement[i] + "-letter word.txt");

            try {
                Scanner scan = new Scanner(file);

                while(scan.hasNextLine()) {
                    String word = scan.nextLine();
                    addToWordList(new WordList(word, totalWords++));
                }
                scan.close();
            } catch (FileNotFoundException e) {}
        }

        char letters[] = {'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M'};
        for(int i = 0; i < letters.length; i++) {
            KeyboardNode node = new KeyboardNode(letters[i], i);

            if(keyboardNode == null) keyboardNode = node;
            else {
                KeyboardNode current = keyboardNode;
                while(current.next != null) {
                    current = current.next;
                }

                current.next = node;
            }
        }

        gameState = new GameState();
        setcLayout(cLayout);
        setCardPanel(cardPanel);
        setStatsPanel(statsPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == submitButton) submitGuess();
        else if(e.getSource() == mainMenuButton) {
            cLayout.show(cardPanel, "Main Menu");
            menuPanel.resetPlayButtonText();
        }
        else if(e.getSource() == hintButton) hint();
        else if(e.getSource() == giveUpButton) {
            statsPanel.updateStats(true, false, false);
            gameState.resetStoreGuess();
            requestPuzzleSolution();
        }
    }

    public void submitGuess() {
        int position = 0;
        int noOfLettersGuessedWord = 0;
        String word = guessInput.getText();

        for(int i = 0; i < word.length(); i++) {
            if(Character.isAlphabetic(word.charAt(i))) {
                noOfLettersGuessedWord++;
            }
        }

        if(noOfLettersGuessedWord == noOfLetters) {
            rightLetters = 0;
            guessedWord = null;

            for(int i = 0; i < word.length(); i++) {
                if(Character.isAlphabetic(word.charAt(i))) {
                    Node node = new Node(Character.toUpperCase(word.charAt(i)), position++);

                    if(guessedWord == null) guessedWord = node;
                    else {
                        Node current = guessedWord;
                        while(current.next != null) {
                            current = current.next;
                        }

                        current.next = node;
                    }
                }
            }

            labelPointer = 0;
            wordleChecker();
            gameState.storeGuesses(guessedWord, guessCtr);
            guessCtr++;
            gameState.setGuessCtr(guessCtr);
            gameState.addArraylistGuess(storeGuesses, guessedWord);

            if(guessCtr >= guessAttempts - 2) giveUpButton.setEnabled(false);

            Node currentWordPointer = randomWord;
            while(currentWordPointer != null) {
                currentWordPointer.isChecked = false;
                currentWordPointer = currentWordPointer.next;
            }

            Node currentGuessPointer = guessedWord;
            while(currentGuessPointer != null) {
                currentGuessPointer.isChecked = false;
                currentGuessPointer = currentGuessPointer.next;
            }

            guessInput.setText("");
            gameState.setCurrentGuessedWord(guessedWord);
            gameState.setCurrentRandomWord(randomWord);

            if(guessCtr == guessAttempts) {
                submitButton.setEnabled(false);
                mainMenuButton.setEnabled(true);
                giveUpButton.setEnabled(false);
                hintButton.setEnabled(false);
                timer.cancel();

                if(rightLetters == stringLength) {
                    JOptionPane.showMessageDialog(null, "Congratulations! The word is " + correctWord(), "CONGRATS!", JOptionPane.INFORMATION_MESSAGE);
                    statsPanel.updateStats(false, true, false);
                } else {
                    JOptionPane.showMessageDialog(null, "The word is " + correctWord(), "OUT OF ATTEMPTS", JOptionPane.INFORMATION_MESSAGE);
                    statsPanel.updateStats(false, false, true);
                }

                gameState.setPlaying(false);
                gameState.resetStoreGuess();
                menuPanel.resetPlayButtonText();
            } else if(rightLetters == stringLength) {
                submitButton.setEnabled(false);
                mainMenuButton.setEnabled(true);
                giveUpButton.setEnabled(false);
                hintButton.setEnabled(false);
                timer.cancel();

                JOptionPane.showMessageDialog(null, "Congratulations! The word is " + correctWord(), "CONGRATS!", JOptionPane.INFORMATION_MESSAGE);
                statsPanel.updateStats(false, true, false);
                gameState.setPlaying(false);
                gameState.resetStoreGuess();
                menuPanel.resetPlayButtonText();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Guessed word must contain EXACTLY " + noOfLetters + " letters!", "WARNING", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void wordleChecker() {
        Node currentGuessPointer = guessedWord;
        while(currentGuessPointer != null) {
            Node currentWordPointer = randomWord;

            while(currentWordPointer != null) {
                if(currentGuessPointer.letter == currentWordPointer.letter
                && currentGuessPointer.position == currentWordPointer.position) {
                    currentGuessPointer.color = "GREEN";
                    keyboardUpdate(currentGuessPointer.letter, currentGuessPointer.color);
                    currentWordPointer.isChecked = true;
                    currentGuessPointer.isChecked = true;
                    rightLetters++;
                    break;
                }

                currentWordPointer = currentWordPointer.next;
            }

            label[guessCtr][labelPointer].setText(Character.toString(currentGuessPointer.letter));
            if(currentGuessPointer.color.equals("GRAY")) {
                label[guessCtr][labelPointer].setBackground(GRAY);
            } else if(currentGuessPointer.color.equals("YELLOW")) {
                label[guessCtr][labelPointer].setBackground(YELLOW);
            } else {
                label[guessCtr][labelPointer].setBackground(GREEN);
            }

            labelPointer++;
            currentGuessPointer = currentGuessPointer.next;
        }

        currentGuessPointer = guessedWord;
        labelPointer = 0;
        while(currentGuessPointer != null) {
            Node currentWordPointer = randomWord;

            if(!currentGuessPointer.isChecked) {
                while(currentWordPointer != null) {
                    if(currentGuessPointer.letter == currentWordPointer.letter
                    && currentGuessPointer.position != currentWordPointer.position && !currentWordPointer.isChecked) {
                        currentGuessPointer.color = "YELLOW";
                        keyboardUpdate(currentGuessPointer.letter, currentGuessPointer.color);
                        currentWordPointer.isChecked = true;
                        break;
                    }

                    currentWordPointer = currentWordPointer.next;
                }

                label[guessCtr][labelPointer].setText(Character.toString(currentGuessPointer.letter));
                if(currentGuessPointer.color.equals("GRAY")) {
                    label[guessCtr][labelPointer].setBackground(GRAY);
                } else if(currentGuessPointer.color.equals("YELLOW")) {
                    label[guessCtr][labelPointer].setBackground(YELLOW);
                } else {
                    label[guessCtr][labelPointer].setBackground(GREEN);
                }
            }

            labelPointer++;
            keyboardUpdate(currentGuessPointer.letter, currentGuessPointer.color);
            currentGuessPointer = currentGuessPointer.next;
        }
    }

    public String correctWord() {
        StringBuilder word = new StringBuilder("");
        Node currentPtr = randomWord;

        while(currentPtr != null) {
            word.append(currentPtr.letter);
            currentPtr = currentPtr.next;
        }

        return word.toString();
    }

    public void startPlaying() {
        submitButton.setEnabled(true);
        mainMenuButton.setEnabled(false);
        giveUpButton.setEnabled(true);
        hintButton.setEnabled(true);

        if(gameState.isPlaying()) {
            guessCtr = gameState.getGuessCtr();

            guessedWord = null;
            guessedWord = gameState.getGuessedWord();
            randomWord = gameState.getRandomWord();

            stringLength = gameState.getWordLength();
            guessAttempts = stringLength + 1;

            noOfLetters = stringLength;
            duplicates.clear();
            storeGuesses.clear();
            letterFrequency();
            gameState.loadArrayList(storeGuesses);

            gridLayout.setRows(guessAttempts);
            gridLayout.setColumns(stringLength);
            guessOutput.removeAll();
            initializeLabels();
            gameState.initializeGuessLabels(label, guessAttempts, stringLength, guessCtr);

            gameState.keyboardUpdate(lettersKeyboard);

            startTimer(gameState.getRemainingTime());
        } else {
            guessCtr = 0;
            randomWord = null;

            gameState.setGuessCtr(guessCtr);

            KeyboardNode currentPtr = keyboardNode;
            while(currentPtr != null) {
                currentPtr.color = "NONE";
                lettersKeyboard[currentPtr.position].setBackground(KEY_DEFAULT);
                lettersKeyboard[currentPtr.position].setForeground(TEXT_DARK);
                currentPtr = currentPtr.next;
            }

            gameState.storeKeyboard(keyboardNode);

            String word = getWord((int)(Math.random() * totalWords)).word;
            int i;

            for(i = 0; i < word.length(); i++) {
                Node node = new Node(Character.toUpperCase(word.charAt(i)), i);

                if(randomWord == null) randomWord = node;
                else {
                    Node current = randomWord;
                    while(current.next != null) {
                        current = current.next;
                    }

                    current.next = node;
                }
            }

            gameState.setCurrentRandomWord(randomWord);
            noOfLetters = i;
            duplicates.clear();
            storeGuesses.clear();
            letterFrequency();

            stringLength = word.length();
            guessAttempts = stringLength + 1;

            gridLayout.setRows(guessAttempts);
            gridLayout.setColumns(stringLength);
            initializeLabels();

            int seconds[] = {120, 150, 180, 210, 240, 360};
            startTimer(seconds[noOfLetters - 5]);
        }
    }

    public void keyboardUpdate(char letter, String color) {
        KeyboardNode currentPtr = keyboardNode;
        while(currentPtr != null) {
            if(letter == currentPtr.letter) {
                if(color.equals("GREEN") && (currentPtr.color.equals("NONE") || currentPtr.color.equals("YELLOW"))) {
                    lettersKeyboard[currentPtr.position].setBackground(GREEN);
                    lettersKeyboard[currentPtr.position].setForeground(Color.WHITE);
                    currentPtr.color = "GREEN";
                } else if(color.equals("YELLOW") && currentPtr.color.equals("NONE")) {
                    lettersKeyboard[currentPtr.position].setBackground(YELLOW);
                    lettersKeyboard[currentPtr.position].setForeground(Color.WHITE);
                    currentPtr.color = "YELLOW";
                } else if(color.equals("GRAY") && currentPtr.color.equals("NONE")) {
                    lettersKeyboard[currentPtr.position].setBackground(GRAY);
                    lettersKeyboard[currentPtr.position].setForeground(Color.WHITE);
                    currentPtr.color = "GRAY";
                }

                break;
            }

            currentPtr = currentPtr.next;
        }

        gameState.storeKeyboard(keyboardNode);
    }

    public void hint() {
        for(ArrayList<Integer> ch : duplicates) {
            if(ch.get(1) > 1) {
                listOfDuplicates.add(ch);
            }
        }

        if(listOfDuplicates.isEmpty()) {
            JOptionPane.showMessageDialog(null, "The word doesn't have any duplicates. But, I heard it starts with letter " + randomWord.letter + "...", "HINT", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder d = new StringBuilder("");

            for(ArrayList<Integer> ch : listOfDuplicates) {
                int ascii = ch.get(0);
                d.append("\n    " + (char) ascii + " appearing " + ch.get(1) + " times");
            }

            JOptionPane.showMessageDialog(null, "Duplicate letters:\n" + d.toString(), "HINT", JOptionPane.INFORMATION_MESSAGE);
        }

        listOfDuplicates.clear();
    }

    public void requestPuzzleSolution() {
        submitButton.setEnabled(false);
        giveUpButton.setEnabled(false);
        hintButton.setEnabled(false);
        timer.cancel();
        menuPanel.resetPlayButtonText();

        Timer timerSolution = new Timer();
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                if(guessCtr == 0) {
                    Node currentPtr = randomWord;

                    while(currentPtr != null) {
                        KeyboardNode currentLetterPtr = keyboardNode;

                        while(currentLetterPtr != null) {
                            label[guessCtr][currentPtr.position].setText(Character.toString(currentLetterPtr.letter));
                            try { Thread.sleep(50); } catch (InterruptedException e) {}

                            if(currentPtr.letter == currentLetterPtr.letter) {
                                label[guessCtr][currentPtr.position].setText(Character.toString(currentLetterPtr.letter));
                                label[guessCtr][currentPtr.position].setBackground(GREEN);
                                try { Thread.sleep(50); } catch (InterruptedException e) {}
                                break;
                            }

                            currentLetterPtr = currentLetterPtr.next;
                        }

                        currentPtr = currentPtr.next;
                    }
                } else {
                    Node currentGuessPointer = guessedWord;
                    Node currentWordPointer;

                    while(currentGuessPointer != null) {
                        if(currentGuessPointer.color.equals("GREEN")) {
                            label[guessCtr][currentGuessPointer.position].setText(Character.toString(currentGuessPointer.letter));
                            label[guessCtr][currentGuessPointer.position].setBackground(GREEN);
                            try { Thread.sleep(150); } catch (InterruptedException e) {}
                        }
                        labelPointer++;
                        currentGuessPointer = currentGuessPointer.next;
                    }

                    currentGuessPointer = guessedWord;
                    while(currentGuessPointer != null) {
                        if(currentGuessPointer.color.equals("YELLOW")) {
                            currentWordPointer = randomWord;
                            while(currentWordPointer != null) {
                                if(label[guessCtr][currentWordPointer.position].getText().isEmpty()
                                && (currentGuessPointer.letter == currentWordPointer.letter)) {
                                    label[guessCtr][currentWordPointer.position].setText(Character.toString(currentGuessPointer.letter));
                                    label[guessCtr][currentWordPointer.position].setBackground(GREEN);
                                    try { Thread.sleep(150); } catch (InterruptedException e) {}
                                    break;
                                }
                                currentWordPointer = currentWordPointer.next;
                            }
                        }
                        currentGuessPointer = currentGuessPointer.next;
                    }

                    currentWordPointer = randomWord;
                    currentGuessPointer = guessedWord;
                    while(currentGuessPointer != null) {
                        if(label[guessCtr][currentGuessPointer.position].getText().isEmpty()) {
                            KeyboardNode currentLetterPtr = keyboardNode;

                            while(currentLetterPtr != null) {
                                if(currentLetterPtr.color.equals("YELLOW") || currentLetterPtr.color.equals("NONE")) {
                                    label[guessCtr][currentGuessPointer.position].setText(Character.toString(currentLetterPtr.letter));
                                    try { Thread.sleep(50); } catch (InterruptedException e) {}

                                    if(currentWordPointer.letter == currentLetterPtr.letter) {
                                        label[guessCtr][currentGuessPointer.position].setText(Character.toString(currentLetterPtr.letter));
                                        label[guessCtr][currentGuessPointer.position].setBackground(GREEN);
                                        try { Thread.sleep(50); } catch (InterruptedException e) {}
                                        break;
                                    }
                                }

                                currentLetterPtr = currentLetterPtr.next;
                            }
                        }

                        currentWordPointer = currentWordPointer.next;
                        currentGuessPointer = currentGuessPointer.next;
                    }
                }

                timerSolution.cancel();
                JOptionPane.showMessageDialog(null, "The word is " + correctWord(), "GIVE UP", JOptionPane.INFORMATION_MESSAGE);
                gameState.setPlaying(false);
                mainMenuButton.setEnabled(true);
            }
        };

        timerSolution.scheduleAtFixedRate(task1, 0, 50);
    }

    public void startTimer(int S) {
        timer = new Timer();

        TimerTask task = new TimerTask() {
            int startSeconds = S;

            @Override
            public void run() {
                if(startSeconds > 0) {
                    gameState.setRemainingTime(startSeconds);
                    int minutes = startSeconds / 60;
                    int seconds = startSeconds % 60;

                    if(seconds < 10) timerLabel.setText(minutes + ":0" + seconds);
                    else timerLabel.setText(minutes + ":" + seconds);

                    startSeconds--;
                } else {
                    timer.cancel();
                    giveUpButton.setEnabled(false);
                    submitButton.setEnabled(false);
                    mainMenuButton.setEnabled(true);
                    hintButton.setEnabled(false);
                    timerLabel.setText("0:00");
                    JOptionPane.showMessageDialog(null, "The word is\n" + correctWord(), "TIME'S UP!", JOptionPane.INFORMATION_MESSAGE);
                    statsPanel.updateStats(false, false, true);
                    gameState.setPlaying(false);
                    gameState.resetStoreGuess();
                    menuPanel.resetPlayButtonText();
                }
            }

        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void guessPanelDesign() {
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel gap1 = new JPanel();
        gap1.setOpaque(false);
        gap1.setMaximumSize(new Dimension(400, 14));
        JPanel gap2 = new JPanel();
        gap2.setOpaque(false);
        gap2.setMaximumSize(new Dimension(400, 14));
        JPanel gap3 = new JPanel();
        gap3.setOpaque(false);
        gap3.setMaximumSize(new Dimension(400, 14));

        BoxLayout boxLayout1 = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout1);

        timerLabel = new JLabel("0:00");
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        timerLabel.setForeground(TEXT_DARK);

        JPanel navigationPanel = new JPanel();
        navigationPanel.setOpaque(false);
        navigationPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        navigationPanel.setMaximumSize(new Dimension(550, 40));
        hintButton = new JButton("HINT");
        hintButton.addActionListener(this);
        giveUpButton = new JButton("GIVE UP");
        giveUpButton.addActionListener(this);
        mainMenuButton = new JButton("MAIN MENU");
        mainMenuButton.addActionListener(this);

        buttonDesign(hintButton, "#787c7e");
        buttonDesign(giveUpButton, "#787c7e");
        buttonDesign(mainMenuButton, "#787c7e");

        navigationPanel.add(timerLabel);
        navigationPanel.add(hintButton);
        navigationPanel.add(giveUpButton);
        navigationPanel.add(mainMenuButton);

        guessOutput = new JPanel();
        guessOutput.setOpaque(false);
        gridLayout = new GridLayout(guessAttempts, stringLength, 5, 5);
        guessOutput.setLayout(gridLayout);
        guessOutput.setMaximumSize(new Dimension(400, 440));

        initializeLabels();

        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        BoxLayout boxLayout2 = new BoxLayout(inputPanel, BoxLayout.Y_AXIS);
        inputPanel.setLayout(boxLayout2);
        inputPanel.setMaximumSize(new Dimension(220, 90));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        guessInput = new JTextField(50);
        guessInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        guessInput.setFont(new Font("Segoe UI", Font.BOLD, 16));
        guessInput.setHorizontalAlignment(JTextField.CENTER);
        guessInput.setMaximumSize(new Dimension(220, 36));
        guessInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(KEY_DEFAULT, 2),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        submitButton = new JButton("SUBMIT");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(this);
        buttonDesign(submitButton, "#6aaa64");

        inputPanel.add(guessInput);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(submitButton);

        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setOpaque(false);
        BoxLayout boxLayout3 = new BoxLayout(keyboardPanel, BoxLayout.Y_AXIS);
        keyboardPanel.setLayout(boxLayout3);
        keyboardPanel.setMaximumSize(new Dimension(340, 110));
        keyboardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        initializeKeyboard(keyboardPanel);

        add(navigationPanel);
        add(gap1);
        add(guessOutput);
        add(gap2);
        add(inputPanel);
        add(gap3);
        add(keyboardPanel);
    }

    public void buttonDesign(JButton button, String color) {
        Color base = Color.decode(color);
        Color hover = base.darker();

        button.setBackground(base);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if(button.isEnabled()) button.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(button.isEnabled()) button.setBackground(base);
            }
        });
    }

    public void initializeLabels() {
        guessOutput.removeAll();
        label = new JLabel[guessAttempts][stringLength];

        for(int i = 0; i < guessAttempts; i++) {
            for(int j = 0; j < stringLength; j++) {
                label[i][j] = new JLabel();
                label[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                label[i][j].setVerticalAlignment(SwingConstants.CENTER);
                label[i][j].setForeground(Color.WHITE);
                label[i][j].setFont(new Font("Segoe UI", Font.BOLD, 26 - stringLength));
                label[i][j].setOpaque(true);
                label[i][j].setBackground(Color.decode("#f7f7f8"));
                label[i][j].setForeground(TEXT_DARK);
                label[i][j].setBorder(BorderFactory.createLineBorder(KEY_DEFAULT, 2));
                guessOutput.add(label[i][j]);
            }
        }

        revalidate();
        repaint();
    }

    public void initializeKeyboard(JPanel keyboardPanel) {
        int letterCount = 0;
        lettersKeyboard = new JLabel[26];
        char letters[] = {'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M'};

        for(int i = 0; i < 26; i++) {
            lettersKeyboard[i] = new JLabel(Character.toString(letters[i]));
            lettersKeyboard[i].setHorizontalAlignment(SwingConstants.CENTER);
            lettersKeyboard[i].setVerticalAlignment(SwingConstants.CENTER);
            lettersKeyboard[i].setForeground(TEXT_DARK);
            lettersKeyboard[i].setFont(new Font("Segoe UI", Font.BOLD, 15));
            lettersKeyboard[i].setOpaque(true);
            lettersKeyboard[i].setBackground(KEY_DEFAULT);
        }

        JPanel gap1 = new JPanel();
        gap1.setOpaque(false);
        gap1.setMaximumSize(new Dimension(400, 4));
        JPanel gap2 = new JPanel();
        gap2.setOpaque(false);
        gap2.setMaximumSize(new Dimension(400, 4));

        upperKeyboard = new JPanel();
        upperKeyboard.setOpaque(false);
        upperKeyboardGrid = new GridLayout(1, 10, 3, 3);
        upperKeyboard.setLayout(upperKeyboardGrid);
        upperKeyboard.setMaximumSize(new Dimension(340, 34));

        while(letterCount < 10) {
            upperKeyboard.add(lettersKeyboard[letterCount]);
            letterCount++;
        }

        middleKeyboard = new JPanel();
        middleKeyboard.setOpaque(false);
        middleKeyboardGrid = new GridLayout(1, 9, 3, 3);
        middleKeyboard.setLayout(middleKeyboardGrid);
        middleKeyboard.setMaximumSize(new Dimension(306, 34));

        while(letterCount < 19) {
            middleKeyboard.add(lettersKeyboard[letterCount]);
            letterCount++;
        }

        lowerKeyboard = new JPanel();
        lowerKeyboard.setOpaque(false);
        lowerKeyboardGrid = new GridLayout(1, 7, 3, 3);
        lowerKeyboard.setLayout(lowerKeyboardGrid);
        lowerKeyboard.setMaximumSize(new Dimension(238, 34));

        while(letterCount < 26) {
            lowerKeyboard.add(lettersKeyboard[letterCount]);
            letterCount++;
        }

        upperKeyboard.setAlignmentX(Component.CENTER_ALIGNMENT);
        middleKeyboard.setAlignmentX(Component.CENTER_ALIGNMENT);
        lowerKeyboard.setAlignmentX(Component.CENTER_ALIGNMENT);

        keyboardPanel.add(upperKeyboard);
        keyboardPanel.add(gap1);
        keyboardPanel.add(middleKeyboard);
        keyboardPanel.add(gap2);
        keyboardPanel.add(lowerKeyboard);
    }

    public void addToWordList(WordList W) {
        int index = W.key % wordListArraySize;

        if(wordList[index] != null) {
            WordList currentPtr = wordList[index];

            while(currentPtr.next != null) {
                currentPtr = currentPtr.next;
            }

            currentPtr.next = W;
        } else {
            wordList[index] = W;
        }
    }

    public WordList getWord(int key) {
        int index = key % wordListArraySize;

        if(wordList[index] != null) {
            WordList currentPtr = wordList[index];

            while(currentPtr != null) {
                if(currentPtr.key == key) {
                    return currentPtr;
                } else {
                    currentPtr = currentPtr.next;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public void letterFrequency() {
        Node currentPtr = randomWord;

        while(currentPtr != null) {
            boolean isFound = false;

            for(ArrayList<Integer> ch : duplicates) {
                if(ch.get(0) == (int) currentPtr.letter) {
                    ch.set(1, ch.get(1) + 1);
                    isFound = true;
                    break;
                }
            }

            if(!isFound) {
                ArrayList<Integer> newLetter = new ArrayList<>();
                newLetter.add((int) currentPtr.letter);
                newLetter.add(1);
                duplicates.add(newLetter);
            }

            currentPtr = currentPtr.next;
        }
    }

    public void setcLayout(CardLayout cLayout) {
        this.cLayout = cLayout;
    }

    public void setCardPanel(JPanel cardPanel) {
        this.cardPanel = cardPanel;
    }

    public GameState getGameState() {
        return gameState;
    }

    public MenuPanel getMenuPanel() {
        return menuPanel;
    }

    public void setMenuPanel(MenuPanel menuPanel) {
        this.menuPanel = menuPanel;
    }

    public StatsPanel getStatsPanel() {
        return statsPanel;
    }

    public void setStatsPanel(StatsPanel statsPanel) {
        this.statsPanel = statsPanel;
    }
}