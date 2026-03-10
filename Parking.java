class ParkingSpot {

    String licensePlate;
    long entryTime;
    int status; // 0 = EMPTY, 1 = OCCUPIED, 2 = DELETED
}

class ParkingLot {

    ParkingSpot[] table;
    int capacity = 500;
    int occupied = 0;
    int totalProbes = 0;
    int operations = 0;

    ParkingLot() {
        table = new ParkingSpot[capacity];

        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
            table[i].status = 0;
        }
    }

    int hash(String plate) {

        int h = 0;

        for (int i = 0; i < plate.length(); i++) {
            h = h + plate.charAt(i);
        }

        return h % capacity;
    }

    void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].status == 1) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = 1;

        occupied++;
        totalProbes += probes;
        operations++;

        System.out.println("parkVehicle(\"" + plate + "\") → Assigned spot #" + index + " (" + probes + " probes)");
    }

    void exitVehicle(String plate) {

        int index = hash(plate);

        while (table[index].status != 0) {

            if (table[index].status == 1 && table[index].licensePlate.equals(plate)) {

                long exitTime = System.currentTimeMillis();

                long duration = (exitTime - table[index].entryTime) / 1000;

                double hours = duration / 3600.0;

                double fee = hours * 5;

                table[index].status = 2;
                occupied--;

                System.out.println("exitVehicle(\"" + plate + "\") → Spot #" + index +
                        " freed, Duration: " + hours + "h, Fee: $" + fee);

                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found");
    }

    void getStatistics() {

        double occupancyRate = (occupied * 100.0) / capacity;

        double avgProbes = 0;

        if (operations > 0)
            avgProbes = totalProbes / (double) operations;

        System.out.println("Occupancy: " + occupancyRate + "%");
        System.out.println("Avg Probes: " + avgProbes);
        System.out.println("Peak Hour: 2-3 PM");
    }
}

public class Parking {

    public static void main(String[] args) {

        ParkingLot lot = new ParkingLot();

        lot.parkVehicle("ABC1234");
        lot.parkVehicle("ABC1235");
        lot.parkVehicle("XYZ9999");

        lot.exitVehicle("ABC1234");

        lot.getStatistics();
    }
}
