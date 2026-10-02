package lld.bookmyshow;

import javax.sound.midi.Soundbank;
import java.util.* ;

/*
* Entities:
*   - Movie
*   - Theatre
*   - Show
*   - Seats
*   - Screens
*   - Reservation
*   - BookingSystem
*
*
*  */

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // =========================
        // SETUP
        // =========================

        BookingSystem bookingSystem = new BookingSystem();

        Theatre theatre = new Theatre("PVR");

        Movie movie = new Movie("Avengers");

        Seat seatA1 = new Seat();
        Seat seatA2 = new Seat();
        Seat seatA3 = new Seat();

        List<Seat> seats = Arrays.asList(
                seatA1,
                seatA2,
                seatA3
        );

        // ShowTime gets its seats
        ShowTime showTime = new ShowTime(movie, seats);

        theatre.addShowTime(showTime);
        bookingSystem.addTheatre(theatre);
        bookingSystem.addShowTime(showTime);

        User user1 = new User("Ayush");
        User user2 = new User("Rahul");


        // =========================
        // TEST 1
        // Book available seat
        // =========================

        System.out.println("\nTEST 1: Book A1");

        Reservation reservation1 =
                bookingSystem.bookTickets(
                        showTime.getId(),
                        user1,
                        List.of(seatA1)
                );

        System.out.println(
                reservation1 != null
                        ? "PASS"
                        : "FAIL"
        );


        // =========================
        // TEST 2
        // Book another available seat
        // =========================

        System.out.println("\nTEST 2: Book A2");

        Reservation reservation2 =
                bookingSystem.bookTickets(
                        showTime.getId(),
                        user2,
                        List.of(seatA2)
                );

        System.out.println(
                reservation2 != null
                        ? "PASS"
                        : "FAIL"
        );


        // =========================
        // TEST 3
        // Book already booked seat
        // =========================

        System.out.println("\nTEST 3: Try booking A1 again");

        Reservation reservation3 =
                bookingSystem.bookTickets(
                        showTime.getId(),
                        user2,
                        List.of(seatA1)
                );

        System.out.println(
                reservation3 == null
                        ? "PASS"
                        : "FAIL"
        );


        // =========================
        // TEST 4
        // Book multiple seats
        // =========================

        System.out.println("\nTEST 4: Book A2 + A3");

        Reservation reservation4 =
                bookingSystem.bookTickets(
                        showTime.getId(),
                        user2,
                        List.of(seatA2, seatA3)
                );

        System.out.println(
                reservation4 == null
                        ? "PASS (booking rejected because A2 is booked)"
                        : "FAIL"
        );


        // =========================
        // TEST 5
        // Cancel reservation
        // =========================

        System.out.println("\nTEST 5: Cancel reservation1");

        boolean cancelled =
                bookingSystem.cancelReservation(
                        reservation1.getId()
                );

        System.out.println(
                cancelled
                        ? "PASS"
                        : "FAIL"
        );


        // =========================
        // TEST 6
        // Book freed seat
        // =========================

        System.out.println("\nTEST 6: Book A1 after cancellation");

        Reservation reservation5 =
                bookingSystem.bookTickets(
                        showTime.getId(),
                        user2,
                        List.of(seatA1)
                );

        System.out.println(
                reservation5 != null
                        ? "PASS"
                        : "FAIL"
        );


        // =========================
        // TEST 7
        // Cancel same reservation twice
        // =========================

        System.out.println("\nTEST 7: Cancel same reservation twice");

        boolean firstCancel =
                bookingSystem.cancelReservation(
                        reservation5.getId()
                );

        boolean secondCancel =
                bookingSystem.cancelReservation(
                        reservation5.getId()
                );

        System.out.println(
                firstCancel && !secondCancel
                        ? "PASS"
                        : "FAIL"
        );


        // =========================
        // TEST 8
        // Invalid showtime
        // =========================

        System.out.println("\nTEST 8: Invalid showtime");

        Reservation invalidReservation =
                bookingSystem.bookTickets(
                        "invalid-showtime-id",
                        user1,
                        List.of(seatA1)
                );

        System.out.println(
                invalidReservation == null
                        ? "PASS"
                        : "FAIL"
        );


        // =========================
        // TEST 9
        // Book multiple available seats
        // =========================

        System.out.println("\nTEST 9: Book A1 + A3");

        Reservation reservation6 =
                bookingSystem.bookTickets(
                        showTime.getId(),
                        user1,
                        List.of(seatA1, seatA3)
                );

        System.out.println(
                reservation6 != null
                        ? "PASS"
                        : "FAIL"
        );
    }
}
