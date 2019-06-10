package sample;

import java.sql.Date;
import java.util.Arrays;

public class Client {
    private String clientName;
    private String address;
    private String checkInDate;
    private double fine;

    public Client(String clientName, String address, String checkInDate, double fine) {
        this.clientName = clientName;
        this.address = address;
        this.checkInDate = checkInDate;
        this.fine = fine;
    }

    public Client(String inp) {
        String[] args = inp.split("#", 4);
        System.out.println(Arrays.toString(args));
        this.clientName = args[0];
        this.address = args[1];
        this.checkInDate = args[2];
        this.fine = Double.valueOf(args[3]);
    }

    public String getClientName() {
        return clientName;
    }

    public String getAddress() {
        return address;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public double getFine() {
        return fine;
    }
}
