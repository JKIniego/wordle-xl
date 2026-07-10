import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JLabel;

// GameState saves the last progress made by the user in case the user exits midgame
// Stores all progress in text files
public class GameState {
    private Color GREEN = Color.decode("#6aaa64");
    private Color YELLOW = Color.decode("#c9b458");
    private Color GRAY = Color.decode("#787c7e");
    
    private boolean isPlaying;
    private int remainingTime, wordLength = 0;

    // ---------- SET UP ----------
    public GameState() {
        readIsPlaying();
        readRemainingTime();
    }

    // ---------- FUNCTIONALITIES ----------
    // Checks if the user is still playing
    public void readIsPlaying() {
        File fileReader = new File("Game State\\Is Playing.txt");

        try {
            Scanner scan = new Scanner(fileReader);

            while(scan.hasNextLine()) {
                String stringIsPlaying = scan.nextLine();

                if(stringIsPlaying.equals("true")) isPlaying = true;
                else if(stringIsPlaying.equals("false")) isPlaying = false;
            }
            scan.close();
        } catch (FileNotFoundException e) {}
    }

    // Gets current remaining time
    public void readRemainingTime() {
        File fileReader = new File("Game State\\Remaining Time.txt");

        try {
            Scanner scan = new Scanner(fileReader);

            while(scan.hasNextLine()) {
                remainingTime = Integer.parseInt(scan.nextLine());
            }
            scan.close();
        } catch (FileNotFoundException e) {}
    }

    // Gets current guesses in the grid
    public void initializeGuessLabels(JLabel[][] label, int guessAttempts, int stringLength, int guessCtr) {
        for(int i = 0; i < guessCtr; i++) {
            File fileReader = new File("Game State\\Store Guesses\\" + i + ".txt");

            try {
                Scanner scan = new Scanner(fileReader);

                while(scan.hasNextLine()) {
                    String content = scan.nextLine();
                    String token[] = content.split(";");

                    label[i][Integer.parseInt(token[3])].setText(token[1]);
                    if(token[0].equals("GRAY")) {
                        label[i][Integer.parseInt(token[3])].setBackground(GRAY);
                    } else if(token[0].equals("YELLOW")) {
                        label[i][Integer.parseInt(token[3])].setBackground(YELLOW);
                    } else {
                        label[i][Integer.parseInt(token[3])].setBackground(GREEN);
                    }
                }
                scan.close();
            } catch (FileNotFoundException e) {}
        }
    }

