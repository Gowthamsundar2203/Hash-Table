class Ecommerce{

    int[] users = new int[50000];
    int front = 0;
    int rear = -1;

    public void add(int userId) {
        rear = rear + 1;
        users[rear] = userId;
    }

    public int size() {
        return rear - front + 1;
    }

    public int getPosition() {
        return size();
    }
}

class Product {

    String productId;
    int stock;
    Ecommerce queue = new Ecommerce();

    Product(String id, int stock) {
        this.productId = id;
        this.stock = stock;
    }
}

class InventoryManager {

    Product[] products = new Product[10];
    int count = 0;

    public void addProduct(String id, int stock) {
        products[count] = new Product(id, stock);
        count = count + 1;
    }

    public Product findProduct(String id) {
        for (int i = 0; i < count; i++) {
            if (products[i].productId.equals(id)) {
                return products[i];
            }
        }
        return null;
    }

    public int checkStock(String productId) {
        Product p = findProduct(productId);
        if (p != null) {
            return p.stock;
        }
        return 0;
    }

    public synchronized String purchaseItem(String productId, int userId) {

        Product p = findProduct(productId);

        if (p.stock > 0) {
            p.stock = p.stock - 1;
            return "Success, " + p.stock + " units remaining";
        }

        p.queue.add(userId);
        int pos = p.queue.getPosition();
        return "Added to waiting list, position #" + pos;
    }

    public static void main(String[] args) {

        InventoryManager manager = new InventoryManager();

        manager.addProduct("IPHONE15_256GB", 100);

        System.out.println(manager.checkStock("IPHONE15_256GB") + " units available");

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));

        for (int i = 0; i < 100; i++) {
            manager.purchaseItem("IPHONE15_256GB", i);
        }

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));
    }
}
