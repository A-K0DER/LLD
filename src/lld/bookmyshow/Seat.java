package lld.bookmyshow;

import java.util.UUID;

public class Seat {

    private final String id;
    private boolean isBooked;

    public Seat() {
        this.id = UUID.randomUUID().toString();
        this.isBooked = false;
    }

    public String getId() {
        return id;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void book() {
        isBooked = true;
    }

    public void free() {
        isBooked = false;
    }
}