package lld.bookmyshow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingSystem {

    private final Map<String, Theatre> theatres;
    private final Map<String, ShowTime> showTimes;
    private final Map<String, Reservation> reservations;

    public BookingSystem() {
        theatres = new HashMap<>();
        showTimes = new HashMap<>();
        reservations = new HashMap<>();
    }

    public boolean addTheatre(Theatre theatre) {

        if (theatres.containsKey(theatre.getName())) {
            System.out.println("Theatre already added");
            return false;
        }

        theatres.put(theatre.getName(), theatre);
        return true;
    }

    public boolean addShowTime(ShowTime showTime) {

        if (showTimes.containsKey(showTime.getId())) {
            System.out.println("Show time already added");
            return false;
        }

        showTimes.put(showTime.getId(), showTime);
        return true;
    }

    private ShowTime getShowTime(String showTimeId) {

        if (!showTimes.containsKey(showTimeId)) {
            System.out.println("Show does not exist");
            return null;
        }

        return showTimes.get(showTimeId);
    }

    public Reservation bookTickets(
            String showTimeId,
            User user,
            List<Seat> seats) {

        ShowTime showTime = getShowTime(showTimeId);

        if (showTime == null) {
            return null;
        }

        Reservation reservation =
                showTime.book(user, seats);

        if (reservation == null) {
            System.out.println("Booking failed");
            return null;
        }

        reservations.put(
                reservation.getId(),
                reservation
        );

        System.out.println("Booking succeeded");

        return reservation;
    }

    public boolean cancelReservation(String reservationId) {

        Reservation reservation =
                reservations.get(reservationId);

        if (reservation == null) {
            System.out.println("Reservation does not exist");
            return false;
        }

        ShowTime showTime =
                getShowTime(reservation.getShowtimeId());

        if (showTime == null) {
            return false;
        }

        boolean cancelled =
                showTime.cancel(reservation);

        if (!cancelled) {
            return false;
        }

        reservations.remove(reservationId);

        return true;
    }
}