package lld.bookmyshow;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShowTime {

    private final String id;
    private final Movie movie;
    private final List<Seat> seats;
    private final List<Reservation> reservations;

    public ShowTime(Movie movie, List<Seat> seats) {
        this.id = UUID.randomUUID().toString();
        this.movie = movie;
        this.seats = new ArrayList<>(seats);
        this.reservations = new ArrayList<>();
    }

    private boolean checkSeatAvailability(List<Seat> requestedSeats) {

        for (Seat seat : requestedSeats) {
            if (seat.isBooked()) {
                return false;
            }
        }

        return true;
    }

    private void bookSeats(List<Seat> seats) {

        for (Seat seat : seats) {
            seat.book();
        }
    }

    private void freeSeats(List<Seat> seats) {

        for (Seat seat : seats) {
            seat.free();
        }
    }

    public Movie getMovie() {
        return movie;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public String getId() {
        return id;
    }

    public Reservation book(
            User user,
            List<Seat> seats) {

        if (!checkSeatAvailability(seats)) {
            System.out.println("Seats are not available");
            return null;
        }

        bookSeats(seats);

        Reservation reservation =
                new Reservation(id, user, seats);

        reservations.add(reservation);

        return reservation;
    }

    public boolean cancel(Reservation reservation) {

        if (!reservations.contains(reservation)) {
            System.out.println("Reservation does not exist");
            return false;
        }

        freeSeats(reservation.getSeats());

        reservations.remove(reservation);

        return true;
    }
}