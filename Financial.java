class Transaction {

    int id;
    int amount;
    String merchant;
    int time; // minutes from start
    String account;

    Transaction(int i, int a, String m, int t, String acc) {
        id = i;
        amount = a;
        merchant = m;
        time = t;
        account = acc;
    }
}

class HashEntry {

    int key;
    Transaction value;
    HashEntry next;

    HashEntry(int k, Transaction v) {
        key = k;
        value = v;
        next = null;
    }
}

class HashTable {

    HashEntry[] table;
    int capacity = 1000;

    HashTable() {
        table = new HashEntry[capacity];
    }

    int hash(int key) {
        return key % capacity;
    }

    void insert(int key, Transaction t) {

        int index = hash(key);

        HashEntry newEntry = new HashEntry(key, t);

        newEntry.next = table[index];
        table[index] = newEntry;
    }

    Transaction search(int key) {

        int index = hash(key);

        HashEntry current = table[index];

        while (current != null) {

            if (current.key == key)
                return current.value;

            current = current.next;
        }

        return null;
    }
}

class FraudDetector {

    Transaction[] transactions;
    int size;

    FraudDetector(Transaction[] t, int n) {
        transactions = t;
        size = n;
    }

    void findTwoSum(int target) {

        HashTable table = new HashTable();

        for (int i = 0; i < size; i++) {

            int complement = target - transactions[i].amount;

            Transaction match = table.search(complement);

            if (match != null) {

                System.out.println("Pair Found → (" +
                        match.id + ", " + transactions[i].id + ")");
            }

            table.insert(transactions[i].amount, transactions[i]);
        }
    }

    void findTwoSumTimeWindow(int target) {

        HashTable table = new HashTable();

        for (int i = 0; i < size; i++) {

            int complement = target - transactions[i].amount;

            Transaction match = table.search(complement);

            if (match != null) {

                int diff = transactions[i].time - match.time;

                if (diff <= 60 && diff >= 0) {

                    System.out.println("Pair within 1 hour → (" +
                            match.id + ", " + transactions[i].id + ")");
                }
            }

            table.insert(transactions[i].amount, transactions[i]);
        }
    }

    void detectDuplicates() {

        for (int i = 0; i < size; i++) {

            for (int j = i + 1; j < size; j++) {

                if (transactions[i].amount == transactions[j].amount &&
                        transactions[i].merchant.equals(transactions[j].merchant) &&
                        !transactions[i].account.equals(transactions[j].account)) {

                    System.out.println("Duplicate Suspicious Payment → Amount: "
                            + transactions[i].amount +
                            " Merchant: " + transactions[i].merchant);
                }
            }
        }
    }

    void findKSum(int start, int k, int target, int[] result, int depth) {

        if (k == 0 && target == 0) {

            System.out.print("K-Sum Found → (");

            for (int i = 0; i < depth; i++)
                System.out.print(result[i] + " ");

            System.out.println(")");

            return;
        }

        if (k == 0)
            return;

        for (int i = start; i < size; i++) {

            result[depth] = transactions[i].id;

            findKSum(i + 1,
                    k - 1,
                    target - transactions[i].amount,
                    result,
                    depth + 1);
        }
    }
}

public class Financial {

    public static void main(String[] args) {

        Transaction[] transactions = new Transaction[3];

        transactions[0] = new Transaction(1, 500, "StoreA", 600, "acc1");
        transactions[1] = new Transaction(2, 300, "StoreB", 615, "acc2");
        transactions[2] = new Transaction(3, 200, "StoreC", 630, "acc3");

        FraudDetector detector = new FraudDetector(transactions, 3);

        System.out.println("Two Sum:");
        detector.findTwoSum(500);

        System.out.println("\nTwo Sum with Time Window:");
        detector.findTwoSumTimeWindow(500);

        System.out.println("\nDuplicate Detection:");
        detector.detectDuplicates();

        System.out.println("\nK-Sum (k=3, target=1000):");

        int[] result = new int[3];

        detector.findKSum(0, 3, 1000, result, 0);
    }
}