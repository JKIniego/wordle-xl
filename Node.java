public class Node {
    Node next;
    String color;
    char letter;
    boolean isChecked;
    int position;

    public Node(char letter, int position) {
        this.letter = letter;
        this.color = "GRAY";
        this.next = null;
        this.isChecked = false;
        this.position = position;
    }

    public Node(String color, char letter, boolean isChecked, int position) {
        this.letter = letter;
        this.color = color;
        this.next = null;
        this.isChecked = isChecked;
        this.position = position;
    }
}
