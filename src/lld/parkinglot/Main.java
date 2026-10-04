package lld.parkinglot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

enum VehicleType {
    BIKE,
    CAR,
    TRUCK
}

enum SpotSize {
    SMALL,
    MEDIUM,
    LARGE
}

class ParkingSpot {
    private final String id;
    private boolean isOccupied;
    private final SpotSize size;

    ParkingSpot(SpotSize size) {
        this.id = UUID.randomUUID().toString();
        this.isOccupied = false;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public SpotSize getSize() {
        return size;
    }

    public void occupy() {
        if (isOccupied) {
            throw new IllegalStateException("Parking spot is already occupied");
        }

        isOccupied = true;
    }

    public void release() {
        isOccupied = false;
    }
}

class Floor {
    private final List<ParkingSpot> parkingSpots;

    Floor() {
        parkingSpots = new ArrayList<>();
    }

    public void addParkingSpot(ParkingSpot spot) {
        parkingSpots.add(spot);
    }

    public List<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }
}

class ParkingLot {
    private final List<Floor> floors;

    ParkingLot() {
        floors = new ArrayList<>();
    }

    public void addFloor(Floor floor) {
        floors.add(floor);
    }

    public List<Floor> getFloors() {
        return floors;
    }
}

class ParkingTicket {
    private final String id;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;
    private boolean isValid;

    ParkingTicket(ParkingSpot spot) {
        this.id = UUID.randomUUID().toString();
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
        this.isValid = true;
    }

    public String getId() {
        return id;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public boolean isValid() {
        return isValid;
    }

    public void invalidate() {
        isValid = false;
    }
}

class ParkingService {
    private final ParkingLot parkingLot;
    private final int ratePerHour;
    private final Map<String, ParkingTicket> tickets;

    ParkingService(int ratePerHour) {
        this.parkingLot = new ParkingLot();
        this.ratePerHour = ratePerHour;
        this.tickets = new HashMap<>();
    }

    public void addFloor(Floor floor) {
        parkingLot.addFloor(floor);
    }

    private ParkingSpot getAvailableSpot(VehicleType vehicleType) {

        for (Floor floor : parkingLot.getFloors()) {

            for (ParkingSpot spot : floor.getParkingSpots()) {

                if (!spot.isOccupied() && canFit(vehicleType, spot)) {
                    return spot;
                }
            }
        }

        return null;
    }

    private boolean canFit(VehicleType vehicleType, ParkingSpot spot) {

        if (vehicleType == VehicleType.BIKE) {
            return true;
        }

        if (vehicleType == VehicleType.CAR) {
            return spot.getSize() == SpotSize.MEDIUM
                    || spot.getSize() == SpotSize.LARGE;
        }

        return spot.getSize() == SpotSize.LARGE;
    }

    private ParkingTicket getTicket(String ticketId) {

        ParkingTicket ticket = tickets.get(ticketId);

        if (ticket == null || !ticket.isValid()) {
            return null;
        }

        return ticket;
    }

    private int calculatePrice(LocalDateTime entryTime) {


        Duration duration = Duration.between(
                entryTime,
                LocalDateTime.now()
        );

        long hours = duration.toHours();

        // Charge for any partial hour
        if (duration.toMinutes() % 60 != 0) {
            hours++;
        }

        // Minimum charge = 1 hour
        hours = Math.max(hours, 1);

        return (int) hours * ratePerHour;
    }

    public ParkingTicket entry(VehicleType vehicleType) {

        ParkingSpot spot = getAvailableSpot(vehicleType);

        if (spot == null) {
            System.out.println("No parking spot available");
            return null;
        }

        spot.occupy();

        ParkingTicket ticket = new ParkingTicket(spot);

        tickets.put(ticket.getId(), ticket);

        System.out.println(
                "Vehicle entered. Ticket ID: " + ticket.getId()
        );

        return ticket;
    }

    public boolean exit(String ticketId) {

        ParkingTicket ticket = getTicket(ticketId);

        if (ticket == null) {
            System.out.println("Invalid ticket ID");
            return false;
        }

        int price = calculatePrice(ticket.getEntryTime());

        System.out.println("Pay: ₹" + price);

        ParkingSpot spot = ticket.getSpot();

        spot.release();
        ticket.invalidate();

        tickets.remove(ticketId);

        System.out.println("Vehicle exited successfully");

        return true;
    }
}

public class Main {

    public static void main(String[] args) {

        ParkingService parkingService = new ParkingService(10);

        // Floor 1
        Floor floor1 = new Floor();

        floor1.addParkingSpot(new ParkingSpot(SpotSize.SMALL));
        floor1.addParkingSpot(new ParkingSpot(SpotSize.MEDIUM));
        floor1.addParkingSpot(new ParkingSpot(SpotSize.LARGE));

        parkingService.addFloor(floor1);

        // Vehicle enters
        ParkingTicket bikeTicket =
                parkingService.entry(VehicleType.BIKE);

        ParkingTicket carTicket =
                parkingService.entry(VehicleType.CAR);

        ParkingTicket truckTicket =
                parkingService.entry(VehicleType.TRUCK);

        // Invalid ticket
        parkingService.exit("invalid-ticket");

        // Vehicle exits
        if (bikeTicket != null) {
            parkingService.exit(bikeTicket.getId());
        }

        if (carTicket != null) {
            parkingService.exit(carTicket.getId());
        }

        if (truckTicket != null) {
            parkingService.exit(truckTicket.getId());
        }
    }
}