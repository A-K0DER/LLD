package lld.notificationservice;/*
 Notification System
    1. Notification
    2. NotificationType
    Email
    SMS
    Push
    3. User
    LLD Interview Progression List
    Page 3 of 7
    4. NotificationService
    5. NotificationSender interface
    6. EmailSender
    7. SMSSender
    8. PushSender
    9. Send notification to a user
    10. User notification preferences

*/

import java.util.* ;

enum NotificationType {
    EMAIL,
    SMS,
    PUSH;
}

class Notification{

    String message;

    Notification(String message){
        this.message = message;
    }

}

interface NotificationSender {
    void sendNotification(Notification notification);

}

class User{
    private final String name;
    public List<NotificationType> preferredTypes;

    User(String name){
        this.name = name;
        preferredTypes = new ArrayList<>();
    }

    public void addPreferredType(NotificationType notifType){
        preferredTypes.add(notifType);
    }
}

class EmailSender implements NotificationSender {

    @Override
    public void sendNotification(Notification notification){
        System.out.println(notification.message + " Sent via Email");
    }
}

class SmsSender implements NotificationSender {

    @Override
    public void sendNotification(Notification notification){
        System.out.println(notification.message + " Sent via SMS");
    }
}

class PushSender implements NotificationSender {

    @Override
    public void sendNotification(Notification notification){
        System.out.println( notification.message + " Sent via Push");
    }
}
class NotificationService{

    private final NotificationType defaultType;
    private final Map<NotificationType, NotificationSender> notifMap;


    NotificationService(){
        this.defaultType = NotificationType.EMAIL;
        this.notifMap = new HashMap<>();
        notifMap.put(NotificationType.EMAIL, new EmailSender());
        notifMap.put(NotificationType.SMS, new SmsSender());
        notifMap.put(NotificationType.PUSH, new PushSender());

    }
    public void sendNotification(Notification notif, User user){

        if(user.preferredTypes.isEmpty()){
            notifMap.get(defaultType).sendNotification(notif);
        }
        for(int i = 0; i < user.preferredTypes.size(); i++){
            notifMap.get(user.preferredTypes.get(i)).sendNotification(notif);
        }

    }

}


public class Main {

    public static void main(String[] args) {

        NotificationService service = new NotificationService();

        Notification notification =
                new Notification("Your order has been shipped");


        // Test 1: No preferences
        System.out.println("TEST 1");
        User user1 = new User("Ayush");

        service.sendNotification(notification, user1);


        // Test 2: EMAIL
        System.out.println("\nTEST 2");
        User user2 = new User("User2");
        user2.addPreferredType(NotificationType.EMAIL);

        service.sendNotification(notification, user2);


        // Test 3: SMS
        System.out.println("\nTEST 3");
        User user3 = new User("User3");
        user3.addPreferredType(NotificationType.SMS);

        service.sendNotification(notification, user3);


        // Test 4: PUSH
        System.out.println("\nTEST 4");
        User user4 = new User("User4");
        user4.addPreferredType(NotificationType.PUSH);

        service.sendNotification(notification, user4);


        // Test 5: EMAIL + SMS
        System.out.println("\nTEST 5");
        User user5 = new User("User5");
        user5.addPreferredType(NotificationType.EMAIL);
        user5.addPreferredType(NotificationType.SMS);

        service.sendNotification(notification, user5);


        // Test 6: SMS + PUSH
        System.out.println("\nTEST 6");
        User user6 = new User("User6");
        user6.addPreferredType(NotificationType.SMS);
        user6.addPreferredType(NotificationType.PUSH);

        service.sendNotification(notification, user6);


        // Test 7: All notification types
        System.out.println("\nTEST 7");
        User user7 = new User("User7");
        user7.addPreferredType(NotificationType.EMAIL);
        user7.addPreferredType(NotificationType.SMS);
        user7.addPreferredType(NotificationType.PUSH);

        service.sendNotification(notification, user7);


        // Test 8: Duplicate preference
        System.out.println("\nTEST 8");
        User user8 = new User("User8");
        user8.addPreferredType(NotificationType.EMAIL);
        user8.addPreferredType(NotificationType.EMAIL);

        service.sendNotification(notification, user8);


        // Test 9: Different users
        System.out.println("\nTEST 9");

        User user9a = new User("User9A");
        user9a.addPreferredType(NotificationType.SMS);

        User user9b = new User("User9B");
        user9b.addPreferredType(NotificationType.PUSH);

        service.sendNotification(notification, user9a);
        service.sendNotification(notification, user9b);


        // Test 10: Empty notification
        System.out.println("\nTEST 10");

        Notification emptyNotification = new Notification("");

        service.sendNotification(emptyNotification, user1);
    }
}