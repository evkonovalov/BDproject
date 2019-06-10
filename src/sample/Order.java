package sample;

import java.sql.Date;
import java.util.Arrays;

public class Order {
    private String bookName;
    private String clientName;
    private String libraryName;
    private String dateFrom;
    private String dateTo;
    private boolean active;

    public Order(String bookName, String clientName, String libraryName,
                 String dateFrom, String dateTo, boolean active) {
        this.bookName = bookName;
        this.clientName = clientName;
        this.libraryName = libraryName;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.active = active;
    }

    public Order(String inp) {
        String[] args = inp.split("#", 6);
        System.out.println(Arrays.toString(args));
        this.bookName = args[0];
        this.clientName =  args[2];
        this.libraryName = args[1];
        this.dateFrom = args[3];
        this.dateTo = args[4];
        this.active = Boolean.valueOf(args[5]);
    }

    public String getBookName() {
        return bookName;
    }

    public String getClientName() {
        return clientName;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public boolean isActive() {
        return active;
    }

    public String getDateTo() {
        return dateTo;
    }
}