    // Gets the current keyboard status
    public void keyboardUpdate(JLabel[] lettersKeyboard) {
        File fileReader = new File("Game State\\Keyboard Update.txt");

        try {
            Scanner scan = new Scanner(fileReader);

            while(scan.hasNextLine()) {
                String content = scan.nextLine();
                String token[] = content.split(";");

                if(token[0].equals("GREEN")) {
                    lettersKeyboard[Integer.parseInt(token[2])].setBackground(GREEN);
                    lettersKeyboard[Integer.parseInt(token[2])].setForeground(Color.WHITE);
                } else if(token[0].equals("YELLOW")) {
                    lettersKeyboard[Integer.parseInt(token[2])].setBackground(YELLOW);
                    lettersKeyboard[Integer.parseInt(token[2])].setForeground(Color.WHITE);
                } else if(token[0].equals("GRAY")) {
                    lettersKeyboard[Integer.parseInt(token[2])].setBackground(GRAY);
                    lettersKeyboard[Integer.parseInt(token[2])].setForeground(Color.WHITE);
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {}
    }

    // Loads all of the guesses in the ArrayList
    public void loadArrayList(ArrayList<String> storeGuesses) {
        File fileReader = new File("Game State\\String Guesses.txt");

        try {
            Scanner scan = new Scanner(fileReader);

            while(scan.hasNextLine()) {
                String str = scan.nextLine();
                storeGuesses.add(str);
            }
            scan.close();
        } catch (FileNotFoundException e) {}
    }

    // Removes all stored guesses in the ArrayList
    public void resetStoreGuess() {
        try {
            FileWriter fileWriter = new FileWriter("Game State\\String Guesses.txt");
            fileWriter.write("");
            fileWriter.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Returns boolean if the user is playing
    public boolean isPlaying() {
        return isPlaying;
    }

    // Returns the last remaining time
    public int getRemainingTime() {
        return remainingTime;
    }

    // Returns the last length of the random word given
    public int getWordLength() {
        return wordLength;
    }

    // Gets the last number of guesses
    public int getGuessCtr() {
        int guessCtr = 0;

        File fileReader = new File("Game State\\Guess Counter.txt");

        try {
            Scanner scan = new Scanner(fileReader);

            while(scan.hasNextLine()) {
                guessCtr = Integer.parseInt(scan.nextLine());
            }
            scan.close();
        } catch (FileNotFoundException e) {}

        return guessCtr;
    }

    // Gets the last random word
    public Node getRandomWord() {
        Node randomWord = null;

        File fileReader = new File("Game State\\Current Random Word.txt");

        try {
            Scanner scan = new Scanner(fileReader);

            while(scan.hasNextLine()) {
                String content = scan.nextLine();
                String token[] = content.split(";");

                Node node = new Node(token[0], token[1].charAt(0), Boolean.parseBoolean(token[2]), Integer.parseInt(token[3]));

                if(randomWord == null) randomWord = node;
                else {
                    Node current = randomWord;
                    while(current.next != null) {
                        current = current.next;
                    }

                    current.next = node;
                }

                wordLength++;
            }
            scan.close();
        } catch (FileNotFoundException e) {}

        return randomWord;
    }

    // Gets the last guessed word
    public Node getGuessedWord() {
        Node guessedWord = null;

        File fileReader = new File("Game State\\Current Guessed Word.txt");

        try {
            Scanner scan = new Scanner(fileReader);

            while(scan.hasNextLine()) {
                String content = scan.nextLine();
                String token[] = content.split(";");

                Node node = new Node(token[0], token[1].charAt(0), Boolean.parseBoolean(token[2]), Integer.parseInt(token[3]));

                if(guessedWord == null) guessedWord = node;
                else {
                    Node current = guessedWord;
                    while(current.next != null) {
                        current = current.next;
                    }

                    current.next = node;
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {}

        return guessedWord;
    }

    // Sets boolean if the user is playing
    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;

        try {
            FileWriter fileWriter = new FileWriter("Game State\\Is playing.txt");

            if(isPlaying) fileWriter.write("true");
            else fileWriter.write("false");

            fileWriter.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Sets last remaining time
    public void setRemainingTime(int seconds) {
        this.remainingTime = seconds;
        
        try {
            FileWriter fileWriter = new FileWriter("Game State\\Remaining Time.txt");
            fileWriter.write(Integer.toString(seconds));
            fileWriter.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Sets last guessed word
    public void setCurrentGuessedWord(Node guessedWord) {
        Node currentPtr = guessedWord;
        try {
            FileWriter fileWriter = new FileWriter("Game State\\Current Guessed Word.txt");

            while(currentPtr != null) {
                fileWriter.write(currentPtr.color + ";" + Character.toString(currentPtr.letter) + ";" + Boolean.toString(currentPtr.isChecked) + ";" + Integer.toString(currentPtr.position) + "\n");
                currentPtr = currentPtr.next;
            }

            fileWriter.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Sets last random word
    public void setCurrentRandomWord(Node randomWord) {
        Node currentPtr = randomWord;
        try {
            FileWriter fileWriter = new FileWriter("Game State\\Current Random Word.txt");

            while(currentPtr != null) {
                fileWriter.write(currentPtr.color + ";" + Character.toString(currentPtr.letter) + ";" + Boolean.toString(currentPtr.isChecked) + ";" + Integer.toString(currentPtr.position) + "\n");
                currentPtr = currentPtr.next;
            }

            fileWriter.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Sets all stores guesses with placements
    public void storeGuesses(Node guessedWord, int currentGuessCtr) {
        Node currentPtr = guessedWord;
        try {
            FileWriter fileWriter = new FileWriter("Game State\\Store Guesses\\" + currentGuessCtr + ".txt");

            while(currentPtr != null) {
                fileWriter.write(currentPtr.color + ";" + Character.toString(currentPtr.letter) + ";" + Boolean.toString(currentPtr.isChecked) + ";" + Integer.toString(currentPtr.position) + "\n");
                currentPtr = currentPtr.next;
            }

            fileWriter.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Sets keyboard update
    public void storeKeyboard(KeyboardNode keyboardNode) {
        KeyboardNode currentPtr = keyboardNode;
        try {
            FileWriter fileWriter = new FileWriter("Game State\\Keyboard Update.txt");

            while(currentPtr != null) {
                fileWriter.write(currentPtr.color + ";" + Character.toString(currentPtr.letter) + ";" + Integer.toString(currentPtr.position) + "\n");
                currentPtr = currentPtr.next;
            }

            fileWriter.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Sets guess counter
    public void setGuessCtr(int guessCtr) {
        try {
            FileWriter fileWriter = new FileWriter("Game State\\Guess Counter.txt");
            fileWriter.write(Integer.toString(guessCtr));
            fileWriter.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Sets all guessed words
    public void addArraylistGuess(ArrayList<String> storeGuesses, Node guessedWord) {
        try {
            FileWriter fileWriter = new FileWriter("Game State\\String Guesses.txt", true);
            StringBuilder sb = new StringBuilder();
            Node currentPtr = guessedWord;

            while(currentPtr != null) {
                sb.append(currentPtr.letter);
                currentPtr = currentPtr.next;
            }

            fileWriter.write(sb.toString() + "\n");
            fileWriter.close();

            storeGuesses.add(sb.toString());
        } catch (IOException e) { e.printStackTrace(); }
    }
}