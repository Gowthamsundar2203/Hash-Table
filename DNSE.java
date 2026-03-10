class DNSEntry {

    String domain;
    String ip;
    long expiryTime;
    long lastAccess;
    DNSEntry next;

    DNSEntry(String d, String i, long ttl) {
        domain = d;
        ip = i;
        long now = System.currentTimeMillis();
        expiryTime = now + ttl * 1000;
        lastAccess = now;
        next = null;
    }
}

class DNSCache {

    DNSEntry[] table;
    int capacity = 10;
    int size = 0;

    int hits = 0;
    int misses = 0;

    DNSCache() {
        table = new DNSEntry[capacity];
    }

    int hash(String key) {

        int hash = 0;

        for (int i = 0; i < key.length(); i++) {
            hash = hash + key.charAt(i);
        }

        return hash % capacity;
    }

    void removeExpired(String domain) {

        int index = hash(domain);

        DNSEntry current = table[index];
        DNSEntry prev = null;

        long now = System.currentTimeMillis();

        while (current != null) {

            if (current.domain.equals(domain) && current.expiryTime < now) {

                if (prev == null)
                    table[index] = current.next;
                else
                    prev.next = current.next;

                size--;
                return;
            }

            prev = current;
            current = current.next;
        }
    }

    void put(String domain, String ip, int ttl) {

        if (size >= capacity) {
            removeLRU();
        }

        int index = hash(domain);

        DNSEntry newEntry = new DNSEntry(domain, ip, ttl);

        newEntry.next = table[index];
        table[index] = newEntry;

        size++;
    }

    String get(String domain) {

        removeExpired(domain);

        int index = hash(domain);

        DNSEntry current = table[index];

        while (current != null) {

            if (current.domain.equals(domain)) {

                hits++;
                current.lastAccess = System.currentTimeMillis();
                return current.ip;
            }

            current = current.next;
        }

        misses++;
        return null;
    }

    void removeLRU() {

        DNSEntry lru = null;
        int lruIndex = -1;

        for (int i = 0; i < capacity; i++) {

            DNSEntry current = table[i];

            while (current != null) {

                if (lru == null || current.lastAccess < lru.lastAccess) {
                    lru = current;
                    lruIndex = i;
                }

                current = current.next;
            }
        }

        if (lru != null) {

            DNSEntry current = table[lruIndex];
            DNSEntry prev = null;

            while (current != null) {

                if (current == lru) {

                    if (prev == null)
                        table[lruIndex] = current.next;
                    else
                        prev.next = current.next;

                    size--;
                    return;
                }

                prev = current;
                current = current.next;
            }
        }
    }

    String queryUpstream(String domain) {

        if (domain.equals("google.com"))
            return "172.217.14.206";

        if (domain.equals("youtube.com"))
            return "142.250.190.14";

        return "8.8.8.8";
    }

    String resolve(String domain) {

        String ip = get(domain);

        if (ip != null) {
            return "Cache HIT → " + ip;
        }

        ip = queryUpstream(domain);

        put(domain, ip, 300);

        return "Cache MISS → Query upstream → " + ip;
    }

    void getCacheStats() {

        int total = hits + misses;

        double rate = 0;

        if (total > 0)
            rate = (hits * 100.0) / total;

        System.out.println("Hit Rate: " + rate + "%");
        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
    }
}

public class DNSE {

    public static void main(String[] args) {

        DNSCache cache = new DNSCache();

        System.out.println(cache.resolve("google.com"));
        System.out.println(cache.resolve("google.com"));
        System.out.println(cache.resolve("youtube.com"));

        cache.getCacheStats();
    }
}