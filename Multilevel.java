class Node {

    String videoId;
    String data;

    Node prev;
    Node next;

    Node(String id, String d) {
        videoId = id;
        data = d;
    }
}

class CacheLevel {

    Node[] table;
    int capacity;
    int size = 0;

    Node head;
    Node tail;

    int hits = 0;
    int misses = 0;

    CacheLevel(int cap) {
        capacity = cap;
        table = new Node[cap];
    }

    int hash(String key) {

        int h = 0;

        for (int i = 0; i < key.length(); i++)
            h += key.charAt(i);

        return h % capacity;
    }

    Node get(String id) {

        int index = hash(id);

        Node node = table[index];

        while (node != null) {

            if (node.videoId.equals(id)) {

                hits++;
                moveToFront(node);
                return node;
            }

            node = node.next;
        }

        misses++;
        return null;
    }

    void put(String id, String data) {

        if (size >= capacity)
            evictLRU();

        Node node = new Node(id, data);

        int index = hash(id);

        node.next = table[index];
        table[index] = node;

        addToFront(node);

        size++;
    }

    void addToFront(Node node) {

        node.prev = null;
        node.next = head;

        if (head != null)
            head.prev = node;

        head = node;

        if (tail == null)
            tail = node;
    }

    void moveToFront(Node node) {

        if (node == head)
            return;

        if (node.prev != null)
            node.prev.next = node.next;

        if (node.next != null)
            node.next.prev = node.prev;

        if (node == tail)
            tail = node.prev;

        addToFront(node);
    }

    void evictLRU() {

        if (tail == null)
            return;

        int index = hash(tail.videoId);

        table[index] = null;

        if (tail.prev != null)
            tail.prev.next = null;

        tail = tail.prev;

        size--;
    }
}

class MultiLevelCache {

    CacheLevel L1 = new CacheLevel(10000);
    CacheLevel L2 = new CacheLevel(100000);

    int l3Hits = 0;

    String databaseFetch(String videoId) {

        l3Hits++;

        return "VideoData:" + videoId;
    }

    void getVideo(String videoId) {

        Node node = L1.get(videoId);

        if (node != null) {

            System.out.println("L1 Cache HIT (0.5ms)");
            return;
        }

        System.out.println("L1 Cache MISS");

        node = L2.get(videoId);

        if (node != null) {

            System.out.println("L2 Cache HIT (5ms)");
            L1.put(videoId, node.data);
            System.out.println("Promoted to L1");
            return;
        }

        System.out.println("L2 Cache MISS");

        String data = databaseFetch(videoId);

        System.out.println("L3 Database HIT (150ms)");

        L2.put(videoId, data);

        System.out.println("Added to L2");
    }

    void getStatistics() {

        int l1Total = L1.hits + L1.misses;
        int l2Total = L2.hits + L2.misses;

        double l1Rate = (l1Total == 0) ? 0 : (L1.hits * 100.0 / l1Total);
        double l2Rate = (l2Total == 0) ? 0 : (L2.hits * 100.0 / l2Total);

        System.out.println("L1: Hit Rate " + l1Rate + "% Avg Time: 0.5ms");
        System.out.println("L2: Hit Rate " + l2Rate + "% Avg Time: 5ms");
        System.out.println("L3: Hits " + l3Hits + " Avg Time: 150ms");
    }

    void invalidate(String videoId) {

        int index = L1.hash(videoId);
        L1.table[index] = null;

        index = L2.hash(videoId);
        L2.table[index] = null;

        System.out.println("Cache invalidated for " + videoId);
    }
}

public class Multilevel {

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        cache.getVideo("video_123");

        cache.getVideo("video_123");

        cache.getVideo("video_999");

        cache.getStatistics();

        cache.invalidate("video_123");
    }
}