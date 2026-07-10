public class WordList {
    WordList next;
    String word;
    int key;

    public WordList(String word, int key) {
        this.word = word;
        this.key = key;
        this.next = null;
    }
}