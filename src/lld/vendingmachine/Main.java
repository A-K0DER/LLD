package lld.vendingmachine;

import java.util.*;

enum TransactionStatus {
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

enum MachineState {
    IDLE,
    PAYMENT_PENDING,
    DISPENSING,
    OUT_OF_SERVICE
}

interface PaymentMethod {
    boolean pay(int amount);
}

class CashPayment implements PaymentMethod {

    @Override
    public boolean pay(int amount) {
        System.out.println("Paid ₹" + amount + " using cash");
        return true;
    }
}

class CardPayment implements PaymentMethod {

    @Override
    public boolean pay(int amount) {
        System.out.println("Paid ₹" + amount + " using card");
        return true;
    }
}

class UPIPayment implements PaymentMethod {

    @Override
    public boolean pay(int amount) {
        System.out.println("Paid ₹" + amount + " using UPI");
        return true;
    }
}

class Product {

    String name;
    int price;

    Product(String name, int price) {
        this.name = name;
        this.price = price;
    }
}

class Slot {

    String id;
    Product item;
    int quantity;

    Slot(Product item, int quantity) {
        this.id = UUID.randomUUID().toString();
        this.item = item;
        this.quantity = quantity;
    }

    public boolean isEmpty() {
        return quantity == 0;
    }

    public boolean hasQuantity(int requestedQuantity) {
        return quantity >= requestedQuantity;
    }

    public void addProduct(Product item, int quantity) {
        this.item = item;
        this.quantity += quantity;
    }

    public void reduceQuantity(int quantity) {
        this.quantity -= quantity;
    }
}

class Transaction {

    String id;
    Product item;
    int quantity;
    int total;
    TransactionStatus status;

    Transaction(Product item, int quantity) {
        this.id = UUID.randomUUID().toString();
        this.item = item;
        this.quantity = quantity;
        this.status = TransactionStatus.IN_PROGRESS;
    }

    public int getTotalCost() {
        this.total = this.quantity * this.item.price;
        return this.total;
    }

    public void updateStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public TransactionStatus getStatus() {
        return status;
    }
}

class VendingMachine {

    private final Map<String, Slot> slots;
    private final Map<String, Transaction> transactions;

    private MachineState state;

    VendingMachine() {
        slots = new HashMap<>();
        transactions = new HashMap<>();
        state = MachineState.IDLE;
    }

    public boolean addSlot(Slot slot) {

        if (state == MachineState.OUT_OF_SERVICE) {
            System.out.println("Machine is out of service");
            return false;
        }

        if (slots.containsKey(slot.id)) {
            return false;
        }

        slots.put(slot.id, slot);
        return true;
    }

    public String buyItem(
            String slotId,
            int quantity,
            PaymentMethod paymentMethod
    ) {

        // Machine must be idle
        if (state != MachineState.IDLE) {
            System.out.println(
                    "Machine is currently " + state
            );
            return null;
        }

        // Validate quantity
        if (quantity < 1) {
            System.out.println(
                    "Quantity should be at least 1"
            );
            return null;
        }

        // Validate payment method
        if (paymentMethod == null) {
            System.out.println(
                    "Payment method is required"
            );
            return null;
        }

        // Find slot
        Slot slot = slots.get(slotId);

        if (slot == null) {
            System.out.println("Invalid slot");
            return null;
        }

        // Check product
        if (slot.item == null || slot.isEmpty()) {
            System.out.println("Item not available");
            return null;
        }

        // Check inventory
        if (!slot.hasQuantity(quantity)) {
            System.out.println("Insufficient quantity");
            return null;
        }

        // Create transaction
        Transaction transaction =
                new Transaction(slot.item, quantity);

        transactions.put(
                transaction.id,
                transaction
        );

        int totalCost = transaction.getTotalCost();

        // Move machine to payment state
        state = MachineState.PAYMENT_PENDING;

        System.out.println(
                "Please pay ₹" + totalCost
        );

        // Process payment
        boolean paymentSuccessful =
                paymentMethod.pay(totalCost);

        if (!paymentSuccessful) {

            transaction.updateStatus(
                    TransactionStatus.CANCELLED
            );

            state = MachineState.IDLE;

            System.out.println("Payment failed");

            return transaction.id;
        }

        // Payment successful
        state = MachineState.DISPENSING;

        System.out.println("Payment successful");

        // Dispense item
        slot.reduceQuantity(quantity);

        System.out.println(
                "Dispensed " +
                        quantity +
                        " " +
                        slot.item.name
        );

        // Complete transaction
        transaction.updateStatus(
                TransactionStatus.COMPLETED
        );

        // Machine becomes idle again
        state = MachineState.IDLE;

        return transaction.id;
    }

    public Transaction getTransaction(String transactionId) {
        return transactions.get(transactionId);
    }

    public MachineState getState() {
        return state;
    }

    public void putOutOfService() {

        if (state != MachineState.IDLE) {
            System.out.println(
                    "Cannot put machine out of service during "
                            + state
            );
            return;
        }

        state = MachineState.OUT_OF_SERVICE;

        System.out.println(
                "Machine is now out of service"
        );
    }

    public void bringIntoService() {

        if (state != MachineState.OUT_OF_SERVICE) {
            System.out.println(
                    "Machine is already operational"
            );
            return;
        }

        state = MachineState.IDLE;

        System.out.println(
                "Machine is now operational"
        );
    }
}

public class Main {

    public static void main(String[] args) {

        VendingMachine machine =
                new VendingMachine();

        Product coke =
                new Product("Coke", 50);

        Product pepsi =
                new Product("Pepsi", 40);

        Slot cokeSlot =
                new Slot(coke, 5);

        Slot pepsiSlot =
                new Slot(pepsi, 3);

        machine.addSlot(cokeSlot);
        machine.addSlot(pepsiSlot);

        PaymentMethod cash =
                new CashPayment();

        PaymentMethod card =
                new CardPayment();

        PaymentMethod upi =
                new UPIPayment();


        // -------------------------------
        // 1. Buy 2 Coke
        // -------------------------------

        String transactionId =
                machine.buyItem(
                        cokeSlot.id,
                        2,
                        upi
                );

        System.out.println(
                "Transaction ID: " + transactionId
        );

        System.out.println(
                "Machine state: " + machine.getState()
        );


        // -------------------------------
        // 2. Buy 1 Pepsi
        // -------------------------------

        machine.buyItem(
                pepsiSlot.id,
                1,
                card
        );


        // -------------------------------
        // 3. Insufficient quantity
        // -------------------------------

        machine.buyItem(
                cokeSlot.id,
                10,
                cash
        );


        // -------------------------------
        // 4. Invalid quantity
        // -------------------------------

        machine.buyItem(
                cokeSlot.id,
                0,
                cash
        );


        // -------------------------------
        // 5. Invalid slot
        // -------------------------------

        machine.buyItem(
                "invalid-slot",
                1,
                cash
        );


        // -------------------------------
        // 6. Put machine out of service
        // -------------------------------

        machine.putOutOfService();


        // -------------------------------
        // 7. Try buying while
        //    machine is out of service
        // -------------------------------

        machine.buyItem(
                cokeSlot.id,
                1,
                cash
        );


        // -------------------------------
        // 8. Bring machine back
        // -------------------------------

        machine.bringIntoService();


        // -------------------------------
        // 9. Buy again
        // -------------------------------

        machine.buyItem(
                cokeSlot.id,
                1,
                cash
        );
    }
}