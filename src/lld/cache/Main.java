package lld.cache;

import java.util.HashMap;
import java.util.Map;

/*
 * LLD Problem: Multi-Tier Cache
 *
 * Design and implement a Multi-Tier Cache System that stores key-value
 * pairs across multiple cache levels.
 *
 * Requirements:
 * 1. Support multiple cache tiers such as L1, L2 and L3.
 * 2. Each tier has its own capacity.
 * 3. Each tier has its own eviction policy.
 * 4. L1 should be checked first, followed by L2, then L3.
 * 5. On a lower-tier hit, the value should be promoted to faster tiers.
 * 6. Eviction policy should be extensible.
 * 7. Cache should be completely in-memory.
 */


/* ============================================================
   DATA
   ============================================================ */

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


/* ============================================================
   EVICTION POLICY
   ============================================================ */

interface EvictionPolicy {

    Node getElementToBeEvicted();

    void onAccess(Node node);

    void onInsert(Node node);

    void onRemove(Node node);
}


/* ============================================================
   LRU EVICTION POLICY
   ============================================================ */

class Lru implements EvictionPolicy {

    private final Node head;
    private final Node tail;

    Lru() {
        head = new Node(0, 0);
        tail = new Node(0, 0);

        head.next = tail;
        tail.prev = head;
    }

    /*
     * List structure:
     *
     * HEAD <-> MRU <-> ... <-> LRU <-> TAIL
     *
     * Most recently used node is next to HEAD.
     * Least recently used node is previous to TAIL.
     */

    private void remove(Node node) {

        if (node == null || node.prev == null || node.next == null) {
            return;
        }

        node.prev.next = node.next;
        node.next.prev = node.prev;

        node.prev = null;
        node.next = null;
    }

    private void insertAtHead(Node node) {

        node.next = head.next;
        node.prev = head;

        head.next.prev = node;
        head.next = node;
    }

    @Override
    public Node getElementToBeEvicted() {

        if (head.next == tail) {
            return null;
        }

        return tail.prev;
    }

    @Override
    public void onAccess(Node node) {

        remove(node);
        insertAtHead(node);
    }

    @Override
    public void onInsert(Node node) {

        insertAtHead(node);
    }

    @Override
    public void onRemove(Node node) {

        remove(node);
    }
}


/* ============================================================
   CACHE TIER
   ============================================================ */

class CacheTier {

    private final Map<Integer, Node> cache;
    private final int capacity;
    private final EvictionPolicy evictionPolicy;

    CacheTier(int capacity) {

        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "Cache capacity must be greater than 0"
            );
        }

        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.evictionPolicy = new Lru();
    }

    public Integer getVal(int key) {

        Node node = cache.get(key);

        // Cache miss
        if (node == null) {
            return null;
        }

        // Accessed item becomes MRU
        evictionPolicy.onAccess(node);

        return node.value;
    }

    public void setVal(int key, int value) {

        /*
         * Update existing key
         */
        if (cache.containsKey(key)) {

            Node node = cache.get(key);

            node.value = value;

            // Updated item becomes MRU
            evictionPolicy.onAccess(node);

            return;
        }

        /*
         * Cache is full.
         * Remove LRU item before inserting new item.
         */
        if (cache.size() >= capacity) {

            Node nodeToRemove =
                    evictionPolicy.getElementToBeEvicted();

            if (nodeToRemove != null) {

                cache.remove(nodeToRemove.key);

                evictionPolicy.onRemove(nodeToRemove);
            }
        }

        /*
         * Insert new item.
         */
        Node newNode = new Node(key, value);

        cache.put(key, newNode);

        evictionPolicy.onInsert(newNode);
    }

    public void remove(int key) {

        Node node = cache.remove(key);

        if (node != null) {
            evictionPolicy.onRemove(node);
        }
    }

    public int size() {
        return cache.size();
    }

    public int capacity() {
        return capacity;
    }
}


/* ============================================================
   MULTI-TIER CACHE SERVICE
   ============================================================ */

class MultiTierCacheService {

    private final CacheTier l1;
    private final CacheTier l2;
    private final CacheTier l3;

    MultiTierCacheService(
            int l1Capacity,
            int l2Capacity,
            int l3Capacity
    ) {

        l1 = new CacheTier(l1Capacity);
        l2 = new CacheTier(l2Capacity);
        l3 = new CacheTier(l3Capacity);
    }

    /*
     * Read path:
     *
     * L1 -> L2 -> L3
     *
     * If found in L2:
     * L2 -> L1
     *
     * If found in L3:
     * L3 -> L2 -> L1
     */
    public Integer getVal(int key) {

        // Check L1
        Integer value = l1.getVal(key);

        if (value != null) {
            return value;
        }

        // Check L2
        value = l2.getVal(key);

        if (value != null) {

            // Promote to L1
            l1.setVal(key, value);

            return value;
        }

        // Check L3
        value = l3.getVal(key);

        if (value != null) {

            // Promote through the hierarchy
            l2.setVal(key, value);
            l1.setVal(key, value);

            return value;
        }

        // Complete cache miss
        return null;
    }

    /*
     * Write-through:
     *
     * Write to all tiers.
     */
    public void setVal(int key, int value) {

        l1.setVal(key, value);
        l2.setVal(key, value);
        l3.setVal(key, value);
    }

    /*
     * Remove value from every tier.
     */
    public void remove(int key) {

        l1.remove(key);
        l2.remove(key);
        l3.remove(key);
    }
}


/* ============================================================
   TEST
   ============================================================ */

public class Main {

    public static void main(String[] args) {

        MultiTierCacheService cache =
                new MultiTierCacheService(
                        2,  // L1
                        3,  // L2
                        4   // L3
                );

        /*
         * -------------------------
         * Basic set/get
         * -------------------------
         */

        cache.setVal(1, 100);
        cache.setVal(2, 200);

        System.out.println(cache.getVal(1)); // 100
        System.out.println(cache.getVal(2)); // 200


        /*
         * -------------------------
         * L1 eviction
         * -------------------------
         */

        cache.setVal(3, 300);

        /*
         * L1 capacity = 2.
         *
         * Before inserting 3:
         *
         * L1:
         * 1 = MRU
         * 2 = LRU
         *
         * Therefore 2 is evicted from L1.
         *
         * But 2 still exists in L2/L3.
         */


        /*
         * -------------------------
         * Lower-tier fallback
         * -------------------------
         */

        System.out.println(cache.getVal(2)); // 200

        /*
         * 2 was evicted from L1.
         *
         * Flow:
         *
         * L1 -> MISS
         * L2 -> HIT
         *
         * Then 2 is promoted back to L1.
         */


        /*
         * -------------------------
         * Complete cache miss
         * -------------------------
         */

        System.out.println(cache.getVal(999)); // null


        /*
         * -------------------------
         * Update existing value
         * -------------------------
         */

        cache.setVal(1, 500);

        System.out.println(cache.getVal(1)); // 500


        /*
         * -------------------------
         * Remove
         * -------------------------
         */

        cache.remove(1);

        System.out.println(cache.getVal(1)); // null
    }
}