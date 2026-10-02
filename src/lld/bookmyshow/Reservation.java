package lld.bookmyshow;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reservation {

    private final String id;
    private final List<Seat> seats;
    private final String showtimeId;
    private final User user;

    public Reservation(
            String showtimeId,
            User user,
            List<Seat> seats) {

        this.id = UUID.randomUUID().toString();
        this.showtimeId = showtimeId;
        this.user = user;
        this.seats = new ArrayList<>(seats);
    }

    public String getId() {
        return id;
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public User getUser() {
        return user;
    }

    public List<Seat> getSeats() {
        return seats;
    }
}