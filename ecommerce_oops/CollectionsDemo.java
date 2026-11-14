import java.util.*;

/**
 * CollectionsDemo.java
 *
 * Dedicated standalone program demonstrating the 4 core Java Collection Framework interfaces:
 * 1. List (ArrayList) -> Insertion order maintain hoti hai, duplicates allow hote hain.
 * 2. Set (HashSet) -> Duplicates allow nahi hote, unique values store hoti hain.
 * 3. Map (HashMap & LinkedHashMap) -> Key-Value pairs me data store hota hai (e.g., Product -> Quantity).
 * 4. PriorityQueue -> Custom priority ke hisab se elements order hote hain (e.g., VIP Customer First).
 * 5. Collections.sort() -> Sorting algorithm.
 */
public class CollectionsDemo {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("           JAVA COLLECTIONS FRAMEWORK - PRACTICAL DEMO            ");
        System.out.println("==================================================================\n");

        demoList();
        demoSet();
        demoMap();
        demoPriorityQueue();
        demoSorting();
    }

    // 1. LIST DEMO (ArrayList)
    private static void demoList() {
        System.out.println("--- 1. LIST (ArrayList) DEMO ---");
        System.out.println("Kyu use karte hain: Ordered list banani ho aur duplicate items allow karne ho.");

        List<String> cartItems = new ArrayList<>();
        cartItems.add("Laptop");
        cartItems.add("Headphones");
        cartItems.add("T-Shirt");
        cartItems.add("Laptop"); // Duplicate item

        System.out.println("Cart Items (List): " + cartItems);
        System.out.println("Total items in cart: " + cartItems.size());
        System.out.println("Item at index 0: " + cartItems.get(0));
        System.out.println();
    }

    // 2. SET DEMO (HashSet)
    private static void demoSet() {
        System.out.println("--- 2. SET (HashSet) DEMO ---");
        System.out.println("Kyu use karte hain: Unique categories track karni ho (Duplicate automatically remove hote hain).");

        Set<String> categories = new HashSet<>();
        categories.add("Electronics");
        categories.add("Clothing");
        categories.add("Electronics"); // Duplicate category - add nahi hogi!
        categories.add("Books");

        System.out.println("Unique Categories (Set): " + categories);
        System.out.println("Does Set contain 'Clothing'? " + categories.contains("Clothing"));
        System.out.println();
    }

    // 3. MAP DEMO (HashMap & LinkedHashMap)
    private static void demoMap() {
        System.out.println("--- 3. MAP (HashMap) DEMO ---");
        System.out.println("Kyu use karte hain: Key-Value pair me data store karne ke liye (e.g. Product -> Quantity).");

        Map<String, Integer> orderCart = new LinkedHashMap<>();
        // put(key, value)
        orderCart.put("E-101 (Laptop)", 2);
        orderCart.put("E-102 (Headphones)", 1);
        orderCart.put("C-201 (Denim Jacket)", 3);

        System.out.println("Order Items & Quantities (Map):");
        for (Map.Entry<String, Integer> entry : orderCart.entrySet()) {
            System.out.printf("  Product: %-25s | Quantity: %d\n", entry.getKey(), entry.getValue());
        }

        // Fast lookup by Key
        System.out.println("Quantity of Laptop: " + orderCart.get("E-101 (Laptop)"));
        System.out.println();
    }

    // 4. PRIORITY QUEUE DEMO
    private static void demoPriorityQueue() {
        System.out.println("--- 4. PRIORITY QUEUE DEMO ---");
        System.out.println("Kyu use karte hain: VIP/Platinum customer ke order ko Regular customer se pehle process karne ke liye.");

        // Priority class: High number = High priority
        class OrderRequest implements Comparable<OrderRequest> {
            String customerName;
            int priorityLevel; // 4=Platinum, 1=Regular

            OrderRequest(String name, int priority) {
                this.customerName = name;
                this.priorityLevel = priority;
            }

            @Override
            public int compareTo(OrderRequest other) {
                // Higher priority comes first
                return Integer.compare(other.priorityLevel, this.priorityLevel);
            }

            @Override
            public String toString() {
                return customerName + " (Priority: " + priorityLevel + ")";
            }
        }

        PriorityQueue<OrderRequest> queue = new PriorityQueue<>();
        queue.offer(new OrderRequest("Regular Customer (Rahul)", 1));
        queue.offer(new OrderRequest("Platinum Customer (Amit)", 4));
        queue.offer(new OrderRequest("Silver Customer (Pooja)", 2));
        queue.offer(new OrderRequest("Gold Customer (Sneha)", 3));

        System.out.println("Processing Orders by Priority (poll):");
        while (!queue.isEmpty()) {
            System.out.println("  Processing order for: " + queue.poll());
        }
        System.out.println();
    }

    // 5. SORTING WITH COLLECTIONS.SORT
    private static void demoSorting() {
        System.out.println("--- 5. COLLECTIONS.SORT() DEMO ---");
        System.out.println("Kyu use karte hain: Data ko naturally sort karne ke liye (Ascending/Descending).");

        List<Double> productPrices = new ArrayList<>(Arrays.asList(1200.0, 150.0, 80.0, 25.0, 300.0));
        System.out.println("Original Prices: " + productPrices);

        Collections.sort(productPrices); // Ascending
        System.out.println("Sorted (Low to High): " + productPrices);

        Collections.reverse(productPrices); // Descending
        System.out.println("Sorted (High to Low): " + productPrices);
        System.out.println();
    }
}
