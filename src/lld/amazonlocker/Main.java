package lld.amazonlocker;

/*
* Entities:
*   - Locker
*   - Slot {small, medium, large}
*   - AccessToken
*   - User
*   - Item
* */

import java.time.LocalDateTime;
import java.util.*;

enum SlotSize{
    Small,
    Medium,
    Large
}

class User{
        String id;
        String name;

        User(String name){
            this.id = UUID.randomUUID().toString();
            this.name = name;
        }
}

class Item{
    String name;
    User owner;
    SlotSize itemSize;

    Item(String name, SlotSize size, User owner){
        this.name = name;
        this.itemSize = size;
        this.owner = owner;
    }

    public String getName(){
        return name;
    }
}

class Slot{
    String slotId;
    SlotSize size;
    private Item item;
    boolean isFree() { return item == null; }
    void assign(Item i) { this.item = i; }
    Item release() { Item i = item; item = null; return i; }

    Slot(SlotSize size){
        this.slotId = UUID.randomUUID().toString();
        this.size = size;
    }

}

class AccessToken{
    String id;
    LocalDateTime expiryTime;

    AccessToken(){
        this.id = UUID.randomUUID().toString();
        this.expiryTime = LocalDateTime.now().plusDays(7);

    }

    public boolean validToken(){
        return !LocalDateTime.now().isAfter(this.expiryTime);
    }

}

class Locker{
    String id;
    List<Slot> totalSlots;
    Map<AccessToken, Slot> tokenToSlot;

    Locker(){
        this.id = UUID.randomUUID().toString();
        this.tokenToSlot = new HashMap<>();
        this.totalSlots = new ArrayList<>();
    }

    private Slot getAvailableSlot(SlotSize size){

        for(var slot : totalSlots){
            if(slot.size == size && slot.isFree()){
                return slot;
            }
        }

        return null;
    }

    public AccessToken depositPackage(Item item) {
        synchronized (this){
            Slot currSlot = getAvailableSlot(item.itemSize);

            if (currSlot == null) {
                System.out.println("No Slot available");
                return null;
            }

            currSlot.assign(item);

            AccessToken accessToken = new AccessToken();
            tokenToSlot.put(accessToken, currSlot);
            return accessToken;
        }

    }

    public boolean takePackage(AccessToken accessToken){


       if(!accessToken.validToken()){
           System.out.println("Token Expired");
           return false;
       }

       if(!tokenToSlot.containsKey(accessToken)){
           System.out.println("Wrong token, please enter a valid token");
           return false;
       }

       Slot slot = tokenToSlot.get(accessToken);
       Item item = slot.release();
       tokenToSlot.remove(accessToken);
       System.out.println("Item :" + item.getName() + " taken out");

       return true;
    }
}

public class Main {

    public static void main(String[] args) {

        // Create users
        User user1 = new User("Ayush");
        User user2 = new User("Rahul");

        // Create locker
        Locker locker = new Locker();

        // Add slots
        locker.totalSlots.add(new Slot(SlotSize.Small));
        locker.totalSlots.add(new Slot(SlotSize.Medium));
        locker.totalSlots.add(new Slot(SlotSize.Large));

        // ==========================================
        // TEST 1: Successful deposit
        // ==========================================

        System.out.println("\n--- TEST 1: Successful Deposit ---");

        Item item1 = new Item("iPhone", SlotSize.Small, user1);

        AccessToken token1 = locker.depositPackage(item1);

        System.out.println("Token generated: " + (token1 != null));


        // ==========================================
        // TEST 2: Deposit different sizes
        // ==========================================

        System.out.println("\n--- TEST 2: Different Slot Sizes ---");

        Item item2 = new Item("Laptop", SlotSize.Medium, user2);
        Item item3 = new Item("TV", SlotSize.Large, user1);

        AccessToken token2 = locker.depositPackage(item2);
        AccessToken token3 = locker.depositPackage(item3);

        System.out.println("Medium deposit: " + (token2 != null));
        System.out.println("Large deposit: " + (token3 != null));


        // ==========================================
        // TEST 3: No slot available
        // ==========================================

        System.out.println("\n--- TEST 3: No Slot Available ---");

        Item item4 = new Item("Another Phone", SlotSize.Small, user2);

        AccessToken token4 = locker.depositPackage(item4);

        System.out.println("Expected: false");
        System.out.println("Actual: " + (token4 != null));


        // ==========================================
        // TEST 4: Successful pickup
        // ==========================================

        System.out.println("\n--- TEST 4: Successful Pickup ---");

        boolean pickedUp = locker.takePackage(token1);

        System.out.println("Expected: true");
        System.out.println("Actual: " + pickedUp);


        // ==========================================
        // TEST 5: Same token used twice
        // ==========================================

        System.out.println("\n--- TEST 5: Reuse Token ---");

        boolean secondPickup = locker.takePackage(token1);

        System.out.println("Expected: false");
        System.out.println("Actual: " + secondPickup);


        // ==========================================
        // TEST 6: Invalid token
        // ==========================================

        System.out.println("\n--- TEST 6: Invalid Token ---");

        AccessToken invalidToken = new AccessToken();

        boolean invalidPickup = locker.takePackage(invalidToken);

        System.out.println("Expected: false");
        System.out.println("Actual: " + invalidPickup);


        // ==========================================
        // TEST 7: Slot becomes available again
        // ==========================================

        System.out.println("\n--- TEST 7: Reuse Freed Slot ---");

        Item item5 = new Item("New Phone", SlotSize.Small, user1);

        AccessToken token5 = locker.depositPackage(item5);

        System.out.println("Expected: true");
        System.out.println("Actual: " + (token5 != null));


        // ==========================================
        // TEST 8: Expired token
        // ==========================================

        System.out.println("\n--- TEST 8: Expired Token ---");

        AccessToken expiredToken =
                new AccessToken();

        // For testing only:
        expiredToken.expiryTime = LocalDateTime.now().minusDays(1);

        boolean expiredPickup = locker.takePackage(expiredToken);

        System.out.println("Expected: false");
        System.out.println("Actual: " + expiredPickup);
    }
}
