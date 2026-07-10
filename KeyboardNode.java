public class KeyboardNode {
    KeyboardNode next;
    String color;
    char letter;
    int position;

    public KeyboardNode(char letter, int position) {
        this.letter = letter;
        this.color = "NONE";
        this.next = null;
        this.position = position;
    }
}