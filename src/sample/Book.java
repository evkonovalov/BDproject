package sample;

import java.util.Arrays;

public class Book {
    private String bookName;
    private String authorName;
    private String libraryName;
    private String genreName;
    private int quantity;

    public Book(String bookName, String authorName, String libraryName,
                String genreName, int quantity) {
        this.bookName = bookName;
        this.authorName = authorName;
        this.libraryName = libraryName;
        this.genreName = genreName;
        this.quantity = quantity;
    }

    public Book(String inp) {
        String[] args = inp.split("#", 5);
        System.out.println(Arrays.toString(args));
        this.bookName = args[0];
        this.authorName = args[1];
        this.libraryName = args[2];
        this.quantity = Integer.valueOf(args[3]);
        this.genreName = args[4];
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public String getGenreName() {
        return genreName;
    }

    public int getQuantity() {
        return quantity;
    }
}
