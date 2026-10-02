package lld.bookmyshow;

import java.util.ArrayList;
import java.util.List;

public class Theatre {

    private final String name;
    private final List<Seat> seats;
    private final List<ShowTime> showTimes;

    public Theatre(String name) {
        this.name = name;
        this.seats = new ArrayList<>();
        this.showTimes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public List<ShowTime> getShowTimes() {
        return showTimes;
    }

    public boolean addSeat(Seat seat) {

        if (seats.contains(seat)) {
            System.out.println("Seat already exists");
            return false;
        }

        seats.add(seat);
        return true;
    }

    public boolean addShowTime(ShowTime showTime) {

        if (showTimes.contains(showTime)) {
            System.out.println("Show time already added");
            return false;
        }

        showTimes.add(showTime);
        return true;
    }
}