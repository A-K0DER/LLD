package lld.cache;
/*
 *
 * LLD Problem: Multi-Tier Cache
 * Design and implement a Multi-Tier Cache System that stores key-value pairs across multiple cache levels.
 * The system should support multiple cache tiers such as L1, L2, and L3.
 * Each tier has its own maximum capacity and eviction policy.
 * L1 should be checked first, followed by L2, then L3.
 * Entities:
 *   - cacheService
 *   - eviction policy
 *   - cache tiers
 *   - data { key, value }
 */

import java.util.HashMap;
import java.util.Map;

enum UpdateType {
    ACCESS,
    INSERT,
    REMOVE
}

class Node {
    int key;
    int value;
    Node prev;
    Node next;

    Node(int key, int value) {
        this.key = key;
        this.value = value;
    }
}

interface EvictionPolicy {
    Node elementToBeRemoved();
    void updateElement(Node node, UpdateType branch);
}

class Lru implements EvictionPolicy {
    Node head;
    Node tail;

    Lru() {
        head = new Node(0, 0);
        tail = new Node(0, 0);

        head.next = tail;
        tail.prev = head;
    }

    private void remove(Node node) {
        node.next.prev = node.prev;
        node.prev.next = node.next;
    }

    private void insert(Node node) {
        node.next = head.next;
        head.next = node;
        node.prev = head;
        node.next.prev = node;
    }

    @Override
    public Node elementToBeRemoved() {
        return tail.prev;
    }

    @Override
    public void updateElement(Node lru, UpdateType branch) {

        switch (branch) {
            case ACCESS:
                remove(lru);
                insert(lru);
                break;

            case INSERT:
                insert(lru);
                break;

            case REMOVE:
                remove(lru);
                break;
        }
    }
}

class CacheTier{
    Map<Integer, Node> map;
    EvictionPolicy evictionPolicy;
    int size;

    CacheTier(int size) {
        map = new HashMap<>();
        evictionPolicy = new Lru();
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public int getVal(int key) {
        if (!map.containsKey(key)) return -1;

        Node currVal = map.get(key);
        evictionPolicy.updateElement(currVal, UpdateType.ACCESS);

        return currVal.value;
    }

    public void setVal(int key, int value) {

        if (map.containsKey(key)) {
            Node currVal = map.get(key);
            currVal.value = value;
            evictionPolicy.updateElement(currVal, UpdateType.ACCESS);
            return;
        }

        if (map.size() == size) {
            Node elementToRemove = evictionPolicy.elementToBeRemoved();
            map.remove(elementToRemove.key);
            evictionPolicy.updateElement(elementToRemove, UpdateType.REMOVE);
        }

        Node newNode = new Node(key, value);

        map.put(key, newNode);

        evictionPolicy.updateElement(newNode, UpdateType.INSERT);
    }
}

class MultiTierCacheService {

    private final CacheTier l1;
    private final CacheTier l2;
    private final CacheTier l3;

    MultiTierCacheService(int l1Size, int l2Size, int l3Size) {
        l1 = new CacheTier(l1Size);
        l2 = new CacheTier(l2Size);
        l3 = new CacheTier(l3Size);
    }

    public int getVal(int key) {

        int value = l1.getVal(key);

        if (value != -1) {
            return value;
        }

        value = l2.getVal(key);

        if (value != -1) {
            l1.setVal(key, value);
            return value;
        }

        value = l3.getVal(key);

        if (value != -1) {
            l1.setVal(key, value);
            return value;
        }

        return -1;
    }

    public void setVal(int key, int value) {

        l1.setVal(key, value);
        l2.setVal(key, value);
        l3.setVal(key, value);
    }
}

public class Main {

}