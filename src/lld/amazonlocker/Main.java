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

    public static void main(String[] args){
        System.out.println("Hello");
    }
}
